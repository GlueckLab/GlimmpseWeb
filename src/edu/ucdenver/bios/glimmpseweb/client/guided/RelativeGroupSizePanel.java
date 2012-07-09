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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.FactorTable;
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
    // maximum relative size
    protected static final int MAX_RELATIVE_SIZE = 10;
    // data table to display possible groups
    protected FlexTable groupSizesTable = new FlexTable();
    // list of relative sizes
    ArrayList<RelativeGroupSize> relativeSizeList = new ArrayList<RelativeGroupSize>();
    
    /**
     * Constructor.
     * @param context wizard context
     */
    public RelativeGroupSizePanel(WizardContext context)
    {
        super(context, GlimmpseWeb.constants.navItemRelativeGroupSize(), 
                WizardStepPanelState.SKIPPED);
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

    /**
     * Clear the panel
     */
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
        groupSizesTable.removeAllRows();
        FactorTable groups = studyDesignContext.getParticipantGroups();
        List<RelativeGroupSize> contextRelativeSizeList = 
            studyDesignContext.getStudyDesign().getRelativeGroupSizeList();
        // build the labels for the groups
        if (groups != null) {
            List<String> columnLabels = groups.getColumnLabels();
            if (columnLabels != null) {
                // relative group size drop down column
                groupSizesTable.getRowFormatter().setStyleName(0, 
                        GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_HEADER);
                groupSizesTable.setWidget(0, 0, new HTML(GlimmpseWeb.constants.relativeGroupSizeTableColumn()));
                // add labels for the between participant factors
                int col = 1;
                for(String label: columnLabels) {
                    groupSizesTable.setWidget(0, col, new HTML(label));
                    col++;
                }
            }
            
            // now fill the columns
            for(int col = 1; col <= groups.getNumberOfColumns(); col++) {
                List<String> column = groups.getColumn(col-1);
                if (column != null) {
                    int row = 1;
                    for(String value: column) {
                        if (col == 1) {
                          ListBox lb = createGroupSizeListBox();
                          if (contextRelativeSizeList != null) {
                              RelativeGroupSize relativeGroupSize = contextRelativeSizeList.get(row-1);
                              if (relativeGroupSize != null) {
                                  int selectionIndex = relativeGroupSize.getValue()-1;
                                  if (selectionIndex > 0 && selectionIndex <= MAX_RELATIVE_SIZE) {
                                      lb.setSelectedIndex(relativeGroupSize.getValue()+1);
                                  }
                              }
                          }
                          groupSizesTable.getRowFormatter().setStyleName(row, 
                                  GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
                          groupSizesTable.setWidget(row, 0, lb);
                        }
                        groupSizesTable.setWidget(row, col, new HTML(value));
                        row++;
                    }
                }
            }
        }
    }

    /**
     * Create a listbox with relative group size values up to 10.
     * @return listbox
     */
    private ListBox createGroupSizeListBox()
    {
        ListBox lb = new ListBox();
        lb.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                // TODO Auto-generated method stub
                changed = true;
            }    
        });
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
            FactorTable groups = studyDesignContext.getParticipantGroups();
            if (groups.getNumberOfRows() <= 1) {
                changeState(WizardStepPanelState.SKIPPED);
            } else {
                changeState(WizardStepPanelState.COMPLETE);
                loadFromContext();
            }
            changed = true;
            break;
        case RELATIVE_GROUP_SIZE_LIST:
            if (this != changeEvent.getSource())
            {
                loadFromContext();
                changed = true;
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
        if (changed) {
            relativeSizeList.clear();
            for(int i = 1; i < groupSizesTable.getRowCount(); i++)
            {
                ListBox lb = (ListBox) groupSizesTable.getWidget(i, 0);
                relativeSizeList.add(new RelativeGroupSize(lb.getSelectedIndex()+1));
            }
            studyDesignContext.setRelativeGroupSizeList(this, relativeSizeList);
            changed = false;
        }
    }

}
