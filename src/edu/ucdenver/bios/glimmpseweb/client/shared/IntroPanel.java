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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;

/**
 * Generic panel for subsection introduction screens
 * @author Sarah Kreidler
 *
 */
public class IntroPanel extends WizardStepPanel
{

    /**
     * Create a new introduction panel.  Includes text describing the current
     * section in the wizard.
     * @param context the wizard context object
     * @param name display name for the panel
     * @param title title text
     * @param description description text
     */
	public IntroPanel(WizardContext context, String name, String title, String description)
	{
		super(context, name, WizardStepPanelState.COMPLETE);
		VerticalPanel panel = new VerticalPanel();
		
		HTML titleHTML = new HTML(title);
		titleHTML.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		panel.add(titleHTML);
		
		HTML pgHTML = new HTML(description);
		pgHTML.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		panel.add(pgHTML);
		
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		initWidget(panel);
	}

	/**
	 * Reset the panel.
	 */
	@Override
	public void reset()
	{
		// static page, no resetting to do
	}

	/**
	 * Respond to context changes
	 */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) {
        // No work required for this panel
    }

    /**
     * Load panel from wizard context
     */
    @Override
    public void onWizardContextLoad() {
        // No work required for this panel
    }

}
