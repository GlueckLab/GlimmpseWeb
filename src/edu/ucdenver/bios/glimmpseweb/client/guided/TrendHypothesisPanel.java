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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Category;
import edu.ucdenver.bios.webservice.common.domain.Hypothesis;
import edu.ucdenver.bios.webservice.common.domain.HypothesisBetweenParticipantMapping;
import edu.ucdenver.bios.webservice.common.domain.HypothesisRepeatedMeasuresMapping;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTypeEnum;

public class TrendHypothesisPanel extends Composite
implements HypothesisBuilder {
    // radio button group for this panel
    private static final String BUTTON_GROUP = "trendButtonGroup"; 

    // lists of variables available to test
    protected FlexTable betweenParticipantFactorsFlexTable = new FlexTable();
    protected FlexTable withinParticipantFactorsFlexTable = new FlexTable();

    // currently selected between participant effect
    protected BetweenParticipantFactor selectedBetweenParticipantFactor = null;
    // name of currently selected within participant effect
    protected RepeatedMeasuresNode selectedRepeatedMeasuresNode = null;

    // trend description panel
    protected EditTrendPanel editTrendPanel =  new EditTrendPanel("_TREND_PANEL_", -1); // TODO

    // parent panel
    protected ClickHandler parent = null;
    
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
     * Create a trend hypothesis panel
     * @param studyDesignContext
     */
    public TrendHypothesisPanel(ClickHandler handler)
    {
        VerticalPanel verticalPanel = new VerticalPanel();

        parent = handler;
        
        HTML text = new HTML(GlimmpseWeb.constants.trendHypothesisPanelText());
        HTML betweenParticipantFactors = 
                new HTML(GlimmpseWeb.constants.hypothesisPanelBetweenParticipantFactorsLabel());
        HTML withinParticipantFactors = 
                new HTML(GlimmpseWeb.constants.hypothesisPanelWithinParticipantFactorsLabel());
        final HTML selectTypeOfTrend = new HTML(
                GlimmpseWeb.constants.hypothesisPanelSelectTypeOfTrend());

        //Style Sheets
        text.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        betweenParticipantFactors.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        betweenParticipantFactors.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        withinParticipantFactors.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        withinParticipantFactors.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);
        selectTypeOfTrend.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        selectTypeOfTrend.addStyleDependentName(GlimmpseConstants.STYLE_WIZARD_STEP_SUBPANEL);

        // layout the panel
        verticalPanel.add(text);
        verticalPanel.add(betweenParticipantFactors);
        verticalPanel.add(betweenParticipantFactorsFlexTable);
        verticalPanel.add(withinParticipantFactors);
        verticalPanel.add(withinParticipantFactorsFlexTable);
        verticalPanel.add(selectTypeOfTrend);
        verticalPanel.add(editTrendPanel);
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
                        }
                    });
                    betweenParticipantFactorsFlexTable.setWidget(
                            row, 0, button);
                }
                row++;
            }
        }
    }

    /**
     * Load repeated measures from the context
     * @param rmNodeList tree of repeated measures information
     */
    public void loadRepeatedMeasures(List<RepeatedMeasuresNode> rmNodeList) {
        this.selectedRepeatedMeasuresNode = null;
        withinParticipantFactorsFlexTable.removeAllRows();
        if (rmNodeList != null) {
            int row = 0;
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
                    }
                });
                withinParticipantFactorsFlexTable.setWidget(
                        row, 0, button);
                row++;
            }
        }
    }

    /**
     * Create a hypothesis object from the panel
     */
    @Override
    public Hypothesis buildHypothesis() {
        Hypothesis hypothesis = new Hypothesis();
        hypothesis.setType(HypothesisTypeEnum.TREND);

        if (selectedRepeatedMeasuresNode != null) {
            HypothesisRepeatedMeasuresMapping mappingNode =
                new HypothesisRepeatedMeasuresMapping();
            mappingNode.setRepeatedMeasuresNode(selectedRepeatedMeasuresNode);
            mappingNode.setType(editTrendPanel.getSelectedTrend());
            List<HypothesisRepeatedMeasuresMapping> mappingList =
                new ArrayList<HypothesisRepeatedMeasuresMapping>();
            mappingList.add(mappingNode);
            hypothesis.setRepeatedMeasuresMapTree(mappingList);
            
        } else if (selectedBetweenParticipantFactor != null) {
            HypothesisBetweenParticipantMapping mappingParticipant =
                new HypothesisBetweenParticipantMapping();
            mappingParticipant.setBetweenParticipantFactor(selectedBetweenParticipantFactor);
            mappingParticipant.setType(editTrendPanel.getSelectedTrend());
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

    @Override
    public NamedMatrix buildThetaNull() {
        return null;
    }
}
