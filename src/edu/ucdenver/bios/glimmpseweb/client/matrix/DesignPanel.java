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

import java.util.ArrayList;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;

/**
 * Matrix mode panel which allows entry of the design essence matrix
 */
public class DesignPanel extends WizardStepPanel
{    	
	// pointer to the study design context
	StudyDesignContext studyDesignContext = (StudyDesignContext) context;
	
    protected ResizableMatrix essenceFixed = new ResizableMatrix(GlimmpseConstants.MATRIX_DESIGN,
			GlimmpseConstants.DEFAULT_N, 
			GlimmpseConstants.DEFAULT_Q, "0", GlimmpseWeb.constants.matrixCategoricalEffectsLabel(),false);
    
   	boolean hasCovariate = false;

	public DesignPanel(WizardContext context)
	{
		super(context, "Design Essence Matrix", WizardStepPanelState.COMPLETE);
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(GlimmpseWeb.constants.matrixDesignTitle());
        HTML description = new HTML(GlimmpseWeb.constants.matrixDesignDescription());
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(essenceFixed);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        // add style
        initWidget(panel);
	}
    
	public void reset()
	{
		essenceFixed.reset(GlimmpseConstants.DEFAULT_N, GlimmpseConstants.DEFAULT_Q);
	}

	public void validate(String value) throws IllegalArgumentException
	{
    	try
    	{
    		TextValidation.parseInteger(value, 0, Integer.MAX_VALUE);
    	}
    	catch (NumberFormatException nfe)
    	{
    		throw new IllegalArgumentException(GlimmpseWeb.constants.errorInvalidAlpha());
    	}
	}
	
	public void onValidRowCount(int validRowCount)
	{
		if (validRowCount > 0)
			notifyComplete();
		else
			notifyInProgress();
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

    	}   	
    }
    
    /**
     * Load the beta matrix information from the context
     */
    @Override
    public void onWizardContextLoad()
    {
//    	NamedMatrix designEssence = studyDesignContext.getDesignEssence();
//    	essenceFixed.loadFromNamedMatrix(designEssence);
    }

    /**
     * Update the beta matrix in the context as we leave the screen
     */
    @Override
    public void onExit()
    {
    	studyDesignContext.setDesignEssence(this, 
    					essenceFixed.toNamedMatrix());
    }

}
