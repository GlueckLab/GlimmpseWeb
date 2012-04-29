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
package edu.ucdenver.bios.glimmpseweb.client.shared;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.enums.SolutionTypeEnum;

/**
 * WizardStepPanel which allows the user to select whether they are solving for
 * power, sample size, or effect size (i.e. beta matrix scale factor)
 * 
 * @author Sarah Kreidler
 *
 */
public class SolvingForPanel extends WizardStepPanel
implements ClickHandler
{
	protected static final String SOLVE_FOR_RADIO_GROUP = "SolvingFor";

	   // context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    
	// "solving for" check boxes
	protected RadioButton solvingForPowerRadioButton; 
	protected RadioButton solvingForSampleSizeRadioButton;
	
	/**
	 * Constructor
	 * 
	 * @param radioGroupPrefix prefix to ensure uniqueness of the radio button group
	 */
	public SolvingForPanel(WizardContext context, String radioGroupPrefix)
	{
		super(context, GlimmpseWeb.constants.solvingForLink(), WizardStepPanelState.INCOMPLETE);
		// since one of the radio buttons will always be checked, this wizardsteppanel
		// is always considered complete (complete member var is from superclass WizardStepPanel)
		
		VerticalPanel panel = new VerticalPanel();
		
		HTML header = new HTML(GlimmpseWeb.constants.solvingForTitle());
		HTML description = new HTML(GlimmpseWeb.constants.solvingForDescription());
		
		// create the radio buttons - note, we add a prefix to the radio group name since multiple
		// instances of this class are created for matrix and guided mode
		String group = radioGroupPrefix + SOLVE_FOR_RADIO_GROUP;
		solvingForPowerRadioButton = 
			new RadioButton(group, GlimmpseWeb.constants.solvingForPowerLabel());
		solvingForSampleSizeRadioButton = 
			new RadioButton(group, GlimmpseWeb.constants.solvingForSampleSizeLabel());
		/*solvingForEffectSizeRadioButton = 
			new RadioButton(group, GlimmpseWeb.constants.solvingForEffectSizeLabel());*/
		
		// layout the radio buttons
		Grid grid = new Grid(2,1);
		grid.setWidget(0, 0, solvingForPowerRadioButton);
		grid.setWidget(1, 0, solvingForSampleSizeRadioButton);
		//grid.setWidget(2, 0, solvingForEffectSizeRadioButton);
		
		// notify the listeners when a radio button is selected
		solvingForPowerRadioButton.addClickHandler(this);
		solvingForSampleSizeRadioButton.addClickHandler(this);
		//solvingForEffectSizeRadioButton.addClickHandler(this);
		
		// layout the panel
		panel.add(header);
		panel.add(description);
		panel.add(grid);
		
		// set style
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		
		// initialize - required by gwt
		initWidget(panel);
	}
	
	/**
	 * Reset the panel to the default (solve for power), and clear the nominal 
	 * power list
	 */
	public void reset()
	{
		solvingForPowerRadioButton.setValue(true);
		solvingForSampleSizeRadioButton.setValue(false);
		changeState(WizardStepPanelState.COMPLETE);
	}
	


	/**
	 * Notify solving for listeners when one of the solution type radio buttons
	 * is clicked.  Also notify the current number of rows in the power list
	 * 
	 * @param event the click event
	 */
	@Override
	public void onClick(ClickEvent event)
	{		
		changeState(WizardStepPanelState.COMPLETE);
		if (solvingForPowerRadioButton.getValue())
		{
			((StudyDesignContext) context).setSolutionType(this, SolutionTypeEnum.POWER);
		}
		else if (solvingForSampleSizeRadioButton.getValue())
		{
			((StudyDesignContext) context).setSolutionType(this, SolutionTypeEnum.SAMPLE_SIZE);
		}
	}
	
    /**
     * Respond to context changes
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e)
    {
        // no action needed
    }
    
    /**
     * Load the solving for info from the context
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
        SolutionTypeEnum solutionType = studyDesignContext.getStudyDesign().getSolutionTypeEnum();
        if (solutionType != null 
                && solutionType == SolutionTypeEnum.SAMPLE_SIZE) {
            solvingForSampleSizeRadioButton.setValue(true);
        } else {
            solvingForPowerRadioButton.setValue(true);
        }
        changeState(WizardStepPanelState.COMPLETE);
    }
	
    
}
