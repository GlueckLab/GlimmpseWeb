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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

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
    protected WizardStepDisclosurePanel currentOpenContainer = null;
    // mapping from step panel to the disclosure panel - needed to update the left nav
    // when next/prev buttons are clicked
    protected HashMap<WizardStepPanel,WizardStepDisclosurePanel> 
    panelToContainerMap =
        new HashMap<WizardStepPanel,WizardStepDisclosurePanel>();
    
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
            // create the mapping from panels to the appropriate disclosure panel
            panelToContainerMap.put(panelGroup.getIntroPanel(), disclosurePanel);
            for(WizardStepPanel stepPanel: panelGroup.getPanelList()) {
                panelToContainerMap.put(stepPanel, disclosurePanel);
            }
        }

        // set style
        panel.setStyleName(STYLE_PANEL);

        initWidget(panel);
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
        // now open the group panel containing the item
        WizardStepDisclosurePanel container = 
            (WizardStepDisclosurePanel) panelToContainerMap.get(panel);
        if (container != null) {
            if (currentOpenContainer != null) {
                currentOpenContainer.closePanel();
            }
            currentOpenContainer = container;
            container.openPanel();
            container.showPanel(panel);
        }
    }

    /**
     * Respond to a state change in a panel
     */
    @Override
    public void onStateChange(WizardStepPanel source,
            WizardStepPanelState oldState, WizardStepPanelState newState)
    {
        WizardStepDisclosurePanel container = 
            (WizardStepDisclosurePanel) panelToContainerMap.get(source);
        if (container != null) {
            container.updateState(source, oldState, newState);
        }
    }

    /**
     * Handler click events for any of the navigation buttons
     */
    @Override
    public void onClick(ClickEvent event) {
        WizardStepPanelButton button = (WizardStepPanelButton) event.getSource();
        WizardStepDisclosurePanel container = (WizardStepDisclosurePanel) button.getContainer();
        if (currentOpenContainer != null && currentOpenContainer != container) {
            currentOpenContainer.closePanel();
        }
        currentOpenContainer = container;
        WizardStepPanel stepPanel = button.getPanel();
        for(WizardActionListener listener: listeners) {
            listener.onPanel(stepPanel);
        }
    }

}
