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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.DynamicTabPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.HtmlTextWithExplanationPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Category;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;

/**
 * Hypothesis selection panel
 * @author VIJAY AKULA
 * @author Sarah Kreidler
 *
 */
public class HypothesisPanel extends WizardStepPanel 
implements ClickHandler, ChangeHandler {

    // clickable panel
    private class ClickableHorizontalPanel extends HorizontalPanel 
    implements HasClickHandlers {
        @Override
        public HandlerRegistration addClickHandler(ClickHandler handler) {
            return addDomHandler(handler, ClickEvent.getType());
        }
    }
    
    // context object
    protected StudyDesignContext studyDesignContext = (StudyDesignContext) context;

    // subpanels for each type of hypothesis
    protected GrandMeanHypothesisPanel grandMeanHypothesisPanelInstance =
        new GrandMeanHypothesisPanel(this);
    protected MainEffectHypothesisPanel mainEffectHypothesisPanelInstance =
        new MainEffectHypothesisPanel(this);
    protected InteractionHypothesisPanel interactionHypothesisPanelInstance =
        new InteractionHypothesisPanel(this);
    protected TrendHypothesisPanel trendHypothesisPanelInstance =
        new TrendHypothesisPanel(this);
    
    /* hypothesis type buttons */
    protected HTML grandMeanHTML = new HTML(
            GlimmpseWeb.constants.hypothesisPanelGrandMean()); 
    protected HTML mainEffectHTML = new HTML(
            GlimmpseWeb.constants.hypothesisPanelMainEffect()); 
    protected HTML interactionHTML = new HTML(
            GlimmpseWeb.constants.hypothesisPanelInteraction()); 
    protected HTML trendHTML = new HTML(
            GlimmpseWeb.constants.hypothesisPanelTrend());
    
    // hypothesis type containers
    protected  ClickableHorizontalPanel grandMeanSelectPanel = 
            new ClickableHorizontalPanel();
    protected  ClickableHorizontalPanel mainEffectSelectPanel = 
            new ClickableHorizontalPanel();
    protected  ClickableHorizontalPanel interactionSelectPanel = 
            new ClickableHorizontalPanel();
    protected  ClickableHorizontalPanel trendSelectPanel = 
            new ClickableHorizontalPanel();

    // tab panel to organize the hypothesis sub screens
    DynamicTabPanel tabPanel = new DynamicTabPanel();
    
    /**
     * Constructor
     * @param context wizard context
     */
    public HypothesisPanel(WizardContext context)
    {		
        super(context, GlimmpseWeb.constants.navItemHypothesis(),
                WizardStepPanelState.NOT_ALLOWED);

        VerticalPanel panel = new VerticalPanel();
        
        // descriptive text
        HTML title = new HTML(
                GlimmpseWeb.constants.hypothesisTitle());
        HTML description = new HTML(
                GlimmpseWeb.constants.hypothesisDescription());

        // hypothesis type selection panel
        // one sample 
        grandMeanSelectPanel.add(grandMeanHTML);
        grandMeanSelectPanel.add(new HtmlTextWithExplanationPanel("",
                GlimmpseWeb.constants.hypothesisPanelGrandMean(),
                GlimmpseWeb.constants.hypothesisPanelGrandMeanExplanation()));

        // main effects
        mainEffectSelectPanel.add(mainEffectHTML);
        mainEffectSelectPanel.add(new HtmlTextWithExplanationPanel("",
                GlimmpseWeb.constants.hypothesisPanelMainEffect(),
                GlimmpseWeb.constants.hypothesisPanelMainEffectExplanation()));
        
        // interaction
        interactionSelectPanel.add(interactionHTML);
        interactionSelectPanel.add(new HtmlTextWithExplanationPanel("",
                GlimmpseWeb.constants.hypothesisPanelInteraction(),
                GlimmpseWeb.constants.hypothesisPanelInteractionExplanation()));

        // trend
        trendSelectPanel.add(trendHTML);
        trendSelectPanel.add(new HtmlTextWithExplanationPanel("",
                GlimmpseWeb.constants.hypothesisPanelTrend(),
                GlimmpseWeb.constants.hypothesisPanelTrendExplanation()));

        // add tabs to the tab panel
        tabPanel.add(grandMeanSelectPanel, (Widget) grandMeanHypothesisPanelInstance);
        tabPanel.addClickHandler(this);
        
        // layout panel
        panel.add(title);
        panel.add(description);
        panel.add(tabPanel);
  
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        title.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        // initialize
        updateHypothesisOptions();
        initWidget(panel);
    }

    /**
     * 
     */
    @Override
    public void reset()
    {
        changeState(WizardStepPanelState.NOT_ALLOWED);
    }

    /**
     * Respond to a change in the context object
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) {
        switch(((StudyDesignChangeEvent) e).getType()) {
        case RESPONSES_LIST:
            grandMeanHypothesisPanelInstance.loadResponseList(
                    studyDesignContext.getStudyDesign().getResponseList());
            updateHypothesisOptions();
            break;
        case BETWEEN_PARTICIPANT_FACTORS:
            List<BetweenParticipantFactor> factorList = 
                studyDesignContext.getStudyDesign().getBetweenParticipantFactorList();
            mainEffectHypothesisPanelInstance.loadBetweenParticipantFactors(factorList);
            interactionHypothesisPanelInstance.loadBetweenParticipantFactors(factorList);
            trendHypothesisPanelInstance.loadBetweenParticipantFactors(factorList);
            updateHypothesisOptions();
            break;
        case REPEATED_MEASURES:
            List<RepeatedMeasuresNode> rmNodeList = 
                studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
            mainEffectHypothesisPanelInstance.loadRepeatedMeasures(rmNodeList);
            interactionHypothesisPanelInstance.loadRepeatedMeasures(rmNodeList);
            trendHypothesisPanelInstance.loadRepeatedMeasures(rmNodeList);
            updateHypothesisOptions();
            break;
        }
    };
    
    /**
     * Show or hide specific hypothesis classes depending on the 
     * number of factors, etc.
     */
    private void updateHypothesisOptions() {
        List<BetweenParticipantFactor> factorList = 
                studyDesignContext.getStudyDesign().getBetweenParticipantFactorList();
        List<RepeatedMeasuresNode> rmNodeList = 
                studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
        List<ResponseNode> responsesList = 
                studyDesignContext.getStudyDesign().getResponseList();
        int totalFactors = 0;
        int maxLevels = 0;
        int totalMultiCategoryFactors = 0;
        if (factorList != null) {
            totalFactors += factorList.size();
            for(BetweenParticipantFactor factor: factorList) {
                List<Category> categoryList = factor.getCategoryList();
                if (categoryList != null) {
                    if (categoryList.size() > maxLevels) {
                        maxLevels = categoryList.size();
                    }
                    if (categoryList.size() > 1) {
                        totalMultiCategoryFactors++;
                    }
                }
            }
        }
        if (rmNodeList != null) {
            totalFactors += rmNodeList.size();
            for(RepeatedMeasuresNode node: rmNodeList) {
                if (node.getNumberOfMeasurements() > maxLevels) {
                    maxLevels = node.getNumberOfMeasurements();
                }
                if (node.getNumberOfMeasurements() > 1) {
                    totalMultiCategoryFactors++;
                }
            }
        }
        
        // show the hypotheses based on the number of factors
        tabPanel.remove(mainEffectSelectPanel);
        tabPanel.remove(interactionSelectPanel);
        tabPanel.remove(trendSelectPanel);
        if (totalFactors > 0 && maxLevels > 1) {
            tabPanel.add(mainEffectSelectPanel, (Widget) mainEffectHypothesisPanelInstance);
            tabPanel.add(trendSelectPanel, (Widget) trendHypothesisPanelInstance);
        } 
        if (totalMultiCategoryFactors > 1) {
            tabPanel.add(interactionSelectPanel, (Widget) interactionHypothesisPanelInstance);
        }
        tabPanel.openTab(0);
        
        // if no factors or one-sample with no responses, set state to not-allowed.  
        // Otherwise check if the panel is complete
        if (totalFactors <= 0 || 
                (totalFactors == 1 && 
                (responsesList == null || responsesList.size() == 0))) {
            changeState(WizardStepPanelState.NOT_ALLOWED);
        } else {
            checkComplete();
        }
    }

    /**
     * Update the screen when the predictors or repeated measures change
     */
    @Override
    public void onWizardContextLoad() 
    {
        
    }

    private void checkComplete() {
        // get the currently visible widget
        HypothesisBuilder builder = (HypothesisBuilder) tabPanel.getVisibleWidget();
        if (builder != null) { 
            if (builder.checkComplete()) {
                changeState(WizardStepPanelState.COMPLETE);
            } else {
                changeState(WizardStepPanelState.INCOMPLETE);
            }
        } else {
            changeState(WizardStepPanelState.INCOMPLETE);
        }
    }
    
    /**
     * Build the hypothesis object and store in the context
     */
    public void onExit()
    {
        // get the currently visible widget
        HypothesisBuilder builder = (HypothesisBuilder) tabPanel.getVisibleWidget();
        if (builder != null) {
            studyDesignContext.setHypothesis(this, builder.buildHypothesis());
            NamedMatrix thetaNull = builder.buildThetaNull();
            if (thetaNull != null) {
                studyDesignContext.setThetaNull(this, thetaNull);
            }
        } else {
            studyDesignContext.setHypothesis(this, null);
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        checkComplete();        
    }

    @Override
    public void onChange(ChangeEvent event) {
        checkComplete();   
    }
}
