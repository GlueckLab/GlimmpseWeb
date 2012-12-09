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

import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.SampleSize;
import edu.ucdenver.bios.webservice.common.enums.SolutionTypeEnum;

/**
 * Entry panel for per group sample sizes
 * @author Sarah Kreidler
 *
 */
public class PerGroupSampleSizePanel extends WizardStepPanel
implements ListValidator
{
	// study design context
	StudyDesignContext studyDesignContext;
	
   	// list of per group sample sizes
    protected ListEntryPanel perGroupNListPanel =
    	new ListEntryPanel(GlimmpseWeb.constants.perGroupSampleSizeTableColumn(), this);
    
	public PerGroupSampleSizePanel(WizardContext context)
	{
		super(context, GlimmpseWeb.constants.navItemPerGroupSampleSize(), 
		        WizardStepPanelState.INCOMPLETE);
		studyDesignContext = (StudyDesignContext) context;
		VerticalPanel panel = new VerticalPanel();
        HTML header = new HTML(GlimmpseWeb.constants.perGroupSampleSizeTitle());
        HTML description = new HTML(GlimmpseWeb.constants.perGroupSampleSizeDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(perGroupNListPanel);
    	
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
	}
	
	/**
	 * Reset the panel 
	 */
	@Override
	public void reset()
	{
		perGroupNListPanel.reset();
		changeState(WizardStepPanelState.INCOMPLETE);
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
		case SOLVING_FOR:
			if (SolutionTypeEnum.SAMPLE_SIZE == studyDesignContext.getStudyDesign().getSolutionTypeEnum())
			{
				changeState(WizardStepPanelState.SKIPPED);
			}
			else
			{
				checkComplete();
			}			
			break;
		case PER_GROUP_N_LIST:
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
     * Load the sample size panel from the study design context information
     */
    public void loadFromContext()
    {
        List<SampleSize> perGroupNList = studyDesignContext.getStudyDesign().getSampleSizeList();
        SolutionTypeEnum solutionType = studyDesignContext.getStudyDesign().getSolutionTypeEnum();
        perGroupNListPanel.reset();

        if (solutionType == SolutionTypeEnum.POWER) {
            if (perGroupNList != null) {
                for(SampleSize size: perGroupNList) {
                    perGroupNListPanel.add(Integer.toString(size.getValue()));
                }
            } 
            checkComplete();
        } else {
            changeState(WizardStepPanelState.SKIPPED);
        }
    }   
    
    /**
     * Add a sample size to the study design
     */
    @Override
    public void onAdd(String value) throws IllegalArgumentException {
        try
        {
            int sampleSize = TextValidation.parseInteger(value, 2, true);
            studyDesignContext.addPerGroupSampleSize(this, sampleSize);
            changeState(WizardStepPanelState.COMPLETE);
        }
        catch (NumberFormatException nfe)
        {
            throw new IllegalArgumentException(GlimmpseWeb.constants.errorInvalidSampleSize());
        }
    }

    /**
     * Delete a sample size from the study design
     */
    @Override
    public void onDelete(String value, int index) {
        int sampleSize = Integer.parseInt(value);
        studyDesignContext.deletePerGroupSampleSize(this, sampleSize, index);
        checkComplete();
    }

    /**
     * Check if the panel is complete
     */
    private void checkComplete() {
        if (perGroupNListPanel.getValidRowCount() > 0)
            changeState(WizardStepPanelState.COMPLETE);
        else
            changeState(WizardStepPanelState.INCOMPLETE);
    }
	
}
