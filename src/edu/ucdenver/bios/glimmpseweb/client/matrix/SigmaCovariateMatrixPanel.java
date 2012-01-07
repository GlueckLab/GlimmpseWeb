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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.Node;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;

/**
 * Matrix Mode panel for entering the variance of the Gaussian covariate
 * for GLMM(F,g) designs
 * 
 * @author Sarah Kreidler
 *
 */
public class SigmaCovariateMatrixPanel extends WizardStepPanel
{
    protected ResizableMatrix sigmaG = 
    	new ResizableMatrix(GlimmpseConstants.MATRIX_SIGMA_COVARIATE,
    			1, 1, "0", GlimmpseWeb.constants.sigmaCovariateMatrixName()); 
    
    public SigmaCovariateMatrixPanel(WizardContext context)
    {
		super(context, "Sigma G");
		// regardless of input, forward navigation is allowed from this panel
		complete = true;
		skip = true;
        HTML header = new HTML(GlimmpseWeb.constants.sigmaCovariateTitle());
        HTML description = new HTML(GlimmpseWeb.constants.sigmaCovariateDescription());
		VerticalPanel panel = new VerticalPanel();
		
        panel.add(header);
        panel.add(description);
		panel.add(sigmaG);        
        
		// disable resize
		sigmaG.setEnabledColumnDimension(false);
		sigmaG.setEnabledRowDimension(false);
		
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
		initWidget(panel);
    }
    
	@Override
	public void reset()
	{
		sigmaG.reset(1, 1);
		complete = true;
		skip = true;
	}

	public void loadFromNode(Node node)
	{
		sigmaG.loadFromDomNode(node);
	}
	
	public String toXML()
	{
		if (skip)
			return "";
		else
			return sigmaG.toXML(GlimmpseConstants.MATRIX_SIGMA_COVARIATE);
	}
    
    @Override 
    public void onExit()
    {
//    	for(VariabilityListener listener: listeners) listener.onCovariateVariance(sigmaG.getData(0,0));
    }
}
