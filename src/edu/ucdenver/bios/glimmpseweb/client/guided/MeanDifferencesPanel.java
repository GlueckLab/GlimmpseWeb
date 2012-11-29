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
    protected int currentColumnOffset = 0;

    // flex table of mean values
    protected FlexTable meansTable = new FlexTable();
    // selection of repeated measures
    protected HTML rmInstructions = 
            new HTML(GlimmpseWeb.constants.meanDifferenceRepeatedMeasuresInstructions());
    protected FlexTable repeatedMeasuresTable = new FlexTable();

    protected int totalBetweenFactors = 0;
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
        currentColumnOffset = 0;

        totalBetweenFactors = 0;
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
        // load new between participant factor information
        FactorTable participantGroups = studyDesignContext.getParticipantGroups();
        if (participantGroups != null && participantGroups.getNumberOfRows() > 0) {
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
            // get the values for the matrix
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
        // total responses - product of all repeated measures and response variables
        int totalResponses = totalResponseVariables;

        List<RepeatedMeasuresNode> rmNodeList = 
                studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
        if (rmNodeList != null && rmNodeList.size() > 0) {
            // calculate the total repeated measures combinations
            int totalRepeatedMeasuresCombinations = 0;
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
                totalResponses *= totalRepeatedMeasuresCombinations;
            } else if (totalRepeatedMeasuresCombinations > 0) {
                totalResponses = totalRepeatedMeasuresCombinations;
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
                        repeatedMeasuresTable.setWidget(row, LISTBOX_COLUMN, 
                                lb);
                        row++;
                    }
                }

            }
            rmInstructions.setVisible(true);
            repeatedMeasuresTable.setVisible(true);
        }
        updateMatrixView();
        checkComplete();
    }

    /**
     * When both the repeated measures and response variables have been
     * specified, this function creates the text box input widgets
     */
    private void fillTextBoxes() {
        if (totalResponseVariables > 0) {
            int rows = 2;
            if (totalBetweenFactors > 0) {
                rows = meansTable.getRowCount();
            }
            // fill in 
            for(int row = 1; row < rows; row++) {
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
        totalResponseVariables = 0;

        // now load the new responses
        List<ResponseNode> outcomeList = studyDesignContext.getStudyDesign().getResponseList();
        if (outcomeList != null && outcomeList.size() > 0) {
            totalResponseVariables = outcomeList.size();
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
        updateMatrixView();
        checkComplete();
    }

    /**
     * Check if the screen is complete
     */
    private void checkComplete() {        
        if (studyDesignContext.getValidResponseVariableCount() > 0) {
            if (studyDesignContext.betaIsValid()) {
                changeState(WizardStepPanelState.COMPLETE);
            } else {
                changeState(WizardStepPanelState.INCOMPLETE);
            }
        } else {
            changeState(WizardStepPanelState.NOT_ALLOWED);
        }
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
                double value = studyDesignContext.getBetaValue(row-1, dataCol+currentColumnOffset);
                if (!Double.isNaN(value)) {
                    tb.setText(Double.toString(value));
                }
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
            studyDesignContext.setBetaValue(this, tb.getRow(), tb.getColumn()+ currentColumnOffset, value);
        }
        catch (NumberFormatException nfe)
        {
            TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidNumber());
            tb.setText("0");
            studyDesignContext.setBetaValue(this, tb.getRow(), tb.getColumn()+ currentColumnOffset, 0);
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
            loadBetweenParticipantFactorsFromContext();
            break;
        case REPEATED_MEASURES:
            // clear the data from the context
            loadRepeatedMeasuresFromContext();
            break;
        case RESPONSES_LIST:
            // clear the data from the context
            loadResponsesFromContext();
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
    }

}
