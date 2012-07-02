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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;

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
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.RelativeGroupSize;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;

/**
 * Entry screen for means by outcome and study subgroup
 */
public class MeanDifferencesPanel extends WizardStepPanel
implements ChangeHandler
{
    // context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;

    protected FlexTable meansTable = new FlexTable();
    protected int betaRows = 0;
    protected int betaColumns = 0;
    protected int dataStartRow = 0;
    protected int dataStartColumn = 0;

    protected boolean hasCovariate = false;
    protected int totalBetweenFactors = 0;
    protected int totalBetweenFactorCombinations = 0;
    protected int totalWithinFactorCombinations = 0;
    protected int totalResponses = 0;
    
    protected HTML errorHTML = new HTML();

    /**
     * Constructor
     * @param context
     */
    public MeanDifferencesPanel(WizardContext context)
    {
        super(context, GlimmpseWeb.constants.navItemMeans(),
                WizardStepPanelState.NOT_ALLOWED);

        VerticalPanel panel = new VerticalPanel();

        HTML header = new HTML(GlimmpseWeb.constants.meanDifferenceTitle());
        HTML description = new HTML(GlimmpseWeb.constants.meanDifferenceDescription());

        panel.add(header);
        panel.add(description);
        panel.add(meansTable);
        panel.add(errorHTML);

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
        changeState(WizardStepPanelState.COMPLETE);
    }

    /**
     * Load between subject factors from the context
     */
    private void loadBetweenParticipantFactorsFromContext() {
        if (totalBetweenFactors > 0) {
            // remove all but the header row
            for(int row = meansTable.getRowCount()-1; row >= 1; row--) {
                meansTable.removeRow(row);
            }
            // remove the header widgets for the between subject factors
            for(int col = 0; col < totalBetweenFactors; col++) {
                meansTable.removeCell(0, col);
            }
        }
        FactorTable participantGroups = studyDesignContext.getParticipantGroups();
        if (participantGroups != null && participantGroups.getNumberOfRows() > 0) {
            totalBetweenFactorCombinations = participantGroups.getNumberOfRows();
            // build the labels for the groups
            if (participantGroups != null) {
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
            }
        }
        checkComplete();
    }
    
    private void loadRepeatedMeasuresFromContext() {
        FactorTable withinParticipantMeasures = studyDesignContext.getWithinParticipantMeasures();
        
    }
    
    private void fillTextBoxes() {
        if (totalBetweenFactors > 0 && totalResponses > 0) {
            for(int row = 1; row < meansTable.getRowCount(); row++) {
                meansTable.getRowFormatter().setStyleName(row, 
                        GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
                for(int col = totalBetweenFactors; col < totalResponses+totalBetweenFactors; col++) {
                    TextBox tb = new TextBox();
                    tb.setText("0");
                    tb.addChangeHandler(this);
                    meansTable.setWidget(row, col, tb);
                }
            }
        }
    }
    
    private void loadResponsesFromContext() {
        if (totalResponses > 0) {
            // remove the columns associated with the responses
            for(int col = totalBetweenFactors; col < totalResponses; col++) {
                for(int row = meansTable.getRowCount()-1; row >= 0; row--) {
                    meansTable.removeCell(row, col);
                }
            }
        }
        // now load the new responses
        List<ResponseNode> outcomeList = studyDesignContext.getStudyDesign().getResponseList();
        totalResponses = outcomeList.size();
        if (outcomeList != null && outcomeList.size() > 0) {
            int col = totalBetweenFactors;
            for(ResponseNode outcome: outcomeList) {
                meansTable.setWidget(0, col, new HTML(outcome.getName()));
                col++;
            }
        }
        // create text boxes to hold the means
        fillTextBoxes();
        
        checkComplete();
    }

    private void checkComplete() {        
        if (totalBetweenFactors > 0 && totalResponses > 0) {
            changeState(WizardStepPanelState.INCOMPLETE);
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
        betaFixed.setRows(betaRows);
        betaFixed.setColumns(betaColumns);
        double[][] betaFixedData = new double[betaRows][betaColumns];
        // fill the fixed matrix from the displayed text boxes
        betaFixed.setName(GlimmpseConstants.MATRIX_BETA);
        for(int r = dataStartRow; r < betaRows; r++) {
            for(int c = dataStartColumn; c < betaColumns; c++) {
                TextBox tb = (TextBox) meansTable.getWidget(r, c);
                betaFixedData[r][c] = Double.parseDouble(tb.getText());
            }
        }
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
     * Textbox input validation
     */
    @Override
    public void onChange(ChangeEvent event)
    {
        TextBox tb = (TextBox) event.getSource();
        try
        {
            Double.parseDouble(tb.getText());
            TextValidation.displayOkay(errorHTML, "");
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
            loadBetweenParticipantFactorsFromContext();
            break;
        case REPEATED_MEASURES:
            loadRepeatedMeasuresFromContext();
            break;
        case RESPONSES_LIST:
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
        loadBetweenParticipantFactorsFromContext();
        loadRepeatedMeasuresFromContext();
        loadResponsesFromContext();
    }

}
