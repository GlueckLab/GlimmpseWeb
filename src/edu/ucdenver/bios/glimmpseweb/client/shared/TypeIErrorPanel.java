/*
 * User Interface for the GLIMMPSE Software System.  Allows
 * users to perform power, sample size, and detectable difference
 * calculations. 
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
package edu.ucdenver.bios.glimmpseweb.client.shared;

import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent.StudyDesignChangeType;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.TypeIError;

/**
 * Panel for entering type I error values
 * 
 * @author Sarah Kreidler
 *
 */
public class TypeIErrorPanel extends WizardStepPanel
implements ListValidator
{
    // context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    // list of alpha values
    protected ListEntryPanel alphaListPanel = 
        new ListEntryPanel(GlimmpseWeb.constants.alphaTableColumn() , this);

    /**
     * Create an empty type I error panel
     */
    public TypeIErrorPanel(WizardContext context)
    {
        super(context, GlimmpseWeb.constants.navItemTypeIError(), 
                WizardStepPanelState.INCOMPLETE);
        VerticalPanel panel = new VerticalPanel();

        // create header/instruction text
        HTML header = new HTML(GlimmpseWeb.constants.alphaTitle());
        HTML description = new HTML(GlimmpseWeb.constants.alphaDescription());

        // layout the panels
        panel.add(header);
        panel.add(description);
        panel.add(alphaListPanel);

        // specify the maximum rows in the listbox
        alphaListPanel.setMaxRows(5);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        // initialize the panel
        initWidget(panel);
    }

    /**
     * Validate new entries in the alpha list
     * @see DynamicListValidator
     */
    public void validate(String value) throws IllegalArgumentException
    {
        try
        {
            TextValidation.parseDouble(value, 0, 1, false);
        }
        catch (NumberFormatException nfe)
        {
            throw new IllegalArgumentException(GlimmpseWeb.constants.errorInvalidAlpha());
        }
    }

    /**
     * Clear the list of alpha values.  Note, the onValidRowCount
     * callback will fire when reset is called
     */
    public void reset()
    {
        alphaListPanel.reset();
        checkComplete();
    }

    /**
     * Load the alpha panel from the study design context information
     */
    public void loadFromContext()
    {
        List<TypeIError> contextTypeIErrorList = studyDesignContext.getStudyDesign().getAlphaList();
        alphaListPanel.reset();
        if (contextTypeIErrorList != null) {
            for(TypeIError typeIError: contextTypeIErrorList)
            {
                alphaListPanel.add(Double.toString(typeIError.getAlphaValue()));
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
        if (((StudyDesignChangeEvent) e).getType() ==
            StudyDesignChangeType.ALPHA_LIST &&
            this != e.getSource())
        {
            loadFromContext();
        }
    };

    /**
     * Response to a context load event
     */
    @Override
    public void onWizardContextLoad() 
    {
        loadFromContext();
    }

    /**
     * Validate the alpha level entered by the user and add to the
     * study design. 
     * @param value the alpha value as a string
     */
    @Override
    public void onAdd(String value) throws IllegalArgumentException {
        try
        {
            double alpha = TextValidation.parseDouble(value, 0, 1, false);
            studyDesignContext.addTypeIErrorRate(this, alpha);
            changeState(WizardStepPanelState.COMPLETE);
        }
        catch (NumberFormatException nfe)
        {
            throw new IllegalArgumentException(GlimmpseWeb.constants.errorInvalidAlpha());
        }
    }

    /**
     * Delete the specified alpha value. 
     * @param value the alpha value as a string
     */
    @Override
    public void onDelete(String value, int index) {
        studyDesignContext.deleteTypeIErrorRate(this, Double.parseDouble(value), 
                index);
        checkComplete();
    }

    /**
     * Check if the panel is complete
     */
    private void checkComplete() {
        if (alphaListPanel.getValidRowCount() > 0)
            changeState(WizardStepPanelState.COMPLETE);
        else
            changeState(WizardStepPanelState.INCOMPLETE);
    }
}
