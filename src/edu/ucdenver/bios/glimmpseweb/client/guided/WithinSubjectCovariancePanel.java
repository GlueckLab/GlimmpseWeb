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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ButtonWithExplainationPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;
/**
 * 
 * @author VIJAY AKULA
 *
 */
public class WithinSubjectCovariancePanel extends WizardStepPanel
{
	// context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;

	TabLayoutPanel tabPanel = new TabLayoutPanel(2.5,Unit.EM);
	
	VerticalPanel verticalPanel = new VerticalPanel();
	
	List<ResponseNode> responseNodeList = studyDesignContext.getStudyDesign().getResponseList();

	WithinSubjectCovariancePanel(WizardContext context) 
	{
		super(context, "Within Subject Covariance");

		HTML header = new HTML();
		HTML instructions = new HTML();
		
		header.setText(GlimmpseWeb.constants.withinSubjectCovarianceHeader());
		instructions.setText(GlimmpseWeb.constants.withinSubjectCovarianceInstructions());
		
		constructTabPanel();
				
		
		ButtonWithExplainationPanel uploadFullCovarianceMatrixButton =
		        new ButtonWithExplainationPanel(
		                GlimmpseWeb.constants.uploadFullCovarianceMatrix(), 
				GlimmpseWeb.constants.explainButtonText(), 
				GlimmpseWeb.constants.fullCovarianceMatrixHeader(), 
				GlimmpseWeb.constants.fullCovarianceMatrixText());
		
		uploadFullCovarianceMatrixButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				
			}
			
		});
				
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		instructions.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		
		verticalPanel.add(header);
		verticalPanel.add(instructions);
		verticalPanel.add(tabPanel);
		verticalPanel.add(uploadFullCovarianceMatrixButton);
			
		initWidget(verticalPanel);
	}
	
	public TabLayoutPanel constructTabPanel()
	{
		
		List<RepeatedMeasuresNode> repeatedMeasuresNodeList = 
				studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
		int size = repeatedMeasuresNodeList.size();
		for(int i = 0; i < size; i++)
		{
			
		RepeatedMeasuresNode obj = repeatedMeasuresNodeList.get(i);
		tabPanel.add(new CovarianceCorrelationDeckPanel(obj), obj.getDimension());
		if(i == size-1)
		{
		    
		    List<String> responseList = new ArrayList<String>();
		    int responseNodeListSize = responseNodeList.size();
		    for(int j = 0; j < responseNodeListSize; j++)
		    {
		        responseList.add(responseNodeList.get(j).getName());
		    }
		    tabPanel.add(new CovarianceCorrelationDeckPanel(responseList),"Responses");
		}
		}
		
		tabPanel.setHeight("525");
		tabPanel.setWidth("600");
		
		tabPanel.setAnimationDuration(700);
		return tabPanel;
	}
	
	@Override
	public void reset() 
	{
		
		
	}
	
	@Override
	public void onExit()
	{
	    for(int i = 0; i < tabPanel.getWidgetCount(); i++)
	    {
	        /*Widget widget = tabPanel.getWidget(i);*/
	        CovarianceCorrelationDeckPanel panel = (CovarianceCorrelationDeckPanel) tabPanel.getWidget(i);
	        Covariance covariance = new Covariance();
	        covariance = panel.getCovariance();
	        if( i == tabPanel.getWidgetCount() - 1)
	        {
	            covariance.setName("_RESPONSES_");
	            studyDesignContext.setCovariance(this, covariance);
	        }
	        else
	        {
	            covariance.setName(responseNodeList.get(i).getName());
	            studyDesignContext.setCovariance(this, covariance);
	        }
	        
	    }
	}
}


