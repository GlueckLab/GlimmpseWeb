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
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;

/**
 * 
 * @author VIJAY AKULA
 * @author Sarah Kreidler
 *
 */
public class WithinParticipantCovariancePanel extends WizardStepPanel
implements ChangeHandler
{
    // context object
    protected StudyDesignContext studyDesignContext;
    // tabs for each covariance
    protected DynamicTabPanel tabPanel = new DynamicTabPanel();
    // keep a ptr to the responses tab header
    protected HTML responsesTabHeader = new HTML(GlimmpseWeb.constants.covarianceResponsesLabel());
    
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
    }

    /**
     * Load repeated measures information
     */
    private void loadRepeatedMeasuresFromContext()
    {
        // clear the context 
        studyDesignContext.clearCovariance(this);
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
            for(RepeatedMeasuresNode node: repeatedMeasuresNodeList)
            {
                if (node.getDimension() != null &&
                        !node.getDimension().isEmpty() &&
                        node.getNumberOfMeasurements() != null &&
                                node.getNumberOfMeasurements() > 1) {
                    if (tabPanel.getTabCount() <= 0) {
                        tabPanel.insert(0, new HTML(node.getDimension()), 
                                new CorrelationDeckPanel(node, this));
                    } else {
                        tabPanel.insert(tabPanel.getTabCount()-1,
                                new HTML(node.getDimension()), 
                                new CorrelationDeckPanel(node, this));
                    }
                }
            }
        }
    }
    
    /**
     * Load the response variables from the context
     */
    private void loadResponsesFromContext()
    {
        // clear the context
        studyDesignContext.clearCovariance(this);
        // remove the tab for the responses if it is set
        tabPanel.remove(responsesTabHeader);
        // load the new responses
        List<ResponseNode> responseNodeList = 
            studyDesignContext.getStudyDesign().getResponseList();
        if (responseNodeList != null && responseNodeList.size() > 0) {
            tabPanel.add(responsesTabHeader,
                    new CovarianceCorrelationDeckPanel(responseNodeList, this, true));
        }
        checkComplete();
    }

    /**
     * Determine if the user has completed this screen
     */
    private void checkComplete() {

        if (tabPanel.getTabCount() > 0) {
            boolean complete = true;
            for(int i = 0; i < tabPanel.getTabCount(); i++) {
                CovarianceBuilder panel = 
                    (CovarianceBuilder) tabPanel.getTabContents(i);
                if (!panel.checkComplete()) {
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
        switch(((StudyDesignChangeEvent) e).getType()) {
        case REPEATED_MEASURES:
            loadRepeatedMeasuresFromContext();
            tabPanel.openTab(0);
            break;
        case RESPONSES_LIST:
            loadResponsesFromContext();
            tabPanel.openTab(0);
            break;
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
    }


    /**
     * Build the covariance objects as we exit the screen
     */
    @Override
    public void onExit()
    {
        studyDesignContext.clearCovariance(this);
        for(int i = 0; i < tabPanel.getTabCount(); i++)
        {
            CovarianceBuilder panel = 
                (CovarianceBuilder) tabPanel.getTabContents(i);
            Covariance covariance = panel.getCovariance();
            studyDesignContext.setCovariance(this, covariance);
        }
    }


    @Override
    public void onChange(ChangeEvent event) {
        checkComplete();
    }
}


