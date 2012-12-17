/*
 * Web Interface for the GLIMMPSE Software System.  Allows
 * users to perform power, sample size, and detectable difference
 * calculations. 
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
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
package edu.ucdenver.bios.glimmpseweb.client.wizard;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ToolsMenuPanel;

/**
 * Generic wizard panel including a left navigation bar,
 * panel display area, and toolbar
 * 
 * @author Sarah Kreidler
 *
 */
public class WizardPanel extends Composite 
implements WizardActionListener, WizardContextListener, 
WizardStepPanelStateChangeHandler, ClickHandler
{
    // style for the main display area
    protected static final String STYLE_WIZARD_PANEL = "wizardPanel";
    protected static final String STYLE_WIZARD_CONTENT_PANEL = "wizardContentPanel";
    protected static final String STYLE_FINISH_BUTTON = "wizardFinishButton";
    // wizard context
    protected WizardContext context = null;
    // main panel
    protected VerticalPanel panel = new VerticalPanel();
    // left navigation / "steps left" panel
    protected WizardLeftNavigationPanel leftNavPanel;
    // finish button
    protected Button finishButton =  new Button(GlimmpseWeb.constants.buttonFinish(), 
            new ClickHandler() {
        public void onClick(ClickEvent event) {
            finish();
        }
    });
    // save, help, cancel
    protected WizardActionPanel actionPanel = new WizardActionPanel();
    // currently visible panel
    protected WizardStepPanel currentStep = null;  
    // panel to display the results of the wizard
    protected WizardStepPanel finishPanel = null;  
    // deck panel containing all steps in the input wizard
    protected DeckPanel wizardDeck = new DeckPanel();

    /**
     * Create a wizard panel with the specified groups of panels
     * 
     * @param wizardPanelGroups list of panel groups to display in the wizard
     */
    public WizardPanel(WizardContext context, 
            List<WizardStepPanelGroup> wizardPanelGroups, WizardStepPanel finishPanel)
    {		
        // store the context
        this.context = context;
        // listen for context changes
        this.context.addContextListener(this);
        // store the finish panel
        this.finishPanel = finishPanel;

        // create overall panel layout containers
        HorizontalPanel upperLayoutPanel = new HorizontalPanel();
        VerticalPanel contentPanel = new VerticalPanel();
        VerticalPanel leftPanel = new VerticalPanel();
        
        // layout the left navigation
        leftNavPanel = new WizardLeftNavigationPanel(wizardPanelGroups);
        leftPanel.add(finishButton);
        leftPanel.add(leftNavPanel);

        // layout the display area and next/prev toolbar
        contentPanel.add(wizardDeck);        

        // layout the overall  wizard panel
        upperLayoutPanel.add(leftPanel);		
        upperLayoutPanel.add(contentPanel);
        panel.add(upperLayoutPanel);
        panel.add(actionPanel);

        // add the panels to the display deck
        for(WizardStepPanelGroup panelGroup: wizardPanelGroups)
        {
            wizardDeck.add(panelGroup.getIntroPanel());
            for(WizardStepPanel step: panelGroup.getPanelList())
            {
                wizardDeck.add(step);
                step.addChangeHandler(this);
                step.addChangeHandler(leftNavPanel);
            }
        }
        // add the finish panel to the deck
        wizardDeck.add(finishPanel);

        // add callbacks for events from the navigation and toolbar subpanels
        leftNavPanel.addActionListener(this);
        actionPanel.addActionListener(this);

        // disable the calculate button to start
        finishButton.setEnabled(false);

        // add style
        leftPanel.setStyleName(GlimmpseConstants.STYLE_LEFT_PANEL);
        contentPanel.setStyleName(GlimmpseConstants.STYLE_RIGHT_PANEL);
        finishButton.setStyleName(STYLE_FINISH_BUTTON);
        finishButton.addStyleDependentName(GlimmpseConstants.STYLE_DISABLED);
        panel.setStyleName(STYLE_WIZARD_PANEL);
        wizardDeck.setStyleName(STYLE_WIZARD_CONTENT_PANEL);

        // initialize
        initWidget(panel);
    }

    /**
     * Show the specified panel in the wizard
     * @param panel panel to display
     */
    public void setVisiblePanel(WizardStepPanel panel)
    {
        if (currentStep != null) currentStep.onExit();
        enterStep(panel);
        leftNavPanel.showPanel(panel);
    }

    /**
     * Move to the next panel in the deck when a "next" event
     * is received
     */
    @Override
    public void onNext()
    {
        // exit the currently displayed step
        int index = wizardDeck.getWidgetIndex(currentStep);
        // display the next non-skipped panel in the deck
        if (index < wizardDeck.getWidgetCount()-1)
        {
            currentStep.onExit();
            do 
            {
                index++;
                currentStep = (WizardStepPanel) wizardDeck.getWidget(index);
            } 
            while ((currentStep.state == WizardStepPanelState.SKIPPED
                    || currentStep.state == WizardStepPanelState.NOT_ALLOWED)
                    && index < wizardDeck.getWidgetCount()-1);

            if (index < wizardDeck.getWidgetCount()) {
                enterStep(currentStep);
                leftNavPanel.showPanel(currentStep);
            }
        }	
    }

    /**
     * Move to the previous panel in the deck when a "previous" event
     * is received
     */
    @Override
    public void onPrevious()
    {
        int index = wizardDeck.getWidgetIndex(currentStep);
        if (index > 0)
        {
            currentStep.onExit();
            do 
            {
                index--;
                currentStep = (WizardStepPanel) wizardDeck.getWidget(index);
            } 
            while ((currentStep.state == WizardStepPanelState.SKIPPED
                    || currentStep.state == WizardStepPanelState.NOT_ALLOWED)
                    && index > 0);

            enterStep(currentStep);
            leftNavPanel.showPanel(currentStep);
        }	
    }

    /**
     * Show the specified panel when the link is clicked in 
     * the left navigation bar
     * 
     * @param panel panel to display
     */
    @Override
    public void onPanel(WizardStepPanel panel)
    {
        // exit the currently displayed step
        currentStep.onExit();
        // show the new step
        enterStep(panel);
    }

    /**
     * Finish the wizard action.
     */
    @Override
    public void onFinish()
    {
        finish();
    }

    private void finish()
    {
        // exit the currently displayed step
        currentStep.onExit();
        // show the new step
        enterStep(finishPanel);
    }

    /**
     * Open the specified panel.
     * @param step the target panel
     */
    private void enterStep(WizardStepPanel step) {
        currentStep = step;
        step.onEnter();
        // set the next/prev buttons - note: count-2 since last panel is finish panel
        actionPanel.allowNext(!(finishPanel == step
                || (currentStep == wizardDeck.getWidget(wizardDeck.getWidgetCount()-2) 
                        && finishPanel.state == WizardStepPanelState.NOT_ALLOWED)));
        // disabled previous if 1st widget
        actionPanel.allowPrevious(wizardDeck.getWidget(0) != step);
        // lastly, show the panel
        wizardDeck.showWidget(wizardDeck.getWidgetIndex(step));    
    }

    /**
     * Open the help manual in a new tab/window when
     * the toolbar help button is clicked.
     */
    @Override
    public void onHelp()
    {

    }

    /**
     * Save the user input
     */
    @Override
    public void onSave()
    {
        // TODO Auto-generated method stub

    }

    /**
     * Cancel the current input
     */
    @Override
    public void onCancel()
    {
        // TODO Auto-generated method stub

    }

    /**
     * Respond to a change in the wizard context
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e)
    {
        if (context.isComplete()) {
            finishPanel.state = WizardStepPanelState.COMPLETE;
        } else {
            finishPanel.state = WizardStepPanelState.NOT_ALLOWED;
        }
        enableFinishButton(context.isComplete());
    }

    /**
     * Enable / disable the finish button and update styles.
     * @param enable true if enabled, false otherwise
     */
    private void enableFinishButton(boolean enabled) {
        finishButton.setEnabled(enabled);
        finishButton.removeStyleDependentName(GlimmpseConstants.STYLE_DISABLED);
        if (!enabled) {
            finishButton.addStyleDependentName(GlimmpseConstants.STYLE_DISABLED);
        }
    }

    /**
     * Fill the wizard context when loaded from a file or database
     */
    @Override
    public void onWizardContextLoad()
    {
        if (context.isComplete()) {
            finishPanel.state = WizardStepPanelState.COMPLETE;
        } else {
            finishPanel.state = WizardStepPanelState.NOT_ALLOWED;
        }
        enableFinishButton(context.isComplete());
    }

    @Override
    public void onClick(ClickEvent event)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStateChange(WizardStepPanel source,
            WizardStepPanelState oldState, WizardStepPanelState newState) {
        if (source == currentStep) {
            // highlight the finish button if the study design is complete
            enableFinishButton(context.isComplete() 
                    && currentStep.state == WizardStepPanelState.COMPLETE);
        }

    }

    public void addWizardActionListener(WizardActionListener listener) {
        actionPanel.addActionListener(listener);
    }

}
