package edu.ucdenver.bios.glimmpseweb.client.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WizardLeftNavigationPanel extends StackLayoutPanel implements NavigationListener
{
	protected HashMap<String,FlexTable> navigationGroups = new HashMap<String,FlexTable>();
	protected ArrayList<NavigationListener> listeners = new ArrayList<NavigationListener>();
	
	private class WizardStepPanelButton extends Button
	{
		private WizardStepPanel panel;
		
		public WizardStepPanelButton(WizardStepPanel panel, ClickHandler handler)
		{
			super(panel.getName(), handler);
			this.panel = panel;
		}
		
		public WizardStepPanel getPanel()
		{
			return panel;
		}
		
	}
	public WizardLeftNavigationPanel(List<WizardStepPanelGroup> panelGroups)
	{
		super(Style.Unit.PX);
		for(WizardStepPanelGroup panelGroup: panelGroups)
		{
			String name = panelGroup.getName();
			FlexTable table = new FlexTable();
			VerticalPanel container = new VerticalPanel();
			container.add(table);
			navigationGroups.put(name, table);
			add(container, createGroupWidget(name), 32);
			int row = 0;
			for(WizardStepPanel panel: panelGroup.getPanelList())
			{
				table.setWidget(row++, 0, createNavigationItem(panel));
			}
		}
	}

//	private void addNavigationItem(String group, WizardStepPanel panel)
//	{
//		FlexTable table = navigationGroups.get(group);
//		if (table == null)
//		{
//			table = new FlexTable();
//			VerticalPanel container = new VerticalPanel();
//			container.add(table);
//			navigationGroups.put(group, table);
//			add(container, createGroupWidget(group), 32);
//		}
//		int numRows = table.getRowCount();
//		GWT.log("entered item");
//		table.setWidget(numRows, 0, createNavigationItem(panel));
//	}
	
	private VerticalPanel createNavigationItem(WizardStepPanel panel)
	{
		VerticalPanel container = new VerticalPanel();
		container.add(new WizardStepPanelButton(panel, new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				WizardStepPanelButton button = (WizardStepPanelButton) event.getSource();
				for(NavigationListener listener: listeners) listener.onPanel(button.getPanel());
			}
		}));
		// TODO: store id
		return container;
	}
	
	private Widget createGroupWidget(String groupName) 
	{
		return new HTML(groupName);
	}

	public void addNavigationListener(NavigationListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void onNext()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPrevious()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPanel(WizardStepPanel panel)
	{
		// TODO Auto-generated method stub
		
	}
}
