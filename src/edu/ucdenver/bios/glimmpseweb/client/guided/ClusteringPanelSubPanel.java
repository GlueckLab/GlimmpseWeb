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

    // pointer to the parent panel to manage changes
    protected ClusteringPanel parent = null;
    // current depth in the clustering tree
    protected int depth = -1;

    /**
     * Constructor
     */
    public ClusteringPanelSubPanel(String dependentStyleName, 
            int depth, ClusteringPanel parent) 
    {
        this.parent = parent;
        this.depth = depth;
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
                try
                {
                    String value = TextValidation.parseString(tb.getValue());
                    TextValidation.parseString(value);
                    TextValidation.displayOkay(errorHTML, "");
                }
                catch (Exception e)
                {
                    TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidString());
                    tb.setText("");
                }
                checkComplete();
                notifyParent();
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
                checkComplete();
                notifyParent();
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
                checkComplete();
                notifyParent();
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
        verticalPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TREE_NODE);
        if (dependentStyleName != null && !dependentStyleName.isEmpty()) {
            verticalPanel.addStyleDependentName(dependentStyleName);
        }
        // Initializing widget
        initWidget(verticalPanel);
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

    /**
     * Load the panel information from the ClusterNode object
     * @param clusterNode the ClusterNode object
     */
    public void loadFromClusterNode(ClusterNode clusterNode)
    {
        if (clusterNode != null) {
            if (clusterNode.getGroupName() != null) {
                groupingTextBox.setText(clusterNode.getGroupName());
            }
            if (clusterNode.getGroupSize() > 0) {
                numberOfGroupsTextBox.setValue(Integer.toString(clusterNode.getGroupSize()));
            }
            if (clusterNode.getIntraClusterCorrelation() >= -1 &&
                    clusterNode.getIntraClusterCorrelation() <= 1) {
                iccTextBox.setValue(Double.toString(clusterNode.getIntraClusterCorrelation()));
            }
        }
    }

    /**
     * Get the cluster name
     */
    public String getClusterName() {
        return groupingTextBox.getValue();
    }
    
    /**
     * Get the cluster size
     * @return
     */
    public int getClusterSize() {
        String numGroups = numberOfGroupsTextBox.getValue();
        if (numGroups != null && !numGroups.isEmpty()) {
           return Integer.parseInt(numGroups);
        } else {
            return -1;
        }
    }
    
    /**
     * Get the intra-cluster correlation
     */
    public double getIntraClusterCorrelation() {
        String icc = iccTextBox.getText();
        if (icc != null && !icc.isEmpty()) {
            return Double.parseDouble(icc);
        } else {
            return Double.NaN;
        }
    }
    
    /**
     * Return the tree depth of this node
     */
    public int getDepth() {
        return depth;
    }
    
    /**
     * Notify the parent panel that a change has occurred
     */
    private void notifyParent() {
        parent.updateClusteringNode(this);
    }
}
