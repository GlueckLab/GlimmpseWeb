/*
 * User Interface for the GLIMMPSE Software System.  Processes
 * incoming HTTP requests for power, sample size, and detectable
 * difference
 * 
 * Copyright (C) 2011 Regents of the University of Colorado.  
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

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextListener;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Hypothesis;
import edu.ucdenver.bios.webservice.common.domain.HypothesisBetweenParticipantMapping;
import edu.ucdenver.bios.webservice.common.domain.HypothesisRepeatedMeasuresMapping;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTypeEnum;

/**
 * Panel to build one sample hypothesis comparing against
 * a known mean
 * @author Sarah Kreidler
 *
 */
public class OneSampleHypothesisPanel extends Composite  
implements HypothesisBuilder, WizardContextListener {
    
    // context object
    protected StudyDesignContext studyDesignContext = null;

    // parent panel, change handler
    protected ChangeHandler parent = null;

    // table of variables to test
    protected TextBox comparisonMeanTextBox = new TextBox();
        
    /**
     * Constructor
     * @param studyDesignContext
     */
    public OneSampleHypothesisPanel(StudyDesignContext studyDesignContext,
            ChangeHandler handler)
    {
        this.parent = handler;
        this.studyDesignContext = studyDesignContext;
        this.studyDesignContext.addContextListener(this);

        VerticalPanel verticalPanel = new VerticalPanel();
        HTML text = new HTML(GlimmpseWeb.constants.oneSampleHypothesisPanelText());

        text.setText(GlimmpseWeb.constants.mainEffectPanelText());

        //Style Sheets
        text.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        //Add individual widgets to vertical panel
        verticalPanel.add(text);

        initWidget(verticalPanel);
    }


    
    /**
     * Returns true if the user has selected sufficient information
     */
    @Override
    public boolean checkComplete() {
//        return (this.selectedBetweenParticipantFactor != null || 
//                this.selectedRepeatedMeasuresNode != null);
        return true;
    }

    /**
     * Reload the panel when a user changes either the fixed predictors 
     * or repeated measures information
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) {
//        switch(((StudyDesignChangeEvent) e).getType()) {
//        case BETWEEN_PARTICIPANT_FACTORS:
//            loadBetweenFactorsFromContext();
//            break;
//        case REPEATED_MEASURES:
//            loadRepeatedMeasuresFromContext();
//            break;
//        }
    }

    /**
     * Fill in the panel on upload events
     */
    @Override
    public void onWizardContextLoad() {
//        loadBetweenFactorsFromContext();
//        loadRepeatedMeasuresFromContext();
    }



    @Override
    public Hypothesis buildHypothesis() {
        // TODO Auto-generated method stub
        return null;
    }

}
