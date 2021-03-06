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

import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.DynamicTabPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;
import edu.ucdenver.bios.webservice.common.enums.CovarianceTypeEnum;

/**
 * 
 * @author VIJAY AKULA
 * @author Sarah Kreidler
 *
 */
public class WithinParticipantCovariancePanel extends WizardStepPanel
implements CovarianceSetManager
{
    // context object
    protected StudyDesignContext studyDesignContext;
    // tabs for each covariance
    protected DynamicTabPanel tabPanel = new DynamicTabPanel();
    // keep a ptr to the responses tab header
    protected HTML responsesTabHeader = 
        new HTML(GlimmpseWeb.constants.covarianceResponsesLabel());
    // map of each covariance component and whether or not it is complete
    protected HashMap<String,Boolean> completenessMap = new HashMap<String,Boolean>();


    /**
     * Constructor
     * @param context study design context
     */
    WithinParticipantCovariancePanel(WizardContext context) 
    {
        super(context, GlimmpseWeb.constants.navItemVariabilityWithinParticipant(),
                WizardStepPanelState.NOT_ALLOWED);
        studyDesignContext = (StudyDesignContext) context;

        VerticalPanel panel = new VerticalPanel();

        // header text
        HTML header = new HTML(GlimmpseWeb.constants.withinSubjectCovarianceHeader());
        HTML instructions = new HTML(GlimmpseWeb.constants.withinSubjectCovarianceInstructions());

        // TODO: allow upload of a full covariance - not necessarily Kronecker
        // for V2 we will just force the user into the Kronecker format for
        // reversible mixed model support - return to this later
        //        // upload a covariance button
        //        ButtonWithExplanationPanel uploadFullCovarianceMatrixButton =
        //            new ButtonWithExplanationPanel(
        //                    GlimmpseWeb.constants.uploadFullCovarianceMatrix(), 
        //                    GlimmpseWeb.constants.fullCovarianceMatrixHeader(), 
        //                    GlimmpseWeb.constants.fullCovarianceMatrixText());
        //        uploadFullCovarianceMatrixButton.addClickHandler(new ClickHandler()
        //        {
        //            @Override
        //            public void onClick(ClickEvent event) 
        //            {
        //
        //            }
        //
        //        });


        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        instructions.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        panel.add(header);
        panel.add(instructions);
        panel.add(tabPanel);
        //panel.add(uploadFullCovarianceMatrixButton);

        initWidget(panel);
    }


    /**
     * Clear the panel
     */
    @Override
    public void reset() 
    {
        tabPanel.clear();
        completenessMap.clear();
    }

    /**
     * Load repeated measures information
     */
    private void loadRepeatedMeasuresFromContext()
    {
        // if the responses tab has been added, don't clear it
        Widget responseTabContents = tabPanel.getTabContents(responsesTabHeader);
        if (responseTabContents != null) {
            int tabCount = tabPanel.getTabCount();
            for(int col = 0; col < tabCount-1; col++) {
                tabPanel.remove(0);
            }
        } else {
            tabPanel.clear();
        }
        // now create panels for the repeated measures
        List<RepeatedMeasuresNode> repeatedMeasuresNodeList = 
            studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();        
        if (repeatedMeasuresNodeList != null) {
            int pos = 0;
            for(RepeatedMeasuresNode node: repeatedMeasuresNodeList)
            {
                if (node.getDimension() != null &&
                        !node.getDimension().isEmpty() &&
                        node.getNumberOfMeasurements() != null &&
                        node.getNumberOfMeasurements() > 1) {
                    
                    CorrelationDeckPanel panel = new CorrelationDeckPanel(this, node);
                    panel.loadCovariance(
                            studyDesignContext.getCovarianceByName(node.getDimension()));
                    tabPanel.insert(pos, new HTML(node.getDimension()), panel);
                    pos++;
                }
            }
        }
    }

    /**
     * Load the response variables from the context
     */
    private void loadResponsesFromContext()
    {
        // remove the tab for the responses if it is set
        tabPanel.remove(responsesTabHeader);
        // load the new responses
        List<ResponseNode> responseNodeList = 
            studyDesignContext.getStudyDesign().getResponseList();
        if (responseNodeList != null && responseNodeList.size() > 0) {
            CovarianceCorrelationDeckPanel panel =
                new CovarianceCorrelationDeckPanel(this, responseNodeList, true);
            panel.loadCovariance(
                    studyDesignContext.getCovarianceByName(
                            GlimmpseConstants.RESPONSES_COVARIANCE_LABEL));
            tabPanel.add(responsesTabHeader, panel);
        }
    }

    /**
     * Determine if the user has completed this screen
     */
    private void checkComplete() {

        if (tabPanel.getTabCount() > 0) {
            boolean complete = true;
            for(Boolean value: completenessMap.values()) {
                if (!value) {
                    complete = false;
                    break;
                }
            }
            if (complete) {
                changeState(WizardStepPanelState.COMPLETE);
            } else {
                changeState(WizardStepPanelState.INCOMPLETE);
            }
        } else {
            changeState(WizardStepPanelState.NOT_ALLOWED);
        }

    }


    /**
     * Respond to a change in the context object
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) 
    {
        if (e.getSource() != this) {
            switch(((StudyDesignChangeEvent) e).getType()) {
            case REPEATED_MEASURES:
                // clear the context 
                loadRepeatedMeasuresFromContext();
                break;
            case RESPONSES_LIST:
                // clear the context 
                loadResponsesFromContext();
                break;
            }
            tabPanel.openTab(0);
            checkComplete();
        }
    };

    /**
     * Update the screen when the predictors or repeated measures change
     */
    @Override
    public void onWizardContextLoad() 
    {
        loadRepeatedMeasuresFromContext();
        loadResponsesFromContext();
        tabPanel.openTab(0);
        checkComplete();
    }

    /**
     * Reset the type in the named covariance object in the context
     * @param name
     * @param type
     */
    @Override
    public void setType(String name, CovarianceTypeEnum type) {
        studyDesignContext.setCovarianceType(this, name, type);
    }

    /**
     * Update the values of the Lear parameters in the named covariance object
     * @param name
     * @param rho
     * @param delta
     */
    @Override
    public void setLearParameters(String name, double rho, double delta) {
        studyDesignContext.setCovarianceLearParameters(this, name, rho, delta);
    }

    /**
     * Set the value of the specified cell in the named covariance matrix
     * @param name
     * @param row
     * @param column
     * @param value
     */
    @Override
    public void setCovarianceCellValue(String name, int row, int column,
            double value) {
        studyDesignContext.setCovarianceValue(this, name, row, column, value);
    }

    /**
     * Mark the named covariance object as complete or 
     * incomplete
     */
    @Override
    public void setComplete(String name, boolean complete) {
        completenessMap.put(name, complete);
        checkComplete();
    }


    @Override
    public void setStandardDeviationValue(String name, int index, double value) {
        studyDesignContext.setCovarianceStandardDeviationValue(this, name, index, value);
    }
}


