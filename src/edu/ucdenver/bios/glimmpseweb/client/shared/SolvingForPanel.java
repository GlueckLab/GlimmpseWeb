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
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign.SolutionType;

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
		super(context, GlimmpseWeb.constants.solvingForLink());
		// since one of the radio buttons will always be checked, this wizardsteppanel
		// is always considered complete (complete member var is from superclass WizardStepPanel)
		complete = true;
		
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
		// select power by default
		solvingForPowerRadioButton.setValue(true);
		
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
		notifyComplete();
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
		if (solvingForPowerRadioButton.getValue())
		{
			notifyComplete();
		}
		else if (solvingForSampleSizeRadioButton.getValue())
		{
		}
	}
	

    
    /**
     * Return an XML representation of this panel for saving the study design
     * @return XML of solving for information
     */
    public String toStudyXML()
    {
    	StringBuffer buffer = new StringBuffer();
    	
    	buffer.append("<");
    	buffer.append(GlimmpseConstants.TAG_SOLVING_FOR);
    	buffer.append(" " + GlimmpseConstants.ATTR_TYPE + "='");
    	if (solvingForPowerRadioButton.getValue())
    	{
    		buffer.append(GlimmpseConstants.SOLUTION_TYPE_POWER);
    	}
    	else if (solvingForSampleSizeRadioButton.getValue())
    	{
    		buffer.append(GlimmpseConstants.SOLUTION_TYPE_SAMPLE_SIZE);
    	}
    	else
    	{
    		buffer.append(GlimmpseConstants.SOLUTION_TYPE_EFFECT_SIZE);
    	}
    	buffer.append("'>");
    	
    	buffer.append(toRequestXML());
    	
    	buffer.append("</");
    	buffer.append(GlimmpseConstants.TAG_SOLVING_FOR);
    	buffer.append(">");
    	
    	return buffer.toString();
    }

    /**
     * Return an XML representation of the nominal power list, or null if 
     * solving for power
     * 
     * @return XML representation of the nominal power list
     */
    public String toRequestXML()
    {
    		return "";
    }
    
    /**
     * Update the context with the new solving for information
     */
    public void onExit()
    {
		if (solvingForPowerRadioButton.getValue())
		{
	    	((StudyDesignContext) context).setSolutionType(this, SolutionType.POWER);
		}
		else if (solvingForSampleSizeRadioButton.getValue())
		{
	    	((StudyDesignContext) context).setSolutionType(this, SolutionType.SAMPLE_SIZE);
		}

    }
    
}
