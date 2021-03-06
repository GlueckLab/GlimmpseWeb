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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;

/**
 * Panel for indicating of a baseline covariate should be included.
 * 
 * @author Sarah Kreidler
 *
 */
public class BaselineCovariatePanel extends WizardStepPanel
{
	// pointer to the study design context
	StudyDesignContext studyDesignContext = (StudyDesignContext) context;
	// covariate checkbox
    protected CheckBox covariateCheckBox = new CheckBox(GlimmpseWeb.constants.covariateCheckBoxLabel());
    
    /**
     * Create the baseline covariate panel
     * @param context wizard context
     */
    public BaselineCovariatePanel(WizardContext context)
    {
    	super(context, GlimmpseWeb.constants.navItemGaussianCovariate(), 
    	        WizardStepPanelState.COMPLETE);
    	// build covariate panel
        VerticalPanel panel = new VerticalPanel();
        
        // create header, description HTML
        HTML header = new HTML(GlimmpseWeb.constants.covariateTitle());
        HTML description = new HTML(GlimmpseWeb.constants.covariateDescription());
        
        // build the checkbox / label to contorl for a covariate
        HorizontalPanel includeCovariatePanel = new HorizontalPanel();
        includeCovariatePanel.add(covariateCheckBox);
        covariateCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                saveToContext();
            }
        });
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(includeCovariatePanel);
        
        // add style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        covariateCheckBox.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
  		
        initWidget(panel);
    }

    /**
     * Clear the covariate checkbox
     */
	@Override
	public void reset()
	{
		covariateCheckBox.setValue(false);
	}
	
    /**
     * Resize the beta matrix when the design matrix dimensions change
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e)
    {
    	// no action needed
    }
    
    /**
     * Load the covariate info from the context
     */
    @Override
    public void onWizardContextLoad()
    {
    	loadFromContext();
    }
    
    /**
     * Load the covariate info from the context
     */
    private void loadFromContext()
    {
    	boolean hasCovariate = studyDesignContext.getStudyDesign().isGaussianCovariate();
    	covariateCheckBox.setValue(hasCovariate);
    }

    /**
     * Save the current value of the checkbox to the context
     */
    private void saveToContext() {
        studyDesignContext.setCovariate(this, covariateCheckBox.getValue());
    }
    
	
}
