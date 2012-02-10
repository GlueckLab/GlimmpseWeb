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
 * Matrix mode panel which allows input of the between-subject contrast matrix (C)
 */
public class BetweenSubjectContrastPanel extends WizardStepPanel
{
	// pointer to the study design context
	StudyDesignContext studyDesignContext = (StudyDesignContext) context;
	
    protected ResizableMatrix betweenSubjectFixed = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_BETWEEN_CONTRAST,
    			GlimmpseConstants.DEFAULT_A, 
    			GlimmpseConstants.DEFAULT_Q, "0", GlimmpseWeb.constants.betweenSubjectContrastMatrixName()); 
    protected boolean hasCovariate = false;
    
	public BetweenSubjectContrastPanel(WizardContext context)
	{
		super(context, "C Contrast");
		complete = true;
		VerticalPanel panel = new VerticalPanel();
		betweenSubjectFixed.setMaxRows(GlimmpseConstants.DEFAULT_A);
        // create header/instruction text
        HTML header = new HTML(GlimmpseWeb.constants.betweenSubjectContrastTitle());
        HTML description = new HTML(GlimmpseWeb.constants.betweenSubjectContrastDescription());

        // layout the panel
        panel.add(header);
        panel.add(description);
        panel.add(betweenSubjectFixed);

        // only allow resize of the row dimension of the fixed matrix since this depends on beta
        betweenSubjectFixed.setEnabledColumnDimension(false);

        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

		initWidget(panel);
	}
    
	@Override
	public void reset()
	{
		betweenSubjectFixed.reset(GlimmpseConstants.DEFAULT_A, 
    			GlimmpseConstants.DEFAULT_Q);
	}

	/**
	 * Resize the between participant contrast to conform with
	 * the design essence matrix and the beta matrix
	 */
	@Override
	public void onWizardContextChange(WizardContextChangeEvent e)
	{
    	StudyDesignChangeEvent changeEvent = (StudyDesignChangeEvent) e;
    	switch (changeEvent.getType())
    	{
    	case DESIGN_ESSENCE_MATRIX:
//    		int designRows = studyDesignContext.getDesignEssence().getRows();
//			betweenSubjectFixed.setMaxRows(designRows - 1);
//			if (betweenSubjectFixed.getRowDimension() > designRows - 1)
//			{
//				betweenSubjectFixed.setRowDimension(designRows - 1);
//			}
    		break;
    	case BETA_MATRIX:
//    		int betaRows = studyDesignContext.getBeta().getFixedMatrix().getRows();
//			betweenSubjectFixed.setColumnDimension(betaRows);
    		break;
    	case COVARIATE:
    		hasCovariate = studyDesignContext.getStudyDesign().isGaussianCovariate();
    		break;
    	}
	}

	/**
	 * Load the between subject contrast from the wizard context
	 */
	@Override
	public void onWizardContextLoad()
	{
//    	FixedRandomMatrix betweenContrast = studyDesignContext.getBetweenParticipantContrast();
//    	betweenSubjectFixed.loadFromNamedMatrix(betweenContrast.getFixedMatrix());
	}

	/**
	 * update the between participant contrast in the context
	 * as we leave the panel
	 */
	@Override
	public void onExit()
	{
    	studyDesignContext.setBeta(this, 
    			new FixedRandomMatrix("betweenSubjectContrast",
    					betweenSubjectFixed.toNamedMatrix(),
    					null, false));
    	// TODO: create  matrix of zeros for random portion
	}
	
}
