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
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.HtmlTextExplainPanel;
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
 *
 */
public class HypothesisPanel extends WizardStepPanel 
implements ClickHandler {

    // context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    
    // radio button group for hypothesis types
    private static final String BUTTON_GROUP = "HypothesisRadioButtonGroup";
    
    // deck panel order
    private static final int MAIN_EFFECT_INDEX = 0;
    private static final int INTERACTION_INDEX = 1;
    private static final int TREND_INDEX = 2;
    
    // contains subpanels for different types of hypotheses (main effect, interaction, etc)
    DeckPanel deckPanel = new DeckPanel();

    // subpanels for each type of hypothesis
    MainEffectHypothesisPanel mainEffectHypothesisPanelInstance =
        new MainEffectHypothesisPanel(studyDesignContext, this);
    InteractionHypothesisPanel interactionHypothesisPanelInstance =
        new InteractionHypothesisPanel(studyDesignContext, this);
    TrendHypothesisPanel trendHypothesisPanelInstance =
        new TrendHypothesisPanel(studyDesignContext, this);
    
    /* hypothesis type buttons */
    RadioButton mainEffectRadioButton = new RadioButton(
            BUTTON_GROUP,
            GlimmpseWeb.constants.hypothesisPanelMainEffect()); 
    RadioButton interactionRadioButton = new RadioButton(
            BUTTON_GROUP,
            GlimmpseWeb.constants.hypothesisPanelInteraction()); 
    RadioButton trendRadioButton = new RadioButton(
            BUTTON_GROUP,
            GlimmpseWeb.constants.hypothesisPanelTrend());

    /**
     * Constructor
     * @param context wizard context
     */
    public HypothesisPanel(WizardContext context)
    {		
        super(context, GlimmpseWeb.constants.navItemHypothesis(),
                WizardStepPanelState.NOT_ALLOWED);

        VerticalPanel verticalPanel = new VerticalPanel();
        
        // descriptive text
        HTML title = new HTML(
                GlimmpseWeb.constants.hypothesisPanelTitle());
        HTML description = new HTML(
                GlimmpseWeb.constants.hypothesisPanelDescription());
        HTML instructions = new HTML(
                GlimmpseWeb.constants.hypothesisPanelInstructions());

        // hypothesis type selection panel
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        HtmlTextExplainPanel mainEffect = new HtmlTextExplainPanel("",
                GlimmpseWeb.constants.hypothesisPanelMainEffect(),
                GlimmpseWeb.constants.hypothesisPanelMainEffectExplination());
        horizontalPanel.add(mainEffectRadioButton);
        horizontalPanel.add(mainEffect);
        
        HtmlTextExplainPanel interaction = new HtmlTextExplainPanel("",
                GlimmpseWeb.constants.hypothesisPanelInteraction(),
                GlimmpseWeb.constants.hypothesisPanelInteractionExplination());
        horizontalPanel.add(interactionRadioButton);
        horizontalPanel.add(interaction);

        HtmlTextExplainPanel trend = new HtmlTextExplainPanel("",
                GlimmpseWeb.constants.hypothesisPanelTrend(),
                GlimmpseWeb.constants.hypothesisPanelTrendExplination());
        horizontalPanel.add(trendRadioButton);
        horizontalPanel.add(trend);		

        // subpanels for each hypothesis type
        deckPanel.add(mainEffectHypothesisPanelInstance);
        deckPanel.add(interactionHypothesisPanelInstance);
        deckPanel.add(trendHypothesisPanelInstance);
        
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
        verticalPanel.add(title);
        verticalPanel.add(description);
        verticalPanel.add(instructions);
        verticalPanel.add(horizontalPanel);
        verticalPanel.add(deckPanel);
  
        // set style
        title.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        instructions.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        // initialize
        initWidget(verticalPanel);
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
        case REPEATED_MEASURES:
            List<BetweenParticipantFactor> factorList = 
                studyDesignContext.getStudyDesign().getBetweenParticipantFactorList();
            List<RepeatedMeasuresNode> rmNodeList = 
                studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
            if ((factorList != null && factorList.size() > 0) || 
                    (rmNodeList != null && rmNodeList.size() > 0)) {
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
}
