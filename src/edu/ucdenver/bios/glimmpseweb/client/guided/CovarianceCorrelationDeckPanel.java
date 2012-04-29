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
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
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
{
    protected String name = "";
    
	protected DeckPanel deckPanel = new DeckPanel();
	protected VerticalPanel controlButtonPanel = new VerticalPanel();
	
	protected ButtonWithExplanationPanel useCustomVariability =
	        new ButtonWithExplanationPanel(
	                GlimmpseWeb.constants.useCustomVariablity(),
			GlimmpseWeb.constants.useCustomVariablityAlertHeader(), 
			GlimmpseWeb.constants.useCustomVariabilityAlertText());
	
	protected ButtonWithExplanationPanel useCustomCorrelation =
	        new ButtonWithExplanationPanel(
	                GlimmpseWeb.constants.useCustomCorrelation(),
			GlimmpseWeb.constants.useCustomCorrelationAlertHeader(), 
			GlimmpseWeb.constants.useCustomCorrelationAlertText());
	
	protected ButtonWithExplanationPanel uploadCorrelationMatrix =
	        new ButtonWithExplanationPanel(
	                GlimmpseWeb.constants.uploadCorrelationMatrix(),
			GlimmpseWeb.constants.correlationMatrixAlertHeader(), 
			GlimmpseWeb.constants.correlationMatrixAlertText());
	
	protected ButtonWithExplanationPanel useStructuredVariability =
	        new ButtonWithExplanationPanel(
	                GlimmpseWeb.constants.useStructuredVariability(),
			GlimmpseWeb.constants.useStructuredVariabilityAlertHeader(), 
			GlimmpseWeb.constants.useStructuredVariabilityAlertText());
	
	
	protected ButtonWithExplanationPanel uploadCovarianceMatrix =
	        new ButtonWithExplanationPanel(
	                GlimmpseWeb.constants.uploadCovarianceMatrix(),
			GlimmpseWeb.constants.covarianceMatrixAlertHeader(), 
			GlimmpseWeb.constants.covarianceMatrixAlertText());
	
	/**
	 * Build a covariance deck for a repeated measures dimension
	 * @param repeatedMeasuresNode
	 */
	public CovarianceCorrelationDeckPanel(RepeatedMeasuresNode repeatedMeasuresNode)
		
	{
	    name = repeatedMeasuresNode.getDimension();
		String dimension = repeatedMeasuresNode.getDimension();
		List<Spacing> spacingList = repeatedMeasuresNode.getSpacingList();
		List<Integer> integerSpacingList = new ArrayList<Integer>();
		 
		List<String> labelList = new ArrayList<String>(repeatedMeasuresNode.getNumberOfMeasurements());
		int size = spacingList.size();
		for(int i = 0; i < size; i++)
		{
		    int value = spacingList.get(i).getValue();
		    labelList.add(i, dimension+""+spacingList.get(i).toString());
		    
		    integerSpacingList.add(value);
		}
		buildDeckPanel(labelList, integerSpacingList);
	}
	
	public CovarianceCorrelationDeckPanel(List<ResponseNode> responseList)
	{
	    name = GlimmpseConstants.RESPONSES_COVARIANCE_LABEL;
	    ArrayList<String> labels = new ArrayList<String>(responseList.size());
		List<Integer> spacingList = new ArrayList<Integer>(responseList.size());
		int i = 1;
		for(ResponseNode node: responseList) {
			spacingList.add(new Integer(i));
			labels.add(node.getName());
			i++;
		}
		buildDeckPanel(labels, spacingList);
	}
	
	
	private void buildDeckPanel(List<String> labelList, List<Integer>spacingList)
	{
		VerticalPanel verticalPanel = new VerticalPanel();
		
		StructuredCorrelationPanel structuredCorrelationPanelInstance = 
				new StructuredCorrelationPanel(labelList, spacingList);
		UnStructuredCovariancePanel unstructuredCorrelationPanelInstance = 
				new UnStructuredCovariancePanel(labelList, spacingList);
		UnStructuredCorrelationPanel unstructuredCovariancePanelInstance = 
				new UnStructuredCorrelationPanel(labelList, spacingList);
		
		deckPanel.add(structuredCorrelationPanelInstance);
		deckPanel.add(unstructuredCovariancePanelInstance);
		deckPanel.add(unstructuredCorrelationPanelInstance);
		
		
		deckPanel.showWidget(0);
		
		useCustomVariability.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				useCustomVariabilityEvent();				
			}
		});
			
		useCustomCorrelation.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				useCustomCorrelationEvent();
							
			}
		});
		
		uploadCorrelationMatrix.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				uploadCorrelationMatrixEvent();
			}
		});
		
		useStructuredVariability.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				useStructuredVariabilityEvent();
			}
		});
		
		uploadCovarianceMatrix.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				uploadCovarianceMatrixEvent();
			}
		});
		
		uploadCorrelationMatrix.setVisible(false);
		useStructuredVariability.setVisible(false);
		uploadCovarianceMatrix.setVisible(false);
		
		
		controlButtonPanel.add(useCustomVariability);
		controlButtonPanel.add(useCustomCorrelation);
		controlButtonPanel.add(uploadCorrelationMatrix);
		controlButtonPanel.add(useStructuredVariability);
		controlButtonPanel.add(uploadCovarianceMatrix);
		
		
		controlButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
				
		verticalPanel.add(deckPanel);
		verticalPanel.add(controlButtonPanel);
		
		
		initWidget(verticalPanel);

	}
	
	public VerticalPanel createControlButtonPanel()
	{
		return createControlButtonPanel();		
	}
	
	public void useCustomVariabilityEvent()
	{
		deckPanel.showWidget(2);
		useCustomVariability.setVisible(false);
		useCustomCorrelation.setVisible(true);
		uploadCorrelationMatrix.setVisible(false);
		useStructuredVariability.setVisible(true);
		uploadCovarianceMatrix.setVisible(true);
	}
	
	public void useCustomCorrelationEvent()
	{
		deckPanel.showWidget(1);
		useCustomVariability.setVisible(true);
		useCustomCorrelation.setVisible(false);
		uploadCorrelationMatrix.setVisible(true);
		useStructuredVariability.setVisible(true);
		uploadCovarianceMatrix.setVisible(false);
	}
	
	public void useStructuredVariabilityEvent()
	{
		deckPanel.showWidget(0);
		useCustomVariability.setVisible(true);
		useCustomCorrelation.setVisible(true);
		uploadCorrelationMatrix.setVisible(false);
		useStructuredVariability.setVisible(false);
		uploadCovarianceMatrix.setVisible(false);
	}
	
	public void uploadCorrelationMatrixEvent()
	{
		
	}
	
	public void uploadCovarianceMatrixEvent()
	{
		
	}
	
	public Covariance getCovariance()
	{
	    int visibleIndex = deckPanel.getVisibleWidget();
	    CovarianceBuilder covarianceBuilder = (CovarianceBuilder) deckPanel.getWidget(visibleIndex);
	    Covariance covariance = covarianceBuilder.getCovariance();
	    covariance.setName(name);
	    return covariance;
	}
	
	public boolean checkComplete() {
	    return true;
	}
}

