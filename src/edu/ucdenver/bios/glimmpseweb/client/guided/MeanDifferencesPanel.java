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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
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


    private void loadFromContext()
    {
        FactorTable participantGroups = studyDesignContext.getParticipantGroups();
        FactorTable withinParticipantMeasures = studyDesignContext.getWithinParticipantMeasures();
        List<ResponseNode> outcomes = studyDesignContext.getStudyDesign().getResponseList();

//        meansTable.removeAllRows();
//        if (participantGroups != null && participantGroups.getNumberOfRows() > 0 
//                && outcomes != null && outcomes.size() > 0) {
//
//            // create the table header row
//            meansTable.getRowFormatter().setStyleName(0, 
//                    GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_HEADER);
//            int col = 0;
//            for(;col < participantGroups.getNumberOfColumns(); col++)
//            {
//                meansTable.setWidget(0, col, new HTML(participantGroups.getColumnLabel(col)));
//            }
//            for(ResponseNode outcome: outcomes)
//            {
//                meansTable.setWidget(0, col, new HTML(outcome.getName()));
//                col++;
//            }
//
//            // now fill in the group values, and add "0" text boxes for entering the means
//            int rowOrderCount = 0;
//            boolean uploadComplete = false;
//            for(int row = 0; row < participantGroups.getNumberOfRows(); row++)
//            {
//                meansTable.getRowFormatter().setStyleName(row+1, GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
//                for(col = 0; col < participantGroups.getNumberOfColumns(); col++)
//                {
//                    meansTable.setWidget(row+1, col, new HTML(participantGroups.getValueString(row, col)));
//                }
//                for(ResponseNode outcome: outcomes)
//                {
//                    TextBox tb = new TextBox();
//                    tb.setText(outcome.getName());
//                    tb.addChangeHandler(this);
//                    meansTable.setWidget(row+1, col, tb);
//                    col++;
//                    rowOrderCount++;
//                }
//            }
//        }
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
    }

    /**
     * Respond to a change in the context object
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) 
    {
        switch(((StudyDesignChangeEvent) e).getType()) {
        case BETWEEN_PARTICIPANT_FACTORS:
        case REPEATED_MEASURES:
        case RESPONSES_LIST:
            loadFromContext();
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
        loadFromContext();
    }

}
