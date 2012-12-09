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

    protected VerticalPanel estimatesPanel = null;
    protected VerticalPanel tailProbabilityPanel = null;
    protected VerticalPanel typePanel = null;

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

    /**
     * Create panel which enables or disables confidence intervals
     * @return enable/disable panel
     */
    private HorizontalPanel createDisablePanel()
    {
        HorizontalPanel panel = new HorizontalPanel();

        // add callbacks
        noCICheckbox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                CheckBox cb = (CheckBox) event.getSource();
                boolean enabled = !cb.getValue();
                showConfidenceIntervalOptions(enabled);
                if (enabled) {
                    addConfidenceIntervalDescription();
                } else {
                    removeConfidenceIntervalDescription();
                }
                checkComplete();
            }
        });

        panel.add(noCICheckbox);
        panel.add(new HTML(GlimmpseWeb.constants.confidenceIntervalOptionsNone()));

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);

        return panel;
    }

    /**
     * Create panel to specify the type of confidence intervals
     * 
     * @return type panel
     */
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
                updateConfidenceIntervalDescription();
                checkComplete();
            }	
        });
        betaSigmaCIRadioButton = new RadioButton(ciTypeRadioGroup, 
                GlimmpseWeb.constants.confidenceIntervalOptionsTypeBetaSigma(), true);
        betaSigmaCIRadioButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                updateConfidenceIntervalDescription();
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

    /**
     * Create panel to enter tail probabilities
     * @return tail probability entry panel
     */
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
                    double alphaLower = 
                            TextValidation.parseDouble(alphaLowerTextBox.getText(), 0, 0.5, true);
                    TextValidation.displayOkay(alphaErrorHTML, "");
                }
                catch (NumberFormatException nfe)
                {
                    TextValidation.displayError(alphaErrorHTML, GlimmpseWeb.constants.errorInvalidTailProbability());
                    alphaLowerTextBox.setText("");
                }
                updateConfidenceIntervalDescription();
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
                updateConfidenceIntervalDescription();
                checkComplete();
            }
        });

        // add style
        grid.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
        alphaErrorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        tailProbabilityPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);

        return tailProbabilityPanel;
    }

    /**
     * Create panel to enter sample size and design matrix rank
     * for data used to estimate beta and sigma
     * @return estimates panel
     */
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
                updateConfidenceIntervalDescription();
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
                updateConfidenceIntervalDescription();
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

    /** 
     * Show or hide the confidence interval options and
     * clear any error messages
     * @param show if true, show the options
     */
    private void showConfidenceIntervalOptions(boolean show) {
        estimatesPanel.setVisible(show);
        typePanel.setVisible(show);
        tailProbabilityPanel.setVisible(show);
        TextValidation.displayOkay(alphaErrorHTML, "");
        TextValidation.displayOkay(estimatesErrorHTML, "");
    }

    /**
     * Add a blank confidence interval description
     * to the study design object
     */
    private void addConfidenceIntervalDescription()
    {
        // update the context
        studyDesignContext.addConfidenceIntervalOptions(this);
        updateConfidenceIntervalDescription();
    }

    /**
     * Remove the confidence interval description from the 
     * study design object
     */
    private void removeConfidenceIntervalDescription()
    {
        // update the context
        studyDesignContext.deleteConfidenceIntervalOptions(this);
    }

    /**
     * Reset the panel
     */
    @Override
    public void reset()
    {
        noCICheckbox.setValue(true);
        showConfidenceIntervalOptions(false);
        // clear the input fields
        betaSigmaCIRadioButton.setValue(false);
        sigmaCIRadioButton.setValue(false);
        alphaLowerTextBox.setText("");
        alphaUpperTextBox.setText("");
        sampleSizeTextBox.setText("");
        rankTextBox.setText("");
        checkComplete();
    }

    /**
     * Populate the screen based on the 
     */
    public void loadFromContext()
    {	
        reset();
        ConfidenceIntervalDescription description = 
                studyDesignContext.getStudyDesign().getConfidenceIntervalDescriptions();
        if (description != null) {
            noCICheckbox.setValue(false);
            showConfidenceIntervalOptions(true);
            if (description.isBetaFixed()) {
                sigmaCIRadioButton.setValue(true);
            } else {
                betaSigmaCIRadioButton.setValue(true);
            }

            // fill in the tail probabilities
            double upperTail = description.getUpperTailProbability();
            if (upperTail >= 0) {
                alphaUpperTextBox.setValue(Double.toString(upperTail));
            }
            double lowerTail = description.getLowerTailProbability();
            if (lowerTail >= 0) {
                alphaLowerTextBox.setValue(Double.toString(lowerTail));
            }

            // fill in the rank and sample size for the pilot data
            int sampleSize = description.getSampleSize();
            if (sampleSize > 0) {
                sampleSizeTextBox.setValue(Integer.toString(description.getSampleSize()));
            }
            int rank = description.getRankOfDesignMatrix();
            if (rank > 0) {
                rankTextBox.setText(Integer.toString(rank));
            }
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
        checkComplete();
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
     * Updates the confidence interval description in the
     * study design to match the screen.
     * Universal handler for any text box changes.
     */
    private void updateConfidenceIntervalDescription() {
        double lowerTailProbability = Double.NaN;
        double upperTailProbability = Double.NaN;
        int rank = -1;
        int sampleSize = -1;
        boolean betaFixed = false;
        boolean sigmaFixed = false;

        if(!alphaLowerTextBox.getValue().isEmpty()) {
            lowerTailProbability = Double.parseDouble(alphaLowerTextBox.getValue());
        }
        if(!alphaUpperTextBox.getValue().isEmpty()) {
            upperTailProbability = Double.parseDouble(alphaUpperTextBox.getValue());
        }
        if(!rankTextBox.getValue().isEmpty()) {
            rank = Integer.parseInt(rankTextBox.getValue());
        }
        if(!sampleSizeTextBox.getValue().isEmpty()) {
            sampleSize = Integer.parseInt(sampleSizeTextBox.getValue());
        }
        if (sigmaCIRadioButton.getValue()) {
            betaFixed = true;
            sigmaFixed = false;
        } else {
            betaFixed = false;
            sigmaFixed = false;
        }
        studyDesignContext.updateConfidenceIntervalOptions(this, 
                betaFixed, sigmaFixed, lowerTailProbability, upperTailProbability,
                sampleSize, rank);

    }

}
