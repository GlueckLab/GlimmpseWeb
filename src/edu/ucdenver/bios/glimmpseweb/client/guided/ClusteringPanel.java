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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.CancelDialogBox;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;



public class ClusteringPanel extends WizardStepPanel implements ChangeHandler
{
	int countTreeLeaves = 1;
	Tree tree = new Tree();
	TreeItem treeitem = new TreeItem();
	boolean hasClustering = false;
	String buttonText = "Add Clustering";
	String description1;
	boolean cluster = false;
	public String Button_Style = "buttonStyle";
	
	protected Button addClusteringButton = new Button(buttonText,
			new ClickHandler() {
				public void onClick(ClickEvent event) {
					hasClustering = true;
					change();
				}
			});
	/**
	 * Click Handler Method to Add a instance of Clustering Panel Sub Panel class to the tree.
	 */
	ClickHandler addSubgroupEvent = new ClickHandler() 
	{
		public void onClick(ClickEvent event) 
		{
			if (currentLeaf != null)
			{
				
				countTreeLeaves = countTreeLeaves+1;
				TreeItem newLeaf = new TreeItem(new ClusteringPanelSubPanel());
				currentLeaf.addItem(newLeaf);
				currentLeaf.setState(true);
				currentLeaf = newLeaf;
			} 
			else 
			{
				countTreeLeaves = countTreeLeaves+1;
				currentLeaf = new TreeItem(new ClusteringPanelSubPanel());
				currentLeaf.setState(true);
				tree.addItem(currentLeaf);
			}
		}
	};
	Button addSubgroupButton = new Button("Add Subgroup", addSubgroupEvent);
	/**
	 * Click Handler method to remove the last instance of the Clustering Panel Sub Panel class added to the Tree
	 */
	ClickHandler removeSubgroupEvent = new ClickHandler() 
	{
		@Override
		public void onClick(ClickEvent event) 
		{
			if(countTreeLeaves == 1)
			{
				buttonText = "Add Clustering";
				addClusteringButton.setText(buttonText);
				tree.clear();
				horizontalpanel.setVisible(false);
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
					countTreeLeaves = countTreeLeaves-1;
					buttonText = "Add Clustering";
					addSubgroupButton.setVisible(false);
					removeSubgroupButton.setVisible(false);
					addClusteringButton.setText(buttonText);
					finishClustering.setVisible(false);
				}
			}
		}
	};
	Button removeSubgroupButton = new Button("Remove Subgroup", removeSubgroupEvent);
	protected TreeItem currentLeaf;
	protected TreeItem root;
	HTML description = new HTML(GlimmpseWeb.constants.clusteringPanelDescription1());
	VerticalPanel panel = new VerticalPanel();
	ClickHandler finishClusteringEvent = new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			checkComplete();
		}
	};
	Button finishClustering = new Button("Finish Clustering",
			finishClusteringEvent);

	HorizontalPanel horizontalpanel = new HorizontalPanel();
	
	
	/**
	 * Constructor for the Clustering Panel Class
	 */
	public ClusteringPanel(WizardContext context)
    {
    	super(context, "Clustering");
		notifyComplete();
		HTML title = new HTML(GlimmpseWeb.constants.clusteringPanelTitle());
		HTML header = new HTML(GlimmpseWeb.constants.clusteringPanelHeader());

		finishClustering.setVisible(false);
		panel.add(title);
		panel.add(header);
		panel.add(description);
		panel.add(addClusteringButton);
		horizontalpanel.setSpacing(10);
		horizontalpanel.add(addSubgroupButton);
		horizontalpanel.add(removeSubgroupButton);
		horizontalpanel.add(finishClustering);
		panel.add(horizontalpanel);
		addSubgroupButton.setVisible(false);
		removeSubgroupButton.setVisible(false);
		tree.addItem(treeitem);
		panel.add(tree);
	
		// Setting Styles 
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		title.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		addSubgroupButton.setStyleName(Button_Style);
		removeSubgroupButton.setStyleName(Button_Style);
		finishClustering.setStyleName(Button_Style);
		
		
		// Initializing widget
		initWidget(panel);
	}
	
    /**
     * change() method is to check the text on the Button and perform the necessary actions
     */
	
	public void change()
	{
		if (buttonText == "Add Clustering") 
		{
			notifyInProgress();
			cluster = true;
			finishClustering.setVisible(true);
			buttonText = "Remove Clustering";
			addSubgroupButton.setVisible(true);
			removeSubgroupButton.setVisible(true);
			addClusteringButton.setText(buttonText);
			description1 = GlimmpseWeb.constants.clusteringPanelDescription2();
			description.setHTML(description1);
			addClustering();

		} 
		else if (buttonText == "Remove Clustering") 
		{
			cluster = false;
			buttonText = "Add Clustering";
			addSubgroupButton.setVisible(false);
			removeSubgroupButton.setVisible(false);
			addClusteringButton.setText(buttonText);
			description1 = GlimmpseWeb.constants.clusteringPanelDescription1();
			description.setHTML(description1);
			finishClustering.setVisible(false);
			tree.removeItems();
			notifyComplete();
			CancelDialogBox cb = new CancelDialogBox();
			((DialogBox) cb.cancelDialogBox()).center();
			//((DialogBox) cb.cancelDialogBox()).show();
		}
	}

	
	/**
	 *  Add Clustering Method initiates the Tree by adding a root node to the tree.
	 */
	
	public void addClustering() 
	{	
		currentLeaf = new TreeItem(new ClusteringPanelSubPanel());
		tree.addItem(currentLeaf);
		horizontalpanel.setVisible(true);
	}
	
	
	/**
	 * checkComplete() method is to check if all the instances of Clustering Panel Sub Panel Class added to the tree are
	 * validated or not and then activate the Next button in the Wizard Setup Panel 
	 */
	
	private void checkComplete() 
	{
		for(int i = countTreeLeaves; i > 0; i--)
		{
			tree.getItem(i);
			new ClusteringPanelSubPanel().checkComplete();

		}
	}
	
	
	public void onChange(ChangeEvent textEntry) 
	{
		

	}
	
	
	public void onClick() 
	{

	}

	@Override
	public void reset() 
	{
		// TODO Auto-generated method stub

	}

}
