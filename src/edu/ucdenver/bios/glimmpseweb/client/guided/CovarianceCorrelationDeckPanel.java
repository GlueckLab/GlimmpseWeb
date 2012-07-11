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

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ButtonWithExplanationPanel;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;
import edu.ucdenver.bios.webservice.common.domain.Spacing;

/**
 * Class which allows input of covariance matrices either as
 * 1. Lear correlation
 * 2. Unstructured correlation
 * 3. Unstructured covariance
 * 
 * @author VIJAY AKULA
 * @author Sarah Kreidler
 *
 */
public class CovarianceCorrelationDeckPanel extends Composite
implements ChangeHandler
{
    // deck panel indices for the correlation/covariance views
    private static final int STRUCT_CORRELATION_INDEX = 0;
    private static final int UNSTRUCT_CORRELATION_INDEX = 1;
    private static final int UNSTRUCT_COVARIANCE_INDEX = 2;
    
    // parent panel
    protected ChangeHandler parent = null;
    // name of the dimension
    protected String name = "";
    // size of the covariance matrix
    protected int size = -1;
    
    // main deck panel containing covariance views
	protected DeckPanel deckPanel = new DeckPanel();
	// 
	protected VerticalPanel controlButtonPanel = new VerticalPanel();
	
	/* buttons to switch between covariance / correlation views */
	// switch to unstructured covariance view 
	protected ButtonWithExplanationPanel unstructuredCovarianceButton =
	        new ButtonWithExplanationPanel(
	                GlimmpseWeb.constants.useCustomVariablity(),
			GlimmpseWeb.constants.useCustomVariablityAlertHeader(), 
			GlimmpseWeb.constants.useCustomVariabilityAlertText());
	
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
	public CovarianceCorrelationDeckPanel(RepeatedMeasuresNode repeatedMeasuresNode,
	        ChangeHandler handler) {
	    parent = handler;
	    name = repeatedMeasuresNode.getDimension();
	    // build a list of labels and spacing values
		List<String> labelList = new ArrayList<String>(repeatedMeasuresNode.getNumberOfMeasurements());
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
	 * Create a covariance input panel for the specified response list
	 * @param responseList response variable list
	 */
	public CovarianceCorrelationDeckPanel(List<ResponseNode> responseList,
	        ChangeHandler handler)
	{
	    parent = handler;
	    name = GlimmpseConstants.RESPONSES_COVARIANCE_LABEL;
	    ArrayList<String> labels = new ArrayList<String>(responseList.size());
		List<Integer> spacingList = new ArrayList<Integer>(responseList.size());
		int i = 1;
		for(ResponseNode node: responseList) {
			spacingList.add(i);
			labels.add(node.getName());
			i++;
		}
		buildDeckPanel(labels, spacingList);
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
				new StructuredCorrelationPanel(name, labelList, spacingList, this);
	      UnStructuredCorrelationPanel unstructuredCorrelationPanelInstance = 
              new UnStructuredCorrelationPanel(name, labelList, spacingList, this);
		UnStructuredCovariancePanel unstructuredCovariancePanelInstance = 
				new UnStructuredCovariancePanel(name, labelList, spacingList, this);
		
		deckPanel.add(structuredCorrelationPanelInstance);
		deckPanel.add(unstructuredCorrelationPanelInstance);
		deckPanel.add(unstructuredCovariancePanelInstance);
		
		// show the first widget
		showWidget(UNSTRUCT_CORRELATION_INDEX);
		
		// add button handlers to switch views
		unstructuredCovarianceButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				showWidget(UNSTRUCT_COVARIANCE_INDEX);
			}
		});
			
		unstructuredCorrelationButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
                showWidget(UNSTRUCT_CORRELATION_INDEX);	
			}
		});
		
		structuredCorrelationButton.addClickHandler(new ClickHandler()
		{
		    @Override
		    public void onClick(ClickEvent event) 
		    {
		        showWidget(STRUCT_CORRELATION_INDEX); 
		    }
		});	
		
		controlButtonPanel.add(unstructuredCovarianceButton);
		controlButtonPanel.add(unstructuredCorrelationButton);
		controlButtonPanel.add(structuredCorrelationButton);		
				
		verticalPanel.add(deckPanel);
		verticalPanel.add(controlButtonPanel);
		
		initWidget(verticalPanel);

	}
	
	private void showWidget(int index) {
	    deckPanel.showWidget(index);
	    unstructuredCovarianceButton.setVisible(index != UNSTRUCT_COVARIANCE_INDEX &&
	            size > 1);
	    unstructuredCorrelationButton.setVisible(index != UNSTRUCT_CORRELATION_INDEX);
	    structuredCorrelationButton.setVisible(index != STRUCT_CORRELATION_INDEX &&
	            size > 2);
	}
	
	/**
	 * Create a covariance domain object based on the currently
	 * visible covariance view
	 * @return Covariance domain object
	 */
	public Covariance getCovariance()
	{
	    int visibleIndex = deckPanel.getVisibleWidget();
	    CovarianceBuilder covarianceBuilder = (CovarianceBuilder) deckPanel.getWidget(visibleIndex);
	    Covariance covariance = covarianceBuilder.getCovariance();
	    return covariance;
	}
	
	/**
	 * Check if the panel is complete
	 * @return true if complete, false otherwise
	 */
	public boolean checkComplete() {
        int visibleIndex = deckPanel.getVisibleWidget();
        CovarianceBuilder covarianceBuilder = (CovarianceBuilder) deckPanel.getWidget(visibleIndex);
        return covarianceBuilder.checkComplete();
	}

    @Override
    public void onChange(ChangeEvent event) {
        parent.onChange(event);
    }
}

