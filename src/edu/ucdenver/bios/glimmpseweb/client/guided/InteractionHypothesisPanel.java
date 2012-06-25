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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextListener;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Hypothesis;
import edu.ucdenver.bios.webservice.common.domain.HypothesisBetweenParticipantMapping;
import edu.ucdenver.bios.webservice.common.domain.HypothesisRepeatedMeasuresMapping;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTypeEnum;

public class InteractionHypothesisPanel extends Composite
implements HypothesisBuilder, WizardContextListener, ClickHandler
{
    // context object
    StudyDesignContext studyDesignContext = null;

    // parent panel, change handler
    ClickHandler parent = null;

    // counter of #variables selected
    int selectedCount = 0;
    
    // variable lists
    protected FlexTable betweenParticipantFactorsFlexTable = new FlexTable();
    protected FlexTable withinParticipantFactorsFlexTable = new FlexTable();

    // panel which contains a between participant effect
    private class BetweenParticipantVariablePanel 
    extends InteractionVariablePanel {
        public BetweenParticipantFactor factor;
        public BetweenParticipantVariablePanel(String label, ClickHandler handler,
                BetweenParticipantFactor factor) {
            super(label, handler);
            this.factor = factor;
        }
    }
    // panel which contains a repeated measures effect
    private class RepeatedMeasuresVariablePanel  
    extends InteractionVariablePanel {
        public RepeatedMeasuresNode factor;
        public RepeatedMeasuresVariablePanel(String label, ClickHandler handler,
                RepeatedMeasuresNode factor) {
            super(label, handler);
            this.factor = factor;
        }
    }   

    /**
     * Constructor
     * @param studyDesignContext
     * @param handler
     */
    public InteractionHypothesisPanel(StudyDesignContext studyDesignContext,
            ClickHandler handler)
    {
        this.studyDesignContext = studyDesignContext;
        this.studyDesignContext.addContextListener(this);
        this.parent = handler;
        VerticalPanel verticalPanel = new VerticalPanel();


        HTML text = new HTML();
        HTML betweenParticipantFactors = new HTML();
        HTML withinParticipantFactors = new HTML();


        text.setText(GlimmpseWeb.constants.interactionHypothesisPanelText());
        betweenParticipantFactors.setText(
                GlimmpseWeb.constants.hypothesisPanelBetweenParticipantFactorsLabel());
        withinParticipantFactors.setText(
                GlimmpseWeb.constants.hypothesisPanelWithinParticipantFactorsLabel());

        //Style Sheets
        text.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        betweenParticipantFactors.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        withinParticipantFactors.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);


        verticalPanel.add(text);
        verticalPanel.add(betweenParticipantFactors);
        verticalPanel.add(betweenParticipantFactorsFlexTable);
        verticalPanel.add(withinParticipantFactors);
        verticalPanel.add(withinParticipantFactorsFlexTable);

        initWidget(verticalPanel);
    }


    /**
     * Reload the between participant factors from the context
     */
    private void loadBetweenFactorsFromContext()
    {
        betweenParticipantFactorsFlexTable.removeAllRows();
        List<BetweenParticipantFactor> factorList = 
            studyDesignContext.getStudyDesign().getBetweenParticipantFactorList();
        if (factorList != null) {
            int i = 0;
            for(BetweenParticipantFactor factor : factorList)
            {
                BetweenParticipantVariablePanel panel = 
                    new BetweenParticipantVariablePanel(
                            factor.getPredictorName(), this, factor);
                betweenParticipantFactorsFlexTable.setWidget(
                        i, 0, panel);
                i++;
            }
        }
    }

    /**
     * Load repeated measures from the context
     */
    private void loadRepeatedMeasuresFromContext() {
        withinParticipantFactorsFlexTable.removeAllRows();
        List<RepeatedMeasuresNode> factorList = 
            studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
        if (factorList != null) {
            int i = 0;
            for(RepeatedMeasuresNode factor : factorList)
            {
                RepeatedMeasuresVariablePanel panel = 
                    new RepeatedMeasuresVariablePanel(
                            factor.getDimension(), this, factor);
                withinParticipantFactorsFlexTable.setWidget(
                        i, 0, panel);
                i++;
            }
        }
    }

    @Override
    public Hypothesis buildHypothesis() {
        Hypothesis hypothesis = new Hypothesis();
        hypothesis.setType(HypothesisTypeEnum.INTERACTION);

        // fill in any between participant effects
        List<HypothesisBetweenParticipantMapping> factorList =
            new ArrayList<HypothesisBetweenParticipantMapping>();
        for(int i = 0; i < betweenParticipantFactorsFlexTable.getRowCount(); i++)
        {
            BetweenParticipantVariablePanel panel = 
                (BetweenParticipantVariablePanel)
                betweenParticipantFactorsFlexTable.getWidget(i, 0);
            if(panel.isChecked())
            {
                HypothesisBetweenParticipantMapping map = 
                    new HypothesisBetweenParticipantMapping();
                map.setBetweenParticipantFactor(panel.factor);
                map.setType(panel.selectedTrend());
                factorList.add(map);
            }
        }
        if (factorList.size() > 0) {
            hypothesis.setBetweenParticipantFactorMapList(factorList);
        }

        // fill in repeated measures information
        List<HypothesisRepeatedMeasuresMapping> nodeList =
            new ArrayList<HypothesisRepeatedMeasuresMapping>();
        for(int i = 0; i < withinParticipantFactorsFlexTable.getRowCount(); i++)
        {
            RepeatedMeasuresVariablePanel panel = 
                (RepeatedMeasuresVariablePanel)
                withinParticipantFactorsFlexTable.getWidget(i, 0);
            if(panel.isChecked())
            {
                HypothesisRepeatedMeasuresMapping map =
                    new HypothesisRepeatedMeasuresMapping();
                map.setRepeatedMeasuresNode(panel.factor);
                map.setType(panel.selectedTrend());
                nodeList.add(map);
            }
        }
        if (nodeList.size() > 0) {
            hypothesis.setRepeatedMeasuresMapTree(nodeList);
        }

        return hypothesis;
    }
    
    @Override
    public boolean checkComplete() {     
        return (selectedCount > 0);
    }


    @Override
    public void onClick(ClickEvent event) {
        InteractionVariablePanel panel = 
            (InteractionVariablePanel) event.getSource();
        if (panel.isChecked()) {
            selectedCount++;
        } else {
            selectedCount--;
        }
    }
    
    /**
     * Reload the panel when a user changes either the fixed predictors 
     * or repeated measures information
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) {
        switch(((StudyDesignChangeEvent) e).getType()) {
        case BETWEEN_PARTICIPANT_FACTORS:
            loadBetweenFactorsFromContext();
            break;
        case REPEATED_MEASURES:
            loadRepeatedMeasuresFromContext();
            break;
        }
    }

    /**
     * Fill in the panel on upload events
     */
    @Override
    public void onWizardContextLoad() {
        loadBetweenFactorsFromContext();
        loadRepeatedMeasuresFromContext();
    }
}
