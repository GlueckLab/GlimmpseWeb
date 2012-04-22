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
import com.google.gwt.user.client.ui.RadioButton;
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

/**
 * Panel to build main effect hypotheses
 * @author VIJAY AKULA
 *
 */
public class MainEffectHypothesisPanel extends Composite  
implements HypothesisBuilder, WizardContextListener {
    
    // context object
    StudyDesignContext studyDesignContext = null;

    // parent panel, change handler
    ClickHandler parent = null;
    
    // radio button group for this panel
    private static final String BUTTON_GROUP = "mainEffectButtonGroup"; 

    // table of variables to test
    protected FlexTable betweenParticipantFactorsFlexTable = new FlexTable();
    protected FlexTable withinParticipantFactorsFlexTable = new FlexTable();

    // currently selected between participant effect
    BetweenParticipantFactor selectedBetweenParticipantFactor = null;
    // name of currently selected within participant effect
    RepeatedMeasuresNode selectedRepeatedMeasuresNode = null;
    
    // RadioButton which contains a between participant effect
    private class BetweenParticipantRadioButton extends RadioButton {
        public BetweenParticipantFactor factor;
        public BetweenParticipantRadioButton(String group, String label,
                BetweenParticipantFactor factor) {
            super(group, label);
            this.factor = factor;
        }
    }
    // RadioButton which contains a repeated measures effect
    private class RepeatedMeasuresRadioButton extends RadioButton {
        public RepeatedMeasuresNode factor;
        public RepeatedMeasuresRadioButton(String group, String label,
                RepeatedMeasuresNode factor) {
            super(group, label);
            this.factor = factor;
        }
    }   
    
    /**
     * Constructor
     * @param studyDesignContext
     */
    public MainEffectHypothesisPanel(StudyDesignContext studyDesignContext,
            ClickHandler handler)
    {
        this.parent = handler;
        this.studyDesignContext = studyDesignContext;
        this.studyDesignContext.addContextListener(this);

        VerticalPanel verticalPanel = new VerticalPanel();
        HTML text = new HTML();
        HTML betweenParticipantFactors = new HTML();
        HTML withinParticipantFactors = new HTML();

        text.setText(GlimmpseWeb.constants.mainEffectPanelText());
        betweenParticipantFactors.setText(
                GlimmpseWeb.constants.mainEffectPanelBetweenParticipantFactors());
        withinParticipantFactors.setText(
                GlimmpseWeb.constants.mainEffectPanelWithinParticipantFactors());

        //Style Sheets
        text.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        betweenParticipantFactors.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        withinParticipantFactors.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        //Add individual widgets to vertical panel
        verticalPanel.add(text);
        verticalPanel.add(betweenParticipantFactors);
        verticalPanel.add(betweenParticipantFactorsFlexTable);
        verticalPanel.add(withinParticipantFactors);
        verticalPanel.add(withinParticipantFactorsFlexTable);
        verticalPanel.setWidth("100%");

        initWidget(verticalPanel);
    }

    /**
     * Reload the between participant factors from the context
     */
    private void loadBetweenFactorsFromContext()
    {
        this.selectedBetweenParticipantFactor = null;
        betweenParticipantFactorsFlexTable.removeAllRows();
        List<BetweenParticipantFactor> factorList = 
            studyDesignContext.getStudyDesign().getBetweenParticipantFactorList();
        if (factorList != null) {
            int i = 0;
            for(BetweenParticipantFactor factor : factorList)
            {
                BetweenParticipantRadioButton button = 
                    new BetweenParticipantRadioButton(
                            BUTTON_GROUP,
                            factor.getPredictorName(), factor);
                button.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        BetweenParticipantRadioButton button = 
                            (BetweenParticipantRadioButton) event.getSource();
                        selectBetweenParticipantFactor(button.factor);
                        parent.onClick(event);
                    }
                });
                betweenParticipantFactorsFlexTable.setWidget(
                        i, 0, button);
                i++;
            }
        }
    }

    /**
     * Load repeated measures from the context
     */
    private void loadRepeatedMeasuresFromContext() {
        this.selectedRepeatedMeasuresNode = null;
        withinParticipantFactorsFlexTable.removeAllRows();
        List<RepeatedMeasuresNode> factorList = 
            studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
        if (factorList != null) {
            int i = 0;
            for(RepeatedMeasuresNode factor : factorList)
            {
                RepeatedMeasuresRadioButton button = 
                    new RepeatedMeasuresRadioButton(BUTTON_GROUP,
                            factor.getDimension(), factor);
                button.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        RepeatedMeasuresRadioButton button = 
                            (RepeatedMeasuresRadioButton) event.getSource();
                        selectRepeatedMeasuresNode(button.factor);
                        parent.onClick(event);
                    }
                });
                withinParticipantFactorsFlexTable.setWidget(
                        i, 0, button);
                i++;
            }
        }
    }

    /**
     * Create a hypothesis object from the panel
     */
    @Override
    public Hypothesis buildHypothesis() {
        Hypothesis hypothesis = new Hypothesis();
        hypothesis.setType(HypothesisTypeEnum.MAIN_EFFECT);

        if (selectedRepeatedMeasuresNode != null) {
            HypothesisRepeatedMeasuresMapping mappingNode =
                new HypothesisRepeatedMeasuresMapping();
            mappingNode.setRepeatedMeasuresNode(selectedRepeatedMeasuresNode);
            List<HypothesisRepeatedMeasuresMapping> mappingList =
                new ArrayList<HypothesisRepeatedMeasuresMapping>();
            mappingList.add(mappingNode);
            hypothesis.setRepeatedMeasuresMapTree(mappingList);
            
        } else if (selectedBetweenParticipantFactor != null) {
            HypothesisBetweenParticipantMapping mappingParticipant =
                new HypothesisBetweenParticipantMapping();
            mappingParticipant.setBetweenParticipantFactor(selectedBetweenParticipantFactor);
            List<HypothesisBetweenParticipantMapping> mappingList = 
                new ArrayList<HypothesisBetweenParticipantMapping>();
            mappingList.add(mappingParticipant);
            hypothesis.setBetweenParticipantFactorMapList(mappingList);
        } else {
            return null;
        }
        return hypothesis;
    }

    /**
     * Select the specified repeated measures node
     * @param node
     */
    private void selectRepeatedMeasuresNode(RepeatedMeasuresNode node) {
        selectedBetweenParticipantFactor = null;
        selectedRepeatedMeasuresNode = node;
    }
    
    /**
     * Select the specified repeated measures node
     * @param node
     */
    private void selectBetweenParticipantFactor(BetweenParticipantFactor factor) {
        selectedBetweenParticipantFactor = factor;
        selectedRepeatedMeasuresNode = null;
    }
    
    
    /**
     * Returns true if the user has selected sufficient information
     */
    @Override
    public boolean checkComplete() {
        return (this.selectedBetweenParticipantFactor != null || 
                this.selectedRepeatedMeasuresNode != null);
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
