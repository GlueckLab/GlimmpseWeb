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

/**
 * Class to define groups of panels in the WizardPanel.  A list of
 * panel groups is passed to the constructor of WizardPanel
 * 
 * @author Sarah Kreidler
 * @see WizardPanel
 *
 */
public class WizardStepPanelGroup
{
	// group name
	private String name;
	// list of panels in the group
	private ArrayList<WizardStepPanel> panelList = new ArrayList<WizardStepPanel>();
	
	/**
	 * Create an empty panel group
	 * 
	 * @param name display name of the panel group
	 */
	public WizardStepPanelGroup(String name)
	{
		this.name = name;
	}
	
	/**
	 * Add a panel to the panel group
	 * 
	 * @param panel panel to add
	 */
	public void addPanel(WizardStepPanel panel)
	{
		panelList.add(panel);
	}

	/**
	 * Get the name of the panel group
	 * 
	 * @return panel group name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Get the list of panels contained in the group
	 * 
	 * @return list of panels contained the group
	 */
	public ArrayList<WizardStepPanel> getPanelList()
	{
		return panelList;
	}

}
