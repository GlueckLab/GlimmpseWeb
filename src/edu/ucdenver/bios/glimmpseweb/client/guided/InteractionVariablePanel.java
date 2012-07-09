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
package edu.ucdenver.bios.glimmpseweb.client.guided;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTrendTypeEnum;

/** 
 * Sub panel to select interaction variables and edit trends
 * associated with each variable
 * 
 * @author Vijay Akula
 * @author Sarah Kreidler
 *
 */
public class InteractionVariablePanel extends Composite
{   
    // parent panel which listens for click events in this subpanel
    protected ClickHandler handler = null;
    
    // selection checkbox
    protected CheckBox checkBox;
    
    // button to allow editing of trends associated with this variable
	protected Button editTrendButton = 
	        new Button(GlimmpseWeb.constants.editTrendLabel(), new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			editTrend();
		}
	});

	// trend editing panel
    protected HorizontalPanel trendPanel = new HorizontalPanel();
	
    // short display of currently selected trend
    protected HorizontalPanel selectedTrendPanel = new HorizontalPanel();
	protected HTML selectedTrendHTML = 
	        new HTML(GlimmpseWeb.constants.editTrendNoTrend());
	// sub panel to select a trend type
	protected EditTrendPanel editTrendPanel = null;
	
	/**
	 * Constructor.
	 * @param label display for the checkbox
	 * @param numLevels number of levels for the variable associated with this
	 * panel.  Used to display valid trend tests.
	 * @param handler click event handler
	 */
	public InteractionVariablePanel(String label, int numLevels, ClickHandler handler)
	{
	    this.handler = handler;
		
	    // overall composite panel
	    VerticalPanel panel = new VerticalPanel();
	    
	    // horizontal layout 
		HorizontalPanel horizontalPanel = new HorizontalPanel();

		// create the selection box for this variable
		checkBox = new CheckBox(label);
		checkBox.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				boolean checked = ((CheckBox) event.getSource()).getValue();
                editTrendButton.setVisible(checked);
                editTrendButton.setEnabled(checked);
                trendPanel.setVisible(false);
                selectedTrendHTML.setVisible(checked);
			}
		});
		checkBox.addClickHandler(handler);
		
		// build the selected trend display panel
		selectedTrendPanel.add(new HTML(GlimmpseWeb.constants.editTrendSelectedTrendPrefix()));
		selectedTrendPanel.add(selectedTrendHTML);
		
		// create the edit trend panel
		editTrendPanel = new EditTrendPanel(label, numLevels);
		trendPanel.add(editTrendPanel);
		editTrendPanel.addClickHandler(new ClickHandler ()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
					RadioButton radioButton = (RadioButton) event.getSource();
					// update the selected trend information
					selectedTrendHTML.setText(radioButton.getHTML());
					selectedTrendPanel.setVisible(true);
					trendPanel.setVisible(false);
					editTrendButton.setVisible(true);
			}
		});
		editTrendPanel.addClickHandler(handler);
		
		
		// layout the top panel for the checkbox and trend display
		horizontalPanel.add(checkBox);
		horizontalPanel.add(editTrendButton);
		horizontalPanel.add(selectedTrendPanel);
		// layout the overall panel
		panel.add(horizontalPanel);
		panel.add(trendPanel);
		
		// add style
		editTrendButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
		
		// hide trend info to start
		reset();

		initWidget(panel);
	}
	
	/**
	 * Reset to default state which is unchecked
	 */
	public void reset() {
	    checkBox.setValue(false);
	    selectedTrendHTML.setVisible(false);
	    trendPanel.setVisible(false);
	}
	
	/**
	 * Show the edit trend panel
	 */
	private void editTrend()
	{
		editTrendButton.setVisible(false);
		trendPanel.setVisible(true);
	}
	
	public void hideTrend()
	{
	    editTrendButton.setVisible(true);
		trendPanel.setVisible(false);
	}
	
	public HypothesisTrendTypeEnum selectedTrend()
	{
	   return editTrendPanel.getSelectedTrend();
	}
	
	public boolean isChecked()
	{
	    return checkBox.getValue();
	}
}
