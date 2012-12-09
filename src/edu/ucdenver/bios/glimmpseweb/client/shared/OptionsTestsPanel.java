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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.StatisticalTest;
import edu.ucdenver.bios.webservice.common.enums.StatisticalTestTypeEnum;

/**
 * Panel for selecting statistical tests
 * @author Sarah Kreidler
 *
 */
public class OptionsTestsPanel extends WizardStepPanel
{
	// study design context
	StudyDesignContext studyDesignContext;
	
	// check boxes for statistical tests
	protected CheckBox hotellingLawleyCheckBox = new CheckBox();
	protected CheckBox pillaiBartlettCheckBox = new CheckBox();
	protected CheckBox wilksCheckBox = new CheckBox();
	protected CheckBox unirepCheckBox = new CheckBox();
	protected CheckBox unirepGGCheckBox = new CheckBox();
	protected CheckBox unirepHFCheckBox = new CheckBox();
	protected CheckBox unirepBoxCheckBox = new CheckBox();

	// only a subset of tests is available for covariate designs
	protected boolean hasCovariate = false;
	
    /**
     * Constructor
     * @param mode mode identifier (needed for unique widget identifiers)
     */
    public OptionsTestsPanel(WizardContext context, String mode)
	{
		super(context, 
		        GlimmpseWeb.constants.navItemStatisticalTest(), 
		        WizardStepPanelState.INCOMPLETE);
		studyDesignContext = (StudyDesignContext) context;
		VerticalPanel panel = new VerticalPanel();

		// create header, description
		HTML header = new HTML(GlimmpseWeb.constants.testTitle());
		HTML description = new HTML(GlimmpseWeb.constants.testDescription());        

		// list of available tests
		Grid grid = new Grid(7,2);
		grid.setWidget(0, 0, hotellingLawleyCheckBox);
		grid.setWidget(0, 1, new HTML(GlimmpseWeb.constants.testHotellingLawleyTraceLabel()));
		grid.setWidget(1, 0, pillaiBartlettCheckBox);
		grid.setWidget(1, 1, new HTML(GlimmpseWeb.constants.testPillaiBartlettTraceLabel()));
		grid.setWidget(2, 0, wilksCheckBox);
		grid.setWidget(2, 1, new HTML(GlimmpseWeb.constants.testWilksLambdaLabel()));
        grid.setWidget(3, 0, unirepBoxCheckBox);
        grid.setWidget(3, 1, new HTML(GlimmpseWeb.constants.testUnirepBoxLabel()));
		grid.setWidget(4, 0, unirepGGCheckBox);
		grid.setWidget(4, 1, new HTML(GlimmpseWeb.constants.testUnirepGeisserGreenhouseLabel()));
		grid.setWidget(5, 0, unirepHFCheckBox);
		grid.setWidget(5, 1, new HTML(GlimmpseWeb.constants.testUnirepHuynhFeldtLabel()));
        grid.setWidget(6, 0, unirepCheckBox);
        grid.setWidget(6, 1, new HTML(GlimmpseWeb.constants.testUnirepLabel()));

		// add callback to check if screen is complete
		unirepCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CheckBox cb = (CheckBox) event.getSource();
                if (cb.getValue()) {
                    addTest(StatisticalTestTypeEnum.UNIREP);
                } else {
                    deleteTest(StatisticalTestTypeEnum.UNIREP);
                }
            }
		});
		unirepGGCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CheckBox cb = (CheckBox) event.getSource();
                if (cb.getValue()) {
                    addTest(StatisticalTestTypeEnum.UNIREPGG);
                } else {
                    deleteTest(StatisticalTestTypeEnum.UNIREPGG);
                }
            }
        });
		unirepHFCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CheckBox cb = (CheckBox) event.getSource();
                if (cb.getValue()) {
                    addTest(StatisticalTestTypeEnum.UNIREPHF);
                } else {
                    deleteTest(StatisticalTestTypeEnum.UNIREPHF);
                }
            }
        });
		unirepBoxCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CheckBox cb = (CheckBox) event.getSource();
                if (cb.getValue()) {
                    addTest(StatisticalTestTypeEnum.UNIREPBOX);
                } else {
                    deleteTest(StatisticalTestTypeEnum.UNIREPBOX);
                }
            }
        });
		hotellingLawleyCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CheckBox cb = (CheckBox) event.getSource();
                if (cb.getValue()) {
                    addTest(StatisticalTestTypeEnum.HLT);
                } else {
                    deleteTest(StatisticalTestTypeEnum.HLT);
                }
            }
        });
		pillaiBartlettCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CheckBox cb = (CheckBox) event.getSource();
                if (cb.getValue()) {
                    addTest(StatisticalTestTypeEnum.PBT);
                } else {
                    deleteTest(StatisticalTestTypeEnum.PBT);
                }
            }
        });
		wilksCheckBox.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                CheckBox cb = (CheckBox) event.getSource();
                if (cb.getValue()) {
                    addTest(StatisticalTestTypeEnum.WL);
                } else {
                    deleteTest(StatisticalTestTypeEnum.WL);
                }
            }
        });
		// set defaults
		reset();
		
		// layout the overall panel
		panel.add(header);
		panel.add(description);
		panel.add(grid);
		// set style
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

		// initialize
		initWidget(panel);
	}

    /**
     * Save the test to the study design
     * @param test
     */
    private void addTest(StatisticalTestTypeEnum test) {
        studyDesignContext.addStatisticalTest(this, test);
        checkComplete();
    }
    
    /**
     * Save the test to the study design
     * @param test
     */
    private void deleteTest(StatisticalTestTypeEnum test) {
        studyDesignContext.deleteStatisticalTest(this, test);
        checkComplete();
    }
    
	/**
	 * Clear the options panel
	 */
	public void reset()
	{
		// clear the statistical tests
		hotellingLawleyCheckBox.setValue(false);
		pillaiBartlettCheckBox.setValue(false);
		pillaiBartlettCheckBox.setEnabled(true);
		wilksCheckBox.setValue(false);
		wilksCheckBox.setEnabled(true);
		unirepCheckBox.setValue(false);
		unirepGGCheckBox.setValue(false);
		unirepHFCheckBox.setValue(false);
		unirepBoxCheckBox.setValue(false);
		
		checkComplete();
	}
	
	/**
	 * Check if the user has selected a complete set of options, and
	 * if so notify that forward navigation is allowed
	 */
	private void checkComplete()
	{
		// check if continue is allowed
		// must have at least one test checked, at least one power method
		if ((hotellingLawleyCheckBox.getValue() || wilksCheckBox.getValue() || 
				pillaiBartlettCheckBox.getValue() || unirepCheckBox.getValue() ||
				unirepGGCheckBox.getValue() || unirepHFCheckBox.getValue() ||
				unirepBoxCheckBox.getValue()))
		{
			changeState(WizardStepPanelState.COMPLETE);
		}
		else
		{
			changeState(WizardStepPanelState.INCOMPLETE);
		}
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
		case COVARIATE:
			hasCovariate = studyDesignContext.getStudyDesign().isGaussianCovariate();
			pillaiBartlettCheckBox.setEnabled(!hasCovariate);
			if (pillaiBartlettCheckBox.getValue() && hasCovariate) {
			    pillaiBartlettCheckBox.setValue(false);
			    studyDesignContext.deleteStatisticalTest(this, StatisticalTestTypeEnum.PBT);
			}
			wilksCheckBox.setEnabled(!hasCovariate);
			if (wilksCheckBox.getValue() && hasCovariate) {
			    wilksCheckBox.setValue(false);
			    studyDesignContext.deleteStatisticalTest(this, StatisticalTestTypeEnum.WL);
			}
			checkComplete();
			break;
		case STATISTICAL_TEST_LIST:
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
	 * Set the test checkboxes based on the current context
	 */
	private void loadFromContext()
	{
	    reset();
	    List<StatisticalTest> contextTestList = studyDesignContext.getStudyDesign().getStatisticalTestList();
	    if (contextTestList != null) {
	        for(StatisticalTest test: contextTestList)
	        {
	            switch(test.getType())
	            {
	            case HLT:
	                hotellingLawleyCheckBox.setValue(true);
	                break;
	            case PBT:
	                pillaiBartlettCheckBox.setValue(true);
	                break;
	            case WL:
	                wilksCheckBox.setValue(true);
	                break;
	            case UNIREP:
	                unirepCheckBox.setValue(true);
	                break;
	            case UNIREPBOX:
	                unirepBoxCheckBox.setValue(true);
	                break;
	            case UNIREPGG:
	                unirepGGCheckBox.setValue(true);
	                break;
	            case UNIREPHF:
	                unirepHFCheckBox.setValue(true);
	                break;
	            }			
	        }
	    }
	    checkComplete();
	}
	

}
