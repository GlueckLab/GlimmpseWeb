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


import java.util.ArrayList;
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
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;

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
implements ClickHandler
{
	// check boxes for power methods (only used when a baseline covariate is specified)
	protected CheckBox unconditionalPowerCheckBox = new CheckBox();
	protected CheckBox quantilePowerCheckBox = new CheckBox(){
		public void onChange(){
			
		}
	};
	protected int numQuantiles = 0;
	
	// dynamic list of quantile values
    protected ListEntryPanel quantileListPanel = 
    	new ListEntryPanel(GlimmpseWeb.constants.quantilesTableColumn(), new ListValidator() {
			@Override
			public void onValidRowCount(int validRowCount)
			{
				numQuantiles = validRowCount;
				checkComplete();
			}
			@Override
			public void validate(String value)
					throws IllegalArgumentException
			{
		    	try
		    	{
		    		TextValidation.parseDouble(value, 0, 1, false);
		    	}
		    	catch (NumberFormatException nfe)
		    	{
		    		throw new IllegalArgumentException(GlimmpseWeb.constants.errorInvalidQuantile());
		    	}
			}
    	});

    /**
     * Constructor
     * @param mode mode identifier (needed for unique widget identifiers)
     */
    public OptionsPowerMethodsPanel(WizardContext context, String mode)
	{
		super(context, "Power Method", WizardStepPanelState.SKIPPED);
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
		quantilePowerCheckBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				quantileListPanel.setVisible(quantilePowerCheckBox.getValue());
			}
		});
		quantileListPanel.setVisible(false);
		
		// add callback to check if screen is complete
		unconditionalPowerCheckBox.addClickHandler(this);
		quantilePowerCheckBox.addClickHandler(this);

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
		numQuantiles = 0;
		quantileListPanel.reset();
		changeState(WizardStepPanelState.SKIPPED);
		checkComplete();
	}

	/**
	 * Create an XML representation of the list of selected power methods
	 * 
	 * @return XML representation of the power methods
	 */
	public String powerMethodListToXML()
	{
		StringBuffer buffer = new StringBuffer();

		buffer.append("<");
		buffer.append(GlimmpseConstants.TAG_POWER_METHOD_LIST);
		buffer.append(">");
		if (WizardStepPanelState.SKIPPED == state)
		{
			buffer.append("<v>");
			buffer.append(GlimmpseConstants.POWER_METHOD_CONDITIONAL);
			buffer.append("</v>");
		}
		else
		{
			if (unconditionalPowerCheckBox.getValue())
			{
				buffer.append("<v>");
				buffer.append(GlimmpseConstants.POWER_METHOD_UNCONDITIONAL);
				buffer.append("</v>");
			}
			if (quantilePowerCheckBox.getValue())
			{
				buffer.append("<v>");
				buffer.append(GlimmpseConstants.POWER_METHOD_QUANTILE);
				buffer.append("</v>");
			}
		}
		buffer.append("</");
		buffer.append(GlimmpseConstants.TAG_POWER_METHOD_LIST);
		buffer.append(">");
		return buffer.toString();
	}


	/**
	 * Create an XML representation of the panel to be saved with
	 * the study design
	 * 
	 * @return study XML
	 */
	public String toStudyXML()
	{
		return toRequestXML();
	}
	
	/**
	 * Create an XML representation of the panel for sending to the
	 * Power web service
	 * 
	 * @return
	 */
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append(powerMethodListToXML());
		buffer.append(quantileListPanel.toXML(GlimmpseConstants.TAG_QUANTILE_LIST));
		return buffer.toString();
	}
	
	/**
	 * Click handler for all checkboxes on the Options screen.
	 * Determines if the current selections represent a complete
	 * set of options.
	 */
	@Override
	public void onClick(ClickEvent event)
	{
		checkComplete();			
	}
	
	/**
	 * Check if the user has selected a complete set of options, and
	 * if so notify that forward navigation is allowed
	 */
	private void checkComplete()
	{
		// check if continue is allowed
		// must have at least one test checked, at least one power method
		if (unconditionalPowerCheckBox.getValue() || 
				quantilePowerCheckBox.getValue())
		{
			if (!quantilePowerCheckBox.getValue() || numQuantiles > 0)
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


    

    /**
     * Notify power method and quantile listeners of any changes
     * as we leave this screen
     */
    @Override
    public void onExit()
    {
    	ArrayList<String> powerMethods = new ArrayList<String>();
    	
    	if (unconditionalPowerCheckBox.getValue())
    		powerMethods.add(GlimmpseConstants.POWER_METHOD_UNCONDITIONAL);
    	if (quantilePowerCheckBox.getValue())
    	{
    		powerMethods.add(GlimmpseConstants.POWER_METHOD_QUANTILE);
    		List<String> values = quantileListPanel.getValues();
    	}
    }
}
