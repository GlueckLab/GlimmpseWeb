/*
 * Web Interface for the GLIMMPSE Software System.  Allows
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
package edu.ucdenver.bios.glimmpseweb.client.matrix;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.FixedRandomMatrix;

/**
 * Matrix Mode panel which allows input of the regression coefficients
 * matrix, B.
 */
public class BetaPanel extends WizardStepPanel
{
	// pointer to the study design context
	StudyDesignContext studyDesignContext = (StudyDesignContext) context;
	
	// matrices holding the fixed and random portions of the 
	// beta matrix
    protected ResizableMatrix betaFixed = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_BETA,
    			GlimmpseConstants.DEFAULT_Q, 
    			GlimmpseConstants.DEFAULT_P, "0", GlimmpseWeb.constants.betaFixedMatrixName()); 
    protected ResizableMatrix betaRandom = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_BETA_RANDOM,
    			1, GlimmpseConstants.DEFAULT_P, 
    			"0", GlimmpseWeb.constants.betaGaussianMatrixName()); 
    
    // boolean indicating that the study design includes a covariate
    boolean hasCovariate;
    
	public BetaPanel(WizardContext context)
	{
		super(context, GlimmpseWeb.constants.stepsLeftBeta());
		complete = true;
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(GlimmpseWeb.constants.betaTitle());
        HTML description = new HTML(GlimmpseWeb.constants.betaDescription());

        // layout the panel
        panel.add(header);
        panel.add(description);
        panel.add(createBetaMatrixPanel());
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
		initWidget(panel);
	}
	
	private VerticalPanel createBetaMatrixPanel()
	{
    	VerticalPanel panel = new VerticalPanel();
    	
        panel.add(betaFixed);
        panel.add(betaRandom);

        // disable the row dimension for beta, since this depends on the design matrix
        betaFixed.setEnabledRowDimension(false);
        betaRandom.setVisible(false);
        // disable both dimensions on the random beta matrix
        betaRandom.setEnabledRowDimension(false);
        betaRandom.setEnabledColumnDimension(false);
    	
//    	panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
//    	panel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        
    	return panel;
	}
		
    public void reset()
    {
    	betaFixed.reset(GlimmpseConstants.DEFAULT_Q, 
    			GlimmpseConstants.DEFAULT_P); 
    }
    
    /**
     * Resize the beta matrix when the design matrix dimensions change
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e)
    {
    	StudyDesignChangeEvent changeEvent = (StudyDesignChangeEvent) e;
    	switch (changeEvent.getType())
    	{
    	case DESIGN_ESSENCE_MATRIX:
//    		StudyDesignNamedMatrix designMatrix = studyDesignContext.getDesignEssence();
//    		betaFixed.setRowDimension(designMatrix.getColumns());
    		break;
    	case COVARIATE:
    		hasCovariate = studyDesignContext.getStudyDesign().isGaussianCovariate();
    		break;
    	}   	
    }
    
    /**
     * Load the beta matrix information from the context
     */
    @Override
    public void onWizardContextLoad()
    {
//    	FixedRandomMatrix beta = studyDesignContext.getBeta();
//    	betaFixed.loadFromNamedMatrix(beta.getFixedMatrix());
//    	betaRandom.loadFromNamedMatrix(beta.getRandomMatrix());
    }

    /**
     * Update the beta matrix in the context as we leave the screen
     */
    @Override
    public void onExit()
    {
    	studyDesignContext.setBeta(this, 
    			new FixedRandomMatrix("beta",
    					betaFixed.toNamedMatrix(),
    					betaRandom.toNamedMatrix(), true));
    }
}
