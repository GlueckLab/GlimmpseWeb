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
package edu.ucdenver.bios.glimmpseweb.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;

import edu.ucdenver.bios.glimmpseweb.client.shared.GlimmpseApplicationPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.GlimmpseFeedbackPanel;

/**
 * Entry point class for the GLIMMPSE web interface.
 */
public class GlimmpseWeb implements EntryPoint
{
    /**
     * This is the entry point method.
     */
    // string constants for internationalization 
    public static final GlimmpseConstants constants =  
        (GlimmpseConstants) GWT.create(GlimmpseConstants.class); 
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{        
	    // remove the splash screen
	    RootPanel loadingPanel = RootPanel.get("loadingMessage");
	    if (loadingPanel != null) {
	        Element loadingDiv =  loadingPanel.getElement();
	        if (loadingDiv != null) {
	            loadingDiv.setInnerHTML(" ");
	        }
	    }
	    
        // add the main application
        RootPanel glimmpseApp = RootPanel.get("glimmpseApp");
        if (glimmpseApp != null)
        {
            glimmpseApp.add(new GlimmpseApplicationPanel());
            glimmpseApp.setStyleName(GlimmpseConstants.STYLE_GLIMMPSE_PANEL);
        }

        // add the feedback panel
        RootPanel glimmpseFeedback = RootPanel.get("glimmpseFeedback");
        if (glimmpseFeedback != null)
        {
            glimmpseFeedback.add(new GlimmpseFeedbackPanel());
            glimmpseFeedback.setStyleName(GlimmpseConstants.STYLE_GLIMMPSE_PANEL);
        }

        // set root style so it recognizes standard css elements like "body"
        RootPanel.get().setStyleName("body");
	}
}

