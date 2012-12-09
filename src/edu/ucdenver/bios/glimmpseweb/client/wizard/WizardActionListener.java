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

/**
 * Event listener for navigation and tool buttons 
 * within a WizardPanel
 * 
 * @author Sarah Kreidler
 * @see WizardPanel
 *
 */
public interface WizardActionListener
{
	/**
	 * Callback for moving to the next panel in
	 * the wizard
	 */
	public void onNext();
	
	/**
	 * Callback for moving to the previous panel in
	 * the wizard
	 */
	public void onPrevious();
	
	/**
	 * Callback for navigating directly to a specific panel
	 * 
	 * @param panel selected panel
	 */
	public void onPanel(WizardStepPanel panel);
	
	/**
	 * Callback for finishing user input
	 */
	public void onFinish();
	
	/**
	 * Callback for clicking the help link
	 */
	public void onHelp();
	
	/**
	 * Callback for clicking the save link
	 */
	public void onSave();
	
	/**
	 * Callback for clicking the cancel button
	 */
	public void onCancel();
}
