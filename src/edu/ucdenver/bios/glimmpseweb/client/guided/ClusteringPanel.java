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

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TreeItemAction;
import edu.ucdenver.bios.glimmpseweb.client.TreeItemIterator;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.ClusterNode;

/**
 * Panel which allows the user to enter information about clustering
 * in the study design
 * 
 * @author VIJAY AKULA
 * @author Sarah Kreidler
 */
public class ClusteringPanel extends WizardStepPanel {
    // context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    // tree describing the clustering hierarchy
    protected Tree clusteringTree = new Tree();
    // number of items in the tree
    protected int itemCount = 0;
    // pointer to the lower most leaf in the tree
    protected TreeItem currentLeaf = null;
    // used to verify completeness
    boolean complete;
    
    // actions performed when iterating over the tree of clustering nodes
    // action to determine if the screen is complete
    TreeItemAction checkCompleteAction = new TreeItemAction() {
        @Override
        public void execute(TreeItem item, int visitCount, int parentVisitCount)
        {
            updateComplete((ClusteringPanelSubPanel) item.getWidget());
        }
    };
    
    /* buttons */
    // button to initiate entering of clustering information
    protected Button addClusteringButton = 
        new Button(GlimmpseWeb.constants.clusteringPanelAddClusteringButton(),
            new ClickHandler() {
        public void onClick(ClickEvent event) {
            addSubgroup();
            addClusteringNodeToStudyDesign();
            checkComplete();
        }
    });
    // button to remove clustering information
    protected Button removeClusteringButton = 
        new Button(GlimmpseWeb.constants.clusteringPanelRemoveClusteringButton(),
            new ClickHandler() {
        public void onClick(ClickEvent event) {
            clearClusteringFromStudyDesign();
            reset();
        }
    });
    //  button for adding a subgroups
    protected Button addSubgroupButton = 
        new Button(GlimmpseWeb.constants.clusteringPanelAddSubgroupButton(),
                new ClickHandler() {
        public void onClick(ClickEvent event) {
            addSubgroup();
            addClusteringNodeToStudyDesign();
            checkComplete();
        }
    });
    //  button for removing subgroups
    protected Button removeSubgroupButton = 
        new Button(GlimmpseWeb.constants.clusteringPanelRemoveSubgroupButton(), 
                new ClickHandler() {
        public void onClick(ClickEvent event) {
            removeSubgroup();
            removeClusteringNodeFromStudyDesign();
            checkComplete();
        }
    });
    // panel containing the add subgroup panel
    protected HorizontalPanel buttonPanel = new HorizontalPanel();

    /**
     * Constructor for the Clustering Panel Class
     */
    public ClusteringPanel(WizardContext context)
    {
        super(context, GlimmpseWeb.constants.navItemClustering(), 
                WizardStepPanelState.COMPLETE);
        VerticalPanel panel = new VerticalPanel();
        // title and header text
        HTML title = new HTML(GlimmpseWeb.constants.clusteringPanelTitle());
        HTML header = new HTML(GlimmpseWeb.constants.clusteringPanelDescription());

        panel.add(title);
        panel.add(header);
        panel.add(addClusteringButton);
        panel.add(removeClusteringButton);
        panel.add(clusteringTree);
        buttonPanel.add(addSubgroupButton);
        buttonPanel.add(removeSubgroupButton);
        panel.add(buttonPanel);
        // show/hide the appropriate buttons
        addClusteringButton.setVisible(true);
        removeClusteringButton.setVisible(false);
        addSubgroupButton.setVisible(false);
        removeSubgroupButton.setVisible(false);

        // Setting Styles 
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        title.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        addSubgroupButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
        removeSubgroupButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
        addClusteringButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
        removeClusteringButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);

        // Initializing widget
        initWidget(panel);
    }

    /**
     * Add a Clustering Panel Sub Panel class to the tree.
     */
    private void addSubgroup() 
    {
        TreeItem parent = currentLeaf;
        // create a new cluster node
        String dependentStyleName = GlimmpseConstants.STYLE_ODD;
        if (itemCount % 2 == 0) {
            dependentStyleName = GlimmpseConstants.STYLE_EVEN;
        }
        ClusteringPanelSubPanel subpanel = 
            new ClusteringPanelSubPanel(dependentStyleName,
                    itemCount, this);	
        // create a new tree item to hold the cluster node
        TreeItem newLeaf = new TreeItem(subpanel);
        // add the new subpanel to the clustering tree
        if (currentLeaf != null) {
            currentLeaf.addItem(newLeaf);
            currentLeaf.setState(true);
        } else {
            clusteringTree.addItem(newLeaf);
            newLeaf.setState(true);
        }
        // set the new leaf as the lowest node in the tree
        currentLeaf = newLeaf;
        // update the item count
        itemCount++;
        // update the visible buttons
        updateButtons();
    }

    /**
     * Click Handler method to remove the last instance of the Clustering Panel Sub Panel class added to the Tree
     */
    private void removeSubgroup() 
    {
        if (currentLeaf != null)
        {
            TreeItem parent = currentLeaf.getParentItem();
            currentLeaf.remove();
            itemCount--;
            currentLeaf = parent;
            updateButtons();
            checkComplete();
        }
    };


    /**
     * show or hide the subgroup buttons
     */
    public void updateButtons()
    {
        addClusteringButton.setVisible(itemCount <= 0);
        removeClusteringButton.setVisible(itemCount > 0);
        addSubgroupButton.setVisible(itemCount > 0 && itemCount < 3);
        removeSubgroupButton.setVisible(itemCount > 0);
    }

    /**
     * checkComplete() method is to check if all the instances of Clustering Panel Sub Panel Class added to the tree are
     * validated or not and then activate the Next button in the Wizard Setup Panel 
     */
    private void checkComplete() 
    {
        // initialize to true
        complete = true;
        // set back to false if any subpanels are incomplete
        TreeItemIterator.traverseDepthFirst(clusteringTree, checkCompleteAction);
        if (complete)
            changeState(WizardStepPanelState.COMPLETE);
        else
        	changeState(WizardStepPanelState.INCOMPLETE);
    }

    /**
     * Determine the overall status of the screen completion by checking
     * each subpanel.  If all subpanels are complete, then the screen is complete,
     * but if any subpanel is incomplete, then the screen is incomplete 
     * 
     * @param subpanel
     */
    private void updateComplete(ClusteringPanelSubPanel subpanel)
    {
        boolean subpanelComplete = subpanel.checkComplete();
        complete = complete && subpanelComplete;
    }

    /**
     * Add a blank clustering node to the study design
     */
    private void addClusteringNodeToStudyDesign() {
        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setParent(itemCount-1);
        clusterNode.setNode(itemCount);
        studyDesignContext.addClusteringNode(this, clusterNode);
    }
    
    /**
     * Remove the last clustering node from the study design
     */
    private void removeClusteringNodeFromStudyDesign() {
        studyDesignContext.deleteClusteringNode(this, itemCount);
    }
    
    /**
     * Update the clustering tree in the study design when anything
     * changes (called by the subpanel)
     *  
     * @param subpanel
     */
    public void updateClusteringNode(ClusteringPanelSubPanel subpanel)
    {
        if (subpanel != null) {
            studyDesignContext.updateClusteringNode(this, 
                    subpanel.getDepth(), subpanel.getClusterName(), 
                    subpanel.getClusterSize(), 
                    subpanel.getIntraClusterCorrelation());
            checkComplete();
        }
    }
    
    /**
     * Clear the panel and reset to the "no clustering" state
     */
    @Override
    public void reset() 
    {
        clusteringTree.removeItems();
        currentLeaf = null;
        itemCount = 0;
        updateButtons();
    }

    /**
     * Completely clear clustering information from the study design
     */
    private void clearClusteringFromStudyDesign() {
        studyDesignContext.clearClustering(this);
    }
    
    /**
     * This function is called when a study design is uploaded
     */
    public void onWizardContextLoad()
    {
        loadFromContext();
    }

    /**
     * This populates the screens based on the data in the study design uploaded.
     */
    public void loadFromContext()
    {
        reset();
        List<ClusterNode> clusterNodeList = studyDesignContext.getStudyDesign().getClusteringTree();
        if (clusterNodeList != null)
        {
            boolean first = true;
            for(ClusterNode clusterNode: clusterNodeList)
            {
                addSubgroup();
                ClusteringPanelSubPanel currentPanel = (ClusteringPanelSubPanel) currentLeaf.getWidget();
                currentPanel.loadFromClusterNode(clusterNode);
            }
        }
        checkComplete();
    }

    /**
     * Respond to context changes
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) {
        // no action needed here        
    }
}
