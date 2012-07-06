/* 
 * GLIMMPSE (General Linear Multivariate Model Power and Sample size Estimator)
 *
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.ucdenver.bios.glimmpseweb.client.guided;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.shared.ListEntryPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.ListValidator;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;

/**
 * Response variables panel
 * 
 * @author Sarah Kreidler
 *
 */
public class ResponseVariablesPanel extends WizardStepPanel
implements ListValidator
{   
    private static final String PARTICIPANT_LABEL = "participant";
    // context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    
    // dynamic table of outcomes
    protected ListEntryPanel outcomesListPanel = 
    	new ListEntryPanel(GlimmpseWeb.constants.responseVariablesTableColumn(), this);

    // array list to hold output
    ArrayList<ResponseNode> outcomesList = new ArrayList<ResponseNode>();
    
    // indicates if anything on the panel has changed
    protected boolean changed;
    
    /**
     * Constructor.
     * @param context wizard context
     */
    public ResponseVariablesPanel(WizardContext context)
    {
    	super(context, GlimmpseWeb.constants.navItemResponses(),
    	        WizardStepPanelState.INCOMPLETE);
        VerticalPanel panel = new VerticalPanel();
        
        // create header/instruction text
        HTML header = new HTML(GlimmpseWeb.constants.responseVariablesTitle());
        HTML description = new HTML(GlimmpseWeb.constants.responseVariablesDescription());
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(outcomesListPanel);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
    }
    
    /**
     * Clear the list of response variables
     */
    @Override
    public void reset()
    {
    	outcomesListPanel.reset();
    }
    
    /**
     * Store the list of response variables in the context
     */
    @Override
    public void onExit()
    {
        if (changed) {
            List<String> stringValues = outcomesListPanel.getValues();
            outcomesList.clear();
            for(String value: stringValues)
            {
                outcomesList.add(new ResponseNode(value));
            }
            // save to context object
            studyDesignContext.setResponseList(this, 
                    PARTICIPANT_LABEL, outcomesList);
            changed = false;
        }
    }
    
    /**
     * Load the response variable list from the context
     */
    private void loadFromContext()
    {
        List<ResponseNode> responsesList = studyDesignContext.getStudyDesign().getResponseList();
        if (responsesList != null) {
            for(ResponseNode response: responsesList)
            {
                outcomesListPanel.add(response.getName());
            }
        }
        onValidRowCount(outcomesListPanel.getValidRowCount());
    }
    
    /**
     * Respond to a change in the context object
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) 
    {
        // no action required
    };

    /**
     * Update the screen when the predictors or repeated measures change
     */
    @Override
    public void onWizardContextLoad() 
    {
        loadFromContext();
    }
    
    /**
     * Ensure a valid string
     */
    public void validate(String value) throws IllegalArgumentException {
        try
        {
           TextValidation.parseString(value);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(GlimmpseWeb.constants.errorInvalidString());
        }
    }

    /**
     * Update state to complete if at least 1 response variable is entered.
     */
    public void onValidRowCount(int validRowCount)
    {
        changed = true;
        if (validRowCount > 0)
            changeState(WizardStepPanelState.COMPLETE);
        else
            changeState(WizardStepPanelState.INCOMPLETE);
    }
}
    
