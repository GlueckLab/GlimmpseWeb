/*
 * User Interface for the GLIMMPSE Software System.  Allows
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;

/**
 * Various extra tools such as save, help, cancel
 * @author Sarah
 *
 */
public class WizardActionPanel extends Composite {

    // style for tools
    protected static final String STYLE_TOOL_PANEL = "wizardActionPanel";
    protected static final String STYLE_SAVE = "wizardActionSaveButton";
    protected static final String STYLE_CANCEL = "wizardActionCancelButton";
    protected static final String STYLE_HELP = "wizardActionHelpButton";
    protected static final String STYLE_NEXT = "wizardActionNextButton";
    protected static final String STYLE_PREVIOUS = "wizardActionPreviousButton";
    
    // listeners for toolbar actions
    ArrayList<WizardActionListener> listeners = new ArrayList<WizardActionListener>();
    
    // create the toolbar buttons
    // "next" nav button
    protected Button nextButton = new Button(GlimmpseWeb.constants.buttonNext(), 
            new ClickHandler() {
        public void onClick(ClickEvent event) {
            for(WizardActionListener listener: listeners) listener.onNext();
        }
    });
    // "previous" nav button
    protected Button previousButton = new Button(GlimmpseWeb.constants.buttonPrevious(), 
            new ClickHandler() {
        public void onClick(ClickEvent event) {
            for(WizardActionListener listener: listeners) listener.onPrevious();
        }
    });
    // save context button
    protected Button saveButton = new Button(GlimmpseWeb.constants.buttonSave(), 
            new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            for(WizardActionListener listener: listeners) listener.onSave();
        }
    });
    // cancel button
    protected Button cancelButton = new Button(GlimmpseWeb.constants.buttonCancel(), 
            new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            for(WizardActionListener listener: listeners) listener.onCancel();
        }
    });
    // button to access help manual
    Button helpButton = new Button(GlimmpseWeb.constants.buttonHelp(), new ClickHandler() {
        @Override
        public void onClick(ClickEvent event)
        {
            openHelpManual();
        }
    });

    /**
     * Create the action panel
     */
    public WizardActionPanel() {
        HorizontalPanel panel = new HorizontalPanel();

        HorizontalPanel navPanel = new HorizontalPanel();
        navPanel.add(previousButton);
        navPanel.add(nextButton);
        
        HorizontalPanel toolsPanel = new HorizontalPanel();
        toolsPanel.add(helpButton);
        toolsPanel.add(saveButton);
        toolsPanel.add(cancelButton);

        panel.add(navPanel);
        panel.add(toolsPanel);
        // add tooltips
        
        // add style
        panel.setStyleName(STYLE_TOOL_PANEL);
        saveButton.setStyleName(STYLE_SAVE);
        cancelButton.setStyleName(STYLE_CANCEL);
        helpButton.setStyleName(STYLE_HELP);
        nextButton.setStyleName(STYLE_NEXT);
        previousButton.setStyleName(STYLE_PREVIOUS);

        initWidget(panel);
    }

    public void notifyOnCancel() {
        for(WizardActionListener listener: listeners) listener.onCancel();
    }

    /**
     * Open the help manual in a new tab/window
     */
    private void openHelpManual() {
        // open manual
        Window.open(GlimmpseWeb.constants.helpManualURI(), "_blank", null);
        for(WizardActionListener listener: listeners) listener.onHelp();
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
