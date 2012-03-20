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
import edu.ucdenver.bios.glimmpseweb.client.shared.ResizableMatrixPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;

/**
 * Matrix mode panel for entering the null hypothesis matrix (theta null)
 * @author Sarah Kreidler
 *
 */
public class ThetaNullPanel extends WizardStepPanel
{   
	// pointer to the study design context
	StudyDesignContext studyDesignContext = (StudyDesignContext) context;
	
    protected ResizableMatrixPanel thetaNull = 
    	new ResizableMatrixPanel(GlimmpseConstants.DEFAULT_A, 
    			GlimmpseConstants.DEFAULT_B); 
    
	public ThetaNullPanel(WizardContext context)
	{
		super(context, GlimmpseWeb.constants.navItemThetaNullMatrix(), 
				WizardStepPanelState.COMPLETE);
		// regardless of user input, this panel allows forward navigation
		
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(GlimmpseWeb.constants.thetaNullTitle());
        HTML description = new HTML(GlimmpseWeb.constants.thetaNullDescription());

        // disabled resizing to ensure matrix conformance
        thetaNull.setEnabledColumnDimension(false);
        thetaNull.setEnabledRowDimension(false);
        
        panel.add(header);
        panel.add(description);
        panel.add(thetaNull);
        
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
		initWidget(panel);
	}  
	
	public void reset()
	{
		thetaNull.reset(GlimmpseConstants.DEFAULT_A, 
    			GlimmpseConstants.DEFAULT_B);
	}

	/**
	 * Update the theta null matrix in the context as we leave the
	 * panel
	 */
	@Override
	public void onExit()
	{
    	studyDesignContext.setThetaNull(this, thetaNull.toNamedMatrix(GlimmpseConstants.MATRIX_THETA));
	}
	
	/**
	 * Respond to changes in the between and within participant 
	 * contrasts to maintain conformance with theta null.
	 */
	@Override
	public void onWizardContextChange(WizardContextChangeEvent e)
	{
    	StudyDesignChangeEvent changeEvent = (StudyDesignChangeEvent) e;
    	switch (changeEvent.getType())
    	{
    	case WITHIN_CONTRAST_MATRIX:
    		int withinContrastColumns = 
    			studyDesignContext.getStudyDesign().getNamedMatrix(GlimmpseConstants.MATRIX_WITHIN_CONTRAST).getColumns();
    		thetaNull.setColumnDimension(withinContrastColumns);
    		break;
    	case BETWEEN_CONTRAST_MATRIX:
    		break;
    	}
	}

	@Override
	public void onWizardContextLoad()
	{
    	NamedMatrix contextThetaNull = 
    		studyDesignContext.getStudyDesign().getNamedMatrix(GlimmpseConstants.MATRIX_THETA);
    	thetaNull.loadFromNamedMatrix(contextThetaNull);
	}
	
}
