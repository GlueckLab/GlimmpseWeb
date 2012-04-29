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
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;

/**
 * Explanation dialog panel
 * 
 * @author Vijay Akula
 * @author Sarah Kreidler
 *
 */
public class ExplanationDialogBox extends DialogBox {

    public ExplanationDialogBox(String header, String text) {
        super();
        // build the associated explanation dialogBox
        VerticalPanel verticalPanel = new VerticalPanel();

        HTML headerText = new HTML(header);
        HTML explanationText = new HTML(text);

        // set style
        headerText.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        explanationText.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        // setup scroll panel in case of long text
        ScrollPanel scrollPanel = new ScrollPanel(explanationText);
        scrollPanel.setSize("200px", "200px");

        Button dialogOK = new Button(GlimmpseWeb.constants.buttonClose(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        dialogOK.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);

        verticalPanel.add(headerText);
        verticalPanel.add(scrollPanel);
        verticalPanel.add(dialogOK);
        add(verticalPanel);
    }

}
