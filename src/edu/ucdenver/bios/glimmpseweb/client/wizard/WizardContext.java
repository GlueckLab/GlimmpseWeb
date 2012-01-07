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
 * Generic context object for tracking user input across 
 * multiple wizard screens.  WizardPanel users should 
 * create a subclass of this object specific to their application
 * 
 * @author Sarah Kreidler
 *
 */
public abstract class WizardContext
{
	// listeners for changes to the context object
	ArrayList<WizardContextListener> contextListeners = new ArrayList<WizardContextListener>();
	
	/**
	 * Add a listener for changes to the context
	 * 
	 * @param listener object implementing the WizardContextListener interface
	 */
	public void addContextListener(WizardContextListener listener)
	{
		contextListeners.add(listener);
	}
	
	/**
	 * Notify all listeners of a context change
	 */
	protected void notifyChanged(WizardContextChangeEvent e)
	{
		for(WizardContextListener listener: contextListeners)
		{
			listener.onChange(e);
		}
	}
	
	/**
	 * Notify all listeners that the context has been loaded from
	 * a file or database
	 */
	protected void notifyLoad()
	{
		for(WizardContextListener listener: contextListeners)
		{
			listener.onLoad();
		}
	}
	
	
}