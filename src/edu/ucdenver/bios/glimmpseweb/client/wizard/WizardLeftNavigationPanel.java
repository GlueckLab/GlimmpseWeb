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
implements WizardStepPanelStateChangeHandler
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
	protected DisclosurePanel currentOpenPanel = null;
	// currenly active item
	protected Widget currentItem = null;
	// maps panels to their associated button to avoid a traversal through the entire stack panel
	protected HashMap<WizardStepPanel,WizardStepPanelButton> panelToButtonMap = 
		new HashMap<WizardStepPanel,WizardStepPanelButton>();
	protected HashMap<WizardStepPanel,Widget> panelToContainerMap = 
		new HashMap<WizardStepPanel,Widget>();
	// TODO: better solution than hashmap?

	/**
	 * Button class added to the FlexTables displayed under each StackLayoutPanel
	 * heading.  The button stores information about its position in the FlexTable
	 * and the associated panel.  This allows next/previous navigation to occur.
	 */
	private class WizardStepPanelButton extends Button
	{
		private WizardStepPanel panel;
		
		/**
		 * Creates a button which stored meta information about the current panel
		 * and its position in the FlexTable
		 * 
		 * @param row row that this button appears in the FlexTable
		 * @param panel panel associated with the button
		 * @param handler click handler function
		 */
		public WizardStepPanelButton(WizardStepPanel panel, ClickHandler handler)
		{
			super(panel.getName(), handler);
			this.panel = panel;
		}
		
		/**
		 * Get the panel associated with this button
		 * @return WizardStepPanel
		 */
		public WizardStepPanel getPanel()
		{
			return panel;
		}
	}
	
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
			// add the heading and group items to the stack
			panel.add(buildPanelGroup(panelGroup));
		}
		
		// set style
		panel.setStyleName(STYLE_PANEL);
		
		initWidget(panel);
	}
	
	/**
	 * Build the disclosure panel for a panel group
	 * @param panelGroup
	 * @return
	 */
	private DisclosurePanel buildPanelGroup(WizardStepPanelGroup panelGroup)
	{
		// create disclosure panel for the group
		DisclosurePanel panel = new DisclosurePanel();
		panel.addOpenHandler(new OpenHandler<DisclosurePanel>() {
			@Override
			public void onOpen(OpenEvent<DisclosurePanel> event)
			{
				openPanel((DisclosurePanel) event.getSource());
			}
		});

		HTML header = new HTML(panelGroup.getName());
		panel.setHeader(header);
		panel.setContent(createGroupItems(panel, panelGroup.getPanelList()));
		
		// set style
		panel.setStyleName(STYLE_GROUP_HEADER);
		
		return panel;
	}
	
	/**
	 * Creates a FlexTable with links to each panel associated with
	 * a given panel group
	 * 
	 * @param panelList list of panels
	 * @return widget containing the list
	 */
	private VerticalPanel createGroupItems(DisclosurePanel parent, List<WizardStepPanel> panelList)
	{
		VerticalPanel container = new VerticalPanel();
		int row = 0;
		for(WizardStepPanel panel: panelList)
		{
			panel.addChangeHandler(this);
			container.add(createNavigationItem(row, panel));
			panelToContainerMap.put(panel, parent);
			row++;
		}
		container.setStyleName(STYLE_GROUP_CONTAINER);
		return container;
	}
	
	/**
	 * Create a button which will navigate to the specified panel
	 * 
	 * @param row row of the FlexTable in which the panel link appears
	 * @param panel the panel
	 * @return
	 */
	private VerticalPanel createNavigationItem(int row, WizardStepPanel panel)
	{
		VerticalPanel container = new VerticalPanel();
		WizardStepPanelButton item =
			new WizardStepPanelButton(panel, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				WizardStepPanelButton button = (WizardStepPanelButton) event.getSource();
				for(WizardActionListener listener: listeners) listener.onPanel(button.getPanel());
				if (currentItem != null) currentItem.removeStyleDependentName(STYLE_OPEN);
				currentItem = button;
				currentItem.addStyleDependentName(STYLE_OPEN);
			}
		}); 
		// set style
		item.setStyleName(STYLE_GROUP_ITEM);
		item.addStyleDependentName(getStyleByState(panel.getState()));
		// add to the container
		container.add(item);
		// setup mappings
		panelToButtonMap.put(panel, item);
		return container;
	}
	
	/**
	 * Create the heading widget for each panel group
	 * 
	 * @param groupName Name of the group
	 * @return heading widget
	 */
	private Widget createGroupWidget(String groupName) 
	{
		return new HTML(groupName);
	}

	/**
	 * Forces only one panel to be open at a time.
	 * @param panel
	 */
	private void openPanel(DisclosurePanel panel)
	{
		if (panel != null) 
		{
			if (currentOpenPanel != null)
			{
				currentOpenPanel.removeStyleDependentName(STYLE_OPEN);
				currentOpenPanel.setOpen(false);
			}
			panel.removeStyleDependentName(STYLE_OPEN);
			panel.addStyleDependentName(STYLE_OPEN);
			currentOpenPanel = panel;
			// now show the first panel in this group
		}
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
		DisclosurePanel container = (DisclosurePanel) panelToContainerMap.get(panel);
		if (container != null && !container.isOpen()) container.setOpen(true);
		// update styles
		Widget item = panelToButtonMap.get(panel);
		if (item != null)
		{
			if (currentItem != null) currentItem.removeStyleDependentName(STYLE_OPEN);
			currentItem = item;
			currentItem.addStyleDependentName(STYLE_OPEN);
		}
	}

	/**
	 * Respond to a state change in a panel
	 */
	@Override
	public void onStateChange(WizardStepPanel source,
			WizardStepPanelState oldState, WizardStepPanelState newState)
	{
		Widget item = panelToButtonMap.get(source);
		if (item != null)
		{
			if (currentItem != null) currentItem.removeStyleDependentName(getStyleByState(oldState));
			currentItem = item;
			currentItem.addStyleDependentName(getStyleByState(newState));
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
	
}
