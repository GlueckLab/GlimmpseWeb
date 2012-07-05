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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.ChartRequestBuilder;
import edu.ucdenver.bios.glimmpseweb.client.ChartRequestBuilder.AxisType;
import edu.ucdenver.bios.glimmpseweb.client.ChartRequestBuilder.StratificationType;
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
import edu.ucdenver.bios.webservice.common.domain.SampleSize;
import edu.ucdenver.bios.webservice.common.domain.SigmaScale;
import edu.ucdenver.bios.webservice.common.domain.StatisticalTest;
import edu.ucdenver.bios.webservice.common.domain.TypeIError;
import edu.ucdenver.bios.webservice.common.enums.HorizontalAxisLabelEnum;
import edu.ucdenver.bios.webservice.common.enums.StatisticalTestTypeEnum;

/**
 * Panel which allows user to select display options
 * for their power/sample size calculations.  Note that two instances of this 
 * class are created (one for matrix mode, one for guided mode) so any
 * radio groups or other unique identifiers must have a mode-specific prefix
 * 
 * @author Sarah Kreidler
 *
 */
public class OptionsDisplayPanel extends WizardStepPanel
implements ClickHandler, WizardContextListener
{
    private static final int TOTAL_N_INDEX = 0;
    private static final int BETA_SCALE_INDEX = 1;
    private static final int SIGMA_SCALE_INDEX = 2;
    
    // context object
    protected StudyDesignContext studyDesignContext;

    protected boolean hasCovariate;
    protected boolean solvingForPower;

    // mutliplier to get total sample size from relative sizes
    protected int totalSampleSizeMultiplier = 1;
    protected ArrayList<Integer> perGroupNList = new ArrayList<Integer>();

    // skip curve button
    protected CheckBox disableCheckbox = new CheckBox();

    // options for x-axis
    protected ListBox xAxisListBox = new ListBox();

    // select boxes for items that must be fixed for the curve
    protected ListBox totalNListBox = new ListBox();
    protected ListBox betaScaleListBox = new ListBox();
    protected ListBox sigmaScaleListBox = new ListBox();
    protected ListBox testListBox = new ListBox();
    protected ListBox alphaListBox = new ListBox();
    protected ListBox powerMethodListBox = new ListBox();
    protected ListBox quantileListBox = new ListBox();
    // labels for each listbox
    protected HTML totalNHTML = 
        new HTML(GlimmpseWeb.constants.curveOptionsSampleSizeLabel());
    protected HTML betaScaleHTML = 
        new HTML(GlimmpseWeb.constants.curveOptionsBetaScaleLabel());
    protected HTML sigmaScaleHTML = 
        new HTML(GlimmpseWeb.constants.curveOptionsSigmaScaleLabel());
    protected HTML testHTML = 
        new HTML(GlimmpseWeb.constants.curveOptionsTestLabel());
    protected HTML alphaHTML = 
        new HTML(GlimmpseWeb.constants.curveOptionsAlphaLabel());
    protected HTML powerMethodHTML = 
        new HTML(GlimmpseWeb.constants.curveOptionsPowerMethodLabel());
    protected HTML quantileHTML = 
        new HTML(GlimmpseWeb.constants.curveOptionsQuantileLabel());

    // listbox showing currently entered data series
    protected ListBox dataSeriesTable = new ListBox();
    
    // panels for x-axis type selection and data series input
    private VerticalPanel xAxisPanel = new VerticalPanel();
    private VerticalPanel dataSeriesPanel = new VerticalPanel();

    // list of data series
    protected ArrayList<PowerCurveDataSeries> dataSeriesList = 
        new ArrayList<PowerCurveDataSeries>();
    
    /**
     * Constructor
     * @param mode mode identifier (needed for unique widget identifiers)
     */
    public OptionsDisplayPanel(WizardContext context, String radioGroupSuffix)
    {
        super(context, GlimmpseWeb.constants.navItemPowerCurve(),
                WizardStepPanelState.COMPLETE);
        studyDesignContext = (StudyDesignContext) context;
        
        VerticalPanel panel = new VerticalPanel();

        // create header, description
        HTML header = new HTML(GlimmpseWeb.constants.curveOptionsTitle());
        HTML description = new HTML(GlimmpseWeb.constants.curveOptionsDescription());        

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
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        // initialize
        initWidget(panel);
    }

    /**
     * Enable or disable the curve options
     * @param enabled
     */
    private void enableOptions(boolean enabled)
    {
        xAxisPanel.setVisible(enabled);
        dataSeriesPanel.setVisible(enabled);
    }

    /** 
     * Create panel with checkbox to enable/disable power curves
     * @return HorizontalPanel containing the checkbox
     */
    private HorizontalPanel createDisablePanel()
    {
        HorizontalPanel panel = new HorizontalPanel();

        // add callbacks
        disableCheckbox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
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
     * @return VerticalPanel
     */
    private VerticalPanel createXAxisPanel()
    {
        // create the listbox for the x-axis values
        xAxisListBox.addItem(GlimmpseWeb.constants.curveOptionsSampleSizeLabel());
        xAxisListBox.addItem(GlimmpseWeb.constants.curveOptionsBetaScaleLabel());
        xAxisListBox.addItem(GlimmpseWeb.constants.curveOptionsSigmaScaleLabel());

        // add callback
        xAxisListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event)
            {
                ListBox lb = (ListBox) event.getSource();
                updateDataSeriesDisplay();
            }
        });

        // layout the panel
        xAxisPanel.add(new HTML(GlimmpseWeb.constants.curveOptionsXAxisLabel()));
        xAxisPanel.add(xAxisListBox);

        // set style
        xAxisListBox.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
        xAxisPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);

        return xAxisPanel;
    }    

    private VerticalPanel createDataSeriesPanel()
    {
        Grid grid = new Grid(7,2);
        // add drop down lists for remaining values that need to be fixed
        grid.setWidget(0, 1, totalNListBox);
        grid.setWidget(1, 1, betaScaleListBox);
        grid.setWidget(2, 1, sigmaScaleListBox);
        grid.setWidget(3, 1, testListBox);
        grid.setWidget(4, 1, alphaListBox);
        grid.setWidget(5, 1, powerMethodListBox);
        grid.setWidget(6, 1, quantileListBox);
        grid.setWidget(0, 0, totalNHTML);
        grid.setWidget(1, 0, betaScaleHTML);
        grid.setWidget(2, 0, sigmaScaleHTML);
        grid.setWidget(3, 0, testHTML);
        grid.setWidget(4, 0, alphaHTML);
        grid.setWidget(5, 0, powerMethodHTML);
        grid.setWidget(6, 0, quantileHTML);

        // layout the panel
        dataSeriesPanel.add(new HTML(GlimmpseWeb.constants.curveOptionsDataSeriesLabel()));
        dataSeriesPanel.add(grid);
        dataSeriesPanel.add(new HTML("Data Series"));
        dataSeriesPanel.add(dataSeriesTable);

        // set style
        grid.setStyleName(GlimmpseConstants.STYLE_WIZARD_INDENTED_CONTENT);
        dataSeriesPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_PARAGRAPH);

        return dataSeriesPanel;
    }

    private void updateDataSeriesDisplay() {
        //we should never display these two list boxes if the user isn't controlling
        //for a Gaussian predictor.
        if(!hasCovariate)
        {
            powerMethodHTML.setVisible(hasCovariate);
            powerMethodListBox.setVisible(hasCovariate);
            quantileHTML.setVisible(hasCovariate);
            quantileListBox.setVisible(hasCovariate);
        }
        totalNHTML.setVisible(solvingForPower && 
                xAxisListBox.getSelectedIndex() != TOTAL_N_INDEX);
        totalNListBox.setVisible(solvingForPower && 
                xAxisListBox.getSelectedIndex() != TOTAL_N_INDEX);
        betaScaleHTML.setVisible(xAxisListBox.getSelectedIndex() != BETA_SCALE_INDEX);
        betaScaleListBox.setVisible(xAxisListBox.getSelectedIndex() != BETA_SCALE_INDEX);
        sigmaScaleHTML.setVisible(xAxisListBox.getSelectedIndex() != SIGMA_SCALE_INDEX);
        sigmaScaleListBox.setVisible(xAxisListBox.getSelectedIndex() != SIGMA_SCALE_INDEX);
    }

    /**
     * Clear the options panel
     */
    public void reset()
    {		
        dataSeriesList.clear();
        // set the display to remove power options
        disableCheckbox.setValue(true);
        enableOptions(false);

        // clear the list boxes
        totalNListBox.clear();
        betaScaleListBox.clear();
        sigmaScaleListBox.clear();
        testListBox.clear();
        alphaListBox.clear();
        powerMethodListBox.clear();
        quantileListBox.clear();
        
        // set defaults
        xAxisListBox.setSelectedIndex(TOTAL_N_INDEX);
        changeState(WizardStepPanelState.COMPLETE);
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
        if (disableCheckbox.getValue())
        {
            changeState(WizardStepPanelState.COMPLETE);
        }
        else
        {
            if (dataSeriesList.size() > 0) {
                changeState(WizardStepPanelState.COMPLETE);
            } else {
                changeState(WizardStepPanelState.INCOMPLETE);
            }
        }
    }

    /**
     * Notify options listeners of selected options as we exit the options screen
     */
    @Override
    public void onExit()
    {
        // build a power curve description object
        if (!disableCheckbox.getValue())
        {
            PowerCurveDescription curveDescription = new PowerCurveDescription();
            if (xAxisListBox.getSelectedIndex() == TOTAL_N_INDEX) {
                curveDescription.setHorizontalAxisLabelEnum(
                        HorizontalAxisLabelEnum.TOTAL_SAMPLE_SIZE);
            } else if (xAxisListBox.getSelectedIndex() == BETA_SCALE_INDEX) {
                curveDescription.setHorizontalAxisLabelEnum(
                        HorizontalAxisLabelEnum.REGRESSION_COEEFICIENT_SCALE_FACTOR);
            } else {
                curveDescription.setHorizontalAxisLabelEnum(
                        HorizontalAxisLabelEnum.VARIABILITY_SCALE_FACTOR);;
            }
            
            
        } else {
            // clear the power curve
        }
    }

    @Override
    public void onWizardContextChange(WizardContextChangeEvent e)
    {
        StudyDesignChangeEvent changeEvent = (StudyDesignChangeEvent) e;
        switch(changeEvent.getType())
        {
        case COVARIATE:
            powerMethodHTML.setVisible(hasCovariate);
            powerMethodListBox.setVisible(hasCovariate);
            quantileHTML.setVisible(hasCovariate);
            quantileListBox.setVisible(hasCovariate);
            break;
        case PER_GROUP_N_LIST:
            List<SampleSize> sampleSizeList = studyDesignContext.getStudyDesign().getSampleSizeList();
            totalNListBox.clear();
            if (sampleSizeList != null) {
                for(SampleSize size: sampleSizeList) {
                    int totalN = size.getValue() * totalSampleSizeMultiplier;
                    totalNListBox.addItem(Integer.toString(totalN));
                }
            }
            break;

        case BETA_SCALE_LIST:
            List<BetaScale> betaScaleList = studyDesignContext.getStudyDesign().getBetaScaleList();
            betaScaleListBox.clear();
            if (betaScaleList != null) {
                for(BetaScale scale: betaScaleList) {
                    betaScaleListBox.addItem(scale.toString());
                }
            }
            break;

        case SIGMA_SCALE_LIST:
            List<SigmaScale> sigmaScaleList = studyDesignContext.getStudyDesign().getSigmaScaleList();
            sigmaScaleListBox.clear();
            if (sigmaScaleList != null) {
                for(SigmaScale scale: sigmaScaleList) {
                    sigmaScaleListBox.addItem(scale.toString());
                }
            }
            break;

        case STATISTICAL_TEST_LIST:
            List<StatisticalTest> testList = studyDesignContext.getStudyDesign().getStatisticalTestList();
            testListBox.clear();
            if (testList != null) {
                for(StatisticalTest test: testList) {
                    testListBox.addItem(test.toString());
                }
            }
            break;

        case ALPHA_LIST:
            List<TypeIError> alphaList = studyDesignContext.getStudyDesign().getAlphaList();
            alphaListBox.clear();
            if (alphaList != null) {
                for(TypeIError alpha: alphaList) {
                    alphaListBox.addItem(alpha.toString());
                }
            }
            break;
        }
    }

    @Override
    public void onWizardContextLoad() {
        // TODO Auto-generated method stub

    }

}
