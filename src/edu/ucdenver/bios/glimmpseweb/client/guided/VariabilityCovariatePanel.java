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

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.XMLUtilities;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;

/**
 * Guided mode panel for entering the s.d. of the covariate
 * @author Sarah
 *
 */
public class VariabilityCovariatePanel extends WizardStepPanel
implements ChangeHandler
{
	protected TextBox standardDeviationTextBox = new TextBox();
	protected HTML errorHTML = new HTML();

	public VariabilityCovariatePanel(WizardContext context)
	{
		super(context, "Variability due to covariates");
		skip = true;
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(GlimmpseWeb.constants.variabilityCovariateTitle());
        HTML description = new HTML(GlimmpseWeb.constants.variabilityCovariateDescription());
		
        HorizontalPanel standardDeviationContainer = new HorizontalPanel();
        standardDeviationContainer.add(standardDeviationTextBox);
        
        // callback for text box
        standardDeviationTextBox.addChangeHandler(this);
        
        // build the panel
        panel.add(header);
        panel.add(description);
        panel.add(standardDeviationContainer);
        panel.add(errorHTML);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        standardDeviationContainer.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        errorHTML.addStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_OKAY);
        
		initWidget(panel);
	}
	
	@Override
	public void reset()
	{
		skip = true;
	}

//	@Override
//	public void loadFromNode(Node node)
//	{
//		if (GlimmpseConstants.TAG_VARIABILITY_G.equalsIgnoreCase(node.getNodeName()))
//		{
//			Node textNode = node.getFirstChild();
//			if (textNode != null) this.standardDeviationTextBox.setText(textNode.getNodeValue());
//			Window.alert(standardDeviationTextBox.getText());
//		}
//	}
//
//	@Override
//	public void onHasCovariate(boolean hasCovariate)
//	{
//		skip = !hasCovariate;
//	}

	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (!skip && complete)
		{
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_SIGMA_COVARIATE, 1, 1);
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ROW);
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_COLUMN);
			double stddev = Double.parseDouble(standardDeviationTextBox.getText());
			buffer.append(stddev * stddev);
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_COLUMN);
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ROW);
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
		}
		return buffer.toString();
	}

	public String toStudyXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (!skip)
		{
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_VARIABILITY_G);
			buffer.append(standardDeviationTextBox.getValue());
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_VARIABILITY_G);	
		}
		return buffer.toString();
	}
	
	@Override
	public void onChange(ChangeEvent event)
	{
		TextBox source = (TextBox) event.getSource();
		try
		{
			TextValidation.parseDouble(source.getText(), 0, true);
			TextValidation.displayOkay(errorHTML, "");
		}
		catch (NumberFormatException nfe)
		{
			TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidPositiveNumber());
			source.setText("");
		}
		checkComplete();
	}
	
	private void checkComplete()
	{
		String value = standardDeviationTextBox.getText();
		if (value == null || value.isEmpty())
			notifyInProgress();
		else
			notifyComplete();
	}
	
	public void onExit()
	{
		if (complete)
		{
			double stddev = Double.parseDouble(standardDeviationTextBox.getText());
			double variance = stddev * stddev;
		}
	}
}
