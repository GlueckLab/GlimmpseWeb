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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
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
import edu.ucdenver.bios.webservice.common.domain.PowerMethod;
import edu.ucdenver.bios.webservice.common.domain.Quantile;
import edu.ucdenver.bios.webservice.common.enums.PowerMethodEnum;

/**
 * Panel which allows user to select statistical tests, display options
 * for their power/sample size calculations.  Note that two instances of this 
 * class are created (one for matrix mode, one for guided mode) so any
 * radio groups or other unique identifiers must have a mode-specific prefix
 * 
 * @author Sarah Kreidler
 *
 */
public class OptionsPowerMethodsPanel extends WizardStepPanel
implements ListValidator
{
    // study design context
    StudyDesignContext studyDesignContext;

    // check boxes for power methods (only used when a baseline covariate is specified)
    protected CheckBox unconditionalPowerCheckBox = new CheckBox();
    protected PowerMethod unconditionalMethod = new PowerMethod(PowerMethodEnum.UNCONDITIONAL);
    protected CheckBox quantilePowerCheckBox = new CheckBox();
    protected PowerMethod quantileMethod = new PowerMethod(PowerMethodEnum.QUANTILE);

    // indicates a design with a gaussian covariate
    protected boolean hasCovariate = false;

    // dynamic list of quantile values
    protected ListEntryPanel quantileListPanel = 
        new ListEntryPanel(GlimmpseWeb.constants.quantilesTableColumn(), this);

    /**
     * Constructor
     * @param mode mode identifier (needed for unique widget identifiers)
     */
    public OptionsPowerMethodsPanel(WizardContext context, String mode)
    {
        super(context, GlimmpseWeb.constants.navItemPowerMethod(), 
                WizardStepPanelState.SKIPPED);
        studyDesignContext = (StudyDesignContext) context;
        VerticalPanel panel = new VerticalPanel();

        // create header, description
        HTML header = new HTML(GlimmpseWeb.constants.powerMethodTitle());
        HTML description = new HTML(GlimmpseWeb.constants.powerMethodDescription());        

        // list of power methods
        Grid grid = new Grid(3,2);
        grid.setWidget(0, 0, unconditionalPowerCheckBox);
        grid.setWidget(0, 1, new HTML(GlimmpseWeb.constants.powerMethodUnconditionalLabel()));
        grid.setWidget(1, 0, quantilePowerCheckBox);
        grid.setWidget(1, 1, new HTML(GlimmpseWeb.constants.powerMethodQuantileLabel()));
        grid.setWidget(2, 1, quantileListPanel);

        // only show quantile list when quantile power is selected
        quantileListPanel.setVisible(false);

        // add callback to check if screen is complete
        unconditionalPowerCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CheckBox cb = (CheckBox) event.getSource();
                if (cb.getValue()) {
                    addPowerMethod(PowerMethodEnum.UNCONDITIONAL);
                } else {
                    deletePowerMethod(PowerMethodEnum.UNCONDITIONAL);
                }
                checkComplete();
            }
        });
        quantilePowerCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CheckBox cb = (CheckBox) event.getSource();
                if (cb.getValue()) {
                    addPowerMethod(PowerMethodEnum.QUANTILE);
                } else {
                    deletePowerMethod(PowerMethodEnum.QUANTILE);
                }
                quantileListPanel.setVisible(cb.getValue());
                checkComplete();
            }
        });

        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(grid);

        // set defaults
        reset();

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        // initialize
        initWidget(panel);
    }

    /**
     * Clear the options panel
     */
    public void reset()
    {
        // set the power method to conditional
        unconditionalPowerCheckBox.setValue(false);
        quantilePowerCheckBox.setValue(false);
        quantileListPanel.setVisible(false);
        quantileListPanel.reset();
        hasCovariate = false;
        changeState(WizardStepPanelState.SKIPPED);
    }

    /**
     * Check if the user has selected a complete set of options, and
     * if so notify that forward navigation is allowed
     */
    private void checkComplete()
    {
        if (!hasCovariate) {
            changeState(WizardStepPanelState.SKIPPED);
        } else {
            // check if continue is allowed
            // must have at least one test checked, at least one power method
            if (unconditionalPowerCheckBox.getValue() || 
                    quantilePowerCheckBox.getValue())
            {
                if (!quantilePowerCheckBox.getValue() || quantileListPanel.getValidRowCount() > 0)
                {
                    changeState(WizardStepPanelState.COMPLETE);
                }
                else
                {
                    changeState(WizardStepPanelState.INCOMPLETE);
                }
            }
            else
            {
                changeState(WizardStepPanelState.INCOMPLETE);
            }
        }
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
        case COVARIATE:
            hasCovariate = studyDesignContext.getStudyDesign().isGaussianCovariate();
            checkComplete();
            break;
        case POWER_METHOD_LIST:
            if (this != changeEvent.getSource())
            {
                loadPowerMethodListFromContext();
                checkComplete();
            }
            break;
        case QUANTILE_LIST:
            if (this != changeEvent.getSource())
            {
                loadQuantileListFromContext();
                checkComplete();
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
     * Set the power method check boxes based on the context
     */
    private void loadPowerMethodListFromContext()
    {
        unconditionalPowerCheckBox.setValue(false);
        quantilePowerCheckBox.setValue(false);
        quantileListPanel.setVisible(false);
        List<PowerMethod> methodList = studyDesignContext.getStudyDesign().getPowerMethodList();
        if (methodList != null) {
            for(PowerMethod method: methodList)
            {
                switch (method.getPowerMethodEnum())
                {
                case UNCONDITIONAL:
                    unconditionalPowerCheckBox.setValue(true);
                    break;
                case QUANTILE:
                    quantilePowerCheckBox.setValue(true);
                    quantileListPanel.setVisible(true);
                    break;
                }
            }
        }
    }


    /**
     * Set the quantile list information from the context
     */
    private void loadQuantileListFromContext()
    {
        List<Quantile> contextQuantileList = studyDesignContext.getStudyDesign().getQuantileList();
        quantileListPanel.reset();
        if (contextQuantileList != null) {
            for(Quantile quantile: contextQuantileList) {
                quantileListPanel.add(Double.toString(quantile.getValue()));
            }
        }
    }

    /**
     * Load the sample size panel from the study design context information
     */
    public void loadFromContext()
    {
        reset();
        hasCovariate = studyDesignContext.getStudyDesign().isGaussianCovariate();
        loadPowerMethodListFromContext();
        loadQuantileListFromContext();
        checkComplete();
    }   

    /**
     * Add the specified power method to the study design
     * @param method
     */
    private void addPowerMethod(PowerMethodEnum method) {
        studyDesignContext.addPowerMethod(this, method);
    }
    
    /**
     * Add the specified power method to the study design
     * @param method
     */
    private void deletePowerMethod(PowerMethodEnum method) {
        studyDesignContext.deletePowerMethod(this, method);
    }
    
    /**
     * Called when a quantile is deleted from the quantile listbox
     */
    @Override
    public void onDelete(String value, int index)
    {
        double quantile = Double.parseDouble(value);
        studyDesignContext.deleteQuantile(this, quantile, index);
        checkComplete();
    }
    
    @Override
    public void onAdd(String value) throws IllegalArgumentException
    {
        try
        {
            double quantile = TextValidation.parseDouble(value, 0, 1, false);
            studyDesignContext.addQuantile(this, quantile);
            changeState(WizardStepPanelState.COMPLETE);
        }
        catch (NumberFormatException nfe)
        {
            throw new IllegalArgumentException(GlimmpseWeb.constants.errorInvalidQuantile());
        }
    }
}
