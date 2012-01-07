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

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Generic wizard panel including a left navigation bar,
 * panel display area, and toolbar
 * 
 * @author Sarah Kreidler
 *
 */
public class WizardPanel extends Composite 
implements WizardActionListener, WizardContextListener
{
	// style for the main display area
	public static final String STYLE_WIZARD_PANEL = "wizardPanel";
	public static final String STYLE_WIZARD_CONTENT_PANEL = "wizardContentPanel";
	// main panel
	protected HorizontalPanel panel = new HorizontalPanel();
	// left navigation / "steps left" panel
    protected WizardLeftNavigationPanel leftNavPanel;
    // toolbar panel
    protected WizardToolBarPanel toolbarPanel = new WizardToolBarPanel();
	// currently visible panel
    protected WizardStepPanel currentStep = null;  
    // deck panel containing all steps in the input wizard
    protected DeckPanel wizardDeck = new DeckPanel();
    
    /**
     * Create a wizard panel with the specified groups of panels
     * 
     * @param wizardPanelGroups list of panel groups to display in the wizard
     */
	public WizardPanel(List<WizardStepPanelGroup> wizardPanelGroups)
	{		
		// create overall panel layout containers
		VerticalPanel contentPanel = new VerticalPanel();
		VerticalPanel leftPanel = new VerticalPanel();
		
		// layout the left navigation
		leftNavPanel = new WizardLeftNavigationPanel(wizardPanelGroups);
		leftPanel.add(leftNavPanel);
		// layout the display area and bottom toolbar
		contentPanel.add(wizardDeck);
		contentPanel.add(toolbarPanel);

		// layout the overall  wizard panel
		panel.add(leftPanel);		
		panel.add(contentPanel);
		
		// add the panels to the display deck
		for(WizardStepPanelGroup panelGroup: wizardPanelGroups)
		{
			for(WizardStepPanel step: panelGroup.getPanelList())
			{
				wizardDeck.add(step);
			}
		}
		
		// add callbacks for events from the navigation and toolbar subpanels
		leftNavPanel.addActionListener(this);
		toolbarPanel.addActionListener(this);
		
		// add style
		panel.setStyleName(STYLE_WIZARD_PANEL);
		wizardDeck.setStyleName(STYLE_WIZARD_CONTENT_PANEL);

		// initialize
		initWidget(panel);
	}

	/**
	 * Show the specified panel in the wizard
	 * @param panel panel to display
	 */
	public void setVisiblePanel(WizardStepPanel panel)
	{
		if (currentStep != null) currentStep.onExit();
		currentStep = panel;
		currentStep.onEnter();
		wizardDeck.showWidget(wizardDeck.getWidgetIndex(panel));
		leftNavPanel.showPanel(panel);
	}

	/**
	 * Move to the next panel in the deck when a "next" event
	 * is received
	 */
	@Override
	public void onNext()
	{
		// exit the currently displayed step
		int index = wizardDeck.getWidgetIndex(currentStep);
		currentStep.onExit();
		// display the next non-skipped panel in the deck
		if (index < wizardDeck.getWidgetCount()-1)
		{
			do 
			{
				index++;
				currentStep = (WizardStepPanel) wizardDeck.getWidget(index);
			} 
			while (currentStep.skip && index < wizardDeck.getWidgetCount());
			
			wizardDeck.showWidget(index);
			leftNavPanel.showPanel(currentStep);
		}	
	}

	/**
	 * Move to the previous panel in the deck when a "previous" event
	 * is received
	 */
	@Override
	public void onPrevious()
	{
		int index = wizardDeck.getWidgetIndex(currentStep);
		currentStep.onExit();
		if (index > 0)
		{
			do 
			{
				index--;
				currentStep = (WizardStepPanel) wizardDeck.getWidget(index);
			} 
			while (currentStep.skip && index > 0);
			
			wizardDeck.showWidget(index);
			leftNavPanel.showPanel(currentStep);
		}	
	}

	/**
	 * Show the specified panel when the link is clicked in 
	 * the left navigation bar
	 * 
	 * @param panel panel to display
	 */
	@Override
	public void onPanel(WizardStepPanel panel)
	{
		// exit the currently displayed step
		currentStep.onExit();
		// show the new step
		currentStep = panel;
		wizardDeck.showWidget(wizardDeck.getWidgetIndex(panel));		
	}

	/**
	 * Finish the wizard action
	 */
	@Override
	public void onFinish()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * Display help information
	 */
	@Override
	public void onHelp()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * Save the user input
	 */
	@Override
	public void onSave()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * Cancel the current input
	 */
	@Override
	public void onCancel()
	{
		// TODO Auto-generated method stub
		
	}

	/**
	 * Respond to a change in the wizard context
	 */
	@Override
	public void onChange(WizardContextChangeEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Fill the wizard context when loaded from a file or database
	 */
	@Override
	public void onLoad()
	{
		// TODO Auto-generated method stub
		
	}
    
}
