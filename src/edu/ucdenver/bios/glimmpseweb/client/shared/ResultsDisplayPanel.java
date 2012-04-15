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

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import edu.ucdenver.bios.glimmpseweb.client.ChartRequestBuilder;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.connector.PowerSvcConnector;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.ConfidenceInterval;
import edu.ucdenver.bios.webservice.common.domain.PowerResult;
import edu.ucdenver.bios.webservice.common.domain.PowerResultList;
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
    
    private static final String STYLE_RESULT_BUTTON = "resultsPanelButton";
    private static final String STYLE_SEPARATOR = "separator";
    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_CREATED = 201;
    private static final String POWER_URL = "/webapps/power/power";
    private static final String SAMPLE_SIZE_URL = "/webapps/power/samplesize";

    private static final String CHART_INPUT_NAME = "chart";
    private static final String SAVE_INPUT_NAME = "save";
    private static final String FILENAME_INPUT_NAME = "filename";
    private static final String SAVE_CSV_FILENAME = "powerResults.csv";
    private NumberFormat doubleFormatter = NumberFormat.getFormat("0.0000");
    
    // context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    
    // connector to the power service
    PowerSvcConnector powerSvcConnector = new PowerSvcConnector();

	// wait dialog
	protected DialogBox waitDialog;

	// google visualization api data table to hold results
	protected DataTable resultsData; 

	// tabular display of results
	protected VerticalPanel resultsTablePanel = new VerticalPanel();
	protected Table resultsTable = new Table(resultsData, null);

	// curve display
	protected VerticalPanel resultsCurvePanel = new VerticalPanel();
	
	// error display
	protected VerticalPanel errorPanel = new VerticalPanel();
	protected HTML errorHTML = new HTML();
	// I tried to build the curves with Google Chart Api, but the scatter chart
	// didn't have enough control over line types, etc.  Thus, I rolled my own
	// restlet on top of JFreeChart.  Images are retrieved via GET
	protected Image powerCurveImage = new Image();
	protected Image legendImage = new Image();
	// matrix popup panel - allows users to view the actual matrices produced for the calculations
	// and hey, it sure is nice for debugging
	protected PopupPanel matrixPopup = new PopupPanel();
	protected MatrixDisplayPanel matrixDisplayPanel = new MatrixDisplayPanel();
	protected Button showMatrixPopupButton = new Button("View Matrices used for these results", 
			new ClickHandler() {
		public void onClick(ClickEvent event)
		{
			matrixPopup.center();
		}
	});

	// blank target window - for saving images
	protected FormPanel saveForm = new FormPanel("_blank");
	protected Hidden saveEntityBodyHidden = new Hidden(CHART_INPUT_NAME);
	protected Hidden saveFilenameHidden = new Hidden(FILENAME_INPUT_NAME);
	protected Hidden saveHidden = new Hidden(SAVE_INPUT_NAME);
	// options for display of data
	protected boolean showCurve = false;
	protected ChartRequestBuilder chartRequestBuilder = null;

	protected boolean showCI = false;
	
	public ResultsDisplayPanel(WizardContext context)
	{
		super(context, "Results", // TODO: GlimmpseWeb.constants.navItemFinish(),
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
		buildMatrixPopup();
		
		// layout the panel
		panel.add(errorPanel);
		panel.add(resultsCurvePanel);
		panel.add(resultsTablePanel);

		// set style
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);

		// initialize
		initWidget(panel);
	}

	private void buildErrorPanel()
	{
		errorPanel.add(errorHTML);
		errorPanel.setVisible(false);
	}
	
	private void buildMatrixPopup()
	{
		VerticalPanel panel = new VerticalPanel();
		panel.add(matrixDisplayPanel);
		panel.add(new Button("Close", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				matrixPopup.hide();
			}
		}));
		matrixPopup.add(panel);
	}
	
	private void buildCurvePanel()
	{
    	HTML header = new HTML("Power Curve");
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
    	// legend images
    	legendImage.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event)
			{
				hideWorkingDialog();
			}
    	});
    	legendImage.addErrorHandler(new ErrorHandler() {

			@Override
			public void onError(ErrorEvent event)
			{
				hideWorkingDialog();
			}
    	});
		// layout the image / legend
		Grid grid = new Grid(1,2);
		grid.setWidget(0,0,powerCurveImage);
		grid.setWidget(0,1,legendImage);
				
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
	
	private void buildTablePanel()
	{
    	HTML header = new HTML("Power Results");
    	HTML description = new HTML("");
    	
		// tools for saving the results as csv and viewing as a matrix
    	HorizontalPanel panel = new HorizontalPanel();
    	Button saveButton = new Button("foo", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				saveTableData();
			}
    	});
    	Button viewMatrixButton = new Button("bar", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				matrixPopup.center();
			}
    	});
    	panel.add(saveButton);
    	panel.add(viewMatrixButton);
    	// layout the sub panel
    	resultsTablePanel.add(header);
    	resultsTablePanel.add(description);
    	resultsTablePanel.add(resultsTable);
    	resultsTablePanel.add(panel);
        // set style
		saveButton.setStyleName(STYLE_RESULT_BUTTON);
		saveButton.addStyleDependentName(STYLE_SEPARATOR);
		viewMatrixButton.setStyleName(STYLE_RESULT_BUTTON);
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
		matrixDisplayPanel.reset();
		resultsData.removeRows(0, resultsData.getNumberOfRows());
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
		resultsData = DataTable.create();
		resultsData.addColumn(ColumnType.STRING, "Test", GlimmpseConstants.COLUMN_NAME_TEST);
		resultsData.addColumn(ColumnType.NUMBER, "Actual Power", 
				GlimmpseConstants.COLUMN_NAME_ACTUAL_POWER);
		resultsData.addColumn(ColumnType.NUMBER, "Total Sample Size", 
				GlimmpseConstants.COLUMN_NAME_SAMPLE_SIZE);
		resultsData.addColumn(ColumnType.NUMBER, "Beta Scale", 
				GlimmpseConstants.COLUMN_NAME_BETA_SCALE);
		resultsData.addColumn(ColumnType.NUMBER, "Sigma Scale", 
				GlimmpseConstants.COLUMN_NAME_SIGMA_SCALE);
		resultsData.addColumn(ColumnType.NUMBER, "Alpha", 
				GlimmpseConstants.COLUMN_NAME_ALPHA);
		resultsData.addColumn(ColumnType.NUMBER, "Nominal Power", 
				GlimmpseConstants.COLUMN_NAME_NOMINAL_POWER);
		resultsData.addColumn(ColumnType.STRING, "Power Method", 
				GlimmpseConstants.COLUMN_NAME_POWER_METHOD);
		resultsData.addColumn(ColumnType.NUMBER, "Quantile", 
				GlimmpseConstants.COLUMN_NAME_QUANTILE);
		resultsData.addColumn(ColumnType.NUMBER, "Power Lower", 
				GlimmpseConstants.COLUMN_NAME_CI_LOWER);
		resultsData.addColumn(ColumnType.NUMBER, "Power Upper", 
				GlimmpseConstants.COLUMN_NAME_CI_UPPER);
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
	    for(PowerResult result: results) {
            // add a blank row to the data table
            int row = resultsData.addRow();
            resultsData.setCell(row, COLUMN_ID_TEST, result.getTest().getType().toString(), 
                    formatTestName(result.getTest()), null);
            resultsData.setCell(row, COLUMN_ID_ACTUAL_POWER, 
                    result.getActualPower(), doubleFormatter.format(result.getActualPower()), null);
            resultsData.setCell(row, COLUMN_ID_TOTAL_SAMPLE_SIZE, 
                    result.getTotalSampleSize(), Integer.toString(result.getTotalSampleSize()), null);
            resultsData.setCell(row, COLUMN_ID_BETA_SCALE, 
                    result.getBetaScale().getValue(), doubleFormatter.format(result.getBetaScale().getValue()), null);
            resultsData.setCell(row, COLUMN_ID_SIGMA_SCALE, 
                    result.getSigmaScale().getValue(), doubleFormatter.format(result.getSigmaScale().getValue()), null);
            resultsData.setCell(row, COLUMN_ID_ALPHA, 
                    result.getAlpha().getAlphaValue(), doubleFormatter.format(result.getAlpha().getAlphaValue()), null);
            resultsData.setCell(row, COLUMN_ID_NOMINAL_POWER, 
                    result.getNominalPower().getValue(), 
                    doubleFormatter.format(result.getNominalPower().getValue()), null);
            resultsData.setCell(row, COLUMN_ID_POWER_METHOD, 
                    result.getPowerMethod().getPowerMethodEnum().toString(), 
                    formatPowerMethodName(result.getPowerMethod().getPowerMethodEnum()), null);
            if (result.getQuantile() != null) {
                resultsData.setCell(row, COLUMN_ID_QUANTILE, 
                        result.getQuantile().getValue(), doubleFormatter.format(result.getQuantile().getValue()), null);
            }
            if (result.getConfidenceInterval() != null) {
                ConfidenceInterval ci = result.getConfidenceInterval();
                resultsData.setCell(row, COLUMN_ID_CI_LOWER, 
                        ci.getLowerLimit(), 
                        doubleFormatter.format(ci.getLowerLimit()), null);
                resultsData.setCell(row, COLUMN_ID_CI_UPPER, 
                        ci.getUpperLimit(), 
                        doubleFormatter.format(ci.getUpperLimit()), null);
            }
	    }
	    
        resultsTable.draw(resultsData);
        resultsTablePanel.setVisible(true);
	    
//		try
//		{      	
//
//			if (chartRequestBuilder != null)
//			{
//				showCurveResults();
//			}
//			else
//			{
//				hideWorkingDialog();
//			}

//		}
//		catch (Exception e)
//		{
//			showError(e.getMessage());
//		}
	}

	private void showCurveResults()
	{
		// submit the result to the chart service
		resultsCurvePanel.setVisible(true);
		chartRequestBuilder.loadData(resultsData);
		powerCurveImage.setUrl(chartRequestBuilder.buildChartRequestURL());
		legendImage.setUrl(chartRequestBuilder.buildLegendRequestURL());
	}

	private String formatPowerMethodName(PowerMethodEnum name)
	{
		return name.toString(); // TODO
	}

	private String formatTestName(StatisticalTest name)
	{
		return name.toString(); // TODO
	}

	private String formatDouble(String valueStr)
	{
		try
		{
			double value = Double.parseDouble(valueStr);
			return doubleFormatter.format(value);
		}
		catch (Exception e)
		{
			return valueStr;
		}
	}

	private void sendPowerRequest()
	{
		showWorkingDialog();
		StudyDesign studyDesign = studyDesignContext.getStudyDesign();
		
		try {
		    powerSvcConnector.getPower(studyDesign, new RequestCallback() {

		        @Override
		        public void onResponseReceived(Request request, Response response) {
		            // TODO Auto-generated method stub
		            hideWorkingDialog();
		            List<PowerResult> results = powerSvcConnector.parsePowerResultList(response.getText());
		            showResults(results);
		        }

		        @Override
		        public void onError(Request request, Throwable exception) {
		            // TODO Auto-generated method stub
		            hideWorkingDialog();
		        }
		    });
		} catch (Exception e) {

		}


//		matrixDisplayPanel.loadFromStudyDesign(studyDesign);
//
//		if (studyDesign.getPowerCurveDescriptions() != null)
//		{
//		    
//		}
	}
	
	/**
	 * Output the data table in CSV format
	 * @return CSV formatted data
	 */
	public String dataTableToCSV()
	{
		StringBuffer buffer = new StringBuffer();
		
		if (resultsData.getNumberOfRows() > 0)
		{
			// add the column headers
			for(int col = 0; col < resultsData.getNumberOfColumns(); col++)
			{
				if (col > 0) buffer.append(",");
				buffer.append(resultsData.getColumnId(col));
			}
			buffer.append("\n");
			// now add the data
			for(int row = 0; row < resultsData.getNumberOfRows(); row++)
			{
				for(int col = 0; col < resultsData.getNumberOfColumns(); col++)
				{
					if (col > 0) buffer.append(",");
					if (resultsData.getColumnType(col) == ColumnType.STRING)
						buffer.append(resultsData.getValueString(row, col));
					else	
						buffer.append(resultsData.getValueDouble(row, col));	
				}
				buffer.append("\n");
			}
		}
		return buffer.toString();
	}
	
	public void saveTableData()
	{
		// submit the result to the file service
//		manager.sendSaveRequest(dataTableToCSV(), SAVE_CSV_FILENAME);
	}
}
