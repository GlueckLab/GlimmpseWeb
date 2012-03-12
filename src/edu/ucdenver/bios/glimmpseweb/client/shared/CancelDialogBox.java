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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;

public class CancelDialogBox extends DialogBox
{
	private static final String STYLE_BUTTON = "buttonStyle";
public Widget cancelDialogBox()
{
	DialogBox dialogBox = new DialogBox();
	dialogBox.setGlassEnabled(true);
    dialogBox.setAnimationEnabled(true);
    HTML query = new HTML(GlimmpseWeb.constants.cancelDialogBoxQuery());
    Button saveButton = new Button("Save", new ClickHandler()
    {
		@Override
		public void onClick(ClickEvent event) 
		{
			// TODO Auto-generated method stub
			
		}
    	
    });
    Button discardChangesButton = new Button("Discard Changes", new ClickHandler()
    {
		@Override
		public void onClick(ClickEvent event) 
		{
			// TODO Auto-generated method stub
			
		}
    	
    });
    Button continueButton = new Button("Continue", new ClickHandler()
    {
		@Override
		public void onClick(ClickEvent event) 
		{
			// TODO Auto-generated method stub
			
		}
    	
    });
    
    saveButton.setStyleName(STYLE_BUTTON);
    discardChangesButton.setStyleName(STYLE_BUTTON);
    continueButton.setStyleName(STYLE_BUTTON);
    HorizontalPanel horizontalPanel = new HorizontalPanel();
    horizontalPanel.add(saveButton);
    horizontalPanel.add(discardChangesButton);
    horizontalPanel.add(continueButton);
    horizontalPanel.setSpacing(10);
    
    VerticalPanel verticalPanel = new VerticalPanel();
    verticalPanel.add(query);
    verticalPanel.add(horizontalPanel);
    
    dialogBox.setText("Cancel Dialog Box");
    dialogBox.add(verticalPanel);
    dialogBox.show();
    
	return dialogBox;
}
}
