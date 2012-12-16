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

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.shared.RowColumnTextBox;
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
    protected VerticalPanel rmPanel = new VerticalPanel();
    protected HTML rmInstructions = 
            new HTML(GlimmpseWeb.constants.randomCovariateCovarianceRepeatedMeasuresInstructions());
    protected FlexTable repeatedMeasuresTable = new FlexTable();
    // check box to allow users to enter one correlation value and share it across all
    // repeated measures
    CheckBox sameCorrelationCheckBox = new CheckBox(
            GlimmpseWeb.constants.randomCovariateCovarianceSameCorrelationInstructions());

    // counts of responses, repeated measures
    protected boolean hasCovariate = false;

    // error message
    protected HTML stdDevErrorHTML = new HTML();
    protected HTML correlationErrorHTML = new HTML(); 

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

        // add validation to standard deviation box
        standardDeviationTextBox.addChangeHandler(new ChangeHandler(){ 
            @Override
            public void onChange(ChangeEvent event)
            {
                TextBox tb = (TextBox)event.getSource();
                double value = 1;
                try
                {
                    value = TextValidation.parseDouble(tb.getValue(), 0, true);
                    TextValidation.displayOkay(stdDevErrorHTML, "");
                }
                catch (Exception e)
                {
                    TextValidation.displayError(stdDevErrorHTML,
                            GlimmpseWeb.constants.errorInvalidStandardDeviation());
                    tb.setText("1");
                }
                notifySigmaG(value);
                checkComplete();
            }
        });

        // add change handler for using the same correlation for all repeated measures
        sameCorrelationCheckBox.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event)
            {
                CheckBox cb = (CheckBox)event.getSource();
                if (cb.getValue()) {
                    setEqualCorrelationForRepeatedMeasures();
                } else {
                    enableUnequalCorrelationForRepeatedMeasures();
                }
            }
        });

        // layout the repeated measures panel
        rmPanel.add(rmInstructions);
        rmPanel.add(repeatedMeasuresTable);
        rmPanel.add(sameCorrelationCheckBox);
        rmPanel.setVisible(false);

        // layout the overall panel
        verticalPanel.add(header);
        verticalPanel.add(description);
        verticalPanel.add(new HTML(
                GlimmpseWeb.constants.randomCovariateCovarianceStandardDeviationInstructions()));
        verticalPanel.add(standardDeviationTextBox);
        verticalPanel.add(stdDevErrorHTML);
        verticalPanel.add( new HTML(
                GlimmpseWeb.constants.randomCovariateCovarianceCorrelationInstructions()));
        verticalPanel.add(sigmaYGTable);
        verticalPanel.add(correlationErrorHTML);
        verticalPanel.add(rmPanel);

        // add style
        verticalPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        sameCorrelationCheckBox.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        sigmaYGTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE);
        stdDevErrorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        correlationErrorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);

        initWidget(verticalPanel);

    }

    /**
     * Clear the text boxes, flex table of correlations, etc.
     */
    @Override
    public void reset() 
    {
        hasCovariate = false;
        sigmaYGTable.removeAllRows();
        repeatedMeasuresTable.removeAllRows();
        currentRowOffset = 0;
        sameCorrelationCheckBox.setValue(false);
        changeState(WizardStepPanelState.SKIPPED);
    }

    /**
     * Copy the currently displayed values across all repeated measurements
     */
    private void setEqualCorrelationForRepeatedMeasures() {	   
        int totalResponseVariables = studyDesignContext.getValidResponseVariableCount();
        int totalResponses = studyDesignContext.getValidTotalResponsesCount();
        // populate the entire sigmaYG matrix with the appropriate values
        if (totalResponseVariables > 0) {
            for(int row = 0; row < totalResponses; row += totalResponseVariables) {
                if (row != currentRowOffset) {
                    for(int responseIdx = 0; responseIdx < totalResponseVariables; responseIdx++) {
                        studyDesignContext.setCovariateOutcomesCovarianceValue(this, 
                                row+responseIdx, 0, 
                                studyDesignContext.getCovariateOutcomesCovarianceValue(
                                        currentRowOffset+responseIdx, 0));
//                                sigmaYGData[row+responseIdx][0] = sigmaYGData[currentRowOffset+responseIdx][0];
                    }
                }
            }
            // now reset the view to the first time point and disable the repeated measures drop down
            for(int i = 0; i < repeatedMeasuresTable.getRowCount(); i++) {
                ListBox lb = 
                        (ListBox) repeatedMeasuresTable.getWidget(i, LISTBOX_COLUMN);
                lb.setSelectedIndex(0);
                lb.setEnabled(false);
            }
            updateMatrixView();
        }
    }

    /**
     * Enable the repeated measures dropdown lists
     */
    private void enableUnequalCorrelationForRepeatedMeasures() {
        // reenable the repeated measures dropdowns
        for(int i = 0; i < repeatedMeasuresTable.getRowCount(); i++) {
            ListBox lb = 
                    (ListBox) repeatedMeasuresTable.getWidget(i, LISTBOX_COLUMN);
            lb.setEnabled(true);
        }
    }

    /**
     * Load the repeated measures information from the context
     */
    private void loadRepeatedMeasuresFromContext() {
        // clear the repeated measures table
        repeatedMeasuresTable.removeAllRows();
        rmPanel.setVisible(false);

        List<RepeatedMeasuresNode> rmNodeList = 
                studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
        if (rmNodeList != null && rmNodeList.size() > 0) {
            int totalResponseVariables = 
                    studyDesignContext.getValidResponseVariableCount();
            int totalRepeatedMeasuresCombinations = 1;
            // calculate the total repeated measures combinations
            for(RepeatedMeasuresNode rmNode: rmNodeList) {
                if (rmNode.getDimension() != null &&
                        !rmNode.getDimension().isEmpty() &&
                        rmNode.getNumberOfMeasurements() != null &&
                        rmNode.getNumberOfMeasurements() > 1) {
                    totalRepeatedMeasuresCombinations *= rmNode.getNumberOfMeasurements();
                }
            }
            int totalResponses = 0;
            if (totalRepeatedMeasuresCombinations > 0 && 
                    totalResponseVariables > 0) {
                totalResponses = totalResponseVariables * totalRepeatedMeasuresCombinations;
            } else if (totalRepeatedMeasuresCombinations > 0) {
                totalResponses = totalRepeatedMeasuresCombinations;
            } else {
                totalResponses = totalResponseVariables;
            }

            /* create dropdowns displaying possible values for repeated measures.
             *  The string displays the spacing value for the dimension of repeated
             *  measures and the value is the column offset corresponding to that 
             *  value
             */
            if (totalRepeatedMeasuresCombinations > 0) {
                int row = 0;
                int offset = (totalResponses > 0 ? totalResponses : 1);
                for(RepeatedMeasuresNode rmNode: rmNodeList) {
                    if (rmNode.getDimension() != null &&
                            !rmNode.getDimension().isEmpty() &&
                            rmNode.getNumberOfMeasurements() != null &&
                            rmNode.getNumberOfMeasurements() > 1) {
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
                                lb.addItem(Integer.toString(i), Integer.toString((i-1)*offset));
                            }
                        }
                        repeatedMeasuresTable.setWidget(row, LISTBOX_COLUMN, lb);
                        row++;
                    }
                }

            }
            rmPanel.setVisible(true);
        } 
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
            double value = studyDesignContext.getCovariateOutcomesCovarianceValue(
                    row-1+currentRowOffset, 0);
            if (!Double.isNaN(value)) {
                tb.setText(Double.toString(value));
            }
        }
    }

    /**
     * Load the responses from the context
     */
    private void loadResponsesFromContext() {
        // clear the data from the screen
        sigmaYGTable.removeAllRows();
        // add the header widgets
        sigmaYGTable.setWidget(0, 0, outcomesHTML);
        sigmaYGTable.setWidget(0, 1, correlationHTML);
        sigmaYGTable.getRowFormatter().setStyleName(0, 
                GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_HEADER);
        // add a row for each response variable
        List<ResponseNode> outcomeList = 
                studyDesignContext.getStudyDesign().getResponseList();
        if (outcomeList != null && outcomeList.size() > 0) {
            // load the display
            int row = 1;
            for(ResponseNode outcome: outcomeList) {
                sigmaYGTable.getRowFormatter().setStyleName(row, 
                        GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
                sigmaYGTable.setWidget(row, 0, new HTML(outcome.getName()));
                RowColumnTextBox tb = new RowColumnTextBox(row-1, 0);
                tb.addChangeHandler(this);
                sigmaYGTable.setWidget(row, 1, tb);
                row++;
            }
            
            // if repeated measures are present, update the column offsets
            if (repeatedMeasuresTable.getRowCount() > 0) {
                int offset = studyDesignContext.getValidTotalResponsesCount();
                for(row = 0; row < repeatedMeasuresTable.getRowCount(); row++) {
                    ListBox lb = (ListBox) repeatedMeasuresTable.getWidget(row, LISTBOX_COLUMN);
                    offset /= lb.getItemCount();
                    for(int i = 0; i < lb.getItemCount(); i++) {
                        lb.setValue(i, Integer.toString(i * offset));
                    }
                }
            }
        }  
        checkComplete();
    }



//    /**
//     * Store the sigma G and sigma YG covariance matrices
//     * in the context.
//     */
//    public  void onExit() 
//    {
//        // store the 1x1 covariance for the Gaussian covariate
//        NamedMatrix sigmaCovariate = null;
//        if (!standardDeviationTextBox.getValue().isEmpty()) {
//            double[][] sigmaCovariateData = new double[1][1];
//            double value = Double.parseDouble(standardDeviationTextBox.getValue());
//            sigmaCovariateData[0][0] = value*value;
//
//            sigmaCovariate = new NamedMatrix();
//            sigmaCovariate.setColumns(1);
//            sigmaCovariate.setRows(1);
//            sigmaCovariate.setDataFromArray(sigmaCovariateData);
//            sigmaCovariate.setName(GlimmpseWeb.constants.MATRIX_SIGMA_COVARIATE);
//        }
//        studyDesignContext.setSigmaCovariate(this, sigmaCovariate);
//
//        // store the px1 covariance for the Gaussian covariate with the outcomes
//        NamedMatrix sigmaYG = null;
//        if (sigmaYGData != null) {
//            sigmaYG = new NamedMatrix();
//            sigmaYG.setColumns(sigmaYGColumns);
//            sigmaYG.setRows(sigmaYGRows);
//            sigmaYG.setDataFromArray(sigmaYGData);
//            sigmaYG.setName(GlimmpseConstants.MATRIX_SIGMA_OUTCOME_COVARIATE);
//        }
//        studyDesignContext.setSigmaOutcomesCovariate(this, sigmaYG);
//    }

    /**
     * Load the data from the context regarding the sigmaG and sigmaYG matrices
     */
    private void loadMatricesFromContext() {
        // load the data for the sigma G matrix
        NamedMatrix sigmaGMatrix = 
                studyDesignContext.getStudyDesign().getNamedMatrix(
                        GlimmpseConstants.MATRIX_SIGMA_COVARIATE);
        if (sigmaGMatrix != null && sigmaGMatrix.getData() != null) {
            double[][] data = sigmaGMatrix.getData().getData();
            if (data != null) {
                standardDeviationTextBox.setText(Double.toString(data[0][0]));
            }
        }

        // load the data for the sigma YG matrix
        updateMatrixView();
    }

    /**
     * Handle changes in responses and repeated measures.
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) {
        switch(((StudyDesignChangeEvent) e).getType()) {
        case COVARIATE:
            hasCovariate = studyDesignContext.getStudyDesign().isGaussianCovariate();
            break;
        case RESPONSES_LIST:
            loadResponsesFromContext();
            loadMatricesFromContext();
            break;
        case REPEATED_MEASURES:
            loadRepeatedMeasuresFromContext();
            loadMatricesFromContext();
            break;
        case COVARIATE_COVARIANCE:
            if (e.getSource() != this) {
                loadMatricesFromContext();
            }
            break;
        }
        checkComplete();
    }

    /**
     * Indicates if the screen is complete
     */
    public void checkComplete() {
        if (hasCovariate) {
            if (sigmaYGTable.getRowCount() > 1) {
                if (!standardDeviationTextBox.getValue().isEmpty() &&
                        studyDesignContext.covariateCovarianceIsValid() &&
                        studyDesignContext.covariateOutcomesCovarianceIsValid()) {
                    changeState(WizardStepPanelState.COMPLETE);
                } else {
                    changeState(WizardStepPanelState.INCOMPLETE);
                }
            } else {
                changeState(WizardStepPanelState.NOT_ALLOWED);
            }
        } else {
            changeState(WizardStepPanelState.SKIPPED);
        }
    }

    /**
     * Load covariance data from the context
     */
    @Override
    public void onWizardContextLoad() {
        reset();
        hasCovariate = studyDesignContext.getStudyDesign().isGaussianCovariate();
        loadResponsesFromContext();
        loadRepeatedMeasuresFromContext();
        loadMatricesFromContext();
        checkComplete();
    }

    /**
     * Textbox input validation and check complete
     */
    @Override
    public void onChange(ChangeEvent event)
    {
        RowColumnTextBox tb = (RowColumnTextBox) event.getSource();
        double value = 0;
        try
        {
            value = TextValidation.parseDouble(tb.getText(),-1.0,1.0,true);
            TextValidation.displayOkay(correlationErrorHTML, "");
        }
        catch (NumberFormatException nfe)
        {
            TextValidation.displayError(correlationErrorHTML, 
                    GlimmpseWeb.constants.errorInvalidCorrelation());
            tb.setText("0");
        }
        notifySigmaYGValue(tb.row+currentRowOffset, 0, value);
        checkComplete();
    }
    
    /**
     * Store the standard deviation value to the context
     * @param stdDev
     */
    private void notifySigmaG(double stdDev) {
        studyDesignContext.setGaussianCovariateStandardDeviation(this, stdDev);
    }
    
    /**
     * Store the sigmaYG correlation value to the context
     * @param stdDev
     */
    private void notifySigmaYGValue(int row, int column, double correlation) {
        studyDesignContext.setCovariateOutcomesCovarianceValue(
                this, row, column, correlation);
    }
}
