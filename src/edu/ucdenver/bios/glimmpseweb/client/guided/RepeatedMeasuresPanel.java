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
import java.util.List;

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

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TreeItemAction;
import edu.ucdenver.bios.glimmpseweb.client.TreeItemIterator;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;

/**
 * Panel for entering information about repeated measures.
 * @author Vijay Akula
 *
 */
public class RepeatedMeasuresPanel extends WizardStepPanel
implements ChangeHandler
{
    // context object
    protected StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    // indicates whether the user had added repeated measures or not
    protected boolean hasRepeatedMeasures = false;
    // tree describing the clustering hierarchy
    protected Tree repeatedMeasuresTree = new Tree();
    // number of items in the tree
    protected int itemCount = 0;
    // pointer to the lower most leaf in the tree
    protected TreeItem currentLeaf = null;
    // used to verify completeness
    protected boolean complete = false;
    
    // list of repeated measures node domain objects
    protected ArrayList<RepeatedMeasuresNode> repeatedMeasuresNodeList = 
        new ArrayList<RepeatedMeasuresNode>();
    
    // actions performed when iterating over the tree of clustering nodes
    // action to determine if the screen is complete
    TreeItemAction checkCompleteAction = new TreeItemAction() {
        @Override
        public void execute(TreeItem item, int visitCount, int parentVisitCount)
        {
            updateComplete((RepeatedMeasuresPanelSubPanel) item.getWidget());
        }
    };
    // action to build the clustering domain object
    TreeItemAction buildClusteringObjectAction = new TreeItemAction() {
        @Override
        public void execute(TreeItem item, int visitCount, int parentVisitCount)
        {
            if (item != null) {
                buildRepeatedMeasuresNode((RepeatedMeasuresPanelSubPanel) item.getWidget(), 
                        visitCount, parentVisitCount);
            }
        }
    };
    
    protected HorizontalPanel buttonPanel = new HorizontalPanel();

    Button addRepeatedMeasuresButton = new Button(
            GlimmpseWeb.constants.repeatedMeasuresPanelAddRMButton(), 
            new ClickHandler() {
        @Override
        public void onClick(ClickEvent event) {
            addSubDimension();
            toggleRepeatedMeasures();
        }
    });

    Button removeRepeatedMeasuresButton = new Button(
            GlimmpseWeb.constants.repeatedMeasuresPanelRemoveRMButton(), 
            new ClickHandler(){
        @Override
        public void onClick(ClickEvent event) {
            reset();
            toggleRepeatedMeasures();
        }
    });

    Button addSubDimensionButton = new Button(
            GlimmpseWeb.constants.repeatedMeasuresPanelAddSubDimensionButton(), 
                new ClickHandler(){
        @Override
        public void onClick(ClickEvent event) {
            addSubDimension();
        }
    });

    Button removeSubDimensionButton = new Button(
            GlimmpseWeb.constants.repeatedMeasuresPanelRemoveSubDimensionButton(), 
            new ClickHandler(){
        @Override
        public void onClick(ClickEvent event) {
            removeSubDimension();
        }
    });



    public RepeatedMeasuresPanel(WizardContext context)
    {
        super(context, GlimmpseWeb.constants.navItemRepeatedMeasure(),
                WizardStepPanelState.COMPLETE);

        VerticalPanel panel = new VerticalPanel();
        HTML title = new HTML(GlimmpseWeb.constants.repeatedMeasuresTitle());
        HTML description = new HTML(GlimmpseWeb.constants.repeatedMeasuresDescription());
        
        panel.add(title);
        panel.add(description);
        panel.add(addRepeatedMeasuresButton);
        removeRepeatedMeasuresButton.setVisible(false);
        panel.add(removeRepeatedMeasuresButton);
        panel.add(repeatedMeasuresTree);

        buttonPanel.add(addSubDimensionButton);
        buttonPanel.add(removeSubDimensionButton);
        panel.add(buttonPanel);
        // show/hide the appropriate buttons
        addRepeatedMeasuresButton.setVisible(!hasRepeatedMeasures);
        removeRepeatedMeasuresButton.setVisible(hasRepeatedMeasures);
        addSubDimensionButton.setVisible(hasRepeatedMeasures);
        removeSubDimensionButton.setVisible(hasRepeatedMeasures);

        //Setting Styles
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        title.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        // button styles
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
        RepeatedMeasuresPanelSubPanel subpanel = new RepeatedMeasuresPanelSubPanel(null);   
        subpanel.addChangeHandler(this);
        TreeItem newLeaf = new TreeItem(subpanel);
        if (currentLeaf == null)
        {
            repeatedMeasuresTree.addItem(newLeaf);
            newLeaf.setState(true);
        }
        else
        {
            currentLeaf.addItem(newLeaf);
            currentLeaf.setState(true);
            if(itemCount == 2)
            {
                addSubDimensionButton.setVisible(false);
            }           
        }
        itemCount++;
        currentLeaf = newLeaf;
        checkComplete();
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
            if (itemCount <= 0)
            {
                toggleRepeatedMeasures();
            }
            else if (itemCount == 2)
            {
                addSubDimensionButton.setVisible(true);
            }
        }
        checkComplete();
    };

    
    /**
     * Called when any of the subpanels change.  Allows us to check
     * if the panel is complete and the user can/cannot proceed 
     * forward
     */
    public void onChange(ChangeEvent e) 
    {
        checkComplete();
    }
    

    /**
     * toggle the repeated measures on/off
     */
    public void toggleRepeatedMeasures()
    {
        hasRepeatedMeasures = !hasRepeatedMeasures;
        addRepeatedMeasuresButton.setVisible(!hasRepeatedMeasures);
        removeRepeatedMeasuresButton.setVisible(hasRepeatedMeasures);
        addSubDimensionButton.setVisible(hasRepeatedMeasures);
        removeSubDimensionButton.setVisible(hasRepeatedMeasures);
        checkComplete();
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
     * Remove all repeated measures information and
     * set back to the default state
     */
    @Override
    public void reset()
    {
        repeatedMeasuresTree.removeItems();
        repeatedMeasuresNodeList.clear();
        currentLeaf = null;
        itemCount = 0;
    }

    /**
     * Update the repeated measures information in the context
     */
    @Override
    public void onExit()
    {
        repeatedMeasuresNodeList.clear();
        TreeItemIterator.traverseDepthFirst(repeatedMeasuresTree, buildClusteringObjectAction);
        studyDesignContext.setRepeatedMeasures(this, repeatedMeasuresNodeList);
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
            buttonPanel.setVisible(true);
            boolean first = true;
            for(RepeatedMeasuresNode repeatedMeasuresNode: contextRepeatedMeasuresNodeList)
            {
                if (first)
                {
                    addSubDimension();
                    toggleRepeatedMeasures();
                    first = false;
                }
                else
                {
                    addSubDimension();
                }

                RepeatedMeasuresPanelSubPanel currentPanel = 
                    (RepeatedMeasuresPanelSubPanel) currentLeaf.getWidget();
                currentPanel.loadFromRepeatedMeasuresNode(repeatedMeasuresNode);
            }
        }
        checkComplete();
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
     * Create a repeated measures node based on the information filled into
     * the current subpanel.
     * @param subpanel subpanel associated with current tree node
     * @param nodeId node visit number in the depth first traversal
     * @param parentId parent's visit number
     */
    private void buildRepeatedMeasuresNode(
            RepeatedMeasuresPanelSubPanel subpanel, int nodeId, int parentId) {
        if (subpanel != null) {
            repeatedMeasuresNodeList.add(subpanel.toRepeatedMeasuresNode(nodeId, parentId));
        }
    }

    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) {
        // no action required for this panel
    }

}
