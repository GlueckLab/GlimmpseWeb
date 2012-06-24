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
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Modified disclosure panel class to allow better control over
 * when the disclosure panel is allowed to open/close
 * 
 * @author Sarah Kreidler
 *
 */
public class WizardStepDisclosurePanel extends Composite {

    protected static final String STYLE_PANEL = "wizardLeftNavDisclosurePanel";
    protected static final String STYLE_GROUP_HEADER = "wizardLeftNavHeader";
    protected static final String STYLE_GROUP_CONTAINER = "wizardLeftNavContent";
    protected static final String STYLE_GROUP_ITEM = "wizardLeftNavLink";
    
    // dependent style for active and completed items
    protected static final String STYLE_SKIPPED = "skipped";
    protected static final String STYLE_NOT_ALLOWED = "notAllowed";
    protected static final String STYLE_INCOMPLETE = "incomplete";
    protected static final String STYLE_COMPLETE = "complete";
    protected static final String STYLE_OPEN = "open";
    
    // button to show/hide display panel - mimics DisclosurePanel
    protected WizardStepPanelButton disclosureButton;
    // lists the steps for this panel group
    protected VerticalPanel contentPanel = new VerticalPanel();
    // add the items for the group
    protected FlexTable table = new FlexTable();
    // indicates if the panel is open or closed
    protected boolean isOpen = false;
    // pointer to panel group associated with this panel
    protected WizardStepPanelGroup panelGroup;
    // list of open handlers
    protected ArrayList<ClickHandler> handlers = new ArrayList<ClickHandler>();
    // currently highlighted item
    protected WizardStepPanelButton currentItem = null;
    
    /**
     * Create a disclosure panel with the specified label
     * @param label
     * @param introPanel
     */
    public WizardStepDisclosurePanel(WizardStepPanelGroup panelGroup) {

        this.panelGroup = panelGroup;

        VerticalPanel panel = new VerticalPanel();

        // create a header button to reveal/hide the widget steps
        disclosureButton = new WizardStepPanelButton(panelGroup.getName(), 
                panelGroup.getIntroPanel(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                openPanel();
                notifyOnClick(event);
            }
        });
        disclosureButton.setGroupButton(true);
        disclosureButton.setContainer(this);

        // create the hidden panel
        contentPanel.add(table);
        List<WizardStepPanel> panelList = panelGroup.getPanelList();
        if (panelList != null && panelList.size() > 0)
        {
            int row = 0;
            for(WizardStepPanel stepPanel: panelList) {
                table.setWidget(row, 0, createNavigationItem(row, stepPanel));
                row++;
            }
        }
        
        // layout the overall panel
        panel.add(disclosureButton);
        panel.add(contentPanel);
        
        // hide the content panel on initialization
        contentPanel.setVisible(false);
        
        // add style
        panel.setStyleName(STYLE_PANEL);
        disclosureButton.setStyleName(STYLE_GROUP_HEADER);
        contentPanel.setStyleName(STYLE_GROUP_CONTAINER);
        initWidget(panel);
    }
    
    /**
     * Create a button which will navigate to the specified panel
     * 
     * @param row row of the FlexTable in which the panel link appears
     * @param panel the panel
     * @return
     */
    private WizardStepPanelButton createNavigationItem(int row, WizardStepPanel panel)
    {
        WizardStepPanelButton item =
            new WizardStepPanelButton(panel, new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                WizardStepPanelButton button = (WizardStepPanelButton) event.getSource();
                if (button.getPanel().state != WizardStepPanelState.NOT_ALLOWED 
                        &&  button.getPanel().state != WizardStepPanelState.SKIPPED) {
                    notifyOnClick(event);
                    updateCurrentItem(button);
                }
            }
        }); 
        // keep a pointer to the overall disclosure panel
        item.setContainer(this);
        // set style
        item.setVisible(panel.getState() != WizardStepPanelState.SKIPPED);
        item.setStyleName(STYLE_GROUP_ITEM);
        item.addStyleDependentName(getStyleByState(panel.getState()));
        return item;
    }
   
    /**
     * Set the specified button as current and update styles
     */
    private void updateCurrentItem(WizardStepPanelButton button)
    {
        if (currentItem != null && !currentItem.isGroupButton()) 
        {
            currentItem.removeStyleDependentName(STYLE_OPEN);
            currentItem.addStyleDependentName(getStyleByState(currentItem.getPanel().getState()));
        }
        if (!button.isGroupButton()) {
            currentItem = button;
            currentItem.removeStyleDependentName(getStyleByState(button.getPanel().getState()));
            currentItem.addStyleDependentName(STYLE_OPEN);
        } else {
            currentItem = null;
        }
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
     * Add a handler for panel open events.
     * @param handler the open handler
     */
    public void addOpenHandler(OpenHandler<DisclosurePanel> handler) {
//        handlers.add(handler);
    }
    
    /**
     * Add a handler for panel open events.
     * @param handler the open handler
     */
    public void addClickHandler(ClickHandler handler) {
        handlers.add(handler);
    }

    /**
     * Open or close the panel
     * @param isOpen if true, the panel will be opened
     */
    public void openPanel() {
        if (!isOpen) {
            isOpen = true;
            contentPanel.setVisible(isOpen);
            disclosureButton.removeStyleDependentName(STYLE_OPEN);
            disclosureButton.addStyleDependentName(STYLE_OPEN);
        }
    }
    
    /**
     * Open or close the panel
     * @param isOpen if true, the panel will be opened
     */
    public void closePanel() {
        if (isOpen) {
            this.isOpen = false;
            contentPanel.setVisible(isOpen);
            disclosureButton.removeStyleDependentName(STYLE_OPEN);
            if (currentItem != null) 
            {
                currentItem.removeStyleDependentName(STYLE_OPEN);
                currentItem.addStyleDependentName(getStyleByState(currentItem.getPanel().getState()));
            }
            currentItem = null;
        }
    }
    
    /**
     * Highlight the button associated with the specified panel.  Used when next/previous
     * events are received from the bottom nav bar.
     * @param panel the step to be highlighted
     */
    public void showPanel(WizardStepPanel panel) {
        WizardStepPanelButton button = getButtonByPanel(panel);
        if (button != null) {
            updateCurrentItem(button);
        }
    }
    
    private void notifyOnClick(ClickEvent event) {
        for(ClickHandler handler: handlers) handler.onClick(event);
    }
    
    /**
     * Find the button associated with the specified wizard step panel/
     * @param panel the step panel
     * @return the button associated with the step panel
     */
    private WizardStepPanelButton getButtonByPanel(WizardStepPanel panel) {
        if (panel != null) {
            if (panel == disclosureButton.getPanel()) {
                // header button matches this panel
                return disclosureButton;
            } else {
                // find the button in the flex table
                for(int i = 0; i < table.getRowCount(); i++) {
                    WizardStepPanelButton button = (WizardStepPanelButton) table.getWidget(i, 0);
                    if (panel == button.getPanel()) {
                        return button;
                    }
                }
            }
        } 
        // null panel or not found
        return null;
    }
    
    /**
     * Respond to a state change in a panel
     * @param panel the panel which changed
     * @param oldState the old state
     * @param newState the new state
     */
    public void updateState(WizardStepPanel panel,
            WizardStepPanelState oldState, WizardStepPanelState newState)
    {
//        Window.alert("panel changed=" + panel.getName() + " old=" + oldState + " new=" + newState);
        WizardStepPanelButton button = getButtonByPanel(panel);
        if (button != null && button != currentItem) {
            button.setVisible(newState != WizardStepPanelState.SKIPPED);
            button.removeStyleDependentName(getStyleByState(oldState));
            button.addStyleDependentName(getStyleByState(newState));
        }
    }
}
