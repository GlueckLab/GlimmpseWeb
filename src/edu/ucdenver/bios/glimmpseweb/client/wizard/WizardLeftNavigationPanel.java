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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;

/**
 * Generic left navigation panel for a WizardPanel.  Panels are organized
 * into groups using a StackLayoutPanel.  Users may jump directly to a
 * specific panel by clicking on the panel name.
 * 
 * @author Sarah Kreidler
 * @see WizardPanel
 */
public class WizardLeftNavigationPanel extends StackLayoutPanel implements NavigationListener
{
	// list of classes listening for navigation events from this panel
	protected ArrayList<NavigationListener> listeners = new ArrayList<NavigationListener>();
	
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
		super(Style.Unit.PX);
		// add groups to the stack panel
		for(WizardStepPanelGroup panelGroup: panelGroups)
		{
			// add the heading and group items to the stack
			add(createGroupItems(panelGroup.getPanelList()), 
					createGroupWidget(panelGroup.getName()), 32);
		}
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
			row++;
		}
		container.add(table);
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
				for(NavigationListener listener: listeners) listener.onPanel(button.getPanel());
			}
		}); 
		// set style
		item.setStyleName(GlimmpseConstants.STYLE_WIZARD_TOOL_BUTTON);
		// add to the container
		container.add(item);
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
	public void addNavigationListener(NavigationListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * Respond to next button events from the parent WizardPanel.
	 * The left nav panel will highlight the panel button for the next
	 * panel.  If at the end of a group, the next group will be
	 * opened and the first item highlighted
	 * 
	 * @see WizardPanel
	 */
	@Override
	public void onNext()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * Respond to previous button events from the parent WizardPanel.
	 * The left nav panel will highlight the panel button for the previous
	 * panel.  If at the start of a group, the previous group will be
	 * opened and the last item in that group highlighted
	 * 
	 * @see WizardPanel
	 */
	@Override
	public void onPrevious()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * Respond to a navigation event when a user clicks on a panel name.
	 * No action taken by the WizardLeftNavigationPanel class for this event
	 * @param panel the panel associated with the name clicked by the user
	 */
	@Override
	public void onPanel(WizardStepPanel panel)
	{
		// No action required since this event is only triggered 
		// by the WizardLeftNavigationPanel
	}
}
