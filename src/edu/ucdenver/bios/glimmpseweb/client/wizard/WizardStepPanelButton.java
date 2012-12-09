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

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

/**
 * Button class added to the FlexTables displayed under each StackLayoutPanel
 * heading.  The button stores information about its position in the FlexTable
 * and the associated panel.  This allows next/previous navigation to occur.
 */
public class WizardStepPanelButton extends Button
{
    private Widget container = null;
    private WizardStepPanel panel;
    private boolean isGroupButton = false;
    
    /**
     * Creates a button which stored meta information about the current panel
     * and its position in the FlexTable
     * 
     * @param row row that this button appears in the FlexTable
     * @param panel panel associated with the button
     * @param handler click handler function
     */
    public WizardStepPanelButton(String name, WizardStepPanel panel, ClickHandler handler)
    {
        super(name, handler);
        this.panel = panel;
    }
    
    /**
     * Creates a button which stored meta information about the current panel
     * and its position in the FlexTable
     * 
     * @param row row that this button appears in the FlexTable
     * @param panel panel associated with the button
     * @param handler click handler function
     */
    public WizardStepPanelButton(WizardStepPanel panel, ClickHandler handler)
    {
        super(panel.getName(), handler);
        this.panel = panel;
    }
    
    /**
     * Returns true if this button is a group header.
     * @return true if the button represents a group header
     */
    public boolean isGroupButton() {
        return isGroupButton;
    }

    /**
     * Set whether this is a panel group header button.
     * @param isGroupButton if true, this button will be set as a group header
     */
    public void setGroupButton(boolean isGroupButton) {
        this.isGroupButton = isGroupButton;
    }

    /**
     * Get the panel associated with this button
     * @return WizardStepPanel
     */
    public WizardStepPanel getPanel()
    {
        return panel;
    }
    
    /**
     * Return the container in which this widget appears.  May be null
     * @return container widget
     */
    public Widget getContainer()
    {
        return container;
    }
    
    /**
     * Set the container widget for this button.  Primarily used to access
     * the disclosure panel for use in left navigation.
     * @param widget container widget
     */
    public void setContainer(Widget widget)
    {
        container = widget;
    }
}
