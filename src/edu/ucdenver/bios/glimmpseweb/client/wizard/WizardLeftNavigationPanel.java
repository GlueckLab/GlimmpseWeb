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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Generic left navigation panel for a WizardPanel.  Panels are organized
 * into groups using a StackLayoutPanel.  Users may jump directly to a
 * specific panel by clicking on the panel name.
 * 
 * @author Sarah Kreidler
 * @see WizardPanel
 */
public class WizardLeftNavigationPanel extends Composite 
implements WizardStepPanelStateChangeHandler, ClickHandler
{
    protected static final String STYLE_PANEL = "wizardLeftNavPanel";
    protected static final String STYLE_GROUP_HEADER = "wizardLeftNavHeader";
    protected static final String STYLE_GROUP_CONTAINER = "wizardLeftNavContent";
    protected static final String STYLE_GROUP_ITEM = "wizardLeftNavLink";

    // dependent style for active and completed items
    protected static final String STYLE_SKIPPED = "skipped";
    protected static final String STYLE_NOT_ALLOWED = "notAllowed";
    protected static final String STYLE_INCOMPLETE = "incomplete";
    protected static final String STYLE_COMPLETE = "complete";
    protected static final String STYLE_OPEN = "open";

    // list of classes listening for navigation events from this panel
    protected ArrayList<WizardActionListener> listeners = new ArrayList<WizardActionListener>();
    // pointer to currently open disclosure panel - used to enforce only one open at a time
    protected WizardStepDisclosurePanel currentOpenPanel = null;
    // currenly active item
    protected WizardStepPanelButton currentItem = null;

    /**
     * Create a left navigation panel for the wizard.  Panels are organized 
     * into groups.
     * 
     * @param panelGroups list of all groups of panels
     */
    public WizardLeftNavigationPanel(List<WizardStepPanelGroup> panelGroups)
    {
        VerticalPanel panel = new VerticalPanel();

        // add groups to the table of disclosure panels
        for(WizardStepPanelGroup panelGroup: panelGroups)
        {
            // create disclosure panel for the group
            WizardStepDisclosurePanel disclosurePanel = new WizardStepDisclosurePanel(panelGroup);
            disclosurePanel.addClickHandler(this);
            // add the heading and group items to the stack
            panel.add(disclosurePanel);
        }

        // set style
        panel.setStyleName(STYLE_PANEL);

        initWidget(panel);
    }

    /**
     * Set the specified button as current and update styles
     */
    private void updateCurrentItem(WizardStepPanelButton button)
    {
        if (currentItem != null) 
        {
            currentItem.removeStyleDependentName(STYLE_OPEN);
            currentItem.addStyleDependentName(getStyleByState(currentItem.getPanel().getState()));
        }
        currentItem = button;
        currentItem.removeStyleDependentName(getStyleByState(button.getPanel().getState()));
        currentItem.addStyleDependentName(STYLE_OPEN);
    }

    /**
     * Open the specified group panel.  Forces only one panel to be open at a time.
     * @param panel
     */
    private void openPanel(DisclosurePanel panel)
    {
//        if (panel != null) 
//        {
//            if (currentOpenPanel != null)
//            {
//                currentOpenPanel.removeStyleDependentName(STYLE_OPEN);
//                currentOpenPanel.setOpen(false);
//            }
//            panel.removeStyleDependentName(STYLE_OPEN);
//            panel.addStyleDependentName(STYLE_OPEN);
//            currentOpenPanel = panel;
//        }
    }

    /**
     * Add a listener for navigation events.  The left navigation panel
     * will send onPanel() events
     * 
     * @param listener
     */
    public void addActionListener(WizardActionListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Display the specific panel and open the group
     * @param panel panel to display
     */
    public void showPanel(WizardStepPanel panel)
    {
//        // now open the group panel containing the item
//        DisclosurePanel container = (DisclosurePanel) panelToContainerMap.get(panel);
//        if (container != null && !container.isOpen()) container.setOpen(true);
//        // update styles for current item 
//        WizardStepPanelButton item = panelToButtonMap.get(panel);
//        if (item != null)
//        {
//            updateCurrentItem(item);
//        }
    }

    /**
     * Respond to a state change in a panel
     */
    @Override
    public void onStateChange(WizardStepPanel source,
            WizardStepPanelState oldState, WizardStepPanelState newState)
    {
//        Widget item = panelToButtonMap.get(source);
//        if (item != null && item != currentItem)
//        {
//            item.setVisible(newState != WizardStepPanelState.SKIPPED);
//            item.removeStyleDependentName(getStyleByState(oldState));
//            item.addStyleDependentName(getStyleByState(newState));
//        }
    }

    /**
     * Convenience routine for mapping panel states to dependent styles
     * @param state panel state
     * @return dependent style name for the state
     */
    private String getStyleByState(WizardStepPanelState state)
    {
        switch(state)
        {
        case SKIPPED:
            return STYLE_SKIPPED;
        case NOT_ALLOWED:
            return STYLE_NOT_ALLOWED;
        case INCOMPLETE:
            return STYLE_INCOMPLETE;
        case COMPLETE:
            return STYLE_COMPLETE;
        default:
            return null;
        }
    }

    /**
     * Handler click events for any of the navigation buttons
     */
    @Override
    public void onClick(ClickEvent event) {
        WizardStepPanelButton button = (WizardStepPanelButton) event.getSource();
        WizardStepPanel panel = button.getPanel();
        if (button.isGroupButton()) {
            
        } else {
            
        }
    }

}
