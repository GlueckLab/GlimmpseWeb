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

import java.util.ArrayList;
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
    // list of sample sizes to be set into the context
    ArrayList<SampleSize> sampleSizeList = new ArrayList<SampleSize>();
    
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
	
	
	@Override
	public void onValidRowCount(int validRowCount)
	{
		if (validRowCount > 0)
			changeState(WizardStepPanelState.COMPLETE);
		else
			changeState(WizardStepPanelState.INCOMPLETE);
	}

	@Override
	public void validate(String value)
			throws IllegalArgumentException
	{
    	try
    	{
    		TextValidation.parseInteger(value, 0, true);
    	}
    	catch (NumberFormatException nfe)
    	{
    		throw new IllegalArgumentException(GlimmpseWeb.constants.errorInvalidSampleSize());
    	}
	}
	
	@Override
	public void reset()
	{
		perGroupNListPanel.reset();
		onValidRowCount(perGroupNListPanel.getValidRowCount());
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
				onValidRowCount(perGroupNListPanel.getValidRowCount());
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
            onValidRowCount(perGroupNListPanel.getValidRowCount());
        } else {
            changeState(WizardStepPanelState.SKIPPED);
        }
    }
    
    /**
     * Notify context of any changes as we exit the screen
     */
    @Override
    public void onExit()
    {
        if (perGroupNListPanel.isChanged()) {
            List<String> stringValues = perGroupNListPanel.getValues();
            sampleSizeList.clear();
            for(String value: stringValues)
            {
                sampleSizeList.add(new SampleSize(Integer.parseInt(value)));
            }
            // save to context object
            studyDesignContext.setPerGroupSampleSizeList(this, sampleSizeList);
            perGroupNListPanel.resetChanged();
        }
    }
    
	
}
