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

package edu.ucdenver.bios.glimmpseweb.client.guided;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;


public class ClusteringPanelSubPanel extends Composite
{
	public String ERROR_STYLE = "message-error";
	TextBox groupingTextBox = new TextBox();
	TextBox numberOfgroupsTextBox = new TextBox();
	ArrayList<ChangeHandler> handlerList = new ArrayList<ChangeHandler>();
	HTML errorHTML = new HTML();
	
	public ClusteringPanelSubPanel() 
	{
		
		VerticalPanel flexTableVerticalPanel = new VerticalPanel();
		Grid grid = new Grid(2,2);
		
		groupingTextBox.addChangeHandler(new ChangeHandler() 
		{			
			@Override
			public void onChange(ChangeEvent event) 
			{	
				TextBox tb = (TextBox)event.getSource();
				String value = tb.getValue();
				try
				{
				if (value == null && value.isEmpty() != false) throw new Exception();
				for(ChangeHandler handler: handlerList) handler.onChange(event);
				errorHTML.setHTML("");
				}
				catch (Exception e)
				{
					errorHTML.setHTML(GlimmpseWeb.constants.errorInvalidClusterGroupingName());
					errorHTML.setVisible(true);
					errorHTML.setStyleName(ERROR_STYLE);
				}
			}
		});
		numberOfgroupsTextBox.addChangeHandler(new ChangeHandler() 
		{
			@Override
			public void onChange(ChangeEvent event) 
			{
				TextBox tb = (TextBox)event.getSource();
				String value = tb.getValue();
				try
				{
				TextValidation.parseInteger(value, 0, true);
				for(ChangeHandler handler: handlerList) handler.onChange(event);
				errorHTML.setHTML("");
				}
				catch (Exception e)
				{
					errorHTML.setHTML(GlimmpseWeb.constants.errorInvalidClusterGroupingNumber());
					errorHTML.setVisible(true);
					errorHTML.setStyleName(ERROR_STYLE);
				}
			}
		});
		grid.setText(0, 0, GlimmpseWeb.constants.clusteringPanelAddClusteringRow1Column1());
		grid.setWidget(0, 1, groupingTextBox);
		grid.setText(1, 0, GlimmpseWeb.constants.clusteringPanelAddClusteringRow2Column1());
		grid.setWidget(1, 1, numberOfgroupsTextBox);

		errorHTML.setVisible(false);
		flexTableVerticalPanel.add(grid);
		flexTableVerticalPanel.add(errorHTML);
		
		// Initializing widget
		initWidget(flexTableVerticalPanel);
		
	}
	/**
	 * A Change Handler method to add changes to the handler
	 * @param handler
	 */
	public void addChangeHandler(ChangeHandler handler)
	{
		handlerList.add(handler);
	}
	
	/**
	 * A method that check the validation of the fields in the Clustering Panel Sub Panel 
	 * @return It will return a boolean value, returns true if the sub panel is complete filled else returns false
	 */
	
	public boolean checkComplete()
		{
			String checkGrouping = groupingTextBox.getText();
			String checkNumberOfGroups = numberOfgroupsTextBox.getText();
			try
			{
			if (checkGrouping != null && checkGrouping.isEmpty() == false && checkNumberOfGroups != null) return true;
			else return false;
			}
			catch (NumberFormatException nfe)
			{
			return false;
			}
		}
}
