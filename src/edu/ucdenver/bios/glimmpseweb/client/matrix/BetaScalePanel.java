/*
 * Web Interface for the GLIMMPSE Software System.  Allows
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
package edu.ucdenver.bios.glimmpseweb.client.matrix;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.shared.ListEntryPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.ListValidator;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.BetaScale;
import edu.ucdenver.bios.webservice.common.enums.SolutionTypeEnum;

/**
 * Matrix Mode panel which allows entry of beta-scale factors
 *
 */
public class BetaScalePanel extends WizardStepPanel
implements ListValidator
{
	// pointer to the study design context
	StudyDesignContext studyDesignContext = (StudyDesignContext) context;
	
   	// list of per group sample sizes
    protected ListEntryPanel betaScaleListPanel =
    	new ListEntryPanel(GlimmpseWeb.constants.betaScaleTableColumn(), this);

    // caches the entered values as doubles
    ArrayList<BetaScale> betaScaleList = new ArrayList<BetaScale>();
    
	public BetaScalePanel(WizardContext context)
	{
		super(context, GlimmpseWeb.constants.navItemBetaScale());
		VerticalPanel panel = new VerticalPanel();
        HTML header = new HTML(GlimmpseWeb.constants.betaScaleTitle());
        HTML description = new HTML(GlimmpseWeb.constants.betaScaleDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(betaScaleListPanel);
    	
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
	}
    
	@Override
	public void reset()
	{
		betaScaleListPanel.reset();
		onValidRowCount(betaScaleListPanel.getValidRowCount());
	}

	@Override
	public void validate(String value) throws IllegalArgumentException
	{
    	try
    	{
    		TextValidation.parseDouble(value, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
    	}
    	catch (NumberFormatException nfe)
    	{
    		throw new IllegalArgumentException(GlimmpseWeb.constants.errorInvalidAlpha());
    	}
	}
	
	@Override
	public void onValidRowCount(int validRowCount)
	{
		if (validRowCount > 0)
			changeState(WizardStepPanelState.COMPLETE);
		else
			changeState(WizardStepPanelState.INCOMPLETE);
	}
	
    /**
     * Notify context of new beta scale list as we leave the panel
     */
    @Override
    public void onExit()
    {
    	List<String> stringValues = betaScaleListPanel.getValues();
    	betaScaleList.clear();
    	for(String value: stringValues)
    	{
    		betaScaleList.add(new BetaScale(Double.parseDouble(value)));
    	}
    	studyDesignContext.setBetaScaleList(this, betaScaleList);
    }

    /**
     * Skip this panel if the user is solving for detectable difference
     */
	@Override
	public void onWizardContextChange(WizardContextChangeEvent e)
	{
    	StudyDesignChangeEvent changeEvent = (StudyDesignChangeEvent) e;
    	switch (changeEvent.getType())
    	{
    	case SOLVING_FOR:
    		if (SolutionTypeEnum.DETECTABLE_DIFFERENCE == 
    			studyDesignContext.getStudyDesign().getSolutionTypeEnum())
    		{
    			changeState(WizardStepPanelState.SKIPPED);
    		}
    		else
    		{
    			onValidRowCount(betaScaleListPanel.getValidRowCount());
    		}
    		break;
    	case BETA_SCALE_LIST:
    		if (this != e.getSource())
    		{
    			loadFromContext();
    		}
    		break;
    	}
	}

	/**
	 * Load the beta scale list from the context
	 */
	@Override
	public void onWizardContextLoad()
	{
		loadFromContext();
	}
	
    /**
     * Load the beta scale panel from the study design context information
     */
    public void loadFromContext()
    {
    	List<BetaScale> contextBetaScaleList = studyDesignContext.getStudyDesign().getBetaScaleList();
    	for(BetaScale scale: contextBetaScaleList)
    	{
    		betaScaleListPanel.add(Double.toString(scale.getValue()));
    	}
    	onValidRowCount(betaScaleListPanel.getValidRowCount());
    }
}
