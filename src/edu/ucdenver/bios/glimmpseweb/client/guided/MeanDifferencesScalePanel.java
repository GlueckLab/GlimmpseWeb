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

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent.StudyDesignChangeType;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.BetaScale;

/**
 * Guided mode equivalent of beta scale panel
 * @author Sarah Kreidler
 *
 */
public class MeanDifferencesScalePanel extends WizardStepPanel
{
    // context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    
    protected CheckBox scaleCheckBox = new CheckBox();
    
    ArrayList<BetaScale> betaScaleList = new ArrayList<BetaScale>();
    
	public MeanDifferencesScalePanel(WizardContext context)
	{
		super(context, GlimmpseWeb.constants.navItemBetaScale(),
		        WizardStepPanelState.COMPLETE);
		VerticalPanel panel = new VerticalPanel();
		
        // create header/instruction text
        HTML header = new HTML(GlimmpseWeb.constants.meanDifferenceScaleTitle());
        HTML description = new HTML(GlimmpseWeb.constants.meanDifferenceScaleDescription());
        // create the beta scale checkbox - asks if the user wants to test 0.5,1,and 2 times the estimated
        // mean difference
        HorizontalPanel checkBoxContainer = new HorizontalPanel();
        checkBoxContainer.add(scaleCheckBox);
        checkBoxContainer.add(new HTML(GlimmpseWeb.constants.meanDifferenceScaleAnswer()));

        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(new HTML(GlimmpseWeb.constants.meanDifferenceScaleQuestion()));
        panel.add(checkBoxContainer);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		
		initWidget(panel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void reset()
	{
		scaleCheckBox.setValue(false);
	}

    /**
     * Load the alpha panel from the study design context information
     */
    public void loadFromContext()
    {
        List<BetaScale> contextBetaScaleList = studyDesignContext.getStudyDesign().getBetaScaleList();
        if (contextBetaScaleList != null &&
                contextBetaScaleList.size() > 1) {
            scaleCheckBox.setValue(true);
        } else {
            scaleCheckBox.setValue(false);
        }
        
    }
    
    /**
     * Respond to a change in the context object
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) 
    {
        if (((StudyDesignChangeEvent) e).getType() ==
            StudyDesignChangeType.BETA_SCALE_LIST &&
            this != e.getSource())
        {
            loadFromContext();
        }
    };
    
    /**
     * Response to a context load event
     */
    @Override
    public void onWizardContextLoad() 
    {
        loadFromContext();
    }

    
    @Override
    public void onExit()
    {
        betaScaleList.clear();
        betaScaleList.add(new BetaScale(1.0));
        if (scaleCheckBox.getValue())
        {
            betaScaleList.add(new BetaScale(0.5));
            betaScaleList.add(new BetaScale(2));
        }
        studyDesignContext.setBetaScaleList(this, betaScaleList);
    }
}
