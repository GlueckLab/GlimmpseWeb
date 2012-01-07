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
package edu.ucdenver.bios.glimmpseweb.client.matrix;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.shared.ListEntryPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.ListValidator;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;

/**
 * Matrix mode panel allowing entry of scale factors for the error covariance
 * 
 * @author Sarah Kreidler
 *
 */
public class SigmaScalePanel extends WizardStepPanel
implements ListValidator
{
    // list of sigma scale factors
    protected ListEntryPanel sigmaScaleListPanel = 
    	new ListEntryPanel(GlimmpseWeb.constants.sigmaScaleTableColumn(), this);

	public SigmaScalePanel(WizardContext context)
	{
		super(context, "Sigma Scale");
		VerticalPanel panel = new VerticalPanel();
        HTML header = new HTML(GlimmpseWeb.constants.sigmaScaleTitle());
        HTML description = new HTML(GlimmpseWeb.constants.sigmaScaleDescription());
        
        panel.add(header);
        panel.add(description);
        panel.add(sigmaScaleListPanel);
    	
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
	}
    
	@Override
	public void reset()
	{
		sigmaScaleListPanel.reset();
		onValidRowCount(sigmaScaleListPanel.getValidRowCount());
	}

	@Override
	public void validate(String value) throws IllegalArgumentException
	{
    	try
    	{
    		TextValidation.parseDouble(value, 0.0, Double.POSITIVE_INFINITY, false);
    	}
    	catch (NumberFormatException nfe)
    	{
    		throw new IllegalArgumentException(GlimmpseWeb.constants.errorInvalidNumber());
    	}
	}
	
	@Override
	public void onValidRowCount(int validRowCount)
	{
		if (validRowCount > 0)
			notifyComplete();
		else
			notifyInProgress();
	}

	@Override
	public void onExit()
	{
    	List<String> values = sigmaScaleListPanel.getValues();
	}

    
	public String toXML()
	{
		return sigmaScaleListPanel.toXML(GlimmpseConstants.TAG_SIGMA_SCALE_LIST);
	}
    
}
