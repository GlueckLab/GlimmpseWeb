/* 
 * GLIMMPSE (General Linear Multivariate Model Power and Sample size Estimator)
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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ListEntryPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.ListValidator;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;

/**
 * OutcomesPanel
 * 
 * @author Sarah Kreidler
 *
 */
public class OutcomesPanel extends WizardStepPanel
{    
    // dynamic table of outcomes
    protected ListEntryPanel outcomesListPanel = 
    	new ListEntryPanel(GlimmpseWeb.constants.outcomesTableColumn(), 
    			new ListValidator() {
    		public void validate(String value) throws IllegalArgumentException {}

    		public void onValidRowCount(int validRowCount)
    		{
    			if (validRowCount > 0)
    				changeState(WizardStepPanelState.COMPLETE);
    			else
    				changeState(WizardStepPanelState.INCOMPLETE);
    		}
    	});

    public OutcomesPanel(WizardContext context)
    {
    	super(context, "Responses");
        VerticalPanel panel = new VerticalPanel();
        
        // create header/instruction text
        HTML header = new HTML(GlimmpseWeb.constants.outcomesTitle());
        HTML description = new HTML(GlimmpseWeb.constants.outcomesDescription());
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(outcomesListPanel);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
    }
    
    @Override
    public void reset()
    {
    	outcomesListPanel.reset();
    }
    
    @Override
    public void onExit()
    {
    	
    }

//	@Override
//	public void loadFromNode(Node node)
//	{
//		outcomesListPanel.loadFromNode(node);
//		if (outcomesListPanel.getValidRowCount() > 0)
//			notifyComplete();
//		else
//			notifyInProgress();
//		notifyOutcomes();
//		changed = false;
//	}

	public String toStudyXML()
	{
		return outcomesListPanel.toXML(GlimmpseConstants.TAG_OUTCOMES_LIST);
	}
}
