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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesign.SolutionType;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent.StudyDesignChangeType;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;

/**
 * Panel for entering nominal power values when performing
 * sample size calculations
 *
 */
public class PowerPanel extends WizardStepPanel
implements ListValidator
{
	// study design context
	StudyDesignContext studyDesignContext;
	
	// list of nominal power values.  Only displayed when solving for effect size or sample size
    protected ListEntryPanel nominalPowerListPanel = 
    	new ListEntryPanel(GlimmpseWeb.constants.solvingForNominalPowerTableColumn(), this);
    
	public PowerPanel(WizardContext context)
	{
		super(context, "Desired Power");
		studyDesignContext = (StudyDesignContext) context;
		skip=true;
		VerticalPanel panel = new VerticalPanel();

		HTML header = new HTML(GlimmpseWeb.constants.solvingForNominalPowerTitle());
		HTML description = new HTML(GlimmpseWeb.constants.solvingForNominalPowerDescription());

		panel.add(header);
		panel.add(description);
		panel.add(nominalPowerListPanel);
		
		// set style
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		
		initWidget(panel);
	}
	
	/**
	 * Validate new entries in the alpha list
	 * @see DynamicListValidator
	 */
	public void validate(String value) throws IllegalArgumentException
	{
		try
		{
			TextValidation.parseDouble(value, 0, 1, false);
		}
		catch (NumberFormatException nfe)
		{
			throw new IllegalArgumentException(GlimmpseWeb.constants.errorInvalidPower());
		}
	}

    /**
     * Callback when the number of valid entries in the list of
     * alpha values changes
     * 
     * @see DynamicListValidator
     */
    public void onValidRowCount(int validRowCount)
    {
    	if (validRowCount > 0)
    		notifyComplete();
    	else
    		notifyInProgress();
    }
    
    /**
     * Return an XML representation of the nominal power list, or null if 
     * solving for power
     * 
     * @return XML representation of the nominal power list
     */
    public String toXML()
    {
    	if (skip)
    		return "";
    	else
    		return nominalPowerListPanel.toXML(GlimmpseConstants.TAG_POWER_LIST);
    }
	
	@Override
	public void reset()
	{
		nominalPowerListPanel.reset();
    	onValidRowCount(nominalPowerListPanel.getValidRowCount());
    	skip = true;
	}

	@Override
	public void onChange(WizardContextChangeEvent e)
	{
		if (((StudyDesignChangeEvent) e).getType() == StudyDesignChangeType.SOLVING_FOR)
		{
			skip = (studyDesignContext.getSolutionType() == SolutionType.POWER);
			Window.alert("skip?" + skip);
		}
	}
}
