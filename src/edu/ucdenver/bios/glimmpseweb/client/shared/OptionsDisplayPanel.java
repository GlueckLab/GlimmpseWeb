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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextListener;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.BetaScale;
import edu.ucdenver.bios.webservice.common.domain.PowerCurveDataSeries;
import edu.ucdenver.bios.webservice.common.domain.PowerCurveDescription;
import edu.ucdenver.bios.webservice.common.domain.PowerMethod;
import edu.ucdenver.bios.webservice.common.domain.Quantile;
import edu.ucdenver.bios.webservice.common.domain.SampleSize;
import edu.ucdenver.bios.webservice.common.domain.SigmaScale;
import edu.ucdenver.bios.webservice.common.domain.StatisticalTest;
import edu.ucdenver.bios.webservice.common.domain.TypeIError;
import edu.ucdenver.bios.webservice.common.enums.HorizontalAxisLabelEnum;
import edu.ucdenver.bios.webservice.common.enums.PowerMethodEnum;
import edu.ucdenver.bios.webservice.common.enums.SolutionTypeEnum;
import edu.ucdenver.bios.webservice.common.enums.StatisticalTestTypeEnum;

/**
 * Panel which allows user to select display options for their power/sample size
 * calculations. Note that two instances of this class are created (one for
 * matrix mode, one for guided mode) so any radio groups or other unique
 * identifiers must have a mode-specific prefix
 * 
 * @author Sarah Kreidler
 * 
 */
public class OptionsDisplayPanel extends WizardStepPanel implements
ClickHandler, WizardContextListener {
    private static final int TOTAL_N_INDEX = 0;
    private static final int BETA_SCALE_INDEX = 1;
    private static final int SIGMA_SCALE_INDEX = 2;

    // context object
    protected StudyDesignContext studyDesignContext;

    protected boolean hasCovariate;
    protected boolean solvingForPower;
    
    // Begin Change : Added a flag for Confidence Interval Description
    protected boolean hasConfidenceIntervalDescription;
    // End Change : Added a flag for Confidence Interval Description

    // mutliplier to get total sample size from relative sizes
    protected int totalSampleSizeMultiplier = 1;
    protected ArrayList<Integer> perGroupNList = new ArrayList<Integer>();

    // skip curve button
    protected CheckBox disableCheckbox = new CheckBox();

    // options for x-axis
    protected ListBox xAxisListBox = new ListBox();

    // select boxes for items that must be fixed for the curve
    protected ListBox totalNListBox = new ListBox();
    protected ListBox nominalPowerListBox = new ListBox();
    protected ListBox betaScaleListBox = new ListBox();
    protected ListBox sigmaScaleListBox = new ListBox();
    protected ListBox testListBox = new ListBox();
    protected ListBox alphaListBox = new ListBox();
    protected ListBox powerMethodListBox = new ListBox();
    protected ListBox quantileListBox = new ListBox();
    protected TextBox dataSeriesLabelTextBox = new TextBox();
    // Begin Change : Added a new checkbox option
    protected CheckBox confidenceLimitsCheckBox = new CheckBox();
    // End Change : Added a new checkbox option

    // labels for each listbox
    protected HTML nominalPowerHTML = new HTML(
            GlimmpseWeb.constants.curveOptionsPowerLabel());
    protected HTML totalNHTML = new HTML(
            GlimmpseWeb.constants.curveOptionsSampleSizeLabel());
    protected HTML betaScaleHTML = new HTML(
            GlimmpseWeb.constants.curveOptionsBetaScaleLabel());
    protected HTML sigmaScaleHTML = new HTML(
            GlimmpseWeb.constants.curveOptionsSigmaScaleLabel());
    protected HTML testHTML = new HTML(
            GlimmpseWeb.constants.curveOptionsTestLabel());
    protected HTML alphaHTML = new HTML(
            GlimmpseWeb.constants.curveOptionsAlphaLabel());
    protected HTML powerMethodHTML = new HTML(
            GlimmpseWeb.constants.curveOptionsPowerMethodLabel());
    protected HTML quantileHTML = new HTML(
            GlimmpseWeb.constants.curveOptionsQuantileLabel());
    protected HTML dataSeriesLabelHTML = new HTML("Data Series Label");
    // Begin Change : Added a new label for checkbox option
    protected HTML confidenceLimitsLabelHTML = new HTML(
            GlimmpseWeb.constants.curveOptionsConfidenceLimitsLabel());
    // End Change : Added a new label for checkbox option

    // listbox showing currently entered data series
    protected ListBox dataSeriesTable = new ListBox(true);

    // panels for x-axis type selection and data series input
    private VerticalPanel xAxisPanel = new VerticalPanel();
    private VerticalPanel dataSeriesPanel = new VerticalPanel();

    // list of data series
    protected ArrayList<PowerCurveDataSeries> dataSeriesList = new ArrayList<PowerCurveDataSeries>();

    /**
     * Constructor
     * 
     * @param mode
     *            mode identifier (needed for unique widget identifiers)
     */
    public OptionsDisplayPanel(WizardContext context, String radioGroupSuffix) {
        super(context, GlimmpseWeb.constants.navItemPowerCurve(),
                WizardStepPanelState.NOT_ALLOWED);
        studyDesignContext = (StudyDesignContext) context;

        VerticalPanel panel = new VerticalPanel();

        // create header, description
        HTML header = new HTML(GlimmpseWeb.constants.curveOptionsTitle());
        HTML description = new HTML(
                GlimmpseWeb.constants.curveOptionsDescription());

        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(createDisablePanel());
        panel.add(createXAxisPanel());
        panel.add(createDataSeriesPanel());

        // set defaults
        reset();

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description
        .setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        // initialize
        initWidget(panel);
    }

    /**
     * Enable or disable the curve options
     * 
     * @param enabled
     */
    private void enableOptions(boolean enabled) {
        xAxisPanel.setVisible(enabled);
        dataSeriesPanel.setVisible(enabled);
    }

    /**
     * Create panel with checkbox to enable/disable power curves
     * 
     * @return HorizontalPanel containing the checkbox
     */
    private HorizontalPanel createDisablePanel() {
        HorizontalPanel panel = new HorizontalPanel();

        // add callbacks
        disableCheckbox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CheckBox cb = (CheckBox) event.getSource();
                enableOptions(!cb.getValue());
            }
        });
        disableCheckbox.setValue(true);
        panel.add(disableCheckbox);
        panel.add(new HTML(GlimmpseWeb.constants.curveOptionsNone()));

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);

        return panel;
    }

    /**
     * Create the panel containing the x-axis type selection dropdown
     * 
     * @return VerticalPanel
     */
    private VerticalPanel createXAxisPanel() {
        // create the listbox for the x-axis values
        xAxisListBox.addItem(GlimmpseWeb.constants
                .curveOptionsSampleSizeLabel());
        xAxisListBox
        .addItem(GlimmpseWeb.constants.curveOptionsBetaScaleLabel());
        xAxisListBox.addItem(GlimmpseWeb.constants
                .curveOptionsSigmaScaleLabel());

        // add callback
        xAxisListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                dataSeriesList.clear();
                dataSeriesTable.clear();
                updateDataSeriesOptions();
            }
        });

        // layout the panel
        HTML xAxixDescription = new HTML(
                GlimmpseWeb.constants.curveOptionsXAxisLabel());
        xAxisPanel.add(xAxixDescription);
        xAxisPanel.add(xAxisListBox);

        // set style
        // xAxixDescription.addStyleDependentName("subText");
        xAxisPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
        xAxisListBox
        .setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);

        return xAxisPanel;
    }

    private VerticalPanel createDataSeriesPanel() {
        VerticalPanel mainPanel = new VerticalPanel();

        Grid grid = new Grid(10, 2);
        // add drop down lists for remaining values that need to be fixed
        grid.setWidget(0, 1, totalNListBox);
        grid.setWidget(1, 1, betaScaleListBox);
        grid.setWidget(2, 1, sigmaScaleListBox);
        grid.setWidget(3, 1, testListBox);
        grid.setWidget(4, 1, alphaListBox);
        grid.setWidget(5, 1, powerMethodListBox);
        grid.setWidget(6, 1, quantileListBox);
        grid.setWidget(7, 1, dataSeriesLabelTextBox);
        grid.setWidget(8, 1, confidenceLimitsCheckBox);

        grid.setWidget(0, 0, totalNHTML);
        grid.setWidget(1, 0, betaScaleHTML);
        grid.setWidget(2, 0, sigmaScaleHTML);
        grid.setWidget(3, 0, testHTML);
        grid.setWidget(4, 0, alphaHTML);
        grid.setWidget(5, 0, powerMethodHTML);
        grid.setWidget(6, 0, quantileHTML);
        grid.setWidget(7, 0, dataSeriesLabelHTML);
        grid.setWidget(8, 0, confidenceLimitsLabelHTML);

        Button addSeriesButton = new Button(GlimmpseWeb.constants.buttonAdd(),
                new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addSeries();
            }
        });
        Button removeSeriesButton = new Button(
                GlimmpseWeb.constants.buttonDelete(), new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        // TODO Auto-generated method stub
                        removeSeries();
                    }
                });

        HorizontalPanel addRemovePanel = new HorizontalPanel();
        addRemovePanel.add(addSeriesButton);
        addRemovePanel.add(removeSeriesButton);
        grid.setWidget(9, 0, addRemovePanel);

        mainPanel.add(grid);
        mainPanel.add(dataSeriesTable);

        // layout the panel
        dataSeriesPanel.add(new HTML(GlimmpseWeb.constants
                .curveOptionsDataSeriesLabel()));
        dataSeriesPanel.add(mainPanel);
        dataSeriesTable.setVisibleItemCount(5);

        // set style
        totalNListBox
        .setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        betaScaleListBox
        .setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        sigmaScaleListBox
        .setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        testListBox.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        alphaListBox.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        powerMethodListBox
        .setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        quantileListBox
        .setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        dataSeriesLabelTextBox
        .setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        confidenceLimitsCheckBox
        .setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_CHECK_BOX);

        addSeriesButton
        .setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
        removeSeriesButton
        .setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
        mainPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_PANEL);
        grid.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
        dataSeriesPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);

        return dataSeriesPanel;
    }

    /**
     * Clear the options panel
     */
    public void reset() {
        dataSeriesList.clear();
        // set the display to remove power options
        disableCheckbox.setValue(true);
        // reset confidence limits checkbox
        confidenceLimitsCheckBox.setValue(false);
        enableOptions(false);

        // clear the list boxes
        totalNListBox.clear();
        betaScaleListBox.clear();
        sigmaScaleListBox.clear();
        testListBox.clear();
        alphaListBox.clear();
        powerMethodListBox.clear();
        quantileListBox.clear();

        // reset the flags
        hasCovariate = false;
        solvingForPower = true;
        updateDataSeriesOptions();

        // set defaults
        xAxisListBox.setSelectedIndex(TOTAL_N_INDEX);
        changeState(WizardStepPanelState.NOT_ALLOWED);
    }

    /**
     * Add a data series from the current list box selections
     */
    private void addSeries() {
        PowerCurveDataSeries series = new PowerCurveDataSeries();
        int index = -1;
        series.setLabel(dataSeriesLabelTextBox.getText());
        if (confidenceLimitsCheckBox.getValue()) {
            series.setConfidenceLimits(true);
        }
        if (hasCovariate) {
            index = powerMethodListBox.getSelectedIndex();
            if (index >= 0) {
                series.setPowerMethod(powerMethodStringToEnum(powerMethodListBox
                        .getItemText(powerMethodListBox.getSelectedIndex())));
            }
            index = quantileListBox.getSelectedIndex();
            if (index >= 0) {
                series.setQuantile(Double.parseDouble(quantileListBox
                        .getItemText(quantileListBox.getSelectedIndex())));
            }
        }

        if (xAxisListBox.getSelectedIndex() == TOTAL_N_INDEX) {
            index = betaScaleListBox.getSelectedIndex();
            if (index >= 0) {
                series.setBetaScale(Double.parseDouble(betaScaleListBox
                        .getItemText(betaScaleListBox.getSelectedIndex())));
            }
            index = sigmaScaleListBox.getSelectedIndex();
            if (index >= 0) {
                series.setSigmaScale(Double.parseDouble(sigmaScaleListBox
                        .getItemText(sigmaScaleListBox.getSelectedIndex())));
            }

        } else if (xAxisListBox.getSelectedIndex() == BETA_SCALE_INDEX) {
            index = totalNListBox.getSelectedIndex();
            if (index >= 0) {
                series.setSampleSize(Integer.parseInt(totalNListBox
                        .getItemText(totalNListBox.getSelectedIndex())));
            }
            index = sigmaScaleListBox.getSelectedIndex();
            if (index >= 0) {
                series.setSigmaScale(Double.parseDouble(sigmaScaleListBox
                        .getItemText(sigmaScaleListBox.getSelectedIndex())));
            }

        } else {
            // sigma scale selected
            index = totalNListBox.getSelectedIndex();
            if (index >= 0) {
                series.setSampleSize(Integer.parseInt(totalNListBox
                        .getItemText(totalNListBox.getSelectedIndex())));
            }
            index = betaScaleListBox.getSelectedIndex();
            if (index >= 0) {
                series.setBetaScale(Double.parseDouble(betaScaleListBox
                        .getItemText(betaScaleListBox.getSelectedIndex())));
            }
        }

        // add alpha
        index = alphaListBox.getSelectedIndex();
        if (index >= 0) {
            series.setTypeIError(Double.parseDouble(alphaListBox
                    .getItemText(alphaListBox.getSelectedIndex())));
        }
        // add test
        index = testListBox.getSelectedIndex();
        if (index >= 0) {
            series.setStatisticalTestTypeEnum(statisticalTestStringToEnum(testListBox
                    .getItemText(testListBox.getSelectedIndex())));
        }

        dataSeriesList.add(series);
        dataSeriesTable.addItem(dataSeriesAsString(series));
    }

    /**
     * Remove the currently selected data series
     */
    private void removeSeries() {
        int index = dataSeriesTable.getSelectedIndex();
        if (index >= 0 && index < dataSeriesList.size()) {
            dataSeriesList.remove(index);
            dataSeriesTable.removeItem(index);
        }
    }

    /**
     * Format data series into pretty string
     * 
     * @param series
     * @return
     */
    private String dataSeriesAsString(PowerCurveDataSeries series) {
        StringBuffer display = new StringBuffer();
        display.append(series.getLabel() + ":");
        if (series.getSampleSize() > 0) {
            display.append(" Sample Size=" + series.getSampleSize());
        }
        if (series.getNominalPower() > 0) {
            display.append(" Nominal Power=" + series.getNominalPower());
        }
        if (series.getStatisticalTestTypeEnum() != null) {
            display.append(" Test="
                    + statisticalTestToString(series
                            .getStatisticalTestTypeEnum()));
        }
        if (series.getBetaScale() != 0) {
            display.append(" Regr. Scale=" + series.getBetaScale());
        }
        if (series.getSigmaScale() != 0) {
            display.append(" Var. Scale=" + series.getSigmaScale());
        }
        if (series.getTypeIError() > 0) {
            display.append(" Alpha=" + series.getTypeIError());
        }
        if (series.getPowerMethod() != null) {
            display.append(" Method="
                    + powerMethodToString(series.getPowerMethod()));
        }
        if (series.getQuantile() > 0) {
            display.append(" Quantile=" + series.getQuantile());
        }
        return display.toString();
    }

    /**
     * Get the test enum for this string
     * 
     * @param testStr
     * @return
     */
    public static StatisticalTestTypeEnum statisticalTestStringToEnum(
            String testStr) {
        if (testStr.equals(GlimmpseWeb.constants
                .testHotellingLawleyTraceLabel())) {
            return StatisticalTestTypeEnum.HLT;
        } else if (testStr.equals(GlimmpseWeb.constants
                .testPillaiBartlettTraceLabel())) {
            return StatisticalTestTypeEnum.PBT;
        } else if (testStr.equals(GlimmpseWeb.constants.testWilksLambdaLabel())) {
            return StatisticalTestTypeEnum.WL;
        } else if (testStr.equals(GlimmpseWeb.constants.testUnirepBoxLabel())) {
            return StatisticalTestTypeEnum.UNIREPBOX;
        } else if (testStr.equals(GlimmpseWeb.constants
                .testUnirepGeisserGreenhouseLabel())) {
            return StatisticalTestTypeEnum.UNIREPGG;
        } else if (testStr.equals(GlimmpseWeb.constants
                .testUnirepHuynhFeldtLabel())) {
            return StatisticalTestTypeEnum.UNIREPHF;
        } else if (testStr.equals(GlimmpseWeb.constants.testUnirepLabel())) {
            return StatisticalTestTypeEnum.UNIREP;
        }
        return null;
    }

    /**
     * Get the test string for this StatisticalTest object
     * 
     * @param testStr
     * @return
     */
    public static String statisticalTestToString(StatisticalTestTypeEnum test) {
        switch (test) {
        case HLT:
            return GlimmpseWeb.constants.testHotellingLawleyTraceLabel();
        case PBT:
            return GlimmpseWeb.constants.testPillaiBartlettTraceLabel();
        case WL:
            return GlimmpseWeb.constants.testWilksLambdaLabel();
        case UNIREPBOX:
            return GlimmpseWeb.constants.testUnirepBoxLabel();
        case UNIREPGG:
            return GlimmpseWeb.constants.testUnirepGeisserGreenhouseLabel();
        case UNIREPHF:
            return GlimmpseWeb.constants.testUnirepHuynhFeldtLabel();
        case UNIREP:
            return GlimmpseWeb.constants.testUnirepLabel();
        }

        return null;
    }

    /**
     * Get the power method enum for this string
     * 
     * @param testStr
     * @return
     */
    public static PowerMethodEnum powerMethodStringToEnum(String powerMethodStr) {
        if (powerMethodStr.equals(GlimmpseWeb.constants
                .powerMethodConditionalLabel())) {
            return PowerMethodEnum.CONDITIONAL;
        } else if (powerMethodStr.equals(GlimmpseWeb.constants
                .powerMethodUnconditionalLabel())) {
            return PowerMethodEnum.UNCONDITIONAL;
        } else if (powerMethodStr.equals(GlimmpseWeb.constants
                .powerMethodQuantileLabel())) {
            return PowerMethodEnum.QUANTILE;
        }
        return null;
    }

    /**
     * Get the test string for this StatisticalTest object
     * 
     * @param testStr
     * @return
     */
    public static String powerMethodToString(PowerMethodEnum powerMethod) {
        switch (powerMethod) {
        case CONDITIONAL:
            return GlimmpseWeb.constants.powerMethodConditionalLabel();
        case UNCONDITIONAL:
            return GlimmpseWeb.constants.powerMethodUnconditionalLabel();
        case QUANTILE:
            return GlimmpseWeb.constants.powerMethodQuantileLabel();
        }

        return null;
    }

    /**
     * Click handler for all checkboxes on the Options screen. Determines if the
     * current selections represent a complete set of options.
     */
    @Override
    public void onClick(ClickEvent event) {
        checkComplete();
    }

    /**
     * Check if the user has selected a complete set of options, and if so
     * notify that forward navigation is allowed
     */
    private void checkComplete() {
        if (disableCheckbox.getValue()) {
            changeState(WizardStepPanelState.COMPLETE);
        } else {
            if ((totalNListBox.isVisible() && totalNListBox.getItemCount() <= 0) ||
                    (nominalPowerListBox.isVisible() && nominalPowerListBox.getItemCount() <= 0) ||
                    (betaScaleListBox.isVisible() && betaScaleListBox.getItemCount() <= 0) ||
                    (sigmaScaleListBox.isVisible() && sigmaScaleListBox.getItemCount() <= 0) ||
                    (testListBox.isVisible() && testListBox.getItemCount() <= 0) ||
                    (alphaListBox.isVisible() && alphaListBox.getItemCount() <= 0) ||
                    (powerMethodListBox.isVisible() && powerMethodListBox.getItemCount() <= 0) ||
                    (quantileListBox.isVisible() && quantileListBox.getItemCount() <= 0)) {
                // if any visible dropdown lists are not filled, then the user
                // can't enter this screen
                changeState(WizardStepPanelState.NOT_ALLOWED);
            } else {
                if (dataSeriesList.size() > 0) {
                    changeState(WizardStepPanelState.COMPLETE);
                } else {
                    changeState(WizardStepPanelState.INCOMPLETE);
                }
            }
        }
    }

    /**
     * Notify options listeners of selected options as we exit the options
     * screen
     */
    @Override
    public void onExit() {
        // build a power curve description object
        PowerCurveDescription curveDescription = null;
        if (!disableCheckbox.getValue()) {
            curveDescription = new PowerCurveDescription();
            if (xAxisListBox.getSelectedIndex() == TOTAL_N_INDEX) {
                curveDescription
                .setHorizontalAxisLabelEnum(HorizontalAxisLabelEnum.TOTAL_SAMPLE_SIZE);
            } else if (xAxisListBox.getSelectedIndex() == BETA_SCALE_INDEX) {
                curveDescription
                .setHorizontalAxisLabelEnum(HorizontalAxisLabelEnum.REGRESSION_COEEFICIENT_SCALE_FACTOR);
            } else {
                curveDescription
                .setHorizontalAxisLabelEnum(HorizontalAxisLabelEnum.VARIABILITY_SCALE_FACTOR);
            }
            curveDescription.setDataSeriesList(dataSeriesList);            

        } else {
            // clear the power curve
        }
        studyDesignContext.setPowerCurveDescription(this, curveDescription);
    }

    private void updateDataSeriesOptions() {

        // select boxes for items that must be fixed for the curve
        totalNListBox.setVisible(solvingForPower &&
                xAxisListBox.getSelectedIndex() != TOTAL_N_INDEX);
        nominalPowerListBox.setVisible(!solvingForPower);
        betaScaleListBox.setVisible(xAxisListBox.getSelectedIndex() != 
            BETA_SCALE_INDEX);
        sigmaScaleListBox.setVisible(xAxisListBox.getSelectedIndex() !=
            SIGMA_SCALE_INDEX);
        testListBox.setVisible(true);
        alphaListBox.setVisible(true);
        powerMethodListBox.setVisible(hasCovariate);
        quantileListBox.setVisible(hasCovariate);
        // Set visibility for Confidence Interval Description CheckBox
        confidenceLimitsCheckBox.setVisible(hasConfidenceIntervalDescription);

        // labels for each listbox
        totalNHTML.setVisible(solvingForPower &&
                xAxisListBox.getSelectedIndex() != TOTAL_N_INDEX);
        nominalPowerHTML.setVisible(!solvingForPower);
        betaScaleHTML.setVisible(xAxisListBox.getSelectedIndex() != 
            BETA_SCALE_INDEX);
        sigmaScaleHTML.setVisible(xAxisListBox.getSelectedIndex() != 
            SIGMA_SCALE_INDEX);
        testHTML.setVisible(true);
        alphaHTML.setVisible(true);
        powerMethodHTML.setVisible(hasCovariate);
        quantileHTML.setVisible(hasCovariate);
        // Set visibility for Confidence Interval Description Label
        confidenceLimitsLabelHTML.setVisible(hasConfidenceIntervalDescription);
    }

    /**
     * Update the screen when the context changes
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) {
        StudyDesignChangeEvent changeEvent = (StudyDesignChangeEvent) e;
        switch (changeEvent.getType()) {
        case SOLVING_FOR:
            solvingForPower = 
                (studyDesignContext.getStudyDesign().getSolutionTypeEnum() == 
                    SolutionTypeEnum.POWER);
            break;
        case COVARIATE:
            hasCovariate = 
                studyDesignContext.getStudyDesign().isGaussianCovariate();
            break;
        case PER_GROUP_N_LIST:
            List<SampleSize> sampleSizeList = 
                studyDesignContext.getStudyDesign().getSampleSizeList();
            totalNListBox.clear();
            if (sampleSizeList != null) {
                for (SampleSize size : sampleSizeList) {
                    int totalN = size.getValue() * totalSampleSizeMultiplier;
                    totalNListBox.addItem(Integer.toString(totalN));
                }
            }
            break;
        case BETA_SCALE_LIST:
            List<BetaScale> betaScaleList = 
                studyDesignContext.getStudyDesign().getBetaScaleList();
            betaScaleListBox.clear();
            if (betaScaleList != null) {
                for (BetaScale scale : betaScaleList) {
                    betaScaleListBox.addItem(
                            Double.toString(scale.getValue()));
                }
            }
            break;

        case SIGMA_SCALE_LIST:
            List<SigmaScale> sigmaScaleList = 
                studyDesignContext.getStudyDesign().getSigmaScaleList();
            sigmaScaleListBox.clear();
            if (sigmaScaleList != null) {
                for (SigmaScale scale : sigmaScaleList) {
                    sigmaScaleListBox.addItem(
                            Double.toString(scale.getValue()));
                }
            }
            break;

        case STATISTICAL_TEST_LIST:
            List<StatisticalTest> testList = 
                studyDesignContext.getStudyDesign().getStatisticalTestList();
            testListBox.clear();
            if (testList != null) {
                for (StatisticalTest test : testList) {
                    testListBox.addItem(
                            statisticalTestToString(test.getType()));
                }
            }
            break;

        case ALPHA_LIST:
            List<TypeIError> alphaList = 
                studyDesignContext.getStudyDesign().getAlphaList();
            alphaListBox.clear();
            if (alphaList != null) {
                for (TypeIError alpha : alphaList) {
                    alphaListBox.addItem(
                            Double.toString(alpha.getAlphaValue()));
                }
            }
            break;
        case POWER_METHOD_LIST:
            List<PowerMethod> powerMethodList = 
                studyDesignContext.getStudyDesign().getPowerMethodList();
            powerMethodListBox.clear();
            if (powerMethodList != null) {
                for (PowerMethod powerMethod : powerMethodList) {
                    powerMethodListBox.addItem(
                            powerMethodToString(
                                    powerMethod.getPowerMethodEnum()));
                }
            }
            break;
        case QUANTILE_LIST:
            List<Quantile> quantileList = 
                studyDesignContext.getStudyDesign().getQuantileList();
            quantileListBox.clear();
            if (quantileList != null) {
                for (Quantile quantile : quantileList) {
                    quantileListBox.addItem(
                            Double.toString(quantile.getValue()));
                }
            }
            break;
        case CONFIDENCE_INTERVAL:
            hasConfidenceIntervalDescription = 
                (studyDesignContext.getStudyDesign()
                        .getConfidenceIntervalDescriptions() != null);            
            break;
        }

        updateDataSeriesOptions();
        checkComplete();
    }

    /**
     * load power curve information from the context
     */
    public void loadFromContext() {
        reset();
        solvingForPower = (studyDesignContext.getStudyDesign()
                .getSolutionTypeEnum() == SolutionTypeEnum.POWER);
        hasCovariate = studyDesignContext.getStudyDesign()
        .isGaussianCovariate();
        hasConfidenceIntervalDescription = (studyDesignContext.getStudyDesign()
                .getConfidenceIntervalDescriptions() != null);     

        // load the per group n list
        List<SampleSize> sampleSizeList = studyDesignContext
        .getStudyDesign().getSampleSizeList();
        totalNListBox.clear();
        if (sampleSizeList != null) {
            for (SampleSize size : sampleSizeList) {
                int totalN = size.getValue() * totalSampleSizeMultiplier;
                totalNListBox.addItem(Integer.toString(totalN));
            }
        }
        // load the beta scale list
        List<BetaScale> betaScaleList = studyDesignContext.getStudyDesign()
        .getBetaScaleList();
        betaScaleListBox.clear();
        if (betaScaleList != null) {
            for (BetaScale scale : betaScaleList) {
                betaScaleListBox.addItem(Double.toString(scale.getValue()));
            }
        }
        // load the sigma scale list
        List<SigmaScale> sigmaScaleList = studyDesignContext
        .getStudyDesign().getSigmaScaleList();
        sigmaScaleListBox.clear();
        if (sigmaScaleList != null) {
            for (SigmaScale scale : sigmaScaleList) {
                sigmaScaleListBox
                .addItem(Double.toString(scale.getValue()));
            }
        }
        // load the statistical test list
        List<StatisticalTest> testList = studyDesignContext
        .getStudyDesign().getStatisticalTestList();
        testListBox.clear();
        if (testList != null) {
            for (StatisticalTest test : testList) {
                testListBox
                .addItem(statisticalTestToString(test.getType()));
            }
        }
        // load the alpha list
        List<TypeIError> alphaList = studyDesignContext.getStudyDesign()
        .getAlphaList();
        alphaListBox.clear();
        if (alphaList != null) {
            for (TypeIError alpha : alphaList) {
                alphaListBox
                .addItem(Double.toString(alpha.getAlphaValue()));
            }
        }
        // load the power method list
        List<PowerMethod> powerMethodList = studyDesignContext
        .getStudyDesign().getPowerMethodList();
        powerMethodListBox.clear();
        if (powerMethodList != null) {
            for (PowerMethod powerMethod : powerMethodList) {
                powerMethodListBox.addItem(powerMethodToString(powerMethod
                        .getPowerMethodEnum()));
            }
        }
        // load the quantile list
        List<Quantile> quantileList = studyDesignContext.getStudyDesign()
        .getQuantileList();
        quantileListBox.clear();
        if (quantileList != null) {
            for (Quantile quantile : quantileList) {
                quantileListBox
                .addItem(Double.toString(quantile.getValue()));
            }
        }
        updateDataSeriesOptions();

        // fill in the data series
        PowerCurveDescription curveDescription = 
            studyDesignContext.getStudyDesign().getPowerCurveDescriptions();
        dataSeriesList.clear();
        dataSeriesTable.clear();
        if (curveDescription != null) {
            disableCheckbox.setValue(false);
            enableOptions(true);
            List<PowerCurveDataSeries> seriesList = curveDescription.getDataSeriesList();
            if (seriesList != null) {
                for(PowerCurveDataSeries series: seriesList) {
                    dataSeriesList.add(series);
                    dataSeriesTable.addItem(dataSeriesAsString(series));
                }
            }
        } else {
            disableCheckbox.setValue(true);
            enableOptions(false);
        }
        checkComplete();
    }


    /**
     * Handler upload and cancel events when a new context is created
     */
    @Override
    public void onWizardContextLoad() {
        loadFromContext();
    }

}
