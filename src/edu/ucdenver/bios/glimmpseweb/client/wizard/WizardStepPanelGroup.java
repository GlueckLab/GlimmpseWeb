package edu.ucdenver.bios.glimmpseweb.client.wizard;

import java.util.ArrayList;

public class WizardStepPanelGroup
{
	private String name;
	private ArrayList<WizardStepPanel> panelList = new ArrayList<WizardStepPanel>();
	
	public WizardStepPanelGroup(String name)
	{
		this.name = name;
	}
	
	public void addPanel(WizardStepPanel panel)
	{
		panelList.add(panel);
	}

	public String getName()
	{
		return name;
	}

	public ArrayList<WizardStepPanel> getPanelList()
	{
		return panelList;
	}
	
	
}
