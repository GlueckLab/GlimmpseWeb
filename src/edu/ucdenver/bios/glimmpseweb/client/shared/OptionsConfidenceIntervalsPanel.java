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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.XMLUtilities;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;

/**
 * Matrix Mode panel which allows the user to set options for confidence
 * intervals on power results
 */
public class OptionsConfidenceIntervalsPanel extends WizardStepPanel
{
	protected String ciTypeRadioGroup= "confidenceIntervalTypeGroup";
	
	protected CheckBox noCICheckbox = new CheckBox();
	
	protected RadioButton sigmaCIRadioButton;
	protected RadioButton betaSigmaCIRadioButton;
	
	protected TextBox alphaLowerTextBox = new TextBox();
	protected TextBox alphaUpperTextBox = new TextBox();
	protected TextBox sampleSizeTextBox = new TextBox();
	protected TextBox rankTextBox = new TextBox();
	
	protected HTML alphaErrorHTML = new HTML();
	protected HTML estimatesErrorHTML = new HTML();

	private VerticalPanel estimatesPanel = null;
	private VerticalPanel tailProbabilityPanel = null;
	private VerticalPanel typePanel = null;
	
	
	public OptionsConfidenceIntervalsPanel(String mode)
	{
		super("Confidence Intervals");
	
		ciTypeRadioGroup += mode;
		VerticalPanel panel = new VerticalPanel();

		// create header, description
		HTML header = new HTML(GlimmpseWeb.constants.confidenceIntervalOptionsTitle());
		HTML description = new HTML(GlimmpseWeb.constants.confidenceIntervalOptionsDescription());        
		
		panel.add(header);
		panel.add(description);
		
		panel.add(createDisablePanel());
		panel.add(createTypePanel());
		panel.add(createTailProbabilityPanel());
		panel.add(createEstimatesPanel());
		
		// add style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		reset();
		initWidget(panel);
	}
	
	private HorizontalPanel createDisablePanel()
	{
		HorizontalPanel panel = new HorizontalPanel();
		
		// add callbacks
		noCICheckbox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				CheckBox cb = (CheckBox) event.getSource();
				enableConfidenceIntervalOptions(!cb.getValue());
				checkComplete();
			}
		});
		
		panel.add(noCICheckbox);
		panel.add(new HTML(GlimmpseWeb.constants.confidenceIntervalOptionsNone()));
		
		// set style
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
		
		return panel;
	}
	
	private VerticalPanel createTypePanel()
	{
		typePanel = new VerticalPanel();
		
		// create the radio buttons
		sigmaCIRadioButton = new RadioButton(ciTypeRadioGroup, 
				GlimmpseWeb.constants.confidenceIntervalOptionsTypeSigma(), true);
		sigmaCIRadioButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				checkComplete();
			}	
		});
		betaSigmaCIRadioButton = new RadioButton(ciTypeRadioGroup, 
				GlimmpseWeb.constants.confidenceIntervalOptionsTypeBetaSigma(), true);
		betaSigmaCIRadioButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				checkComplete();
			}	
		});
		// layout the panel
		typePanel.add(new HTML(GlimmpseWeb.constants.confidenceIntervalOptionsTypeQuestion()));
		typePanel.add(sigmaCIRadioButton);
		typePanel.add(betaSigmaCIRadioButton);
		
		// set style
		sigmaCIRadioButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
		betaSigmaCIRadioButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
		typePanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
		return typePanel;
	}
	
	
	private VerticalPanel createTailProbabilityPanel()
	{
		tailProbabilityPanel = new VerticalPanel();
		
		Grid grid = new Grid(2,2);
		grid.setWidget(0, 0, new HTML(GlimmpseWeb.constants.confidenceIntervalOptionsAlphaLower()));
		grid.setWidget(0, 1, alphaLowerTextBox);
		grid.setWidget(1, 0, new HTML(GlimmpseWeb.constants.confidenceIntervalOptionsAlphaUpper()));
		grid.setWidget(1, 1, alphaUpperTextBox);
		
		// layout the panel
		tailProbabilityPanel.add(new HTML(GlimmpseWeb.constants.confidenceIntervalOptionsAlphaQuestion()));
		tailProbabilityPanel.add(grid);
		tailProbabilityPanel.add(alphaErrorHTML);
		
		// callbacks for error checking
		alphaLowerTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				try
				{
					TextValidation.parseDouble(alphaLowerTextBox.getText(), 0, 0.5, false);
					TextValidation.displayOkay(alphaErrorHTML, "");
				}
				catch (NumberFormatException nfe)
				{
					TextValidation.displayError(alphaErrorHTML, GlimmpseWeb.constants.errorInvalidTailProbability());
					alphaLowerTextBox.setText("");
				}
				checkComplete();
			}
		});
		alphaUpperTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				try
				{
					TextValidation.parseDouble(alphaUpperTextBox.getText(), 0, 0.5, false);
					TextValidation.displayOkay(alphaErrorHTML, "");
				}
				catch (NumberFormatException nfe)
				{
					TextValidation.displayError(alphaErrorHTML, GlimmpseWeb.constants.errorInvalidTailProbability());
					alphaUpperTextBox.setText("");
				}
				checkComplete();
			}
		});
		
		// add style
		grid.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
		alphaErrorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
		tailProbabilityPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
		
		return tailProbabilityPanel;
	}
	
	private VerticalPanel createEstimatesPanel()
	{
		estimatesPanel = new VerticalPanel();
		
		Grid grid = new Grid(2,2);
		grid.setWidget(0, 0, new HTML(GlimmpseWeb.constants.confidenceIntervalOptionsSampleSize()));
		grid.setWidget(0, 1, sampleSizeTextBox);
		grid.setWidget(1, 0, new HTML(GlimmpseWeb.constants.confidenceIntervalOptionsRank()));
		grid.setWidget(1, 1, rankTextBox);
		
		// call backs for error checking
		sampleSizeTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				try
				{
					// first make sure it's an integer
					int sampleSize = TextValidation.parseInteger(sampleSizeTextBox.getText(),
							0, true);
					if (!rankTextBox.getText().isEmpty())
					{
						int rank = Integer.parseInt(rankTextBox.getText());
						if (sampleSize > rank)
						{
							TextValidation.displayOkay(estimatesErrorHTML, "");
						}
						else
						{
							TextValidation.displayError(estimatesErrorHTML, GlimmpseWeb.constants.errorSampleSizeLessThanRank());
							sampleSizeTextBox.setText("");
						}
					}
					else
					{
						TextValidation.displayOkay(estimatesErrorHTML, "");
					}
				}
				catch (NumberFormatException nfe)
				{
					TextValidation.displayError(estimatesErrorHTML, GlimmpseWeb.constants.errorInvalidSampleSize());
					sampleSizeTextBox.setText("");
				}
				checkComplete();
			}
		});
		rankTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				try
				{
					// first make sure it's an integer
					int rank = TextValidation.parseInteger(rankTextBox.getText(),
							0, true);
					if (!sampleSizeTextBox.getText().isEmpty())
					{
						int sampleSize = Integer.parseInt(sampleSizeTextBox.getText());
						if (sampleSize > rank)
						{
							TextValidation.displayOkay(estimatesErrorHTML, "");
						}
						else
						{
							TextValidation.displayError(estimatesErrorHTML, GlimmpseWeb.constants.errorSampleSizeLessThanRank());
							rankTextBox.setText("");
						}
					}
					else
					{
						TextValidation.displayOkay(estimatesErrorHTML, "");
					}
				}
				catch (NumberFormatException nfe)
				{
					TextValidation.displayError(estimatesErrorHTML, GlimmpseWeb.constants.errorInvalidPositiveNumber());
					rankTextBox.setText("");
				}
				checkComplete();
			}
		});
		// layout the panel
		estimatesPanel.add(new HTML(GlimmpseWeb.constants.confidenceIntervalOptionsEstimatedDataQuestion()));
		estimatesPanel.add(grid);
		estimatesPanel.add(estimatesErrorHTML);
		
		// add style
		grid.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
		estimatesErrorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
		estimatesPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
		
		return estimatesPanel;
	}
	
	private void enableConfidenceIntervalOptions(boolean enabled)
	{
		estimatesPanel.setVisible(enabled);
		typePanel.setVisible(enabled);
		tailProbabilityPanel.setVisible(enabled);
//		sigmaCIRadioButton.setEnabled(enabled);
//		betaSigmaCIRadioButton.setEnabled(enabled);
//		alphaLowerTextBox.setEnabled(enabled);
//		alphaUpperTextBox.setEnabled(enabled);
//		sampleSizeTextBox.setEnabled(enabled);
//		rankTextBox.setEnabled(enabled);
		TextValidation.displayOkay(alphaErrorHTML, "");
		TextValidation.displayOkay(estimatesErrorHTML, "");
	}
	
	@Override
	public void reset()
	{
		noCICheckbox.setValue(true);
		enableConfidenceIntervalOptions(false);
		alphaLowerTextBox.setText("");
		alphaUpperTextBox.setText("");
		sampleSizeTextBox.setText("");
		rankTextBox.setText("");
		TextValidation.displayOkay(alphaErrorHTML, "");
		TextValidation.displayOkay(estimatesErrorHTML, "");
		checkComplete();
	}


	public void loadFromNode(Node node)
	{	
		if (GlimmpseConstants.TAG_CONFIDENCE_INTERVAL.equalsIgnoreCase(node.getNodeName()))
		{
			NamedNodeMap attrs = node.getAttributes();
			// set type of CI
			Node typeNode = attrs.getNamedItem(GlimmpseConstants.ATTR_TYPE);
			if (typeNode != null) 
			{
				if (GlimmpseConstants.CONFIDENCE_INTERVAL_BETA_KNOWN_EST_SIGMA.equals(typeNode.getNodeValue()))
					sigmaCIRadioButton.setValue(true);
				else if (GlimmpseConstants.CONFIDENCE_INTERVAL_EST_BETA_SIGMA.equals(typeNode.getNodeValue()))
					betaSigmaCIRadioButton.setValue(true);
			}
			// set upper/lower tails
			Node upperTailNode = attrs.getNamedItem(GlimmpseConstants.ATTR_CI_ALPHA_UPPER);
			if (upperTailNode != null) alphaUpperTextBox.setValue(upperTailNode.getNodeValue());
			Node lowerTailNode = attrs.getNamedItem(GlimmpseConstants.ATTR_CI_ALPHA_LOWER);
			if (lowerTailNode != null) alphaLowerTextBox.setValue(lowerTailNode.getNodeValue());
			
			// set estimation data set info
			Node sampleSizeNode = attrs.getNamedItem(GlimmpseConstants.ATTR_CI_ESTIMATES_SAMPLE_SIZE);
			if (sampleSizeNode != null) sampleSizeTextBox.setValue(sampleSizeNode.getNodeValue());
			Node rankNode = attrs.getNamedItem(GlimmpseConstants.ATTR_CI_ESTIMATES_RANK);
			if (rankNode != null) rankTextBox.setText(rankNode.getNodeValue());
		}
	}
	
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (complete && !noCICheckbox.getValue())
		{
			StringBuffer attrs = new StringBuffer();
			attrs.append(GlimmpseConstants.ATTR_TYPE);
			attrs.append("='");
			if (sigmaCIRadioButton.getValue())
				attrs.append(GlimmpseConstants.CONFIDENCE_INTERVAL_BETA_KNOWN_EST_SIGMA);
			else if (betaSigmaCIRadioButton.getValue())
				attrs.append(GlimmpseConstants.CONFIDENCE_INTERVAL_EST_BETA_SIGMA);
			attrs.append("' ");
			attrs.append(GlimmpseConstants.ATTR_CI_ALPHA_LOWER);
			attrs.append("='");
			attrs.append(alphaLowerTextBox.getText());
			attrs.append("' ");
			attrs.append(GlimmpseConstants.ATTR_CI_ALPHA_UPPER);
			attrs.append("='");
			attrs.append(alphaUpperTextBox.getText());
			attrs.append("' ");
			attrs.append(GlimmpseConstants.ATTR_CI_ESTIMATES_SAMPLE_SIZE);
			attrs.append("='");
			attrs.append(sampleSizeTextBox.getText());
			attrs.append("' ");
			attrs.append(GlimmpseConstants.ATTR_CI_ESTIMATES_RANK);
			attrs.append("='");
			attrs.append(rankTextBox.getText());
			attrs.append("' ");
						
			XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_CONFIDENCE_INTERVAL,
					attrs.toString());
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_CONFIDENCE_INTERVAL);
		}
		return buffer.toString();
	}
	
	public String toStudyXML()
	{
		if (noCICheckbox.getValue())
		{
			return "";
		}
		else
		{
			return toRequestXML();
		}
	}
	
	private void checkComplete()
	{
		if (noCICheckbox.getValue() || 
				((sigmaCIRadioButton.getValue() || betaSigmaCIRadioButton.getValue()) 
						&& (!alphaLowerTextBox.getText().isEmpty() && 
								!alphaUpperTextBox.getText().isEmpty() &&
								!sampleSizeTextBox.getText().isEmpty() &&
								!rankTextBox.getText().isEmpty())))
		{
			notifyComplete();
		}
		else
		{
			notifyInProgress();
		}
	}

}
