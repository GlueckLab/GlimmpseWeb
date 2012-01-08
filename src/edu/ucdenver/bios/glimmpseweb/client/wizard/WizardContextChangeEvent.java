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
 * Abstract superclass for change events from the 
 * wizard context.
 * 
 * @author Sarah
 *
 */
public abstract class WizardContextChangeEvent
{
	// panel that initiated the context change
	private WizardStepPanel source = null;
	
	/**
	 * Create a context change event from the specified source panel
	 * @param source
	 */
	public WizardContextChangeEvent(WizardStepPanel source)
	{
		this.source = source;
	}

	/**
	 * Get the panel that initiated the change
	 * @return panel which changed the context
	 */
	public WizardStepPanel getSource()
	{
		return source;
	}
	
	
}

