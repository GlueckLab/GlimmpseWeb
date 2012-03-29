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

import com.google.gwt.user.client.ui.Composite;

/**
 * Generic superclass for panel representing individual 
 * "steps" in a wizard
 * 
 * @author Sarah Kreidler
 *
 */
public abstract class WizardStepPanel extends Composite
implements WizardContextListener
{
	// context object coordinating user input across screens
	protected WizardContext context = null;
	// panel completion state.  incomplete by default
	protected WizardStepPanelState state = WizardStepPanelState.INCOMPLETE;
	// handlers for state changes
	ArrayList<WizardStepPanelStateChangeHandler> handlers = 
		new ArrayList<WizardStepPanelStateChangeHandler>();
	// name of the panel
	protected String name = "";
	
	/**
	 * Create a wizard step panel.  Each step is required for filling
	 * in a part of the "context" of the wizard.
	 * 
	 * @param context context object
	 * @param name display name of the panel
	 */
	public WizardStepPanel(WizardContext context, String name)
	{
		this(context, name, WizardStepPanelState.INCOMPLETE);
	}
	
	/**
	 * Create a wizard step panel.  Each step is required for filling
	 * in a part of the "context" of the wizard.
	 * 
	 * @param context context object
	 * @param name display name of the panel
	 * @param initialState starting state of the panel
	 */
	public WizardStepPanel(WizardContext context, String name,
			WizardStepPanelState initialState)
	{
		this.name = name;
		this.context = context;
		this.context.addContextListener(this);
		this.state = initialState;
	}
	
	
	/**
	 * Clear the panel.  Must be implemented in the subclass.
	 */
	public abstract void reset();
	
	/**
	 * Notify any handlers that the panel state has changed
	 * 
	 * @param newState
	 */
	protected void changeState(WizardStepPanelState newState)
	{
		if (this.state != newState)
		{
			WizardStepPanelState oldState = this.state;
			this.state = newState;
			for(WizardStepPanelStateChangeHandler handler: handlers) 
				handler.onStateChange(this, oldState, newState);
		}
	}
	
	/**
	 * Get the current state of the panel
	 * @return state
	 */
	public WizardStepPanelState getState()
	{
		return state;
	}
	
	/**
	 * Add a handler for panel state changes
	 * 
	 * @param handler a class implementing the WizardStepPanelStateChangeHandler interface
	 */
	protected void addChangeHandler(WizardStepPanelStateChangeHandler handler)
	{
		handlers.add(handler);
	}
    
    /**
     * Perform any setup when first entering this step in the wizard
     */
    public void onEnter() {}
    
    /**
     * Perform any cleanup when first exiting this step in the wizard
     * @return true if exit allowed
     */
    public void onExit() {}
    
    /**
     * Returns the name of the panel
     * @return panel name
     */
    public String getName()
    {
    	return name;
    }
   
	/**
	 * Respond to a change in the context object
	 */
    @Override
	public void onWizardContextChange(WizardContextChangeEvent e) {};
    
    /**
     * Response to a context load event
     */
    @Override
	public void onWizardContextLoad() {}
}
