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
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;

/**
 * Various extra tools such as save, help, cancel
 * @author Sarah
 *
 */
public class WizardActionPanel extends Composite {

    // style for tools
    protected static final String STYLE_TOOL_PANEL = "wizardToolsPanel";
    protected static final String STYLE_TOOL_BUTTON = "wizardToolsPanelButton";
    protected static final String STYLE_CONTENT_PANEL = "wizardContentPanel";
    protected static final String STYLE_SAVE = "save";
    protected static final String STYLE_CANCEL = "cancel";
    protected static final String STYLE_HELP = "help";


    
    protected ArrayList<WizardActionListener> listeners = new ArrayList<WizardActionListener>();

    public WizardActionPanel() {
        VerticalPanel panel = new VerticalPanel();

        Button saveButton = new Button(GlimmpseWeb.constants.buttonSave(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                notifyOnSave();
            }
        });

        Button cancelButton = new Button(GlimmpseWeb.constants.buttonCancel(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                notifyOnCancel();
            }
        });
        Button helpButton = new Button(GlimmpseWeb.constants.buttonHelp(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                openHelpManual();
            }
        });
        panel.add(saveButton);
        panel.add(helpButton);
        panel.add(cancelButton);


        
        
        // add style
        panel.setStyleName(STYLE_TOOL_PANEL);
        saveButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
        saveButton.addStyleDependentName(STYLE_SAVE);
        cancelButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
        cancelButton.addStyleDependentName(STYLE_CANCEL);
        helpButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
        helpButton.addStyleDependentName(STYLE_HELP);

        initWidget(panel);
    }

    public void notifyOnCancel() {
        for(WizardActionListener listener: listeners) listener.onCancel();
    }

    /**
     * Open the help manual in a new tab/window
     */
    public void openHelpManual() {
        // open manual
        Window.open(GlimmpseWeb.constants.helpManualURI(), "_blank", null);
        for(WizardActionListener listener: listeners) listener.onHelp();
    }

    /**
     * Notify listeners of save events
     */
    public void notifyOnSave() {
        for(WizardActionListener listener: listeners) listener.onSave();
    }

    /**
     * Add a listener for action events
     * @param listener
     */
    public void addActionListener(WizardActionListener listener) {
        listeners.add(listener);
    }
}
