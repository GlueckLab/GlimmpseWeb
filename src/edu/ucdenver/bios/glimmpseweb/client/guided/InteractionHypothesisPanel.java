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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Category;
import edu.ucdenver.bios.webservice.common.domain.Hypothesis;
import edu.ucdenver.bios.webservice.common.domain.HypothesisBetweenParticipantMapping;
import edu.ucdenver.bios.webservice.common.domain.HypothesisRepeatedMeasuresMapping;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTrendTypeEnum;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTypeEnum;

public class InteractionHypothesisPanel extends Composite
{
    // study design context
    protected StudyDesignContext studyDesignContext = null;
    // parent panel
    protected HypothesisPanel parent = null;

    // counter of #variables selected
    int selectedCount = 0;

    // variable lists
    protected FlexTable betweenParticipantFactorsFlexTable = new FlexTable();
    protected FlexTable withinParticipantFactorsFlexTable = new FlexTable();
    // labels for variable lists
    HTML betweenParticipantFactors = 
        new HTML(GlimmpseWeb.constants.hypothesisPanelBetweenParticipantFactorsLabel());
    HTML withinParticipantFactors = 
        new HTML(GlimmpseWeb.constants.hypothesisPanelWithinParticipantFactorsLabel());
    
    
    // panel which contains a between participant effect
    private class BetweenParticipantVariablePanel 
    extends InteractionVariablePanel {
        public BetweenParticipantFactor factor;
        public BetweenParticipantVariablePanel(String label, 
                BetweenParticipantFactor factor) {
            super(label, factor.getCategoryList().size());
            addVariableClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    CheckBox cb = (CheckBox) event.getSource();
                    updateFactor(cb.getValue());
                } 
            });
            addTrendClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    TrendRadioButton rb = (TrendRadioButton) event.getSource();
                    updateTrend(rb.getTrend());
                }
            });
            this.factor = factor;
        }
        
        private void updateTrend(HypothesisTrendTypeEnum type) {
            updateBetweenParticipantFactorTrend(factor, type);
        }
        
        private void updateFactor(boolean selected) {
            selectBetweenParticipantFactor(selected, factor);
        }
    }
    // panel which contains a repeated measures effect
    private class RepeatedMeasuresVariablePanel  
    extends InteractionVariablePanel {
        public RepeatedMeasuresNode factor;
        public RepeatedMeasuresVariablePanel(String label, 
                RepeatedMeasuresNode factor) {
            super(label, factor.getNumberOfMeasurements());
            this.factor = factor;
            addVariableClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    CheckBox cb = (CheckBox) event.getSource();
                    updateFactor(cb.getValue());
                } 
            });
            addTrendClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    TrendRadioButton rb = (TrendRadioButton) event.getSource();
                    updateTrend(rb.getTrend());
                }
            });
        }
        
        private void updateTrend(HypothesisTrendTypeEnum type) {
            updateWithinParticipantFactorTrend(factor, type);
        }
        
        private void updateFactor(boolean selected) {
            selectWithinParticipantFactor(selected, factor);
        }
    }   

    /**
     * Constructor
     * @param studyDesignContext
     * @param handler
     */
    public InteractionHypothesisPanel(StudyDesignContext context,
            HypothesisPanel parent)
    {
        this.studyDesignContext = context;
        this.parent = parent;

        VerticalPanel verticalPanel = new VerticalPanel();

        HTML text = new HTML(GlimmpseWeb.constants.interactionHypothesisPanelText());

        // hide the flex table labels until we have some rows in the tables
        betweenParticipantFactors.setVisible(false);
        withinParticipantFactors.setVisible(false);
        
        //Style Sheets
        text.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        betweenParticipantFactors.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_SUBHEADER);
        withinParticipantFactors.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_SUBHEADER);
        verticalPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DECK_PANEL_SUBPANEL);

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
        betweenParticipantFactorsFlexTable.removeAllRows();
        if (factorList != null) {
            int row = 0;
            for(BetweenParticipantFactor factor : factorList)
            {
                List<Category> categoryList = factor.getCategoryList();
                if (categoryList != null && categoryList.size() > 1) {
                    BetweenParticipantVariablePanel panel = 
                        new BetweenParticipantVariablePanel(
                                factor.getPredictorName(), factor);
                    betweenParticipantFactorsFlexTable.setWidget(
                            row, 0, panel);
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
        withinParticipantFactorsFlexTable.removeAllRows();
        if (rmNodeList != null) {
            int row = 0;
            for(RepeatedMeasuresNode rmNode : rmNodeList)
            {
                if (rmNode.getDimension() != null &&
                        !rmNode.getDimension().isEmpty() &&
                        rmNode.getNumberOfMeasurements() != null &&
                        rmNode.getNumberOfMeasurements() > 1) {
                    RepeatedMeasuresVariablePanel panel = 
                        new RepeatedMeasuresVariablePanel(
                                rmNode.getDimension(), rmNode);
                    withinParticipantFactorsFlexTable.setWidget(
                            row, 0, panel);
                    row++;
                }
            }
        }
        // hide the label if no factors of this type
        withinParticipantFactors.setVisible(
                withinParticipantFactorsFlexTable.getRowCount() > 0);
    }

    /**
     * Load the hypothesis information.  Should be called after 
     * loadBetweenParticipantFactors and loadRepeatedMeasures
     */
    public void loadHypothesis(Hypothesis hypothesis) {
        // load the hypothesis info
        selectedCount = 0;
        if (hypothesis != null && 
                HypothesisTypeEnum.INTERACTION == hypothesis.getType()) {
            // load between participant factors
            List<HypothesisBetweenParticipantMapping> btwnFactorList = 
                hypothesis.getBetweenParticipantFactorMapList();
            if (btwnFactorList != null && btwnFactorList.size() > 0) {
                for(HypothesisBetweenParticipantMapping factorMapping: btwnFactorList) {
                    BetweenParticipantFactor factor = 
                        btwnFactorList.get(0).getBetweenParticipantFactor();
                    if (factor != null) {
                        selectCheckBoxByFactor(factor.getPredictorName(), 
                                factorMapping.getType(),
                                betweenParticipantFactorsFlexTable);
                    }
                }
            } 
            // load within factor info
            List<HypothesisRepeatedMeasuresMapping> withinFactorList = 
                hypothesis.getRepeatedMeasuresMapTree();
            if (withinFactorList != null && withinFactorList.size() > 0) {
                for(HypothesisRepeatedMeasuresMapping factorMapping: withinFactorList) {
                    RepeatedMeasuresNode factor = 
                        withinFactorList.get(0).getRepeatedMeasuresNode();
                    if (factor != null) {
                        String factorName = factor.getDimension();
                        selectCheckBoxByFactor(factorName,
                                factorMapping.getType(),
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
     * @param trendType trend associated with the factor
     * @param table the between or within participant factor table
     */
    private void selectCheckBoxByFactor(String factorName, 
            HypothesisTrendTypeEnum trendType, FlexTable table) {
        for(int row = 0; row < table.getRowCount(); row++) {
            InteractionVariablePanel ivPanel = 
                (InteractionVariablePanel) table.getWidget(row, 0);
            if (ivPanel.getLabel().equals(factorName)) {
                ivPanel.setChecked(true);
                ivPanel.setTrend(trendType);
                selectedCount++;
                break;
            }
        }
    }

    /**
     * Panel is complete provided two or more variables are selected
     */
    public boolean checkComplete() {     
        return (selectedCount > 1);
    }

    /**
     * Update the between participant variable list for the hypothesis
     * @param selected
     * @param factor
     */
    private void selectBetweenParticipantFactor(boolean selected,
            BetweenParticipantFactor factor) {
        if (selected) {
            selectedCount++;
            studyDesignContext.addHypothesisBetweenParticipantFactor(parent, factor);
        } else {
            selectedCount--;
            studyDesignContext.deleteHypothesisBetweenParticipantFactor(parent, factor);
        }
        parent.checkComplete();
    }
    
    /**
     * Update the between participant variable trend for the hypothesis
     * @param selected
     * @param factor
     * @param trendType
     */
    private void updateBetweenParticipantFactorTrend(
            BetweenParticipantFactor factor, HypothesisTrendTypeEnum trendType) {
        studyDesignContext.updateHypothesisBetweenParticipantFactorTrend(
                parent, factor, trendType);
        parent.checkComplete();
    }
    
    /**
     * Update the within participant variable trend for the hypothesis
     * @param selected
     * @param factor
     * @param trendType
     */
    private void updateWithinParticipantFactorTrend(
            RepeatedMeasuresNode factor, HypothesisTrendTypeEnum trendType) {
        studyDesignContext.updateHypothesisRepeatedMeasuresFactorTrend(
                parent, factor, trendType);
        parent.checkComplete();
    }
    
    /**
     * Update the within participant variable list for the hypothesis
     * @param selected
     * @param factor
     */
    private void selectWithinParticipantFactor(boolean selected,
            RepeatedMeasuresNode factor) {
        if (selected) {
            selectedCount++;
            studyDesignContext.addHypothesisRepeatedMeasuresFactor(parent, factor);
        } else {
            selectedCount--;
            studyDesignContext.deleteHypothesisRepeatedMeasuresFactor(parent, factor);
        }
        parent.checkComplete();
    }

    
    /**
     * Forces panel to update the studyDesign object with current selections.
     * Called by parent panel when the user selects the interaction hypothesis.
     */
    public void syncStudyDesign() {
        studyDesignContext.clearHypothesisFactors(parent);
        for(int row = 0; row < betweenParticipantFactorsFlexTable.getRowCount(); row++) {
            BetweenParticipantVariablePanel panel = 
                (BetweenParticipantVariablePanel) 
                betweenParticipantFactorsFlexTable.getWidget(row, 0);
            if (panel.isChecked()) {
                studyDesignContext.addHypothesisBetweenParticipantFactor(parent, 
                        panel.factor);
                studyDesignContext.updateHypothesisBetweenParticipantFactorTrend(parent, 
                        panel.factor, panel.getSelectedTrend());
            }
        }
        for(int row = 0; row < withinParticipantFactorsFlexTable.getRowCount(); row++) {
            RepeatedMeasuresVariablePanel panel = 
                (RepeatedMeasuresVariablePanel) 
                withinParticipantFactorsFlexTable.getWidget(row, 0);
            if (panel.isChecked()) {
                studyDesignContext.addHypothesisRepeatedMeasuresFactor(parent, 
                        panel.factor);
                studyDesignContext.updateHypothesisRepeatedMeasuresFactorTrend(parent, 
                        panel.factor, panel.getSelectedTrend());
            }
        }
    }
    
}
