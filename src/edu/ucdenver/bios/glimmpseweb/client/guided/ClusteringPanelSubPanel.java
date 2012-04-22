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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301, USA.
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

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.webservice.common.domain.ClusterNode;

/**
 * Panel for tree item in clustering tree hierarchy.
 * @author VIJAY AKULA
 *
 */
public class ClusteringPanelSubPanel extends Composite {
    // text box for entering the name of the clustering level
    // (ex. school, census tract, etc.)
    protected TextBox groupingTextBox = new TextBox();

    // text box for entering the number of groups/participants within
    // each cluster
    protected TextBox numberOfGroupsTextBox = new TextBox(); 
    
    // text box for entering the intraclass correlation
    protected TextBox iccTextBox = new TextBox(); 
    
    
    //html widget to display the error message
    protected HTML errorHTML = new HTML();
    
    // Parent panels, etc which listen for changes within the subpanel
    protected ArrayList<ChangeHandler> handlerList = new ArrayList<ChangeHandler>();

    /**
     * Constructor
     */
    public ClusteringPanelSubPanel() 
    {
        // vertical panel to hold all the widgets within
        VerticalPanel verticalPanel = new VerticalPanel();
        
        // grid to hold the html naming widgets and the text boxes
        Grid grid = new Grid(3,2);
        
        // Add handlers
        // handler to validate clustering name
        groupingTextBox.addChangeHandler(new ChangeHandler() 
        {            
            @Override
            public void onChange(ChangeEvent event) 
            {    
                TextBox tb = (TextBox)event.getSource();
                String value = tb.getValue();
                try
                {
                    TextValidation.parseString(value);
                    TextValidation.displayOkay(errorHTML, "");
                }
                catch (Exception e)
                {
                    TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidString());
                    tb.setText("");
                }
                for(ChangeHandler handler: handlerList) handler.onChange(event);
            }
        });
        // handler to validate integer inputs in the number of groups box
        numberOfGroupsTextBox.addChangeHandler(new ChangeHandler() 
        {
            @Override
            public void onChange(ChangeEvent event) 
            {
                TextBox tb = (TextBox)event.getSource();
                String value = tb.getValue();
                try
                {
                    TextValidation.parseInteger(value, 0, true);
                    TextValidation.displayOkay(errorHTML, "");
                }
                catch (Exception e)
                {
                    TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidClusterSize());
                       tb.setText("");
                }
                for(ChangeHandler handler: handlerList) handler.onChange(event);
            }
        });
        // handler to validate integer inputs in the number of groups box
        iccTextBox.addChangeHandler(new ChangeHandler() 
        {
            @Override
            public void onChange(ChangeEvent event) 
            {
                TextBox tb = (TextBox)event.getSource();
                String value = tb.getValue();
                try
                {
                    TextValidation.parseDouble(value, -1, 1, true);
                    TextValidation.displayOkay(errorHTML, "");
                }
                catch (Exception e)
                {
                    TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidCorrelation());
                       tb.setText("");
                }
                for(ChangeHandler handler: handlerList) handler.onChange(event);
            }
        });
        
        
        // layout the panel
        grid.setWidget(0, 0, new HTML(GlimmpseWeb.constants.clusteringNodePanelNameLabel()));
        grid.setWidget(0, 1, groupingTextBox);
        grid.setWidget(1, 0, new HTML(GlimmpseWeb.constants.clusteringNodePanelNumberOfGroupsLabel()));
        grid.setWidget(1, 1, numberOfGroupsTextBox);
        grid.setWidget(2, 0, new HTML(GlimmpseWeb.constants.clusteringNodePanelIntraclassCorrelationLabel()));
        grid.setWidget(2, 1, iccTextBox);
        verticalPanel.add(grid);
        verticalPanel.add(errorHTML);
        
        // set style
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        
        // Initializing widget
        initWidget(verticalPanel);
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
        String checkNumberOfGroups = numberOfGroupsTextBox.getText();
        String checkICC = iccTextBox.getText();
        if (checkGrouping != null && !checkGrouping.isEmpty()
                && checkNumberOfGroups != null && !checkNumberOfGroups.isEmpty()
                && checkICC != null && !checkICC.isEmpty()) 
            return true;
        else 
            return false;
    }
    
    public String getGroupingName()
    {
        return groupingTextBox.getText();
    }
    
    public int getNumberOfGroups()
    {
        return Integer.parseInt(numberOfGroupsTextBox.getValue());
    }
    
    public void setGroupingName(String name)
    {
        groupingTextBox.setText(name);
    }
    
    public void setNumberOfGroups(int numGroups)
    {
        numberOfGroupsTextBox.setValue(Integer.toString(numGroups));
    }
    
    
    /**
     * Convert the contents of  clustering node sub panel into a Cluster Node 
     * domain object
     * 
     * @param nodeId node identifier
     * @param parentId node identifier for the node's parent
     * @return A clustering node instance
     */
    public ClusterNode toClusterNode(int nodeId, int parentId)
    {
        if (checkComplete()) {
            ClusterNode clusterNode = new ClusterNode();

            clusterNode.setGroupName(groupingTextBox.getValue());
            clusterNode.setGroupSize(Integer.parseInt(numberOfGroupsTextBox.getValue()));
            clusterNode.setIntraClusterCorrelation(Double.parseDouble(iccTextBox.getText()));
            clusterNode.setNode(nodeId);
            clusterNode.setParent(parentId);
            return clusterNode;
        } else {
            return null;
        }
    }

    /**
     * Load the panel information from the ClusterNode object
     * @param clusterNode the ClusterNode object
     */
    public void loadFromClusterNode(ClusterNode clusterNode)
    {
        setGroupingName(clusterNode.getGroupName());
        setNumberOfGroups(clusterNode.getGroupSize());
    }
    
    
}
