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
package edu.ucdenver.bios.glimmpseweb.client.guided;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
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
import edu.ucdenver.bios.glimmpseweb.context.FactorTable;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.Blob2DArray;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;
import edu.ucdenver.bios.webservice.common.domain.Spacing;

/**
 * Entry screen for means by outcome and study subgroup
 */
public class MeanDifferencesPanel extends WizardStepPanel
implements ChangeHandler
{
    // column indices for repeated measures dropdown lists
    protected static final int LABEL_COLUMN = 0;
    protected static final int LISTBOX_COLUMN = 1;


    // context object
    protected StudyDesignContext studyDesignContext;

    // complete matrix data 
    protected double[][] betaFixedData = null;
    protected int currentColumnOffset = 0;

    // flex table of mean values
    protected FlexTable meansTable = new FlexTable();
    // selection of repeated measures
    protected HTML rmInstructions = 
        new HTML(GlimmpseWeb.constants.meanDifferenceRepeatedMeasuresInstructions());
    protected FlexTable repeatedMeasuresTable = new FlexTable();

    protected int betaRows = 0;
    protected int betaColumns = 0;

    protected boolean hasCovariate = false;
    protected int totalBetweenFactors = 0;
    protected int totalBetweenFactorCombinations = 0;
    protected int totalRepeatedMeasures = 0;
    protected int totalRepeatedMeasuresCombinations = 0;
    protected int totalWithinFactorCombinations = 0;
    protected int totalResponseVariables = 0;

    protected HTML errorHTML = new HTML();

    /**
     * textbox with row and column information
     */
    private class RowColumnTextBox extends TextBox {
        private int row = 0;
        private int column = 0;

        public RowColumnTextBox(int row, int column) {
            super();
            this.row = row;
            this.column = column;
        }

        public int getRow() { return row; }
        public int getColumn() { return column; }
    }

    /**
     * Constructor
     * @param context
     */
    public MeanDifferencesPanel(WizardContext context)
    {
        super(context, GlimmpseWeb.constants.navItemMeans(),
                WizardStepPanelState.NOT_ALLOWED);
        studyDesignContext = (StudyDesignContext) context;
        
        VerticalPanel panel = new VerticalPanel();

        HTML header = new HTML(GlimmpseWeb.constants.meanDifferenceTitle());
        HTML description = new HTML(GlimmpseWeb.constants.meanDifferenceDescription());

        panel.add(header);
        panel.add(description);
        panel.add(meansTable);
        panel.add(errorHTML);
        panel.add(rmInstructions);
        panel.add(repeatedMeasuresTable);

        // hide the repeated measures info initially
        rmInstructions.setVisible(false);
        repeatedMeasuresTable.setVisible(false);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        meansTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        initWidget(panel);
    }

    @Override
    public void reset()
    {
        meansTable.removeAllRows();
        betaFixedData = null;
        betaRows = 0;
        betaColumns = 0;
        currentColumnOffset = 0;

        hasCovariate = false;
        totalBetweenFactors = 0;
        totalBetweenFactorCombinations = 0;
        totalRepeatedMeasures = 0;
        totalRepeatedMeasuresCombinations = 0;
        totalWithinFactorCombinations = 0;
        totalResponseVariables = 0;
        
        
        
        changeState(WizardStepPanelState.NOT_ALLOWED);
    }

    /**
     * Load between participant factors from the context
     */
    private void loadBetweenParticipantFactorsFromContext() {
        // remove existing information related to between factors
        if (totalBetweenFactors > 0) {
            // remove all but the header row
            for(int row = meansTable.getRowCount()-1; row >= 1; row--) {
                meansTable.removeRow(row);
            }
            // remove the header widgets for the between subject factors
            for(int col = 0; col < totalBetweenFactors; col++) {
                meansTable.removeCell(0, 0);
            }
        }
        totalBetweenFactors = 0;
        totalBetweenFactorCombinations = 0;
        // load new between participant factor information
        FactorTable participantGroups = studyDesignContext.getParticipantGroups();
        if (participantGroups != null && participantGroups.getNumberOfRows() > 0) {
            totalBetweenFactorCombinations = participantGroups.getNumberOfRows();
            // build the labels for the groups
            List<String> columnLabels = participantGroups.getColumnLabels();
            totalBetweenFactors = columnLabels.size();
            if (columnLabels != null) {
                // relative group size drop down column
                meansTable.getRowFormatter().setStyleName(0, 
                        GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_HEADER);
                // add labels for the between participant factors
                int col = 0;
                for(String label: columnLabels) {
                    if (meansTable.getRowCount() > 0 && meansTable.getCellCount(0) > 0) {
                        meansTable.insertCell(0, col);
                    }
                    meansTable.setWidget(0, col, new HTML(label));
                    col++;
                }
            }

            // now fill the columns
            for(int col = 0; col < participantGroups.getNumberOfColumns(); col++) {
                List<String> column = participantGroups.getColumn(col);
                if (column != null) {
                    int row = 1;
                    for(String value: column) {
                        meansTable.setWidget(row, col, new HTML(value));
                        row++;
                    }
                }
            }

            // lastly, fill in the text boxes
            fillTextBoxes();
            updateMatrixData();
            updateMatrixView();
        }
        checkComplete();
    }

    /**
     * Load within participant factors from the context
     */
    private void loadRepeatedMeasuresFromContext() {
        // now clear the panel
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
                            lb.addItem(Integer.toString(i), Integer.toString((i-1)*offset));
                        }
                    }
                    repeatedMeasuresTable.setWidget(row, LISTBOX_COLUMN, 
                            lb);
                    row++;
                }

            }
            rmInstructions.setVisible(true);
            repeatedMeasuresTable.setVisible(true);
        }
        updateMatrixData();
        updateMatrixView();
        checkComplete();
    }
        
    /**
     * Allocate a new beta matrix
     */
    private void updateMatrixData() {
        betaFixedData = null;
        if (totalWithinFactorCombinations > 0 && totalBetweenFactorCombinations > 0) {
            betaFixedData = 
                new double[totalBetweenFactorCombinations][totalWithinFactorCombinations];
            betaRows = totalBetweenFactorCombinations;
            betaColumns = totalWithinFactorCombinations;
            for(int row = 0; row < totalBetweenFactorCombinations; row++) {
                for(int col = 0; col < totalWithinFactorCombinations; col++) {
                    betaFixedData[row][col] = 0;
                }
            }
        }
    }

    /**
     * When both the repeated measures and response variables have been
     * specified, this function creates the text box input widgets
     */
    private void fillTextBoxes() {
        if (totalBetweenFactors > 0 && totalResponseVariables > 0) {
            for(int row = 1; row < meansTable.getRowCount(); row++) {
                meansTable.getRowFormatter().setStyleName(row, 
                        GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
                for(int col = totalBetweenFactors; col < totalResponseVariables+totalBetweenFactors; col++) {
                    RowColumnTextBox tb = new RowColumnTextBox(row-1, col-totalBetweenFactors);
                    tb.setText("0");
                    tb.addChangeHandler(this);
                    meansTable.setWidget(row, col, tb);
                }
            }
        }
    }
    
    /**
     * Load the beta matrix information from the context
     */
    private void loadMatrixDataFromContext() {
        NamedMatrix betaMatrix = 
            studyDesignContext.getStudyDesign().getNamedMatrix(
                    GlimmpseConstants.MATRIX_BETA);
        if (betaMatrix != null && betaMatrix.getColumns() == totalWithinFactorCombinations &&
                betaMatrix.getRows() == totalBetweenFactorCombinations) {
            Blob2DArray blob = betaMatrix.getData();
            if (blob != null && blob.getData() != null) {
                double[][] betaData = blob.getData();
                for(int row = 0; row < totalBetweenFactorCombinations; row++) {
                    for(int col = 0; col < totalWithinFactorCombinations; col++) {
                        betaFixedData[row][col] = betaData[row][col];
                    }
                }
            }
        }
        updateMatrixView();
        checkComplete();
    }

    /**
     * Load response variable information from the context
     */    
    private void loadResponsesFromContext() {
        // remove existing information about response variables
        if (totalResponseVariables > 0) {
            // remove the columns associated with the responses
            for(int col = totalBetweenFactors + totalResponseVariables-1; 
                col > totalBetweenFactors; col--) {
                for(int row = meansTable.getRowCount()-1; row >= 0; row--) {
                    meansTable.removeCell(row, col);
                }
            }
            // update the offsets in the list boxes
            for(int row = 0; row < repeatedMeasuresTable.getRowCount(); row++) {
                ListBox lb = (ListBox) repeatedMeasuresTable.getWidget(row, LISTBOX_COLUMN);
                for(int i = 0; i < lb.getItemCount(); i++) {
                    int currentValue = Integer.parseInt(lb.getValue(i));
                    currentValue /= totalResponseVariables;
                    lb.setValue(i, Integer.toString(currentValue));
                }
            }
        }
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
            int col = totalBetweenFactors;
            for(ResponseNode outcome: outcomeList) {
                meansTable.setWidget(0, col, new HTML(outcome.getName()));
                col++;
            }
        }

        // if repeated measures are present, update the column offsets
        if (totalResponseVariables > 0) {
            for(int row = 0; row < repeatedMeasuresTable.getRowCount(); row++) {
                ListBox lb = (ListBox) repeatedMeasuresTable.getWidget(row, LISTBOX_COLUMN);
                for(int i = 0; i < lb.getItemCount(); i++) {
                    int currentValue = Integer.parseInt(lb.getValue(i));
                    currentValue *= totalResponseVariables;
                    lb.setValue(i, Integer.toString(currentValue));
                }
            }
        }
        // create text boxes to hold the means
        fillTextBoxes();
        updateMatrixData();
        updateMatrixView();
        checkComplete();
    }

    private void checkComplete() {        
        if (totalBetweenFactors > 0 && totalWithinFactorCombinations > 0) {
            if (totalWithinFactorCombinations == 1) {
                changeState(WizardStepPanelState.COMPLETE);
            } else {
                boolean hasNonZero = false;
                for(int row = 0; row < totalBetweenFactorCombinations; row++) {
                    for(int col = 0; col < totalWithinFactorCombinations; col++) {
                        if (betaFixedData[row][col] != 0) {
                            hasNonZero = true;
                            break;
                        }
                    }
                    if (hasNonZero) {
                        break;
                    }
                }
                if (hasNonZero) {
                    changeState(WizardStepPanelState.COMPLETE);
                } else {
                    changeState(WizardStepPanelState.INCOMPLETE);
                }
            }
        } else {
            changeState(WizardStepPanelState.NOT_ALLOWED);
        }
    }

    /**
     * Exit the panel and set the beta matrix to the context
     */
    public void onExit()
    {
        // random portion of beta matrix
        NamedMatrix betaRandom = null;

        // fixed portion of beta matrix
        NamedMatrix betaFixed = new NamedMatrix();
        betaFixed.setName(GlimmpseConstants.MATRIX_BETA);
        betaFixed.setRows(betaRows);
        betaFixed.setColumns(betaColumns);
        betaFixed.setDataFromArray(betaFixedData);

        if (hasCovariate) {
            betaRandom = new NamedMatrix();
            betaRandom.setName(GlimmpseConstants.MATRIX_BETA_RANDOM);
            betaRandom.setRows(1);
            betaRandom.setColumns(betaColumns);
            double[][] betaRandomData = new double[1][betaColumns];
            for(int c = 0; c < betaColumns; c++) { betaRandomData[0][c] = 1; }
            betaRandom.setDataFromArray(betaRandomData);
        }

        studyDesignContext.setBeta(this, betaFixed, betaRandom);
    }

    /**
     * Update the correlation text boxes to display the sigma YG
     * submatrix corresponding to the currently selected 
     * repeated measures observation episode
     */
    private void updateMatrixView() {
        updateColumnOffset();
        // update the values in the textboxes
        for(int row = 1; row < meansTable.getRowCount(); row++) {
            for(int dataCol = 0, col = totalBetweenFactors; 
            col < totalResponseVariables+totalBetweenFactors; dataCol++, col++) {
                RowColumnTextBox tb = (RowColumnTextBox) meansTable.getWidget(row, col);
                tb.setText(Double.toString(betaFixedData[row-1][dataCol+currentColumnOffset]));
            }
        }
    }
    
    /**
     * Calculate the new column offset for the selected
     * repeated measures.
     */
    private void updateColumnOffset() {
        // calculate the new column offset
        currentColumnOffset = 0;
        for(int i = 0; i < repeatedMeasuresTable.getRowCount(); i++) {
            ListBox lb = (ListBox) repeatedMeasuresTable.getWidget(i, LISTBOX_COLUMN);
            String valueStr = lb.getValue(lb.getSelectedIndex());
            int value = Integer.parseInt(valueStr);
            currentColumnOffset += value;
        }
    }

    /**
     * Textbox input validation
     */
    @Override
    public void onChange(ChangeEvent event)
    {
        RowColumnTextBox tb = (RowColumnTextBox) event.getSource();
        try
        {
            double value = Double.parseDouble(tb.getText());
            TextValidation.displayOkay(errorHTML, "");
            betaFixedData[tb.getRow()][tb.getColumn() + currentColumnOffset] = value;
        }
        catch (NumberFormatException nfe)
        {
            TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidNumber());
            tb.setText("0");
        }
        checkComplete();
    }

    /**
     * Respond to a change in the context object
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) 
    {
        switch(((StudyDesignChangeEvent) e).getType()) {
        case BETWEEN_PARTICIPANT_FACTORS:
            // clear the data from the context
            studyDesignContext.setBeta(this, null, null);
            loadBetweenParticipantFactorsFromContext();
            break;
        case REPEATED_MEASURES:
            // clear the data from the context
            studyDesignContext.setBeta(this, null, null);
            loadRepeatedMeasuresFromContext();
            break;
        case RESPONSES_LIST:
            // clear the data from the context
            studyDesignContext.setBeta(this, null, null);
            loadResponsesFromContext();
            break;
        case COVARIATE:
            this.hasCovariate = studyDesignContext.getStudyDesign().isGaussianCovariate();
            break;
        }
    };

    /**
     * Update the screen when the predictors or repeated measures change
     */
    @Override
    public void onWizardContextLoad() 
    {
        reset();
        loadBetweenParticipantFactorsFromContext();
        loadRepeatedMeasuresFromContext();
        loadResponsesFromContext();
        loadMatrixDataFromContext();
    }

}
