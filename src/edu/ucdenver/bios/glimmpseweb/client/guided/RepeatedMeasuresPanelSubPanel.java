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


import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;



public class RepeatedMeasuresPanelSubPanel extends Composite {
	
	int index = 0;
	int flexTableCellCount = 0;
	int no_of_measurements;
	DisclosurePanel disclosurePanel = new DisclosurePanel("Add Unequal Spacing");
	FlexTable flexTable = new FlexTable();
	TextBox noOfMeasurementsTextBox = new TextBox();
	HTML htmlerror = new HTML();
	HTML label = new HTML("Enter Spacing");
	VerticalPanel verticalSubPanel = new VerticalPanel();
	HorizontalPanel horizontalPanel = new HorizontalPanel();
	ListBox listbox = new ListBox(false);
	Button reset = new Button("Reset", new ClickHandler(){

		@Override
		public void onClick(ClickEvent event) 
		{
			clearFlexTableTextBoxes();
		}
		
	});
	public RepeatedMeasuresPanelSubPanel()
	{
		VerticalPanel panel = new VerticalPanel();

		Grid grid = new Grid(3,2);
		
		//Grid Row 1
		HTML dimension = new HTML("Dimension");
		TextBox dimensionTextBox = new TextBox();
		
		//Grid Row 2
		HTML type = new HTML("Type");
		listbox.addItem("Numeric");
		listbox.addItem("Ordinal");
		listbox.addItem("Caterogiocal");
		listbox.setItemSelected(0, true);
		listbox.addChangeHandler(new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent event) {
				
				ListBox lb = (ListBox)event.getSource();
				index = lb.getSelectedIndex();
				if(index == 0)
				{
					onSelectNumericItem();
				}
				else
				{
					onDeselectNumericItem();					
				}
			}
		});
		
		//Grid Row 3
		HTML noOfMeasurements = new HTML("No of Measurements");
		noOfMeasurementsTextBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				no_of_measurements = Integer.parseInt(noOfMeasurementsTextBox.getValue());
				showSpacingBar();
			}
		});
	
		
		grid.setWidget(0, 0, dimension);
		grid.setWidget(0, 1, dimensionTextBox);
		grid.setWidget(1, 0, type);
		grid.setWidget(1, 1, listbox);
		grid.setWidget(2, 0, noOfMeasurements);
		grid.setWidget(2, 1, noOfMeasurementsTextBox);
	
		
		panel.add(grid);
		verticalSubPanel.setVisible(false);
		verticalSubPanel.add(htmlerror);
		verticalSubPanel.add(horizontalPanel);
		verticalSubPanel.add(reset);
		horizontalPanel.setWidth("100%");
		horizontalPanel.add(label);
		horizontalPanel.add(flexTable);
		htmlerror.setVisible(false);
		panel.add(verticalSubPanel);
		

		/*Initializing Widget*/
		initWidget(panel);
		
	}
	public void clearFlexTableTextBoxes()
	{
		no_of_measurements = Integer.parseInt(noOfMeasurementsTextBox.getValue());
		if(no_of_measurements > 0)
		{
			for(int i = 0; i < no_of_measurements; i++)
			{
				flexTable.setWidget(0, i ,unequalSizeTextBox(i));
			}
		}
		else
		{
			
		}
	}
	public void modifyFlexTable()
	{
		if(flexTable.getRowCount()>0)
		{
		flexTableCellCount = flexTable.getCellCount(0);
		}
		if(no_of_measurements > flexTableCellCount)
		{
			for(int i = flexTableCellCount; i < no_of_measurements; i++)
			{
				flexTable.setWidget(0, i ,unequalSizeTextBox(i));
			}
		}
		else
		{
			for( int i = flexTableCellCount-1; i >= no_of_measurements; i--)
			{
				flexTable.removeCell(0, i);
			}
		}
	}
	public void onSelectNumericItem()
	{
		showSpacingBar();
		noOfMeasurementsTextBox.setText("");
	}
	public void onDeselectNumericItem()
	{
		noOfMeasurementsTextBox.setText("");
		verticalSubPanel.setVisible(false);
	}
	
	public TextBox unequalSizeTextBox(int i)
	{
		TextBox measurementInputs = new TextBox();
		measurementInputs.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				TextBox textbox = (TextBox)event.getSource();
				String value = textbox.getValue();
				unEqualSpacingEntry(value);
			}
		});
		i = i+1;
		String value = String.valueOf(i);
		measurementInputs.setWidth("50%");
		measurementInputs.setText(value);
		return measurementInputs;
	}

	public void unEqualSpacingEntry(String value)
	{
		try
		{
			htmlerror.setText("");
			htmlerror.setVisible(false);
			TextValidation.parseDouble(value, 1, true);
		}
		catch(NumberFormatException e)
		{
			htmlerror.setText("The Spacing should be an Integer value greater than 0");
			htmlerror.setVisible(true);
		}
		
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

	public void showSpacingBar()
	{
		try
		{
			int i = Integer.parseInt(noOfMeasurementsTextBox.getValue());
			if(i > 0 && listbox.isItemSelected(0))
			{
				verticalSubPanel.setVisible(true);
				modifyFlexTable();	
			}
		}
		catch(Exception e)
		{
			verticalSubPanel.setVisible(false);
		}
		
	}
}
