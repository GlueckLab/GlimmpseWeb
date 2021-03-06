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
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.NominalPower;
import edu.ucdenver.bios.webservice.common.enums.SolutionTypeEnum;

/**
 * Panel for entering nominal power values when performing
 * sample size calculations
 *
 */
public class PowerPanel extends WizardStepPanel
implements ListValidator
{
    // study design context
    StudyDesignContext studyDesignContext;

    // list of nominal power values.  Only displayed when solving for effect size or sample size
    protected ListEntryPanel nominalPowerListPanel = 
        new ListEntryPanel(GlimmpseWeb.constants.solvingForNominalPowerTableColumn(), this);

    public PowerPanel(WizardContext context)
    {
        super(context, GlimmpseWeb.constants.navItemNominalPower(), 
                WizardStepPanelState.SKIPPED);
        studyDesignContext = (StudyDesignContext) context;
        VerticalPanel panel = new VerticalPanel();

        HTML header = new HTML(GlimmpseWeb.constants.solvingForNominalPowerTitle());
        HTML description = new HTML(GlimmpseWeb.constants.solvingForNominalPowerDescription());

        panel.add(header);
        panel.add(description);
        panel.add(nominalPowerListPanel);

        // set style
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);

        initWidget(panel);
    }

    /**
     * Clear the power panel.
     */
    @Override
    public void reset()
    {
        nominalPowerListPanel.reset();
        changeState(WizardStepPanelState.SKIPPED);
    }

    /**
     * Respond to context changes.
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e)
    {
        StudyDesignChangeEvent changeEvent = (StudyDesignChangeEvent) e;
        switch (changeEvent.getType())
        {
        case SOLVING_FOR:
            if (SolutionTypeEnum.POWER == studyDesignContext.getStudyDesign().getSolutionTypeEnum())
            {
                changeState(WizardStepPanelState.SKIPPED);
            }
            else
            {
                checkComplete();
            }			
            break;
        case POWER_LIST:
            if (this != changeEvent.getSource())
            {
                loadFromContext();
            }
            break;
        }
    }

    /**
     * Respond to context load events
     */
    @Override
    public void onWizardContextLoad()
    {
        loadFromContext();
    }

    /**
     * Load the power panel from the study design context information
     */
    public void loadFromContext()
    {
        reset();
        List<NominalPower> contextPowerList = 
            studyDesignContext.getStudyDesign().getNominalPowerList();
        nominalPowerListPanel.reset();
        if (contextPowerList != null) {
            for(NominalPower power: contextPowerList)
            {
                nominalPowerListPanel.add(Double.toString(power.getValue()));
            }
            checkComplete();
        } 
    }
    
    /**
     * Add a nominal power to the study design
     */
    @Override
    public void onAdd(String value) throws IllegalArgumentException {
        try
        {
            double power = TextValidation.parseDouble(value, 0, 1, false);
            studyDesignContext.addNominalPower(this, power);
            changeState(WizardStepPanelState.COMPLETE);
        }
        catch (NumberFormatException nfe)
        {
            throw new IllegalArgumentException(GlimmpseWeb.constants.errorInvalidPower());
        }
    }

    /**
     * Delete a nominal power from the study design
     */
    @Override
    public void onDelete(String value, int index) {
        double power = Double.parseDouble(value);
        studyDesignContext.deleteNominalPower(this, power, index);
        checkComplete();
    }

    /**
     * Check if the panel is complete
     */
    private void checkComplete() {
        if (nominalPowerListPanel.getValidRowCount() > 0)
            changeState(WizardStepPanelState.COMPLETE);
        else
            changeState(WizardStepPanelState.INCOMPLETE);
    }
      
}
