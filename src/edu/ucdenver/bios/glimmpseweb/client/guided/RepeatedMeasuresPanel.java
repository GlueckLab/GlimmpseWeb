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
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;

/**
 * Panel for entering information about repeated measures.
 * @author Vijay Akula
 * @author Sarah Kreidler
 *
 */
public class RepeatedMeasuresPanel extends WizardStepPanel {
    // context object
    protected StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    // tree describing the clustering hierarchy
    protected Tree repeatedMeasuresTree = new Tree();
    // number of items in the tree
    protected int itemCount = 0;
    // pointer to the lower most leaf in the tree
    protected TreeItem currentLeaf = null;
    // used to verify completeness
    protected boolean complete = false;
    
    // actions performed when iterating over the tree of clustering nodes
    // action to determine if the screen is complete
    TreeItemAction checkCompleteAction = new TreeItemAction() {
        @Override
        public void execute(TreeItem item, int visitCount, int parentVisitCount)
        {
            updateComplete((RepeatedMeasuresPanelSubPanel) item.getWidget());
        }
    };

    /* buttons */
    // button to initiate entering of repeated measures
    Button addRepeatedMeasuresButton = new Button(
            GlimmpseWeb.constants.repeatedMeasuresPanelAddRMButton(), 
            new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            addSubDimension();
            addRepeatedMeasuresNodeToStudyDesign();
            checkComplete();
        }
    });
    // button to remove repeated measures information
    Button removeRepeatedMeasuresButton = 
        new Button(GlimmpseWeb.constants.repeatedMeasuresPanelRemoveRMButton(), 
            new ClickHandler(){
        public void onClick(ClickEvent event) {
            clearRepeatedMeasuresFromStudyDesign();
            reset();
        }
    });
    //  button for adding a sub dimensions
    Button addSubDimensionButton = 
        new Button(GlimmpseWeb.constants.repeatedMeasuresPanelAddSubDimensionButton(), 
                new ClickHandler(){
        public void onClick(ClickEvent event) {
            addSubDimension();
            addRepeatedMeasuresNodeToStudyDesign();
            checkComplete();
        }
    });
    // button for removing repeated measures dimensions
    Button removeSubDimensionButton = 
        new Button(GlimmpseWeb.constants.repeatedMeasuresPanelRemoveSubDimensionButton(), 
            new ClickHandler(){
        public void onClick(ClickEvent event) {
            removeSubDimension();
            removeRepeatedMeasuresNodeFromStudyDesign();
            checkComplete();
        }
    });
    // panel containing the add dimension panel
    protected HorizontalPanel buttonPanel = new HorizontalPanel();

    /**
     * Constructor for the Repeated Measures Panel Class
     */
    public RepeatedMeasuresPanel(WizardContext context)
    {
        super(context, GlimmpseWeb.constants.navItemRepeatedMeasure(),
                WizardStepPanelState.COMPLETE);
        VerticalPanel panel = new VerticalPanel();
        // title and header text
        HTML title = new HTML(GlimmpseWeb.constants.repeatedMeasuresTitle());
        HTML description = new HTML(GlimmpseWeb.constants.repeatedMeasuresDescription());

        panel.add(title);
        panel.add(description);
        panel.add(addRepeatedMeasuresButton);
        panel.add(removeRepeatedMeasuresButton);
        panel.add(repeatedMeasuresTree);
        buttonPanel.add(addSubDimensionButton);
        buttonPanel.add(removeSubDimensionButton);
        panel.add(buttonPanel);
        // show/hide the appropriate buttons
        addRepeatedMeasuresButton.setVisible(true);
        removeRepeatedMeasuresButton.setVisible(false);
        addSubDimensionButton.setVisible(false);
        removeSubDimensionButton.setVisible(false);

        //Setting Styles
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        title.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        addRepeatedMeasuresButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
        removeRepeatedMeasuresButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
        addSubDimensionButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
        removeSubDimensionButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
        
        //Initializing Widget
        initWidget(panel);
    }   
    
    /**
     * Add a Clustering Panel Sub Panel class to the tree.
     */
    private void addSubDimension() 
    {        
        TreeItem parent = currentLeaf;
        // create a new repeated measures node
        String dependentStyleName = GlimmpseConstants.STYLE_ODD;
        if (itemCount % 2 == 0) {
            dependentStyleName = GlimmpseConstants.STYLE_EVEN;
        }
        RepeatedMeasuresPanelSubPanel subpanel = 
            new RepeatedMeasuresPanelSubPanel(dependentStyleName,
                itemCount, this);   
        // create a new tree item to hold the cluster node
        TreeItem newLeaf = new TreeItem(subpanel);
        // add the new subpanel to the repeated measures tree
        if (currentLeaf != null) {
            currentLeaf.addItem(newLeaf);
            currentLeaf.setState(true);
        } else {
            repeatedMeasuresTree.addItem(newLeaf);
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
     * Click Handler method to remove the last instance of the 
     * Repeated measures Sub Panel class added to the Tree
     */
    private void removeSubDimension() 
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
        addRepeatedMeasuresButton.setVisible(itemCount <= 0);
        removeRepeatedMeasuresButton.setVisible(itemCount > 0);
        addSubDimensionButton.setVisible(itemCount > 0 && itemCount < 3);
        removeSubDimensionButton.setVisible(itemCount > 0);
    }

    /**
     * checkComplete() method is to check if all the instances of Clustering Panel Sub Panel Class added to the tree are
     * validated or not and then activate the Next button in the Wizard Setup Panel 
     */
    private void checkComplete() 
    {
        // intialize to true
        complete = true;
        // set back to false if any subpanels are incomplete
        TreeItemIterator.traverseDepthFirst(repeatedMeasuresTree, checkCompleteAction);
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
     * @param subpanel subpanel associated with current tree node
     */
    private void updateComplete(RepeatedMeasuresPanelSubPanel subpanel)
    {
        boolean subpanelComplete = subpanel.checkComplete();
        complete = complete && subpanelComplete;
    }

    /**
     * Add a blank clustering node to the study design
     */
    private void addRepeatedMeasuresNodeToStudyDesign() {
        RepeatedMeasuresNode rmNode = new RepeatedMeasuresNode();
        rmNode.setParent(itemCount-1);
        rmNode.setNode(itemCount);
        studyDesignContext.addRepeatedMeasuresNode(this, rmNode);
    }
    
    /**
     * Remove the last repeated measures node from the study design
     */
    private void removeRepeatedMeasuresNodeFromStudyDesign() {
        studyDesignContext.deleteRepeatedMeasuresNode(this, itemCount);
    }
    
    /**
     * Update the repeated measures tree in the study design when anything
     * changes (called by the subpanel)
     *  
     * @param subpanel
     */
    public void updateRepeatedMeasuresNode(RepeatedMeasuresPanelSubPanel subpanel)
    {
        if (subpanel != null) {
            studyDesignContext.updateRepeatedMeasuresNode(this, 
                    subpanel.getDepth(), subpanel.getDimensionName(), 
                    subpanel.getType(), subpanel.getNumberOfMeausrements(), 
                    subpanel.getSpacingList());
            checkComplete();
        }
    }
    
    /**
     * Completely clear repeated measures information from the study design
     */
    private void clearRepeatedMeasuresFromStudyDesign() {
        studyDesignContext.clearRepeatedMeasures(this);
    }
    
    /**
     * Clear the panel and reset to the "no repeated measures" state
     */
    @Override
    public void reset()
    {
        repeatedMeasuresTree.removeItems();
        currentLeaf = null;
        itemCount = 0;
        updateButtons();
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
        List<RepeatedMeasuresNode> contextRepeatedMeasuresNodeList = 
            studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
        if (contextRepeatedMeasuresNodeList != null)
        {
            for(RepeatedMeasuresNode repeatedMeasuresNode: contextRepeatedMeasuresNodeList)
            {
                addSubDimension();
                RepeatedMeasuresPanelSubPanel currentPanel = 
                    (RepeatedMeasuresPanelSubPanel) currentLeaf.getWidget();
                currentPanel.loadFromRepeatedMeasuresNode(repeatedMeasuresNode);
            }
        }
        checkComplete();
    }

    /**
     * Respond to context changes
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) {
        // no action required for this panel
    }
}
