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

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.RelativeGroupSize;

/**
 * Entry screen for relative group sizes
 * @author Sarah Kreidler
 *
 */
public class RelativeGroupSizePanel extends WizardStepPanel
{
    // pointer to the study design context
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    
	protected static final int MAX_RELATIVE_SIZE = 10;
    // data table to display possible groups
    protected FlexTable groupSizesTable = new FlexTable();
    // list of relative sizes
    ArrayList<RelativeGroupSize> relativeSizeList = new ArrayList<RelativeGroupSize>();
    
    
	public RelativeGroupSizePanel(WizardContext context)
	{
		super(context, GlimmpseWeb.constants.navItemRelativeGroupSize(), 
		        WizardStepPanelState.NOT_ALLOWED);
        VerticalPanel panel = new VerticalPanel();
        
        // create header/instruction text
        HTML header = new HTML(GlimmpseWeb.constants.relativeGroupSizeTitle());
        HTML description = new HTML(GlimmpseWeb.constants.relativeGroupSizeDescription());     
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(groupSizesTable);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        groupSizesTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE);
        
        initWidget(panel);
	}

	@Override
	public void reset()
	{
		groupSizesTable.removeAllRows();
		changeState(WizardStepPanelState.NOT_ALLOWED);
	}

	/**
	 * Load from the study design context.
	 */
	private void loadFromContext()
	{
	    reset();
	    DataTable groups = studyDesignContext.getParticipantGroups();
	    List<RelativeGroupSize> contextRelativeSizeList = 
	        studyDesignContext.getStudyDesign().getRelativeGroupSizeList();
	    // build the labels for the groups
	    if (groups.getNumberOfRows() > 0)
	    {
	        groupSizesTable.getRowFormatter().setStyleName(0, 
	                GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_HEADER);
	        groupSizesTable.setWidget(0, 0, new HTML(GlimmpseWeb.constants.relativeGroupSizeTableColumn()));
	        for(int col = 0; col < groups.getNumberOfColumns(); col++)
	        {
	            groupSizesTable.setWidget(0, col+1, new HTML(groups.getColumnLabel(col)));
	        }
	        for(int row = 0; row < groups.getNumberOfRows(); row++)
	        {
	            ListBox lb = createGroupSizeListBox();
	            if (contextRelativeSizeList != null) {
	                RelativeGroupSize relativeGroupSize = contextRelativeSizeList.get(row);
	                if (relativeGroupSize != null) {
	                    int selectionIndex = relativeGroupSize.getValue()-1;
	                    if (selectionIndex > 0 && selectionIndex <= MAX_RELATIVE_SIZE) {
	                        lb.setSelectedIndex(relativeGroupSize.getValue()+1);
	                    }
	                }
	            }
	            groupSizesTable.setWidget(row+1, 0, lb);
	            
	            groupSizesTable.getRowFormatter().setStyleName(row+1, GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
	            for(int col = 0; col < groups.getNumberOfColumns(); col++)
	            {
	                groupSizesTable.setWidget(row+1, col+1, new HTML(groups.getValueString(row, col)));
	            }
	        }
	        changeState(WizardStepPanelState.COMPLETE);
	    }
	    else
	    {
	        changeState(WizardStepPanelState.INCOMPLETE);
	    }
	}

	/**
	 * Create a listbox with relative group size values up to 10.
	 * @return listbox
	 */
	private ListBox createGroupSizeListBox()
	{
		ListBox lb = new ListBox();
		for(int i = 1; i <= MAX_RELATIVE_SIZE; i++) lb.addItem(Integer.toString(i));
		return lb;
	}
	
	/**
	 * Respond to context changes.
	 */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e)
    {
        StudyDesignChangeEvent changeEvent = (StudyDesignChangeEvent) e;
        switch (changeEvent.getType())
        {
        case BETWEEN_PARTICIPANT_FACTORS:
            loadFromContext();
            break;
        case RELATIVE_GROUP_SIZE_LIST:
            if (this != changeEvent.getSource())
            {
                loadFromContext();
            }
            break;
        }
    }
    
    /**
     * Respond to context load events
     */
    @Override
    public void onWizardContextLoad()
    {
        loadFromContext();
    }
	
	/**
	 * Update the context with the relative group sizes
	 */
	@Override
	public void onExit()
	{
        relativeSizeList.clear();
		for(int i = 1; i < groupSizesTable.getRowCount(); i++)
		{
			ListBox lb = (ListBox) groupSizesTable.getWidget(i, 0);
			relativeSizeList.add(new RelativeGroupSize(lb.getSelectedIndex()+1));
		}
		studyDesignContext.setRelativeGroupSizeList(this, relativeSizeList);
	}

}
