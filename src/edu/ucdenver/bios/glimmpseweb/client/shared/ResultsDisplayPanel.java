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

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.Table;
import com.smartgwt.client.types.AutoFitWidthApproach;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.connector.ChartSvcConnector;
import edu.ucdenver.bios.glimmpseweb.client.connector.FileSvcConnector;
import edu.ucdenver.bios.glimmpseweb.client.connector.PowerSvcConnector;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.ConfidenceInterval;
import edu.ucdenver.bios.webservice.common.domain.PowerResult;
import edu.ucdenver.bios.webservice.common.domain.Quantile;
import edu.ucdenver.bios.webservice.common.domain.StatisticalTest;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;
import edu.ucdenver.bios.webservice.common.enums.PowerMethodEnum;

/**
 * Final results display panel
 * @author Sarah Kreidler
 *
 */
public class ResultsDisplayPanel extends WizardStepPanel
{	
    // columns associated with each quantity in the data table
    private static final int COLUMN_ID_TEST = 0;
    private static final int COLUMN_ID_ACTUAL_POWER = 1;
    private static final int COLUMN_ID_TOTAL_SAMPLE_SIZE = 2;
    private static final int COLUMN_ID_BETA_SCALE = 3;
    private static final int COLUMN_ID_SIGMA_SCALE = 4;
    private static final int COLUMN_ID_ALPHA = 5;
    private static final int COLUMN_ID_NOMINAL_POWER = 6;
    private static final int COLUMN_ID_POWER_METHOD = 7;
    private static final int COLUMN_ID_QUANTILE = 8;
    private static final int COLUMN_ID_CI_LOWER = 9;
    private static final int COLUMN_ID_CI_UPPER = 10;

    private static final String STYLE_SEPARATOR = "separator";

    private NumberFormat doubleFormatter = NumberFormat.getFormat("0.000");

    // context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;

    // connector to the power service
    PowerSvcConnector powerSvcConnector = new PowerSvcConnector();
    // connector to the power service
    ChartSvcConnector chartSvcConnector = new ChartSvcConnector();
    // connector to the file service
    FileSvcConnector fileSvcConnector = new FileSvcConnector();

    // wait dialog
    protected DialogBox waitDialog;

    // google visualization api data table to hold results
//    protected DataTable resultsData = null; 
    // Smart GWT grid to hold the results
    protected ListGrid resultsGrid = new ListGrid();

    // tabular display of results
    protected VerticalPanel resultsTablePanel = new VerticalPanel();
//    protected Table resultsTable = new Table(resultsData, null);

    // curve display
    protected VerticalPanel resultsCurvePanel = new VerticalPanel();

    // error display
    protected VerticalPanel errorPanel = new VerticalPanel();
    protected HTML errorHTML = new HTML();
    // I tried to build the curves with Google Chart Api, but the scatter chart
    // didn't have enough control over line types, etc.  Thus, I rolled my own
    // restlet on top of JFreeChart.  Images are retrieved via GET
    protected Image powerCurveImage = new Image();
    //	protected Image legendImage = new Image();

    private class PowerRecord extends ListGridRecord {
        public PowerRecord(PowerResult result) {
            setAttribute(GlimmpseConstants.COLUMN_NAME_ACTUAL_POWER, 
                    doubleFormatter.format(result.getActualPower()));  
            setAttribute(GlimmpseConstants.COLUMN_NAME_ALPHA, 
                    result.getAlpha().getAlphaValue());  
            setAttribute(GlimmpseConstants.COLUMN_NAME_SAMPLE_SIZE, 
                    result.getTotalSampleSize());  
            setAttribute(GlimmpseConstants.COLUMN_NAME_NOMINAL_POWER, 
                    doubleFormatter.format(result.getNominalPower().getValue()));  
            setAttribute(GlimmpseConstants.COLUMN_NAME_TEST, 
                    formatTestName(result.getTest()));  
            setAttribute(GlimmpseConstants.COLUMN_NAME_BETA_SCALE, 
                    result.getBetaScale().getValue());  
            setAttribute(GlimmpseConstants.COLUMN_NAME_SIGMA_SCALE, 
                    result.getSigmaScale().getValue());  
            
            // covariate related data
            setAttribute(GlimmpseConstants.COLUMN_NAME_POWER_METHOD, 
                    formatPowerMethodName(result.getPowerMethod().getPowerMethodEnum()));  
            Quantile quantile = result.getQuantile();
            if (quantile != null) {
                setAttribute(GlimmpseConstants.COLUMN_NAME_QUANTILE, 
                        result.getQuantile());  
            }
                        
            // set confidence intervals if applicable
            ConfidenceInterval ci = result.getConfidenceInterval();
            if (ci != null) {
                setAttribute(GlimmpseConstants.COLUMN_NAME_CI_LOWER, 
                        ci.getLowerLimit());  
                setAttribute(GlimmpseConstants.COLUMN_NAME_CI_UPPER, 
                        ci.getUpperLimit());  
            }
        }
    }
    
    
    /**
     * Constructor.
     * @param context the study design context
     */
    public ResultsDisplayPanel(WizardContext context)
    {
        super(context, GlimmpseWeb.constants.navItemFinish(),
                WizardStepPanelState.NOT_ALLOWED);
        VerticalPanel panel = new VerticalPanel();

        // build the wait dialog
        buildWaitDialog();
        // build the data table 
        buildDataTable();
        // build the display panels
        buildErrorPanel();
        buildCurvePanel();
        buildTablePanel();

        // layout the panel
        panel.add(errorPanel);
        panel.add(resultsCurvePanel);
        panel.add(resultsTablePanel);
        // need the save form to actually appear in the panel
        panel.add(fileSvcConnector);
        panel.add(powerSvcConnector);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);

        // initialize
        initWidget(panel);
    }

    /**
     * Build the error display panel
     */
    private void buildErrorPanel()
    {
        errorPanel.add(errorHTML);
        errorPanel.setVisible(false);
    }

    /**
     * Build the panel containing the power curve display
     */
    private void buildCurvePanel()
    {
        HTML header = new HTML(GlimmpseWeb.constants.resultsPowerCurveLabel());
        HTML description = new HTML("");

        // add load callbacks
        powerCurveImage.addErrorHandler(new ErrorHandler() {

            @Override
            public void onError(ErrorEvent event)
            {
                hideWorkingDialog();
            }
        });
        powerCurveImage.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event)
            {
                hideWorkingDialog();
            }
        });

        // layout the image / legend
        Grid grid = new Grid(1,2);
        grid.setWidget(0,0,powerCurveImage);
        //		grid.setWidget(0,1,legendImage);

        // layout the sub panel
        resultsCurvePanel.add(header);
        resultsCurvePanel.add(description);
        resultsCurvePanel.add(grid);

        // set style
        //		powerCurveImage.setStyleName(STYLE_POWER_CURVE_FRAME);
        //		legendImage.setStyleName(STYLE_POWER_CURVE_FRAME);
        resultsCurvePanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        resultsCurvePanel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        description.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
    }

    /**
     * Build the panel containing the power results table
     */
    private void buildTablePanel()
    {
        HTML header = new HTML(GlimmpseWeb.constants.resultsPowerResultsLabel());
        HTML description = new HTML("");

        // tools for saving the results as csv and viewing as a matrix
        HorizontalPanel buttonPanel = new HorizontalPanel();
        Button saveButton = new Button(GlimmpseWeb.constants.resultsSaveToCSVLabel(), 
                new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                saveDataToCSV();
            }
        });
        // view matrices button
        Button viewMatricesButton = new Button(GlimmpseWeb.constants.resultsViewMatricesLabel(), 
                new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                viewMatrices();
            }
        });
        buttonPanel.add(saveButton);
        buttonPanel.add(viewMatricesButton);

        // layout the sub panel
        resultsTablePanel.add(header);
        resultsTablePanel.add(description);
        resultsTablePanel.add(resultsGrid);
        resultsTablePanel.add(buttonPanel);
        // set style
        saveButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
        saveButton.addStyleDependentName(STYLE_SEPARATOR);
        viewMatricesButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
        //		viewMatrixButton.setStyleName(STYLE_RESULT_BUTTON);
        resultsTablePanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        resultsTablePanel.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        description.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
    }

    @Override
    public void reset()
    {
        resultsGrid.selectAllRecords();
        resultsGrid.removeSelectedData();
//        resultsData.removeRows(0, resultsData.getNumberOfRows());
        resultsTablePanel.setVisible(false);
        resultsCurvePanel.setVisible(false);
        errorPanel.setVisible(false);
    }

    @Override
    public void onEnter()
    {
        reset();
        sendPowerRequest();
    }

    private void buildDataTable()
    {
        // set up the columns in the data table
        resultsGrid.setWidth(660);  
        resultsGrid.setHeight(400);
        resultsGrid.setUseAllDataSourceFields(true);  
        resultsGrid.setAutoFitFieldWidths(true);
        resultsGrid.setAutoFitWidthApproach(AutoFitWidthApproach.BOTH);
        // power
        ListGridField powerField = 
            createListGridField(GlimmpseConstants.COLUMN_NAME_ACTUAL_POWER, 
                "Power",50);
        // confidence interval upper
        ListGridField powerUpperField = 
            createListGridField(GlimmpseConstants.COLUMN_NAME_CI_UPPER, 
                "CI Upper",50);
        // confidence interval lower
        ListGridField powerLowerField = 
            createListGridField(GlimmpseConstants.COLUMN_NAME_CI_LOWER, 
                "CI Lower",50);
        // sample size
        ListGridField totalNField = 
            createListGridField(GlimmpseConstants.COLUMN_NAME_SAMPLE_SIZE, 
                "Total Sample Size",50);
        // nominal power
        ListGridField nominalPowerField = 
            createListGridField(GlimmpseConstants.COLUMN_NAME_NOMINAL_POWER, 
                "Target Power",50);
        // test
        ListGridField testField = createListGridField(GlimmpseConstants.COLUMN_NAME_TEST, "Test",50);
        // type I error
        ListGridField alphaField = createListGridField(GlimmpseConstants.COLUMN_NAME_ALPHA, "Type I Error Rate",50);
        // beta scale
        ListGridField betaScaleField = createListGridField(GlimmpseConstants.COLUMN_NAME_BETA_SCALE, 
                "Means Scale Factor",50);
        // sigma scale
        ListGridField sigmaScaleField = createListGridField(GlimmpseConstants.COLUMN_NAME_SIGMA_SCALE, 
                "Variability Scale Factor",50);
        // power method
        ListGridField powerMethodField = createListGridField(GlimmpseConstants.COLUMN_NAME_POWER_METHOD, 
                "Calculation Method",50);
        ListGridField quantileField = createListGridField(GlimmpseConstants.COLUMN_NAME_QUANTILE, 
                "Quantile",50);
        
        resultsGrid.setFields(powerField, powerUpperField, powerLowerField,
                totalNField,nominalPowerField,testField,alphaField,betaScaleField,
                sigmaScaleField,powerMethodField,quantileField); 
        
        // default, these fields are hidden
        resultsGrid.hideField(GlimmpseConstants.COLUMN_NAME_CI_LOWER);
        resultsGrid.hideField(GlimmpseConstants.COLUMN_NAME_CI_UPPER);
        resultsGrid.hideField(GlimmpseConstants.COLUMN_NAME_POWER_METHOD);
        resultsGrid.hideField(GlimmpseConstants.COLUMN_NAME_QUANTILE);
    }

    private ListGridField createListGridField(String name, String label,
            int width) {
        ListGridField field = new ListGridField(name, label);
        field.setCanDragResize(false);
        field.setCanFreeze(false);
        field.setCanGroupBy(false);
        return field;
    }
    
    private void buildWaitDialog()
    {
        waitDialog = new DialogBox();
        waitDialog.setGlassEnabled(true);
        HTML text = new HTML("Processing, Please Wait...");
        text.setStyleName("waitDialogText");
        waitDialog.setStyleName("waitDialog");
        waitDialog.setWidget(text);
    }

    private void showWorkingDialog()
    {
        waitDialog.center();
    }

    private void hideWorkingDialog()
    {
        waitDialog.hide();
    }

    private void showError(String message)
    {
        errorHTML.setHTML(message);
        errorPanel.setVisible(true);
        hideWorkingDialog();
    }

    private void showResults(List<PowerResult> results)
    {
        if (results != null) {
            for(PowerResult result: results) {
                
                resultsGrid.addData(new PowerRecord(result));
            }

//            resultsTable.draw(resultsData);
            resultsTablePanel.setVisible(true);     
        }
    }

    private void showCurveResults(List<PowerResult> resultList)
    {
        if (resultList != null && resultList.size() > 0 &&
                studyDesignContext.getStudyDesign().getPowerCurveDescriptions() != null) {
            // submit the result to the chart service
            String queryStr = chartSvcConnector.buildQueryString(resultList, 
                    studyDesignContext.getStudyDesign().getPowerCurveDescriptions());
            powerCurveImage.setUrl(chartSvcConnector.buildScatterURL(queryStr));
            //	        legendImage.setUrl(chartSvcConnector.buildLegendURL(queryStr));
            resultsCurvePanel.setVisible(true);
        }
    }

    private String formatPowerMethodName(PowerMethodEnum name)
    {
        return name.toString(); // TODO
    }

    private String formatTestName(StatisticalTest name)
    {
        return name.getType().toString(); // TODO
    }

    private void sendPowerRequest()
    {
        showWorkingDialog();
        StudyDesign studyDesign = studyDesignContext.getStudyDesign();

        // send an ajax request to calculate power
        try {
            RequestCallback callback = new RequestCallback() {

                @Override
                public void onResponseReceived(Request request, Response response) {
                    hideWorkingDialog();
                    if (response.getStatusCode() == Response.SC_OK) {
                        
                        String resultsJSON = response.getText();
                        
                        List<PowerResult> results = powerSvcConnector.parsePowerResultList(resultsJSON);
                        if (results != null && results.size() > 0) {
                            showResults(results);
                            showCurveResults(results);
                        } else {
                            showError("0 results");
                        }
                    } else {
                        showError(response.getText());
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    hideWorkingDialog();
                    showError(exception.getMessage());
                }
            };
            switch(studyDesign.getSolutionTypeEnum()) {
            case POWER:
                powerSvcConnector.getPower(studyDesign, callback);
                break;
            case SAMPLE_SIZE:
                powerSvcConnector.getSampleSize(studyDesign, callback);
                break;
            }
        } catch (Exception e) {

        }

    }

    /**
     * Issue a call to the file service to save the results to a csv file
     */
    private void saveDataToCSV() {
//        fileSvcConnector.saveDataTableAsCSV(resultsData, null);
    }
    
    /**
     * Submit a form to display matrices in a separate window
     */
    private void viewMatrices() {
        powerSvcConnector.getMatricesAsHTML(studyDesignContext.getStudyDesign());
    }

    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) {
        StudyDesignChangeEvent changeEvent = (StudyDesignChangeEvent) e;
        switch (changeEvent.getType())
        {
        case CONFIDENCE_INTERVAL:
            if (studyDesignContext.getStudyDesign().getConfidenceIntervalDescriptions() != null) {
                resultsGrid.hideField(GlimmpseConstants.COLUMN_NAME_CI_LOWER);
                resultsGrid.hideField(GlimmpseConstants.COLUMN_NAME_CI_UPPER);
            } else {
                resultsGrid.showField(GlimmpseConstants.COLUMN_NAME_CI_LOWER);
                resultsGrid.showField(GlimmpseConstants.COLUMN_NAME_CI_UPPER);
            }
            break;
        case COVARIATE:
            if (studyDesignContext.getStudyDesign().isGaussianCovariate()) {
                resultsGrid.hideField(GlimmpseConstants.COLUMN_NAME_POWER_METHOD);
                resultsGrid.hideField(GlimmpseConstants.COLUMN_NAME_QUANTILE);
            } else {
                resultsGrid.showField(GlimmpseConstants.COLUMN_NAME_POWER_METHOD);
                resultsGrid.showField(GlimmpseConstants.COLUMN_NAME_QUANTILE);
            }
            break;
        }  
    }

    @Override
    public void onWizardContextLoad() {
        // no action required

    }
}
