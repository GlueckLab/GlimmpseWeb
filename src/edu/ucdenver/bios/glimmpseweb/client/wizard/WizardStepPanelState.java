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
 * Enumerates the different states of completion for a step in the
 * wizard
 * 
 * @author Sarah Kreidler
 *
 */
public enum WizardStepPanelState
{
	 // panel is not relevant to current design, so not displayed
	SKIPPED,
	// panel is relevant, but requires more information before it can be accesses
	NOT_ALLOWED, 
	// panel can be accessed but is incomplete
	INCOMPLETE,
	// panel is complete
	COMPLETE
}
