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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ButtonWithExplanationPanel;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.Spacing;
import edu.ucdenver.bios.webservice.common.enums.CovarianceTypeEnum;

/**
 * Class which allows input of correlation matrices either as
 * 1. Lear correlation
 * 2. Unstructured correlation
 * 
 * @author VIJAY AKULA
 * @author Sarah Kreidler
 *
 */
public class CorrelationDeckPanel extends Composite
implements CovarianceBuilder
{
    // deck panel indices for the correlation/covariance views
    private static final int STRUCT_CORRELATION_INDEX = 0;
    private static final int UNSTRUCT_CORRELATION_INDEX = 1;
    
    // parent panel
    protected CovarianceSetManager manager = null;
    // name of the dimension
    protected String name = "";
    // row / column dimension of the correlation matrix
    protected int size = -1;
    
    // main deck panel containing covariance views
	protected DeckPanel deckPanel = new DeckPanel();
	// 
	protected VerticalPanel controlButtonPanel = new VerticalPanel();
	
	/* buttons to switch between structured and unstructured views */
	protected ButtonWithExplanationPanel unstructuredCorrelationButton =
	        new ButtonWithExplanationPanel(
	                GlimmpseWeb.constants.useCustomCorrelation(),
			GlimmpseWeb.constants.useCustomCorrelationAlertHeader(), 
			GlimmpseWeb.constants.useCustomCorrelationAlertText());
	
	protected ButtonWithExplanationPanel structuredCorrelationButton =
	        new ButtonWithExplanationPanel(
	                GlimmpseWeb.constants.useStructuredVariability(),
			GlimmpseWeb.constants.useStructuredVariabilityAlertHeader(), 
			GlimmpseWeb.constants.useStructuredVariabilityAlertText());
	
	/**
	 * Build a covariance deck for a repeated measures dimension
	 * @param repeatedMeasuresNode
	 */
	public CorrelationDeckPanel(CovarianceSetManager manager,
            RepeatedMeasuresNode repeatedMeasuresNode) {
	    this.manager = manager;
	    name = repeatedMeasuresNode.getDimension();
	    // build a list of labels and spacing values
		List<String> labelList = 
		    new ArrayList<String>(repeatedMeasuresNode.getNumberOfMeasurements());
		List<Spacing> spacingList = repeatedMeasuresNode.getSpacingList();
		List<Integer> integerSpacingList = new ArrayList<Integer>();
		if (spacingList != null && 
		        spacingList.size() == repeatedMeasuresNode.getNumberOfMeasurements()) {
		    // potentially unequal spacing for numeric repeated measures (time, weeks, etc.)
		    for(Spacing spacing: spacingList) {
		        labelList.add(name+","+spacing.getValue());
		        integerSpacingList.add(spacing.getValue());
		    }
		} else {
		    // use equal spacing for categorical and nominal repeated measures
            for(int i = 1; i <= repeatedMeasuresNode.getNumberOfMeasurements(); i++) {
                labelList.add(name + "," + i);
                integerSpacingList.add(i);
            }
		}
		buildDeckPanel(labelList, integerSpacingList);
	}
	
	/**
	 * Create the structured correlation, unstructured correlation,
	 * and unstructured covariance input panels
	 * 
	 * @param labelList
	 * @param spacingList
	 */
	private void buildDeckPanel(List<String> labelList, List<Integer>spacingList)
	{
	    size = spacingList.size();
		VerticalPanel verticalPanel = new VerticalPanel();
		
		StructuredCorrelationPanel structuredCorrelationPanelInstance = 
				new StructuredCorrelationPanel(manager, name, labelList, spacingList);
		UnStructuredCorrelationPanel unstructuredCorrelationPanelInstance = 
              new UnStructuredCorrelationPanel(manager, name, labelList, spacingList, false);
		
		deckPanel.add(structuredCorrelationPanelInstance);
		deckPanel.add(unstructuredCorrelationPanelInstance);
		
		// show the first widget
		if (spacingList.size() <= 2) {
		    showWidget(UNSTRUCT_CORRELATION_INDEX);
		} else {
		    showWidget(STRUCT_CORRELATION_INDEX);
		}
		
		// add button handlers to switch views			
		unstructuredCorrelationButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
                showWidget(UNSTRUCT_CORRELATION_INDEX);	
                syncCovariance();
			}
		});
		structuredCorrelationButton.addClickHandler(new ClickHandler()
		{
		    @Override
		    public void onClick(ClickEvent event) 
		    {
		        showWidget(STRUCT_CORRELATION_INDEX); 
		        syncCovariance();
		    }
		});	
		
		controlButtonPanel.add(unstructuredCorrelationButton);
		controlButtonPanel.add(structuredCorrelationButton);		
				
		verticalPanel.add(deckPanel);
		verticalPanel.add(controlButtonPanel);
		
		initWidget(verticalPanel);

	}
	
	private void showWidget(int index) {
	    deckPanel.showWidget(index);
	    unstructuredCorrelationButton.setVisible(index != UNSTRUCT_CORRELATION_INDEX);
	    structuredCorrelationButton.setVisible(index != STRUCT_CORRELATION_INDEX &&
	            size > 2);
	}

    /**
     * Load information from the specified covariance object into the panel
     * @param covariance the covariance object
     */
    public void loadCovariance(Covariance covariance) {
        if (covariance != null) {
            int index = -1;
            switch (covariance.getType()) {
            case UNSTRUCTURED_CORRELATION:
                index = UNSTRUCT_CORRELATION_INDEX;
                break;
            case LEAR_CORRELATION:
                index = STRUCT_CORRELATION_INDEX;
                break;
            }
            if (index > -1) {
                CovarianceBuilder covarianceBuilder = 
                    (CovarianceBuilder) deckPanel.getWidget(index);
                showWidget(index);
                if (covarianceBuilder != null) {
                    covarianceBuilder.loadCovariance(covariance);
                }
            }
        }
    }
    
    /**
     * Sync the current GUI view to the context
     */
    public void syncCovariance() {
        CovarianceBuilder covarianceBuilder = 
            (CovarianceBuilder) deckPanel.getWidget(deckPanel.getVisibleWidget());
        covarianceBuilder.syncCovariance();
    }
}

