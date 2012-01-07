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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.StackLayoutPanel;
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
{
	protected static final String STYLE_PANEL = "wizardLeftNavPanel";
	protected static final String STYLE_ITEM_CONTAINER = "wizardLeftNavContent";
	protected static final String STYLE_ITEM = "wizardLeftNavLink";
	// dependent style for active and completed items
    protected static final String STYLE_ACTIVE = "active";
    protected static final String STYLE_COMPLETE = "complete";
    
	// list of classes listening for navigation events from this panel
	protected ArrayList<WizardActionListener> listeners = new ArrayList<WizardActionListener>();
	// stack panel to display panel links
	protected StackLayoutPanel stackPanel = new StackLayoutPanel(Style.Unit.PX);
	// maps panels to their associated button to avoid a traversal through the entire stack panel
	protected HashMap<WizardStepPanel,WizardStepPanelButton> panelToButtonMap = 
		new HashMap<WizardStepPanel,WizardStepPanelButton>();
	protected HashMap<WizardStepPanel,Widget> panelToContainerMap = 
		new HashMap<WizardStepPanel,Widget>();
	// TODO: better solution than hashmap?
	
	// currenly active item
	protected Widget currentItem = null;
	
	/**
	 * Button class added to the FlexTables displayed under each StackLayoutPanel
	 * heading.  The button stores information about its position in the FlexTable
	 * and the associated panel.  This allows next/previous navigation to occur.
	 */
	private class WizardStepPanelButton extends Button
	{
		private WizardStepPanel panel;
		private int row;
		
		/**
		 * Creates a button which stored meta information about the current panel
		 * and its position in the FlexTable
		 * 
		 * @param row row that this button appears in the FlexTable
		 * @param panel panel associated with the button
		 * @param handler click handler function
		 */
		public WizardStepPanelButton(int row, WizardStepPanel panel, ClickHandler handler)
		{
			super(panel.getName(), handler);
			this.row = row;
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
		
		/**
		 * Get the row of the FlexTable in which this button appears
		 * @return WizardStepPanel
		 */
		public int getRow()
		{
			return row;
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
	    stackPanel.setPixelSize(150, 400);

		// add groups to the stack panel
		for(WizardStepPanelGroup panelGroup: panelGroups)
		{
			// add the heading and group items to the stack
			stackPanel.add(createGroupItems(panelGroup.getPanelList()), 
					createGroupWidget(panelGroup.getName()), 32);
		}
		panel.add(stackPanel);
		
		// set style
		panel.setStyleName(STYLE_PANEL);
		
		initWidget(panel);
	}
	
	/**
	 * Creates a FlexTable with links to each panel associated with
	 * a given panel group
	 * 
	 * @param panelList list of panels
	 * @return widget containing the list
	 */
	private VerticalPanel createGroupItems(List<WizardStepPanel> panelList)
	{
		VerticalPanel container = new VerticalPanel();
		FlexTable table = new FlexTable();
		int row = 0;
		for(WizardStepPanel panel: panelList)
		{
			table.setWidget(row, 0, createNavigationItem(row, panel));
			panelToContainerMap.put(panel, container);
			row++;
		}
		container.add(table);
		container.setStyleName(STYLE_ITEM_CONTAINER);
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
			new WizardStepPanelButton(row, panel, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				WizardStepPanelButton button = (WizardStepPanelButton) event.getSource();
				for(WizardActionListener listener: listeners) listener.onPanel(button.getPanel());
				if (currentItem != null) currentItem.removeStyleDependentName(STYLE_ACTIVE);
				currentItem = button;
				currentItem.addStyleDependentName(STYLE_ACTIVE);
			}
		}); 
		// set style
		item.setStyleName(STYLE_ITEM);
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
		Widget container = panelToContainerMap.get(panel);
		if (container != null) stackPanel.showWidget(container, false);
		// update styles
		Widget item = panelToButtonMap.get(panel);
		if (item != null)
		{
			if (currentItem != null) currentItem.removeStyleDependentName(STYLE_ACTIVE);
			currentItem = item;
			currentItem.addStyleDependentName(STYLE_ACTIVE);
		}
	}
	
	// TODO
//	public void setPanelComplete(WizardStepPanel panel, boolean complete)
//	{
//		Widget item = panelToButtonMap.get(panel);
//		item.removeStyleDependentName(STYLE_COMPLETE);
//	}
	
}
