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

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;

/**
 * Button which opens and explanation dialog box
 * 
 * @author Vijay Akula
 * @author Sarah Kreidler
 *
 */
public class ExplanationButton extends Button
{
    private static final String STYLE = "explanationButton";

    protected ExplanationDialogBox dialogBox;
    
	/**
	 * Constructor
	 * @param buttonText
	 * @param alertTextHeader
	 * @param alertText
	 */
	public ExplanationButton(String headerText, String explanationText)
	{
	    // set up the button itself
	    super(GlimmpseWeb.constants.buttonExplain());
	    this.addClickHandler(new ClickHandler(){
	        @Override
	        public void onClick(ClickEvent event) 
	        {
	            showDialog();
	        }
	    });
	       
	    // set style
	    this.setStyleName(STYLE);
	    
	    dialogBox = new ExplanationDialogBox(headerText, explanationText);
	}

	private void showDialog() {
	    dialogBox.center();
	}
	
}
