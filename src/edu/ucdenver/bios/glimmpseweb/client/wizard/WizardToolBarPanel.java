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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;

/**
 * Navigation toolbar for the wizard.  Includes the following buttons
 * <ul>
 * <li>Next button - navigate to the next panel in the wizard</li>
 * <li>Previous button - navigate to the previous panel in the wizard</li>
 * </ul>
 * 
 * @author Sarah Kreidler
 *
 */
public class WizardToolBarPanel extends Composite
{
	protected static final String STYLE_TOOLBAR_PANEL = "wizardToolbarPanel";
	protected static final String STYLE_TOOLBAR_BUTTON = "wizardToolbarButton";
	
	// listeners for toolbar actions
    ArrayList<WizardActionListener> listeners = new ArrayList<WizardActionListener>();
    
    // create the toolbar buttons
    protected Button nextButton = new Button(GlimmpseWeb.constants.buttonNext(), 
    		new ClickHandler() {
        public void onClick(ClickEvent event) {
            for(WizardActionListener listener: listeners) listener.onNext();
        }
    });
    protected Button previousButton = new Button(GlimmpseWeb.constants.buttonPrevious(), 
    		new ClickHandler() {
        public void onClick(ClickEvent event) {
            for(WizardActionListener listener: listeners) listener.onPrevious();
        }
    });
    
	/**
	 * Create a wizard toolbar
	 */
    public WizardToolBarPanel()
    {
        DockPanel panel = new DockPanel();

        HorizontalPanel navPanel = new HorizontalPanel();
        navPanel.add(previousButton);
        navPanel.add(nextButton);
        panel.add(navPanel, DockPanel.EAST);
        
        // add style
        panel.addStyleName(STYLE_TOOLBAR_PANEL);
        previousButton.setStyleName(STYLE_TOOLBAR_BUTTON);
        nextButton.setStyleName(STYLE_TOOLBAR_BUTTON);
        
        initWidget(panel);
    }
   
    /**
     * Enable/disable the next button
     * 
     * @param allow indicates if the next button should be enabled
     */
    public void allowNext(boolean allow)
    {
        nextButton.setEnabled(allow);
        updateStyle(nextButton, allow);
    }
    
    /**
     * Enable/disable the previous button
     * 
     * @param allow indicates if the previous button should be enabled
     */
    public void allowPrevious(boolean allow)
    {
        previousButton.setEnabled(allow);
        updateStyle(previousButton, allow);
    }

    /**
     * Add a listener for toolbar actions
     * 
     * @param listener listener for toolbar actions
     */
    public void addActionListener(WizardActionListener listener)
    {
        listeners.add(listener);
    }
    
    /**
     * Reset the button style to enabled/disabled
     * @param button the button
     * @param enabled indicates if the button is enabled
     */
    private void updateStyle(Button button, boolean enabled) {
        button.removeStyleDependentName(GlimmpseConstants.STYLE_DISABLED);
        if (!enabled) {
            button.addStyleDependentName(GlimmpseConstants.STYLE_DISABLED);
        }
    }

}
