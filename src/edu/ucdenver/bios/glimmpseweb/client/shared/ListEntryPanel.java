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
package edu.ucdenver.bios.glimmpseweb.client.shared;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;

/**
 * Common widget for entering lists of values.  Values are validated 
 * before being added to the list.
 * 
 * @author Sarah Kreidler
 *
 */
public class ListEntryPanel extends Composite
{
	protected static final int VISIBLE_COUNT = 4;
	protected static final int HEADER_ROW = 0;
	protected static final int LISTBOX_ROW = 1;
	
    // text entry box
    protected TextBox listEntryTextBox = new TextBox();
    // list of current entries
    protected ListBox listBox = new ListBox(true);    
    // error display
    protected HTML errorHTML = new HTML();
    // object for validating new entries
    protected ListValidator validator;
    // counter of the number of valid rows in the table
    protected int validRowCount = 0;
    // optional maximum number of entries for list
    protected int maxRows = -1;
    
    /**
     * Construct a new list entry panel
     * @param columnName title/label for list entry items
     * @param validator class which validates list entries
     * @see ListValidator
     */
	public ListEntryPanel(String columnName, ListValidator validator)
	{
		this.validator = validator;
		
        // create the dynamic table for entering predictors
        VerticalPanel panel = new VerticalPanel();
        
        // layout the panel;
        panel.add(createTextEntry(columnName));
        panel.add(listBox);
        panel.add(errorHTML);
        
        // set style
        listBox.setVisibleItemCount(VISIBLE_COUNT); // can't seem to do this with css?
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_PANEL);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        
        // initialize the panel
        initWidget(panel);
	}
	
	/**
	 * Create the top input bar for the list entry panel
	 * 
	 * @param columnName title/label for list items
	 * @return panel
	 */
	private HorizontalPanel createTextEntry(String columnName)
	{
		HorizontalPanel panel = new HorizontalPanel();
		listEntryTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				addListIem();
			}
		});
		Button deleteButton = new Button((GlimmpseWeb.constants.buttonDelete()), 
				new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				removeSelectedListItems();
			}
		});
		Button addButton = new Button((GlimmpseWeb.constants.buttonAdd()), 
				new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				addListIem();
			}
		});
        
		
		// layout the panel
		panel.add(new HTML(columnName + ": "));
		panel.add(listEntryTextBox);
		panel.add(addButton);
		panel.add(deleteButton);
		// add style
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_HEADER);
		addButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
		deleteButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
		return panel;
	}
	
	/**
	 * Validate and add an item to the listbox
	 */
	private void addListIem()
	{
		String value = listEntryTextBox.getValue();
		if (value != null && !value.isEmpty())
		{
			try
			{
				if (maxRows > 0 && listBox.getItemCount() >= maxRows)
					throw new IllegalArgumentException(GlimmpseWeb.constants.errorMaxRows());
				validator.validate(value);
				listBox.addItem(value);
				TextValidation.displayOkay(errorHTML, "");
				validator.onValidRowCount(listBox.getItemCount());
			}
			catch (IllegalArgumentException iae)
			{
				TextValidation.displayError(errorHTML, iae.getMessage());
			}
			finally
			{
				listEntryTextBox.setText("");
			}
		}
	}
	
	/**
	 * Clear selected items from the listbox
	 */
	private void removeSelectedListItems()
	{
		for(int i = listBox.getItemCount()-1; i >= 0; i--)
		{
			if (listBox.isItemSelected(i)) listBox.removeItem(i);
		}
		validator.onValidRowCount(listBox.getItemCount());
	}
	
	/**
	 * Return an XML representation of the list
	 * @param tagName the enclosing list tag name
	 * @return XML representation of the list
	 */
	public String toXML(String tagName)
	{
    	StringBuffer buffer = new StringBuffer();
    	if (listBox.getItemCount() > 0)
    	{
    		buffer.append("<" + tagName + ">");
    		for(int i = 0; i < listBox.getItemCount(); i++)
    		{
    			buffer.append("<v>");
    			buffer.append(listBox.getItemText(i));
    			buffer.append("</v>");	
    		}
    		buffer.append("</" + tagName + ">");
    	}
    	return buffer.toString();
	}
	
	/**
	 * Get the number of entries in the list
	 * @return number of entries in the list
	 */
    public int getValidRowCount()
    {
    	return listBox.getItemCount();
    }
    
    /**
     * Get a list of the values in the list as Strings
     * @return list of values
     */
    public List<String> getValues()
    {
    	ArrayList<String> outputList = new ArrayList<String>();
    	for(int i = 0; i < listBox.getItemCount(); i++)
    	{
    		outputList.add(listBox.getItemText(i));
    	}
    	return outputList;
    }
    
    /**
     * Clear the list
     */
    public void reset()
    {
    	listBox.clear();
    	TextValidation.displayOkay(errorHTML, "");
    }
    
    public void setMaxRows(int maxRows)
    {
    	this.maxRows = maxRows;
    }
    
    /**
     * Manually add an item to the list.  Invalid entries are ignored
     * @param value the item to be added
     */
    public void add(String value)
    {
    	try
    	{
    		validator.validate(value);
        	listBox.addItem(value);
    	}
    	catch (Exception e)
    	{
    		// ignore invalid entries
    	}
    }
    
    /**
     * Load the list of doubles into the list box widget.
     * 
     * @param valueList list of doubles
     */
    public void loadFromDoubleList(List<Double> valueList, boolean clear)
    {
    	if (clear) reset();
		for(Double value: valueList)
		{
			add(Double.toString(value));
		}
		validator.onValidRowCount(listBox.getItemCount());
    }
    
    /**
     * Load the list of integers into the list box widget.
     * 
     * @param valueList list of integers
     */
    public void loadFromIntegerList(List<Integer> valueList, boolean clear)
    {
    	if (clear) reset();
		for(Integer value: valueList)
		{
			add(Integer.toString(value));
		}
		validator.onValidRowCount(listBox.getItemCount());
    }
    
    /**
     * Load the list of String values into the list box widget.
     * 
     * @param valueList list of Strings
     */
    public void loadFromStringList(List<String> valueList, boolean clear)
    {
    	if (clear) reset();
		for(String value: valueList)
		{
			add(value);
		}
		validator.onValidRowCount(listBox.getItemCount());
    }
    
    
}
