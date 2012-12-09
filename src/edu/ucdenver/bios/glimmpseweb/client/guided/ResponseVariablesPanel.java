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
    // context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    
    // dynamic table of outcomes
    protected ListEntryPanel outcomesListPanel = 
    	new ListEntryPanel(GlimmpseWeb.constants.responseVariablesTableColumn(), this);
    
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
     * Load the response variable list from the context
     */
    private void loadFromContext()
    {
        reset();
        List<ResponseNode> responsesList = studyDesignContext.getStudyDesign().getResponseList();
        if (responsesList != null) {
            for(ResponseNode response: responsesList)
            {
                outcomesListPanel.add(response.getName());
            }
        }
        checkComplete();
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

    @Override
    public void onAdd(String value) throws IllegalArgumentException {
        try {
           TextValidation.parseString(value);
           studyDesignContext.addResponseVariable(this, value);
           changeState(WizardStepPanelState.COMPLETE);
        } catch (Exception e) {
            throw new IllegalArgumentException(GlimmpseWeb.constants.errorInvalidString());
        }
    }

    @Override
    public void onDelete(String value, int index) {
        studyDesignContext.deleteResponseVariable(this, value, index);
        checkComplete();
    }
    
    /**
     * Check if at least one response variable has been entered
     */
    private void checkComplete() {
        if (outcomesListPanel.getValidRowCount() > 0)
            changeState(WizardStepPanelState.COMPLETE);
        else
            changeState(WizardStepPanelState.INCOMPLETE);
    }
}
    
