/*
 * User Interface for the GLIMMPSE Software System.  Processes
 * incoming HTTP requests for power, sample size, and detectable
 * difference
 * 
 * Copyright (C) 2011 Regents of the University of Colorado.  
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
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
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
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;
import edu.ucdenver.bios.webservice.common.domain.Spacing;

/**
 * Screen to input the correlation between the gaussian covariate
 * and the outcomes.
 * @author VIJAY AKULA
 * @author Sarah Kreidler
 *
 */
public class GaussianCovariateCovariancePanel extends WizardStepPanel
implements ChangeHandler
{
	// context object
    protected StudyDesignContext studyDesignContext;
    
    // column indices for repeated measures dropdown lists
    protected static final int LABEL_COLUMN = 0;
    protected static final int LISTBOX_COLUMN = 1;
    // flextable index for correlation textboxes
    protected static final int TEXTBOX_COLUMN = 1;
    
    // complete matrix data for covariance of outcomes and Gaussian covariate
    protected double[][] sigmaYGData = null;
    protected int sigmaYGRows = 0;
    protected int sigmaYGColumns = 1;
    protected int currentRowOffset = 0;

    // standard deviation of Gaussian covariate
    protected TextBox standardDeviationTextBox = new TextBox();
    // flex table representing sigma YG
    protected FlexTable sigmaYGTable = new FlexTable();
    // table headers
    protected HTML outcomesHTML = 
            new HTML(GlimmpseWeb.constants.randomCovariateCovarianceOutcomesColumnLabel());
    protected HTML correlationHTML = 
            new HTML(GlimmpseWeb.constants.randomCovariateCovarianceCorrelationColumnLabel());
    // selection of repeated measures
    protected HTML rmInstructions = 
        new HTML(GlimmpseWeb.constants.randomCovariateCovarianceRepeatedMeasuresInstructions());
    protected FlexTable repeatedMeasuresTable = new FlexTable();

    protected boolean hasCovariate = false;
    protected int totalRepeatedMeasures = 0;
    protected int totalRepeatedMeasuresCombinations = 0;
    protected int totalWithinFactorCombinations = 0;
    protected int totalResponseVariables = 0;

    protected HTML errorHTML = new HTML();
    
    /**
     * Constructor
     * @param context study design context object
     */
	public GaussianCovariateCovariancePanel(WizardContext context) 
	{
		super(context, GlimmpseWeb.constants.navItemVariabilityGaussianCovariate(),
		        WizardStepPanelState.SKIPPED);
		studyDesignContext = (StudyDesignContext) context;
		
		VerticalPanel verticalPanel = new VerticalPanel();
		// header text
		HTML header = new HTML(
		        GlimmpseWeb.constants.randomCovariateCovarianceHeader());
		HTML description = new HTML(
		        GlimmpseWeb.constants.randomCovariateCovarianceDescription());
		
		// build panel for standard deviation of Gaussian covariate
		HorizontalPanel stddevPanel = new HorizontalPanel();
		stddevPanel.add( new HTML(
                GlimmpseWeb.constants.randomCovariateCovarianceStandardDeviationInstructions()));
		stddevPanel.add(standardDeviationTextBox);
		
	      // add validation to standard deviation box
        standardDeviationTextBox.addChangeHandler(new ChangeHandler(){ 
            @Override
            public void onChange(ChangeEvent event)
            {
                TextBox tb = (TextBox)event.getSource();
                try
                {
                    String value = tb.getValue();
                    TextValidation.parseDouble(value);
                    TextValidation.displayOkay(errorHTML, "");
                }
                catch (Exception e)
                {
                    TextValidation.displayError(errorHTML,
                            GlimmpseWeb.constants.errorInvalidStandardDeviation());
                }
            }
        });
        
        // check box to allow users to enter one correlation value and share it across all
        // repeated measures
		CheckBox sameCorrelationCheckBox = new CheckBox(
		        GlimmpseWeb.constants.randomCovariateCovarianceSameCorrelationInstructions());
		sameCorrelationCheckBox.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				CheckBox cb = (CheckBox)event.getSource();
				editCorrelationTextBoxes(cb.getValue());
			}
		});

		// layout the overall panel
		verticalPanel.add(header);
		verticalPanel.add(description);
		verticalPanel.add(stddevPanel);
		verticalPanel.add( new HTML(
                GlimmpseWeb.constants.randomCovariateCovarianceCorrelationInstructions()));
		verticalPanel.add(sameCorrelationCheckBox);
		verticalPanel.add(sigmaYGTable);
		verticalPanel.add(errorHTML);
		
		// add style
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		sameCorrelationCheckBox.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);


		initWidget(verticalPanel);

	}

	/**
	 * Clear the text boxes, flex table of correlations, etc.
	 */
	@Override
	public void reset() 
	{
        sigmaYGTable.removeAllRows();
        sigmaYGData = null;
        currentRowOffset = 0;
        changeState(WizardStepPanelState.SKIPPED);
	}
	
	/**
	 * Load the repeated measures information from the context
	 */
	private void loadRepeatedMeasuresFromContext() {
        sigmaYGTable.removeAllRows();
        repeatedMeasuresTable.removeAllRows();
        rmInstructions.setVisible(false);
        repeatedMeasuresTable.setVisible(false);
        totalRepeatedMeasures = 0;
        totalRepeatedMeasuresCombinations = 0;
        totalWithinFactorCombinations = totalResponseVariables;
        List<RepeatedMeasuresNode> rmNodeList = 
            studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();

        if (rmNodeList != null && rmNodeList.size() > 0) {
            // calculate the total repeated measures combinations
            totalRepeatedMeasuresCombinations = 0;
            for(RepeatedMeasuresNode rmNode: rmNodeList) {
                if (rmNode.getDimension() != null &&
                        !rmNode.getDimension().isEmpty() &&
                        rmNode.getNumberOfMeasurements() != null &&
                        rmNode.getNumberOfMeasurements() > 1) {
                    if (totalRepeatedMeasuresCombinations == 0) {
                        totalRepeatedMeasuresCombinations = rmNode.getNumberOfMeasurements();
                    } else {
                        totalRepeatedMeasuresCombinations *= rmNode.getNumberOfMeasurements();
                    }
                }
            }
            if (totalRepeatedMeasuresCombinations > 0 && 
                    totalResponseVariables > 0) {
                totalWithinFactorCombinations *= totalRepeatedMeasuresCombinations;
            } else if (totalRepeatedMeasuresCombinations > 0) {
                totalWithinFactorCombinations = totalRepeatedMeasuresCombinations;
            } else {
                totalWithinFactorCombinations = totalResponseVariables;
            }

            /* create dropdowns displaying possible values for repeated measures.
             *  The string displays the spacing value for the dimension of repeated
             *  measures and the value is the column offset corresponding to that 
             *  value
             */
            if (totalRepeatedMeasuresCombinations > 0) {
                int row = 0;
                int offset = (totalWithinFactorCombinations > 0 ?
                        totalWithinFactorCombinations : 1);
                for(RepeatedMeasuresNode rmNode: rmNodeList) {
                    offset /= rmNode.getNumberOfMeasurements();
                    // add the label
                    repeatedMeasuresTable.setWidget(row, LABEL_COLUMN, 
                            new HTML(rmNode.getDimension()));
                    // add the listbox
                    ListBox lb = new ListBox();
                    lb.addChangeHandler(new ChangeHandler() {
                        @Override
                        public void onChange(ChangeEvent event) {
                            updateMatrixView();
                        }                      
                    });
                    List<Spacing> spacingList = rmNode.getSpacingList();
                    if (spacingList != null) {
                        int index = 0;
                        for(Spacing spacing: spacingList) {
                            lb.addItem(Integer.toString(spacing.getValue()), 
                                    Integer.toString(index*offset));
                            index++;
                        }
                    } else {
                        for(int i = 1; i <= rmNode.getNumberOfMeasurements(); i++) {
                            lb.addItem(Integer.toString(i), Integer.toString(i*offset));
                        }
                    }
                    repeatedMeasuresTable.setWidget(row, LISTBOX_COLUMN, lb);
                    row++;
                }

            }
            rmInstructions.setVisible(true);
            repeatedMeasuresTable.setVisible(true);
        }
        updateMatrixData();
        checkComplete();
	}
	
	/**
	 * Update the correlation text boxes to display the sigma YG
	 * submatrix corresponding to the currently selected 
	 * repeated measures observation episode
	 */
	private void updateMatrixView() {
	    // calculate the new row offset
	    currentRowOffset = 0;
	    for(int i = 0; i < repeatedMeasuresTable.getRowCount(); i++) {
	        ListBox lb = 
	                (ListBox) repeatedMeasuresTable.getWidget(i, LISTBOX_COLUMN);
	        String valueStr = lb.getValue(lb.getSelectedIndex());
	        int value = Integer.parseInt(valueStr);
	        currentRowOffset += value;
	    }

	    // update the values in the textboxes
	    for(int row = 1; row < sigmaYGTable.getRowCount(); row++) {
	        TextBox tb = (TextBox) sigmaYGTable.getWidget(row, TEXTBOX_COLUMN);
	        tb.setText(Double.toString(sigmaYGData[row-1+currentRowOffset][0]));
	    }
	}
    	
	/**
	 * Load the responses from the context
	 */
	private void loadResponsesFromContext() {
        sigmaYGTable.removeAllRows();
        // add the header widgets
        sigmaYGTable.setWidget(0, 0, outcomesHTML);
        sigmaYGTable.setWidget(0, 0, correlationHTML);
        // add a row for each response variable
        totalWithinFactorCombinations = totalRepeatedMeasuresCombinations;
        totalResponseVariables = 0;
        
        // now load the new responses
        List<ResponseNode> outcomeList = studyDesignContext.getStudyDesign().getResponseList();
        if (outcomeList != null && outcomeList.size() > 0) {
            totalResponseVariables = outcomeList.size();
            totalWithinFactorCombinations = totalResponseVariables;
            if (totalRepeatedMeasuresCombinations > 0) {
                totalWithinFactorCombinations *= totalRepeatedMeasuresCombinations;
            }
            // resize the matrix data buffer
            updateMatrixData();
            // load the display
            int row = 1;
            for(ResponseNode outcome: outcomeList) {
                sigmaYGTable.getRowFormatter().setStyleName(row, 
                        GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
                sigmaYGTable.setWidget(row, 1, new HTML(outcome.getName()));
                TextBox tb = new TextBox();
                tb.setText(Double.toString(sigmaYGData[row-1+currentRowOffset][0]));
                tb.addChangeHandler(this);
                sigmaYGTable.setWidget(row, 1, tb);
                row++;
            }
        }  
	}
		
    /**
     * Allocate a new beta matrix
     */
    private void updateMatrixData() {
        sigmaYGData = null;
        if (totalWithinFactorCombinations > 0) {
            sigmaYGData = 
                new double[totalWithinFactorCombinations][1];
            sigmaYGRows = totalWithinFactorCombinations;
            for(int row = 0; row < totalWithinFactorCombinations; row++) {
                sigmaYGData[row][0] = 0;
            }
        }
    }
    
	public void editCorrelationTextBoxes(boolean allowEdit)
	{
//		if(allowEdit)
//		{
//			for(int i = 2; i <= flexTableRows; i++)
//			{
//				TextBox textBox = (TextBox)flexTable.getWidget(i, dataList.size());
//				textBox.setEnabled(false);
//			}
//			
//			TextBox tb = (TextBox)flexTable.getWidget(1, dataList.size());
//			tb.addChangeHandler(new ChangeHandler() 
//			{
//				@Override
//				public void onChange(ChangeEvent event)
//				{
//					try
//					{
//						TextBox t = (TextBox)event.getSource();
//						String value = t.getValue();
//						double d = TextValidation.parseDouble(value, -1.0, 1.0, true);
//						t.setValue(""+d);
//						TextValidation.displayOkay(errorHTMLUP, "");
//						TextValidation.displayOkay(errorHTMLDOWN, "");
//						for(int i = 2; i <= flexTableRows; i ++)
//						{
//							TextBox correlationTb = (TextBox)flexTable.getWidget(i, dataList.size());
//							correlationTb.setValue(""+d);
//						}
//					}
//					catch(Exception e)
//					{
//						TextValidation.displayError(errorHTMLUP, GlimmpseWeb.constants.randomCovariateCovarianceCorrelationValueError());
//						TextValidation.displayError(errorHTMLDOWN, GlimmpseWeb.constants.randomCovariateCovarianceCorrelationValueError());
//					}
//					
//				}
//			});
//		}
//		else
//		{
//			for(int i = 2; i <= flexTableRows; i++)
//			{
//				TextBox textBox = (TextBox)flexTable.getWidget(i, dataList.size());
//				textBox.setEnabled(true);
//			}
//		}
	}
	
	/**
	 * Store the sigma G and sigma YG covariance matrices
	 * in the context.
	 */
	public  void onExit() 
	{
	    // store the 1x1 covariance for the Gaussian covariate
	    NamedMatrix sigmaCovariate = null;
	    if (!standardDeviationTextBox.getValue().isEmpty()) {
	        double[][] sigmaCovariateData = new double[1][1];
	        double value = Double.parseDouble(standardDeviationTextBox.getValue());
	        sigmaCovariateData[0][0] = value*value;
	        
	        sigmaCovariate = new NamedMatrix();
	        sigmaCovariate.setColumns(1);
	        sigmaCovariate.setRows(1);
	        sigmaCovariate.setDataFromArray(sigmaCovariateData);
	        sigmaCovariate.setName(GlimmpseWeb.constants.MATRIX_SIGMA_COVARIATE);
	    }
        studyDesignContext.setSigmaCovariate(this, sigmaCovariate);
        
        // store the px1 covariance for the Gaussian covariate with the outcomes
        NamedMatrix sigmaYG = null;
        if (sigmaYGData != null) {
            sigmaYG = new NamedMatrix();
            sigmaYG.setColumns(sigmaYGColumns);
            sigmaYG.setRows(sigmaYGRows);
            sigmaYG.setDataFromArray(sigmaYGData);
            sigmaYG.setName(GlimmpseWeb.constants.MATRIX_SIGMA_OUTCOME_COVARIATE);
        }
        studyDesignContext.setSigmaOutcomesCovariate(this, sigmaYG);
    }

	private void loadMatricesFromContext() {
	    // load the data for the sigma G matrix
	    NamedMatrix sigmaGMatrix = 
	        studyDesignContext.getStudyDesign().getNamedMatrix(
	                GlimmpseConstants.MATRIX_SIGMA_COVARIATE);
	    if (sigmaGMatrix != null && sigmaGMatrix.getData() != null) {
	        double[][] data = sigmaGMatrix.getData().getData();
	        if (data != null) {
	            standardDeviationTextBox.setText(Double.toString(Math.sqrt(data[0][0])));
	        }
	    }
	    
	    // load the data for the sigma YG matrix
	    NamedMatrix sigmaYGMatrix = 
	        studyDesignContext.getStudyDesign().getNamedMatrix(
	                GlimmpseConstants.MATRIX_SIGMA_OUTCOME);
	    if (sigmaYGMatrix != null && 
	            sigmaYGMatrix.getRows() > 0 &&
	            sigmaYGMatrix.getColumns() > 0 &&
	            sigmaYGMatrix.getData() != null) {
	        sigmaYGData = sigmaYGMatrix.getData().getData();
	        
	    }
	    updateMatrixView();
	}
	
	/**
	 * Handle changes in responses and repeated measures.
	 */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) {
        switch(((StudyDesignChangeEvent) e).getType()) {
        case COVARIATE:
            if (studyDesignContext.getStudyDesign().isGaussianCovariate()) {
                checkComplete();
            } else {
                changeState(WizardStepPanelState.SKIPPED);
            }
            break;
        case RESPONSES_LIST:
            loadResponsesFromContext();
            break;
        case REPEATED_MEASURES:
            loadRepeatedMeasuresFromContext();
            break;
        }
    }

    /**
     * Indicates if the screen is complete
     */
    public void checkComplete() {
        if (!standardDeviationTextBox.getValue().isEmpty() &&
                sigmaYGData != null &&
                sigmaYGRows > 0 &&
                sigmaYGColumns > 0) {
            changeState(WizardStepPanelState.COMPLETE);
        } else {
            changeState(WizardStepPanelState.INCOMPLETE);
        }
    }
    
    /**
     * Load covariance data from the context
     */
    @Override
    public void onWizardContextLoad() {
        loadResponsesFromContext();
        loadRepeatedMeasuresFromContext();
        loadMatricesFromContext();
    }

    /**
     * Check for screen completion when new inputs are received
     */
    @Override
    public void onChange(ChangeEvent event) {
        checkComplete();
    }
}
