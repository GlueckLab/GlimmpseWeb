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
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Category;
import edu.ucdenver.bios.webservice.common.domain.Hypothesis;
import edu.ucdenver.bios.webservice.common.domain.HypothesisBetweenParticipantMapping;
import edu.ucdenver.bios.webservice.common.domain.HypothesisRepeatedMeasuresMapping;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTrendTypeEnum;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTypeEnum;

/**
 * Panel to build main effect hypotheses
 * @author VIJAY AKULA
 *
 */
public class MainEffectHypothesisPanel extends Composite  
implements HypothesisBuilder {
    // study design context
    protected StudyDesignContext studyDesignContext = null;
    // parent panel
    protected HypothesisPanel parent = null;
    
    // table column containing the radio buttons
    private static final int RADIO_BUTTON_COLUMN = 0;
    
    // radio button group for this panel
    private static final String BUTTON_GROUP = "mainEffectButtonGroup"; 

    // table of variables to test
    protected FlexTable betweenParticipantFactorsFlexTable = new FlexTable();
    protected FlexTable withinParticipantFactorsFlexTable = new FlexTable();
    // labels for the flex tables
    protected HTML betweenParticipantFactors = 
        new HTML(GlimmpseWeb.constants.hypothesisPanelBetweenParticipantFactorsLabel());
    protected HTML withinParticipantFactors = 
        new HTML(GlimmpseWeb.constants.hypothesisPanelWithinParticipantFactorsLabel());
    
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
    public MainEffectHypothesisPanel(StudyDesignContext context,
            HypothesisPanel parent)
    {
        this.studyDesignContext = context;
        this.parent = parent;

        VerticalPanel verticalPanel = new VerticalPanel();
        HTML text = new HTML(GlimmpseWeb.constants.mainEffectPanelText());

        // hide the flex table labels until we have some rows in the tables
        betweenParticipantFactors.setVisible(false);
        withinParticipantFactors.setVisible(false);
        
        //Style Sheets
        text.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        betweenParticipantFactors.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        withinParticipantFactors.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);

        //Add individual widgets to vertical panel
        verticalPanel.add(text);
        verticalPanel.add(betweenParticipantFactors);
        verticalPanel.add(betweenParticipantFactorsFlexTable);
        verticalPanel.add(withinParticipantFactors);
        verticalPanel.add(withinParticipantFactorsFlexTable);

        initWidget(verticalPanel);
    }

    /**
     * Load the between participant factors
     * @param factorList list of between participant factors
     */
    public void loadBetweenParticipantFactors(List<BetweenParticipantFactor> factorList)
    {
        this.selectedBetweenParticipantFactor = null;
        betweenParticipantFactorsFlexTable.removeAllRows();
        if (factorList != null) {
            int row = 0;
            for(BetweenParticipantFactor factor : factorList)
            {
                List<Category> categoryList = factor.getCategoryList();
                if (categoryList != null && categoryList.size() > 1) {
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
                            row, RADIO_BUTTON_COLUMN, button);
                }
                row++;
            }
        }
        // hide the label if no factors of this type
        betweenParticipantFactors.setVisible(
                betweenParticipantFactorsFlexTable.getRowCount() > 0);
    }

    /**
     * Load repeated measures from the context
     * @param rmNodeList tree of repeated measures information
     */
    public void loadRepeatedMeasures(List<RepeatedMeasuresNode> rmNodeList) {
        this.selectedRepeatedMeasuresNode = null;
        withinParticipantFactorsFlexTable.removeAllRows();
        if (rmNodeList != null) {
            int i = 0;
            for(RepeatedMeasuresNode rmNode : rmNodeList)
            {
                RepeatedMeasuresRadioButton button = 
                    new RepeatedMeasuresRadioButton(BUTTON_GROUP,
                            rmNode.getDimension(), rmNode);
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
                        i, RADIO_BUTTON_COLUMN, button);
                i++;
            }
        }
        // hide the label if no factors of this type
        withinParticipantFactors.setVisible(
                withinParticipantFactorsFlexTable.getRowCount() > 0);
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
            mappingNode.setType(HypothesisTrendTypeEnum.NONE);
            List<HypothesisRepeatedMeasuresMapping> mappingList =
                new ArrayList<HypothesisRepeatedMeasuresMapping>();
            mappingList.add(mappingNode);
            hypothesis.setRepeatedMeasuresMapTree(mappingList);
            
        } else if (selectedBetweenParticipantFactor != null) {
            HypothesisBetweenParticipantMapping mappingParticipant =
                new HypothesisBetweenParticipantMapping();
            mappingParticipant.setBetweenParticipantFactor(selectedBetweenParticipantFactor);
            mappingParticipant.setType(HypothesisTrendTypeEnum.NONE);
            List<HypothesisBetweenParticipantMapping> mappingList = 
                new ArrayList<HypothesisBetweenParticipantMapping>();
            mappingList.add(mappingParticipant);
            hypothesis.setBetweenParticipantFactorMapList(mappingList);
            
        } 
        
        return hypothesis;
    }

    /**
     * Select the specified repeated measures node
     * @param node
     */
    private void selectRepeatedMeasuresNode(RepeatedMeasuresNode node) {
        if (selectedBetweenParticipantFactor != null) {
            studyDesignContext.deleteHypothesisBetweenParticipantFactor(parent, 
                    selectedBetweenParticipantFactor);
        }
        if (selectedRepeatedMeasuresNode != null) {
            studyDesignContext.deleteHypothesisRepeatedMeasuresFactor(parent, 
                    selectedRepeatedMeasuresNode);
        }
        selectedBetweenParticipantFactor = null;
        selectedRepeatedMeasuresNode = node;
        studyDesignContext.addHypothesisRepeatedMeasuresFactor(parent, 
                selectedRepeatedMeasuresNode);
    }
    
    /**
     * Select the specified repeated measures node
     * @param node
     */
    private void selectBetweenParticipantFactor(BetweenParticipantFactor factor) {
        if (selectedBetweenParticipantFactor != null) {
            studyDesignContext.deleteHypothesisBetweenParticipantFactor(parent, 
                    selectedBetweenParticipantFactor);
        }
        if (selectedRepeatedMeasuresNode != null) {
            studyDesignContext.deleteHypothesisRepeatedMeasuresFactor(parent, 
                    selectedRepeatedMeasuresNode);
        }
        selectedBetweenParticipantFactor = factor;
        selectedRepeatedMeasuresNode = null;
        studyDesignContext.addHypothesisBetweenParticipantFactor(parent, 
                selectedBetweenParticipantFactor);
    }
    
    /**
     * Load the hypothesis information.  Should be called after 
     * loadBetweenParticipantFactors and loadRepeatedMeasures
     */
    public void loadHypothesis(Hypothesis hypothesis) {
        if (hypothesis != null && 
                HypothesisTypeEnum.MAIN_EFFECT == hypothesis.getType()) {
            List<HypothesisBetweenParticipantMapping> btwnFactorList = 
                hypothesis.getBetweenParticipantFactorMapList();
            if (btwnFactorList != null && btwnFactorList.size() > 0) {
                // main effect on a between factor 
                BetweenParticipantFactor factor = 
                    btwnFactorList.get(0).getBetweenParticipantFactor();
                if (factor != null) {
                    String factorName = factor.getPredictorName();
                    selectRadioButtonByFactor(factorName,
                            betweenParticipantFactorsFlexTable);
                }                
            } else {
                List<HypothesisRepeatedMeasuresMapping> withinFactorList = 
                    hypothesis.getRepeatedMeasuresMapTree();
                if (withinFactorList != null && withinFactorList.size() > 0) {
                    // main effect on a within factor
                    RepeatedMeasuresNode factor = 
                        withinFactorList.get(0).getRepeatedMeasuresNode();
                    if (factor != null) {
                        String factorName = factor.getDimension();
                        selectRadioButtonByFactor(factorName,
                                withinParticipantFactorsFlexTable);  
                    }
                }
            }
        }
    }
    
    /**
     * Find and select the radio button corresponding to the factor.  
     * If no match, then no effect.
     * @param factorName name of the factor
     * @param table the between or within participant factor table
     */
    private void selectRadioButtonByFactor(String factorName, FlexTable table) {
        for(int row = 0; row < table.getRowCount(); row++) {
            RadioButton rb = 
                (RadioButton) table.getWidget(row, RADIO_BUTTON_COLUMN);
            if (rb.getText().equals(factorName)) {
                rb.setValue(true);
                break;
            }
        }
    }
    
    /**
     * Returns true if the user has selected sufficient information
     */
    @Override
    public boolean checkComplete() {
        return (this.selectedBetweenParticipantFactor != null || 
                this.selectedRepeatedMeasuresNode != null);
    }

    @Override
    public NamedMatrix buildThetaNull() {
        return null;
    }
}
