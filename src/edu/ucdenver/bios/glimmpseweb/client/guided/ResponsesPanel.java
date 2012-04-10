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


package edu.ucdenver.bios.glimmpseweb.client.guided;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ListEntryPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.ListValidator;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;



public class ResponsesPanel extends WizardStepPanel {

 // context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    
	TextBox textbox = new TextBox();
	
	ListEntryPanel responsesPanel;
	/**
	 * Constructor for the class Response Panel
	 */
	public ResponsesPanel(WizardContext context)
	{
		super(context, "Responses");
		VerticalPanel panel = new VerticalPanel();
		// create header/instruction text
		HTML header = new HTML(GlimmpseWeb.constants.responsesPanelTitle());
		HTML description = new HTML(GlimmpseWeb.constants.responsesPanelDescription());
		HorizontalPanel hp = new HorizontalPanel();
		HTML prefix = new HTML(GlimmpseWeb.constants.responsesPanelPrefix());

		HTML sufix = new HTML(GlimmpseWeb.constants.responsesPanelSufix());
		HTML instructions = new HTML(GlimmpseWeb.constants.responsesPanelInstructions());              
		responsesPanel = 
			new ListEntryPanel(GlimmpseWeb.constants.responsesTableColumn(), 
					new ListValidator() {
				public void validate(String value) throws IllegalArgumentException {}

				public void onValidRowCount(int validRowCount)
				{
					if (validRowCount > 0)
						checkComplete();
					else
						changeState(WizardStepPanelState.INCOMPLETE);
				}
			});

		//layout horizontal panel
		hp.setWidth("56%");
		hp.add(prefix);
		hp.add(textbox);
		hp.add(sufix);

		// layout the overall panel
		panel.add(header);
		panel.add(description);
		panel.add(hp);
		panel.add(instructions);
		panel.add(responsesPanel);

		// Set Styles
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		//hp.setStyleName(GlimmpseConstants.STYLE_WIZARD_RESPONSES_PANEL_HORIZONTAL_PANEL);
		prefix.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		sufix.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		instructions.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

		//Initializing Panel
		initWidget(panel);
	}
	/**
	 * This method is used to check if the text box is filled or not
	 */
	public void checkComplete()
	{
		String checkText = textbox.getText();
		if (checkText != null)
		{
			if(checkText.isEmpty() == false)
			{
				changeState(WizardStepPanelState.COMPLETE);
			}
		}
		else
		{
			changeState(WizardStepPanelState.INCOMPLETE);
		}
	}

	/**
	 * It is a method from the extended class Wizard 
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub	
	}
	
	public void onExit()
	{
	    studyDesignContext.setResponseVariables(this,
	            textbox.getValue(), responsesPanel.getValues());
	}
}
