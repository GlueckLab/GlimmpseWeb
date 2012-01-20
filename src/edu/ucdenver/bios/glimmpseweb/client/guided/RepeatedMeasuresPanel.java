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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;


import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;

public class RepeatedMeasuresPanel extends WizardStepPanel
implements ClickHandler, ChangeHandler
{
	int countTreeLeaves = 1;
	Tree tree = new Tree();
	TreeItem treeItem = new TreeItem();
	TreeItem currentLeaf = new TreeItem();
	HTML description = new HTML(GlimmpseWeb.constants.repeatedMeasuresDescription());
	HTML instructions = new HTML(GlimmpseWeb.constants.repeatedMeasuresInstructions());
	HorizontalPanel horizontalPanel = new HorizontalPanel();
	Button addRepeatedMeasuresButton = new Button("Add Repeated Measures", new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			addRepeatedMeasuresEvent();
		}
	});
	
    Button removeRepeatedMeasuresButton = new Button("Remove Repeated Measures", new ClickHandler(){

		@Override
		public void onClick(ClickEvent event) {
			removeRepeatedMeasuresEvent();
		}
    });
    
    Button addRepeatedMeasuresWithinDimension = new Button("Add Repeated Measures within Dimension", new ClickHandler(){
		@Override
		public void onClick(ClickEvent event) {
			if (currentLeaf != null) 
			{
				countTreeLeaves = countTreeLeaves+1;
				TreeItem newLeaf = new TreeItem(new RepeatedMeasuresPanelSubPanel());
				currentLeaf.addItem(newLeaf);
				currentLeaf.setState(true);
				currentLeaf = newLeaf;
			} 
			else 
			{
				currentLeaf = new TreeItem(new RepeatedMeasuresPanelSubPanel());
				currentLeaf.setState(true);
				tree.addItem(currentLeaf);
			}
		}
	});
    
    Button removeRepeatedMeasuresWithinDimension = new Button("Remove Repeated Measures within Dimension", new ClickHandler(){
		@Override
		public void onClick(ClickEvent event) {
			if(countTreeLeaves == 1)
			{
				tree.clear();
				horizontalPanel.setVisible(false);
				removeRepeatedMeasuresButton.setVisible(false);
				addRepeatedMeasuresButton.setVisible(true);
			}
			else
			{
				try
				{
					countTreeLeaves = countTreeLeaves-1;
					TreeItem parentLeaf = currentLeaf.getParentItem();
					currentLeaf.remove();
					currentLeaf = parentLeaf;	
				}
				catch(Exception e)
				{
					horizontalPanel.setVisible(false);
					removeRepeatedMeasuresButton.setVisible(false);
					addRepeatedMeasuresButton.setVisible(true);
				}
			}	
		}
	});
  
    VerticalPanel panel = new VerticalPanel();
   
	public RepeatedMeasuresPanel(WizardContext context)
    {
    	super(context, "Repeated Measures");
		HTML title = new HTML(GlimmpseWeb.constants.repeatedMeasuresTitle());
		HTML header = new HTML(GlimmpseWeb.constants.repeatedMeasuresHeader());
								
		panel.add(title);
		panel.add(header);
		panel.add(description);
		instructions.setVisible(false);
		panel.add(instructions);
		panel.add(addRepeatedMeasuresButton);
		removeRepeatedMeasuresButton.setVisible(false);
		panel.add(removeRepeatedMeasuresButton);
		panel.add(tree);
		
		horizontalPanel.add(addRepeatedMeasuresWithinDimension);
		horizontalPanel.add(removeRepeatedMeasuresWithinDimension);
		horizontalPanel.setVisible(false);
		panel.add(horizontalPanel);
		
		
		//Setting Styles
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		title.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        instructions.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		
        //Initializing Widget
		initWidget(panel);
	}

	public void addRepeatedMeasuresEvent()
	{
		removeRepeatedMeasuresButton.setVisible(true);
		addRepeatedMeasuresButton.setVisible(false);
		instructions.setVisible(true);
		description.setHTML(GlimmpseWeb.constants.repeatedMeasuresDescription1());
		currentLeaf = new TreeItem(new RepeatedMeasuresPanelSubPanel());
		tree.addItem(currentLeaf);
		horizontalPanel.setVisible(true);
	}
	public void removeRepeatedMeasuresEvent()
	{
		addRepeatedMeasuresButton.setVisible(true);
		horizontalPanel.setVisible(false);
		removeRepeatedMeasuresButton.setVisible(false);
		tree.clear();
	}
    
    @Override
    public void onExit()
    {
    	
    }
    
	@Override
	public void reset()
	{
		
	}

	@Override
	public void onClick(ClickEvent event)
	{
		
	}
	
	@Override
	public void onChange(ChangeEvent event)
	{
		
	}

}
