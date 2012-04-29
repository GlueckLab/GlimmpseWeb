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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ButtonWithExplanationPanel;
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
{
    // context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;

    DynamicTabPanel tabPanel = new DynamicTabPanel();

    /**
     * Constructor
     * @param context
     */
    WithinParticipantCovariancePanel(WizardContext context) 
    {
        super(context, GlimmpseWeb.constants.navItemVariabilityWithinParticipant(),
                WizardStepPanelState.NOT_ALLOWED);

        VerticalPanel panel = new VerticalPanel();
        HTML header = new HTML(GlimmpseWeb.constants.withinSubjectCovarianceHeader());
        HTML instructions = new HTML(GlimmpseWeb.constants.withinSubjectCovarianceInstructions());

        ButtonWithExplanationPanel uploadFullCovarianceMatrixButton =
            new ButtonWithExplanationPanel(
                    GlimmpseWeb.constants.uploadFullCovarianceMatrix(), 
                    GlimmpseWeb.constants.fullCovarianceMatrixHeader(), 
                    GlimmpseWeb.constants.fullCovarianceMatrixText());

        uploadFullCovarianceMatrixButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event) 
            {

            }

        });

        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);

        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        instructions.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        panel.add(header);
        panel.add(instructions);
        panel.add(tabPanel);
        panel.add(uploadFullCovarianceMatrixButton);

        initWidget(panel);
    }



    @Override
    public void reset() 
    {


    }

    private void loadFromContext()
    {
        tabPanel.clear();

        List<RepeatedMeasuresNode> repeatedMeasuresNodeList = 
            studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
        if (repeatedMeasuresNodeList != null) {
            for(RepeatedMeasuresNode node: repeatedMeasuresNodeList)
            {
                tabPanel.add(node.getDimension(), 
                        new CovarianceCorrelationDeckPanel(node));
            }
        }
        List<ResponseNode> responseNodeList = 
            studyDesignContext.getStudyDesign().getResponseList();
        if (responseNodeList != null && responseNodeList.size() > 0) {
            tabPanel.add(GlimmpseWeb.constants.responsesTableColumn(),
                    new CovarianceCorrelationDeckPanel(responseNodeList));
        }

        checkComplete();
        //       tabPanel.setHeight("525");
        //       tabPanel.setWidth("600");
        //       
        //       tabPanel.setAnimationDuration(700);
        //        return tabPanel;
    }


    private void checkComplete() {

        if (tabPanel.getTabCount() > 0) {
            boolean complete = true;
            for(int i = 0; i < tabPanel.getTabCount(); i++) {
                CovarianceCorrelationDeckPanel panel = 
                    (CovarianceCorrelationDeckPanel) tabPanel.getWidget(i);
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
        case RESPONSES_LIST:
            GWT.log("GOT EVENT");
            loadFromContext();
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



    @Override
    public void onExit()
    {
        for(int i = 0; i < tabPanel.getTabCount(); i++)
        {
            CovarianceCorrelationDeckPanel panel = (CovarianceCorrelationDeckPanel) tabPanel.getWidget(i);
            Covariance covariance = panel.getCovariance();
            studyDesignContext.setCovariance(this, covariance);
        }
    }
}


