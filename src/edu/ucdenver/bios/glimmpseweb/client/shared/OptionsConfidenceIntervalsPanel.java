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

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.ConfidenceIntervalDescription;

/**
 * Matrix Mode panel which allows the user to set options for confidence
 * intervals on power results
 */
public class OptionsConfidenceIntervalsPanel extends WizardStepPanel
{
    // context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    
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
	
	
	public OptionsConfidenceIntervalsPanel(WizardContext context, String mode)
	{
		super(context, GlimmpseWeb.constants.navItemConfidenceIntervals(),
		        WizardStepPanelState.COMPLETE);
	
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
					TextValidation.parseDouble(alphaLowerTextBox.getText(), 0, 0.5, true);
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
					TextValidation.parseDouble(alphaUpperTextBox.getText(), 0, 0.5, true);
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


	public void loadFromContext()
	{	
	    reset();
	    ConfidenceIntervalDescription description = 
	        studyDesignContext.getStudyDesign().getConfidenceIntervalDescriptions();
	    if (description != null) {
	        enableConfidenceIntervalOptions(true);
	        if (description.isBetaFixed()) {
                sigmaCIRadioButton.setValue(true);
	        } else {
	            betaSigmaCIRadioButton.setValue(true);
	        }
	        
	        // fill in the tail probabilities
	        alphaUpperTextBox.setValue(Double.toString(description.getUpperTailProbability()));
	        alphaLowerTextBox.setValue(Double.toString(description.getLowerTailProbability()));
	        
	        // fill in the rank and sample size for the pilot data
	        sampleSizeTextBox.setValue(Integer.toString(description.getSampleSize()));
	        rankTextBox.setText(Integer.toString(description.getRankOfDesignMatrix()));
	    }

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
        case CONFIDENCE_INTERVAL:
            if (e.getSource() != this) loadFromContext();
            break;
        }       
    }
    
    /**
     * Load the beta matrix information from the context
     */
    @Override
    public void onWizardContextLoad()
    {
        loadFromContext();
    }

	/**
	 * Check if the screen is complete
	 */
	private void checkComplete()
	{
		if (noCICheckbox.getValue() || 
				((sigmaCIRadioButton.getValue() || betaSigmaCIRadioButton.getValue()) 
						&& (!alphaLowerTextBox.getText().isEmpty() && 
								!alphaUpperTextBox.getText().isEmpty() &&
								!sampleSizeTextBox.getText().isEmpty() &&
								!rankTextBox.getText().isEmpty())))
		{
			changeState(WizardStepPanelState.COMPLETE);
		}
		else
		{
			changeState(WizardStepPanelState.INCOMPLETE);
		}
	}
	
	/**
	 * Send the confidence interval to the context
	 */
	@Override
	public void onExit()
	{
	    ConfidenceIntervalDescription ciDescr = null;	    
	    if (!noCICheckbox.getValue() ) {
	        ciDescr =  new ConfidenceIntervalDescription();
	        if(!alphaLowerTextBox.getValue().isEmpty())
	            ciDescr.setLowerTailProbability(Float.parseFloat(alphaLowerTextBox.getValue()));
	        if(!alphaUpperTextBox.getValue().isEmpty())
	            ciDescr.setUpperTailProbability(Float.parseFloat(alphaUpperTextBox.getValue()));
	        if(!rankTextBox.getValue().isEmpty())
	            ciDescr.setRankOfDesignMatrix(Integer.parseInt(rankTextBox.getValue()));
	        if(!sampleSizeTextBox.getValue().isEmpty())
	            ciDescr.setSampleSize(Integer.parseInt(sampleSizeTextBox.getValue()));
	    } 
	    else {
	        // clear the confidence interval description object
	    }
        studyDesignContext.setConfidenceIntervalOptions(this, ciDescr);
	    
	}

}
