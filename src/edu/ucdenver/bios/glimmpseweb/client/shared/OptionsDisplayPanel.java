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
import com.smartgwt.client.types.AutoFitWidthApproach;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

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
import edu.ucdenver.bios.webservice.common.domain.NominalPower;
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
    // index in the x-axis dropdown
    private static final int TOTAL_N_INDEX = 0;
    private static final int BETA_SCALE_INDEX = 1;
    private static final int SIGMA_SCALE_INDEX = 2;
    // names of the columns in the table
    private static final String COLUMN_LABEL = "label";
    private static final String COLUMN_SAMPLE_SIZE = "sampleSize";
    private static final String COLUMN_BETA_SCALE = "betaScale";
    private static final String COLUMN_SIGMA_SCALE = "sigmaScale";
    private static final String COLUMN_ALPHA = "alpha";
    private static final String COLUMN_TEST = "test";
    private static final String COLUMN_CI = "ci";
    private static final String COLUMN_NOMINAL_POWER = "nominalPower";
    private static final String COLUMN_POWER_METHOD = "powerMethod";
    private static final String COLUMN_QUANTILE = "quantile";
    // context object
    protected StudyDesignContext studyDesignContext;
    // work around for problem with ListGrid: cannot hide/show fields
    // unless the widget is currently visible
    protected boolean visible = false;

    // cache some information about solution type and covariate
    protected boolean hasCovariate = false;
    protected boolean solvingForPower = true;
    protected boolean hasConfidenceIntervalDescription = false;
    protected boolean hasQuantilePower = false;

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
    protected HTML dataSeriesLabelHTML = new HTML(
            GlimmpseWeb.constants.curveOptionsDataSeriesLabel());
    protected HTML confidenceLimitsLabelHTML = new HTML(
            GlimmpseWeb.constants.curveOptionsConfidenceLimitsLabel());

    // listbox showing the data series
    protected ListGrid dataSeriesGrid = new ListGrid();

    // panels for x-axis type selection and data series input
    private VerticalPanel xAxisPanel = new VerticalPanel();
    private VerticalPanel dataSeriesPanel = new VerticalPanel();

    // Column model for the data series display grid
    private class DataSeriesRecord extends ListGridRecord {
        public DataSeriesRecord(PowerCurveDataSeries series) {
            setAttribute(COLUMN_LABEL, series.getLabel());  
            if (series.getSampleSize() > 0) {
                setAttribute(COLUMN_SAMPLE_SIZE, 
                        Integer.toString(series.getSampleSize()));
            }
            setAttribute(COLUMN_BETA_SCALE, 
                    Double.toString(series.getBetaScale()));
            setAttribute(COLUMN_SIGMA_SCALE, 
                    Double.toString(series.getSigmaScale()));
            setAttribute(COLUMN_ALPHA, 
                    Double.toString(series.getTypeIError()));
            setAttribute(COLUMN_TEST, 
                    statisticalTestToString(series.getStatisticalTestTypeEnum()));
            setAttribute(COLUMN_CI, 
                    (series.isConfidenceLimits() ? 
                            GlimmpseWeb.constants.yes() :
                                GlimmpseWeb.constants.no()));
            if (series.getNominalPower() > 0) {
                setAttribute(COLUMN_NOMINAL_POWER, 
                        Double.toString(series.getNominalPower()));
            }
            if (series.getPowerMethod() != null) {
                setAttribute(COLUMN_POWER_METHOD, 
                        powerMethodToString(series.getPowerMethod()));
            }
            if (series.getQuantile() > 0) {
                setAttribute(COLUMN_QUANTILE, 
                        Double.toString(series.getQuantile()));
            }
        }
    }

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
        description.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

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
     * Sync the GUI to the study design context
     */
    private void syncPowerCurveDescription() {
        boolean enabled = !disableCheckbox.getValue();
        studyDesignContext.setPowerCurveDescription(this, enabled);
        if (enabled) {
            // sync the axis type
            switch (xAxisListBox.getSelectedIndex()) {
            case TOTAL_N_INDEX:
                studyDesignContext.setPowerCurveXAxisType(this, 
                        HorizontalAxisLabelEnum.TOTAL_SAMPLE_SIZE);
                break;
            case BETA_SCALE_INDEX:
                studyDesignContext.setPowerCurveXAxisType(this, 
                        HorizontalAxisLabelEnum.REGRESSION_COEEFICIENT_SCALE_FACTOR);
                break;
            case SIGMA_SCALE_INDEX:
                studyDesignContext.setPowerCurveXAxisType(this, 
                        HorizontalAxisLabelEnum.VARIABILITY_SCALE_FACTOR);
                break;
            }
            // sync the data series
            ListGridRecord[] recordList = dataSeriesGrid.getRecords();
            for(ListGridRecord record: recordList) {
                studyDesignContext.addPowerCurveDataSeries(this, 
                        recordToDataSeries((DataSeriesRecord) record));
            }
        } 
    }

    /**
     * Create a data series object from a record in the ListGrid
     * @param record
     * @return
     */
    private PowerCurveDataSeries recordToDataSeries(DataSeriesRecord record) {
        PowerCurveDataSeries series = new PowerCurveDataSeries();
        series.setLabel(record.getAttribute(COLUMN_LABEL));
        String alphaStr = record.getAttribute(COLUMN_ALPHA);
        if (alphaStr != null && !alphaStr.isEmpty()) {
            series.setTypeIError(Double.parseDouble(alphaStr));
        }
        String betaScaleStr = record.getAttribute(COLUMN_BETA_SCALE);
        if (betaScaleStr != null && !betaScaleStr.isEmpty()) {
            series.setBetaScale(Double.parseDouble(betaScaleStr));
        }
        String sigmaScaleStr = record.getAttribute(COLUMN_SIGMA_SCALE);
        if (sigmaScaleStr != null && !sigmaScaleStr.isEmpty()) {
            series.setSigmaScale(Double.parseDouble(sigmaScaleStr));
        }
        series.setStatisticalTestTypeEnum(
                statisticalTestStringToEnum(record.getAttribute(COLUMN_TEST)));
        String sampleSizeStr = record.getAttribute(COLUMN_SAMPLE_SIZE);
        if (sampleSizeStr != null && !sampleSizeStr.isEmpty()) {
            series.setSampleSize(Integer.parseInt(sampleSizeStr));
        }
        String nominalPowerStr = 
            record.getAttribute(COLUMN_NOMINAL_POWER);
        if (nominalPowerStr != null && !nominalPowerStr.isEmpty()) {
            series.setNominalPower(Double.parseDouble(nominalPowerStr));
        }
        series.setPowerMethod(
                powerMethodStringToEnum(record.getAttribute(COLUMN_POWER_METHOD)));
        String quantileStr = record.getAttribute(COLUMN_QUANTILE);
        if (quantileStr != null && !quantileStr.isEmpty()) {
            series.setQuantile(Double.parseDouble(quantileStr));
        }
        String ciString = record.getAttribute(COLUMN_CI);
        if (ciString != null && ciString.equals(GlimmpseWeb.constants.yes())) {
            series.setConfidenceLimits(true);
        }
        return series;
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
                syncPowerCurveDescription();
                checkComplete();
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
        xAxisListBox.addItem(
                GlimmpseWeb.constants.curveOptionsSampleSizeLabel());
        xAxisListBox.addItem(
                GlimmpseWeb.constants.curveOptionsBetaScaleLabel());
        xAxisListBox.addItem(
                GlimmpseWeb.constants.curveOptionsSigmaScaleLabel());

        // add callback
        xAxisListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                clearDataSeries();
                updateDataSeriesOptions();
                checkComplete();
            }
        });

        // layout the panel
        HTML xAxixDescription = new HTML(
                GlimmpseWeb.constants.curveOptionsXAxisDescription());
        xAxisPanel.add(xAxixDescription);
        xAxisPanel.add(xAxisListBox);

        // set style
        // xAxixDescription.addStyleDependentName("subText");
        xAxisPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);
        xAxisListBox
        .setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);

        return xAxisPanel;
    }

    /**
     * Create the panel allowing the user to specify data series
     * @return
     */
    private VerticalPanel createDataSeriesPanel() {
        VerticalPanel mainPanel = new VerticalPanel();

        // set the columns of the grid, size, etc
        dataSeriesGrid.setWidth(575);  
        dataSeriesGrid.setHeight(120);
        dataSeriesGrid.setUseAllDataSourceFields(true);  
        dataSeriesGrid.setAutoFitFieldWidths(true);
        dataSeriesGrid.setAutoFitWidthApproach(AutoFitWidthApproach.TITLE);
        dataSeriesGrid.setSelectionType(SelectionStyle.MULTIPLE);  
        // build columns
        ListGridField dataLabelField = new ListGridField(
                COLUMN_LABEL, 
                GlimmpseWeb.constants.curveOptionsDataSeriesLabel());  
        ListGridField sampleSizeField = new ListGridField(
                COLUMN_SAMPLE_SIZE, 
                GlimmpseWeb.constants.curveOptionsSampleSizeLabel()); 
        ListGridField betaScaleField = new ListGridField(
                COLUMN_BETA_SCALE, 
                GlimmpseWeb.constants.curveOptionsBetaScaleLabel());  
        ListGridField sigmaScaleField = new ListGridField(
                COLUMN_SIGMA_SCALE, 
                GlimmpseWeb.constants.curveOptionsSigmaScaleLabel());  
        ListGridField alphaField = new ListGridField(
                COLUMN_ALPHA, 
                GlimmpseWeb.constants.curveOptionsAlphaLabel());  
        ListGridField testField = new ListGridField(
                COLUMN_TEST, 
                GlimmpseWeb.constants.curveOptionsTestLabel());  
        ListGridField ciField = new ListGridField(
                COLUMN_CI, 
                GlimmpseWeb.constants.curveOptionsConfidenceLimitsLabel());  
        ListGridField nominalPowerField = new ListGridField(
                COLUMN_NOMINAL_POWER, 
                GlimmpseWeb.constants.curveOptionsNominalPowerLabel());  
        ListGridField powerMethodField = new ListGridField(
                COLUMN_POWER_METHOD, 
                GlimmpseWeb.constants.curveOptionsPowerMethodLabel());  
        ListGridField quantileField = new ListGridField(
                COLUMN_QUANTILE, 
                GlimmpseWeb.constants.curveOptionsQuantileLabel());  
        dataSeriesGrid.setFields(dataLabelField, nominalPowerField,
                sampleSizeField, betaScaleField, sigmaScaleField, 
                testField, alphaField, powerMethodField, 
                quantileField, ciField);  

        // input fields for the data series
        Grid grid = new Grid(10, 2);
        // labels
        grid.setWidget(0, 0, dataSeriesLabelHTML);
        grid.setWidget(1, 0, nominalPowerHTML);
        grid.setWidget(2, 0, totalNHTML);
        grid.setWidget(3, 0, betaScaleHTML);
        grid.setWidget(4, 0, sigmaScaleHTML);
        grid.setWidget(5, 0, testHTML);
        grid.setWidget(6, 0, alphaHTML);
        grid.setWidget(7, 0, powerMethodHTML);
        grid.setWidget(8, 0, quantileHTML);
        grid.setWidget(9, 0, confidenceLimitsLabelHTML);
        // list widgets
        grid.setWidget(0, 1, dataSeriesLabelTextBox);
        grid.setWidget(1, 1, nominalPowerListBox);
        grid.setWidget(2, 1, totalNListBox);
        grid.setWidget(3, 1, betaScaleListBox);
        grid.setWidget(4, 1, sigmaScaleListBox);
        grid.setWidget(5, 1, testListBox);
        grid.setWidget(6, 1, alphaListBox);
        grid.setWidget(7, 1, powerMethodListBox);
        grid.setWidget(8, 1, quantileListBox);
        grid.setWidget(9, 1, confidenceLimitsCheckBox);

        // add a data series button
        Button addSeriesButton = new Button(GlimmpseWeb.constants.buttonAdd(),
                new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addSeries();
            }
        });
        // remove a data series button
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
        mainPanel.add(dataSeriesGrid);

        // layout the panel
        dataSeriesPanel.add(new HTML(
                GlimmpseWeb.constants.curveOptionsDataSeriesDescription()));
        dataSeriesPanel.add(mainPanel);

        // set style
        nominalPowerListBox.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        totalNListBox.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        betaScaleListBox.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        sigmaScaleListBox.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        testListBox.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        alphaListBox.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        powerMethodListBox.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        quantileListBox.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        dataSeriesLabelTextBox.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BOX);
        confidenceLimitsCheckBox.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_CHECK_BOX);

        addSeriesButton.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
        removeSeriesButton.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
        mainPanel.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_LIST_PANEL);
        grid.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
        dataSeriesPanel.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);

        return dataSeriesPanel;
    }

    /**
     * Clear the options panel
     */
    public void reset() {
        visible = false;

        hasCovariate = false;
        solvingForPower = true;
        hasConfidenceIntervalDescription = false;
        hasQuantilePower = false;

        // set the display to remove power options
        disableCheckbox.setValue(true);
        // reset confidence limits checkbox
        confidenceLimitsCheckBox.setValue(false);
        enableOptions(false);

        // clear the list boxes
        nominalPowerListBox.clear();
        totalNListBox.clear();
        betaScaleListBox.clear();
        sigmaScaleListBox.clear();
        testListBox.clear();
        alphaListBox.clear();
        powerMethodListBox.clear();
        quantileListBox.clear();

        // clear the data series
        dataSeriesGrid.selectAllRecords();
        dataSeriesGrid.removeSelectedData();

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
        // set label in table and data series object
        series.setLabel(dataSeriesLabelTextBox.getText());
        // set the show confidence limit flag
        if (confidenceLimitsCheckBox.getValue()) {
            series.setConfidenceLimits(true);
        } else {
            series.setConfidenceLimits(false);
        }
        // set the power method
        if (hasCovariate) {
            index = powerMethodListBox.getSelectedIndex();
            if (index >= 0) {
                series.setPowerMethod(
                        powerMethodStringToEnum(
                                powerMethodListBox.getItemText(
                                        powerMethodListBox.getSelectedIndex())));
            }
            index = quantileListBox.getSelectedIndex();
            if (index >= 0) {
                series.setQuantile(Double.parseDouble(
                        quantileListBox.getItemText(
                                quantileListBox.getSelectedIndex())));
            }
        }
        // remaining fields depend on the selected axis type 
        if (xAxisListBox.getSelectedIndex() == TOTAL_N_INDEX) {
            // set the sample beta scale and sigma scale 
            index = betaScaleListBox.getSelectedIndex();
            if (index >= 0) {
                series.setBetaScale(
                        Double.parseDouble(
                                betaScaleListBox.getItemText(
                                        betaScaleListBox.getSelectedIndex())));
            }
            index = sigmaScaleListBox.getSelectedIndex();
            if (index >= 0) {
                series.setSigmaScale(
                        Double.parseDouble(
                                sigmaScaleListBox.getItemText(
                                        sigmaScaleListBox.getSelectedIndex())));
            }

        } else if (xAxisListBox.getSelectedIndex() == BETA_SCALE_INDEX) {
            // set sample size and sigma scale
            index = totalNListBox.getSelectedIndex();
            if (index >= 0) {
                series.setSampleSize(Integer.parseInt(
                        totalNListBox.getItemText(
                                totalNListBox.getSelectedIndex())));
            }
            index = sigmaScaleListBox.getSelectedIndex();
            if (index >= 0) {
                series.setSigmaScale(
                        Double.parseDouble(
                                sigmaScaleListBox.getItemText(
                                        sigmaScaleListBox.getSelectedIndex())));
            }

        } else {
            // set sample size and beta scale
            index = totalNListBox.getSelectedIndex();
            if (index >= 0) {
                series.setSampleSize(
                        Integer.parseInt(
                                totalNListBox.getItemText(
                                        totalNListBox.getSelectedIndex())));
            }
            index = betaScaleListBox.getSelectedIndex();
            if (index >= 0) {
                series.setBetaScale(
                        Double.parseDouble(
                                betaScaleListBox.getItemText(
                                        betaScaleListBox.getSelectedIndex())));
            }
        }

        // add alpha
        index = alphaListBox.getSelectedIndex();
        if (index >= 0) {
            series.setTypeIError(
                    Double.parseDouble(
                            alphaListBox.getItemText(
                                    alphaListBox.getSelectedIndex())));
        }
        // add test
        index = testListBox.getSelectedIndex();
        if (index >= 0) {
            series.setStatisticalTestTypeEnum(
                    statisticalTestStringToEnum(
                            testListBox.getItemText(
                                    testListBox.getSelectedIndex())));
        }

        // display the record
        dataSeriesGrid.addData(new DataSeriesRecord(series));
        // update the context
        studyDesignContext.addPowerCurveDataSeries(this, series);
    }

    /**
     * Remove the currently selected data series
     */
    private void removeSeries() {
        ListGridRecord[] selectedRecords = 
            dataSeriesGrid.getSelectedRecords();
        if (selectedRecords != null && selectedRecords.length > 0) {
            for(ListGridRecord record: selectedRecords) {
                int index = dataSeriesGrid.getRecordIndex(record);
                studyDesignContext.deletePowerCurveDataSeries(this, index);
                dataSeriesGrid.removeData(record);
            }
        }
    }

    /**
     * Get the test enum for this string
     * 
     * @param testStr
     * @return
     */
    public static StatisticalTestTypeEnum statisticalTestStringToEnum(
            String testStr) {
        if (testStr != null) {
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
    public static PowerMethodEnum powerMethodStringToEnum(
            String powerMethodStr) {
        if (powerMethodStr != null) {
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
        if ((studyDesignContext.getStudyDesign().getSolutionTypeEnum() == null) ||
                (solvingForPower && totalNListBox.getItemCount() <= 0) ||
                (!solvingForPower && nominalPowerListBox.getItemCount() <= 0) ||
                (betaScaleListBox.getItemCount() <= 0) ||
                (sigmaScaleListBox.getItemCount() <= 0) ||
                (testListBox.getItemCount() <= 0) ||
                (alphaListBox.getItemCount() <= 0) ||
                (hasCovariate && powerMethodListBox.getItemCount() <= 0) ||
                (hasQuantilePower && quantileListBox.getItemCount() <= 0)) {
            // if any visible dropdown lists are not filled, then the user
            // can't enter this screen
            changeState(WizardStepPanelState.NOT_ALLOWED);
        } else {
            if (disableCheckbox.getValue() || dataSeriesGrid.getTotalRows() > 0) {
                changeState(WizardStepPanelState.COMPLETE);
            } else {
                changeState(WizardStepPanelState.INCOMPLETE);
            }
        }
    }

    /**
     * Update the displayed options for the data series
     */
    private void updateDataSeriesOptions() {

        // determine which fields to hide/show
        boolean showSampleSize = 
            (solvingForPower && 
                    xAxisListBox.getSelectedIndex() != TOTAL_N_INDEX);
        boolean showNominalPower = !solvingForPower;
        boolean showBetaScale = 
            (xAxisListBox.getSelectedIndex() != BETA_SCALE_INDEX);
        boolean showSigmaScale = 
            (xAxisListBox.getSelectedIndex() != SIGMA_SCALE_INDEX);

        // hide and show options
        // sample size
        totalNHTML.setVisible(showSampleSize);
        totalNListBox.setVisible(showSampleSize);
        showDataSeriesGridField(COLUMN_SAMPLE_SIZE, showSampleSize);
        // nominal power
        nominalPowerHTML.setVisible(showNominalPower);
        nominalPowerListBox.setVisible(showNominalPower);
        showDataSeriesGridField(COLUMN_NOMINAL_POWER, showNominalPower);
        // beta scale
        betaScaleHTML.setVisible(showBetaScale);
        betaScaleListBox.setVisible(showBetaScale);
        showDataSeriesGridField(COLUMN_BETA_SCALE, showBetaScale);
        // sigma scale
        sigmaScaleHTML.setVisible(showSigmaScale);
        sigmaScaleListBox.setVisible(showSigmaScale);
        showDataSeriesGridField(COLUMN_SIGMA_SCALE, showSigmaScale);
        // power method
        powerMethodHTML.setVisible(hasCovariate);
        powerMethodListBox.setVisible(hasCovariate);
        showDataSeriesGridField(COLUMN_POWER_METHOD, hasCovariate);
        // quantile
        quantileHTML.setVisible(hasQuantilePower);
        quantileListBox.setVisible(hasQuantilePower);
        showDataSeriesGridField(COLUMN_QUANTILE, hasQuantilePower);
        // Set visibility for Confidence Interval Description CheckBox
        confidenceLimitsLabelHTML.setVisible(hasConfidenceIntervalDescription);
        confidenceLimitsCheckBox.setVisible(hasConfidenceIntervalDescription);
        showDataSeriesGridField(COLUMN_CI, hasConfidenceIntervalDescription);
    }

    /**
     * Hide or show a column in the data series grid
     * @param fieldName
     * @param show
     */
    private void showDataSeriesGridField(String fieldName, boolean show) {
        if (show) {
            if (visible) {
                dataSeriesGrid.showField(fieldName);
            }
        } else {
            dataSeriesGrid.hideField(fieldName);
        }
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
            clearDataSeries();
            break;
        case COVARIATE:
            hasCovariate = 
                studyDesignContext.getStudyDesign().isGaussianCovariate();
            clearDataSeries();
            break;
        case POWER_LIST:
            List<NominalPower> nominalPowerList = 
                studyDesignContext.getStudyDesign().getNominalPowerList();
            nominalPowerListBox.clear();
            if (nominalPowerList != null) {
                for (NominalPower power : nominalPowerList) {
                    nominalPowerListBox.addItem(Double.toString(power.getValue()));
                }
            }
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
                hasQuantilePower = false;
                for (PowerMethod powerMethod : powerMethodList) {
                    if (powerMethod.getPowerMethodEnum() ==
                        PowerMethodEnum.QUANTILE) {
                        hasQuantilePower = true;
                    }
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
        solvingForPower = (
                studyDesignContext.getStudyDesign().getSolutionTypeEnum() ==
                    SolutionTypeEnum.POWER);
        hasCovariate = 
            studyDesignContext.getStudyDesign().isGaussianCovariate();
        hasConfidenceIntervalDescription = (
                studyDesignContext.getStudyDesign()
                .getConfidenceIntervalDescriptions() != null);     

        // load the nominal power list
        List<NominalPower> nominalPowerList = 
            studyDesignContext.getStudyDesign().getNominalPowerList();
        nominalPowerListBox.clear();
        if (nominalPowerList != null) {
            for (NominalPower power : nominalPowerList) {
                nominalPowerListBox.addItem(Double.toString(power.getValue()));
            }
        }
        // load the per group n list
        List<SampleSize> sampleSizeList = 
            studyDesignContext.getStudyDesign().getSampleSizeList();
        totalNListBox.clear();
        if (sampleSizeList != null) {
            for (SampleSize size : sampleSizeList) {
                int totalN = size.getValue() * totalSampleSizeMultiplier;
                totalNListBox.addItem(Integer.toString(totalN));
            }
        }
        // load the beta scale list
        List<BetaScale> betaScaleList = 
            studyDesignContext.getStudyDesign().getBetaScaleList();
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
                sigmaScaleListBox.addItem(Double.toString(scale.getValue()));
            }
        }
        // load the statistical test list
        List<StatisticalTest> testList = studyDesignContext
        .getStudyDesign().getStatisticalTestList();
        testListBox.clear();
        if (testList != null) {
            for (StatisticalTest test : testList) {
                testListBox.addItem(statisticalTestToString(test.getType()));
            }
        }
        // load the alpha list
        List<TypeIError> alphaList = studyDesignContext.getStudyDesign()
        .getAlphaList();
        alphaListBox.clear();
        if (alphaList != null) {
            for (TypeIError alpha : alphaList) {
                alphaListBox.addItem(Double.toString(alpha.getAlphaValue()));
            }
        }
        // load the power method list
        List<PowerMethod> powerMethodList = studyDesignContext
        .getStudyDesign().getPowerMethodList();
        powerMethodListBox.clear();
        if (powerMethodList != null) {
            hasQuantilePower = false;
            for (PowerMethod powerMethod : powerMethodList) {
                if (powerMethod.getPowerMethodEnum() ==
                    PowerMethodEnum.QUANTILE) {
                    hasQuantilePower = true;
                }
                powerMethodListBox.addItem(
                        powerMethodToString(powerMethod.getPowerMethodEnum()));
            }
        }
        // load the quantile list
        List<Quantile> quantileList = studyDesignContext.getStudyDesign()
        .getQuantileList();
        quantileListBox.clear();
        if (quantileList != null) {
            for (Quantile quantile : quantileList) {
                quantileListBox.addItem(Double.toString(quantile.getValue()));
            }
        }
        updateDataSeriesOptions();

        // fill in the data series
        PowerCurveDescription curveDescription = 
            studyDesignContext.getStudyDesign().getPowerCurveDescriptions();
        // clear the display grid
        dataSeriesGrid.selectAllRecords();
        dataSeriesGrid.removeSelectedData();
        if (curveDescription != null) {
            disableCheckbox.setValue(false);
            enableOptions(true);
            List<PowerCurveDataSeries> seriesList = curveDescription.getDataSeriesList();
            if (seriesList != null) {
                for(PowerCurveDataSeries series: seriesList) {
                    dataSeriesGrid.addData(new DataSeriesRecord(series));
                }
            }
        } else {
            disableCheckbox.setValue(true);
            enableOptions(false);
        }
        checkComplete();
    }

    /**
     * Clear the data series
     */
    private void clearDataSeries() {
        studyDesignContext.clearPowerCurveDataSeries(this);
        dataSeriesGrid.selectAllRecords();
        dataSeriesGrid.removeSelectedData();
    }

    /**
     * Handler upload and cancel events when a new context is created
     */
    @Override
    public void onWizardContextLoad() {
        loadFromContext();
    }

    /**
     * Update the data series grid upon entering the
     * screen - avoids bug in SmartGWT: can't show a field
     * unless the widget is currently visible on screen
     */
    @Override
    public void onEnter() {
        visible = true;
        updateDataSeriesOptions();
    }

    /**
     * Change the state to not visible - workaround
     * for SmartGWT bug
     */
    @Override
    public void onExit() {
        visible = false;
    }
}
