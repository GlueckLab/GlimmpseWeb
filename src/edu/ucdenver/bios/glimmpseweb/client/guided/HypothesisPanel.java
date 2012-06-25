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
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.HtmlTextWithExplanationPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;

/**
 * Hypothesis selection panel
 * @author VIJAY AKULA
 * @author Sarah Kreidler
 *
 */
public class HypothesisPanel extends WizardStepPanel 
implements ClickHandler, ChangeHandler {

    // context object
    protected StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    
    // radio button group for hypothesis types
    private static final String BUTTON_GROUP = "HypothesisRadioButtonGroup";
    
    // deck panel order
    private static final int ONE_SAMPLE_INDEX = 0;
    private static final int MAIN_EFFECT_INDEX = 1;
    private static final int INTERACTION_INDEX = 2;
    private static final int TREND_INDEX = 3;
    
    // contains subpanels for different types of hypotheses (main effect, interaction, etc)
    protected DeckPanel deckPanel = new DeckPanel();

    // subpanels for each type of hypothesis
    protected OneSampleHypothesisPanel oneSampleHypothesisPanelInstance =
        new OneSampleHypothesisPanel(studyDesignContext, this);
    protected MainEffectHypothesisPanel mainEffectHypothesisPanelInstance =
        new MainEffectHypothesisPanel(studyDesignContext, this);
    protected InteractionHypothesisPanel interactionHypothesisPanelInstance =
        new InteractionHypothesisPanel(studyDesignContext, this);
    protected TrendHypothesisPanel trendHypothesisPanelInstance =
        new TrendHypothesisPanel(studyDesignContext, this);
    
    /* hypothesis type buttons */
    protected RadioButton oneSampleRadioButton = new RadioButton(
            BUTTON_GROUP,
            GlimmpseWeb.constants.hypothesisPanelOneSample()); 
    protected RadioButton mainEffectRadioButton = new RadioButton(
            BUTTON_GROUP,
            GlimmpseWeb.constants.hypothesisPanelMainEffect()); 
    protected RadioButton interactionRadioButton = new RadioButton(
            BUTTON_GROUP,
            GlimmpseWeb.constants.hypothesisPanelInteraction()); 
    protected RadioButton trendRadioButton = new RadioButton(
            BUTTON_GROUP,
            GlimmpseWeb.constants.hypothesisPanelTrend());
    // hypothesis type containers - allows us to hide/show these 
    // depending on the between/within participant factors
    protected  HorizontalPanel oneSampleSelectPanel = new HorizontalPanel();
    protected  HorizontalPanel mainEffectSelectPanel = new HorizontalPanel();
    protected  HorizontalPanel interactionSelectPanel = new HorizontalPanel();
    protected  HorizontalPanel trendSelectPanel = new HorizontalPanel();

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
        HtmlTextWithExplanationPanel oneSample = new HtmlTextWithExplanationPanel("",
                GlimmpseWeb.constants.hypothesisPanelOneSample(),
                GlimmpseWeb.constants.hypothesisPanelOneSampleExplanation());
        oneSampleSelectPanel.add(oneSampleRadioButton);
        oneSampleSelectPanel.add(oneSample);
        // main effects
        HtmlTextWithExplanationPanel mainEffect = new HtmlTextWithExplanationPanel("",
                GlimmpseWeb.constants.hypothesisPanelMainEffect(),
                GlimmpseWeb.constants.hypothesisPanelMainEffectExplanation());
        mainEffectSelectPanel.add(mainEffectRadioButton);
        mainEffectSelectPanel.add(mainEffect);
        // interaction
        HtmlTextWithExplanationPanel interaction = new HtmlTextWithExplanationPanel("",
                GlimmpseWeb.constants.hypothesisPanelInteraction(),
                GlimmpseWeb.constants.hypothesisPanelInteractionExplanation());
        interactionSelectPanel.add(interactionRadioButton);
        interactionSelectPanel.add(interaction);
        // trend
        HtmlTextWithExplanationPanel trend = new HtmlTextWithExplanationPanel("",
                GlimmpseWeb.constants.hypothesisPanelTrend(),
                GlimmpseWeb.constants.hypothesisPanelTrendExplanation());
        trendSelectPanel.add(trendRadioButton);
        trendSelectPanel.add(trend);		

        // layout overall panel
        HorizontalPanel typeSelectPanel = new HorizontalPanel();
        typeSelectPanel.add(oneSampleSelectPanel);
        typeSelectPanel.add(mainEffectSelectPanel);
        typeSelectPanel.add(interactionSelectPanel);
        typeSelectPanel.add(trendSelectPanel);

        // subpanels for each hypothesis type
        HorizontalPanel contentPanel = new HorizontalPanel();
        deckPanel.add(oneSampleHypothesisPanelInstance);
        deckPanel.add(mainEffectHypothesisPanelInstance);
        deckPanel.add(interactionHypothesisPanelInstance);
        deckPanel.add(trendHypothesisPanelInstance);
        contentPanel.add(deckPanel);
        
        // add radio button handlers
        mainEffectRadioButton.addClickHandler(new ClickHandler() 
        {
            @Override
            public void onClick(ClickEvent event)
            {			
                showWidget(MAIN_EFFECT_INDEX);
            }
        });
        interactionRadioButton.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event) 
            {
                showWidget(INTERACTION_INDEX);
            }
        });
        trendRadioButton.addClickHandler(new ClickHandler() 
        {
            @Override
            public void onClick(ClickEvent event) 
            {
                showWidget(TREND_INDEX);
            }
        });

        // layout panel
        panel.add(title);
        panel.add(description);
        panel.add(typeSelectPanel);
        panel.add(contentPanel);
  
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        title.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        typeSelectPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TAB_HEADER);
        contentPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TAB_CONTENT);

        // initialize
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
    public void onWizardContextChange(WizardContextChangeEvent e) 
    {
        switch(((StudyDesignChangeEvent) e).getType()) {
        case BETWEEN_PARTICIPANT_FACTORS:
            List<BetweenParticipantFactor> factorList = 
                studyDesignContext.getStudyDesign().getBetweenParticipantFactorList();
            if ((factorList != null && factorList.size() > 0)) {
                checkComplete();
            } else {
                changeState(WizardStepPanelState.NOT_ALLOWED);
            }
            break;
        case REPEATED_MEASURES:
            List<RepeatedMeasuresNode> rmNodeList = 
                studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
            if ((rmNodeList != null && rmNodeList.size() > 0)) {
                checkComplete();
            } else {
                changeState(WizardStepPanelState.NOT_ALLOWED);
            }
            break;
        }
    };

    /**
     * Update the screen when the predictors or repeated measures change
     */
    @Override
    public void onWizardContextLoad() 
    {
        
    }

    private void checkComplete() {
        // get the currently visible widget
        if (deckPanel.getVisibleWidget() > -1) {
            HypothesisBuilder builder = (HypothesisBuilder) deckPanel.getWidget(deckPanel.getVisibleWidget());
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
        if (deckPanel.getVisibleWidget() > -1) {
            HypothesisBuilder builder = (HypothesisBuilder) deckPanel.getWidget(deckPanel.getVisibleWidget());
            if (builder != null) {
                studyDesignContext.setHypothesis(this, builder.buildHypothesis());
            } else {
                studyDesignContext.setHypothesis(this, null);
            }
        } else {
            studyDesignContext.setHypothesis(this, null);
        }
    }

    /**
     * Display the widget at the specified index
     * @param index index in deck panel
     */
    private void showWidget(int index) {
        deckPanel.showWidget(index);
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
