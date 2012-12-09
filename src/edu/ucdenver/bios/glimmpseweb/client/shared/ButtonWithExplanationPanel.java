/*
 * User Interface for the GLIMMPSE Software System.  Processes
 * incoming HTTP requests for power, sample size, and detectable
 * difference
 * 
 * Copyright (C) 2011 Regents of the University of Colorado.  
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
package edu.ucdenver.bios.glimmpseweb.client.shared;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;

/**
 * Panel containing an action button and explanation button
 * 
 * @author Vijay Akula
 * @author Sarah Kreidler
 *
 */
public class ButtonWithExplanationPanel extends Composite 
{
    protected Button button = new Button();
	public ButtonWithExplanationPanel(String name, String explainHeader, String explainText)
	{
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		
		button.setText(name);
		
		horizontalPanel.add(button);
		horizontalPanel.add(new ExplanationButton(explainHeader, explainText));
		
		// add style
		button.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
		
		initWidget(horizontalPanel);
	}
	
	public void addClickHandler(ClickHandler handler)
	{
		button.addClickHandler(handler);
	}

}
