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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Hypothesis;
import edu.ucdenver.bios.webservice.common.domain.HypothesisBetweenParticipantMapping;
import edu.ucdenver.bios.webservice.common.domain.HypothesisRepeatedMeasuresMapping;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTrendTypeEnum;

public class InteractionHypothesisPanel extends Composite
{
    StudyDesign studyDesign;
	protected FlexTable betweenParticipantFactorsFlexTable = new FlexTable();
	protected FlexTable withinParticipantFactorsFlexTable = new FlexTable();

	List<BetweenParticipantFactor> betweenParticipantFactors =
	        new ArrayList<BetweenParticipantFactor>();
    List<String> betweenParticipantFactorDataList = new ArrayList<String>();
    
    List<RepeatedMeasuresNode> repeatedMeasuresNodes =
            new ArrayList<RepeatedMeasuresNode>();
    
    List<String> withinParticipantFactorDataList =
            new ArrayList<String>();
    
	public InteractionHypothesisPanel(StudyDesign studyDesign)
	{
		VerticalPanel verticalPanel = new VerticalPanel();
		this.studyDesign = studyDesign;
		
		betweenParticipantFactors = studyDesign.getBetweenParticipantFactorList();
        
        repeatedMeasuresNodes = studyDesign.getRepeatedMeasuresTree();
		HTML text = new HTML();
		HTML betweenParticipantFactors = new HTML();
		HTML withinParticipantFactors = new HTML();
		
		
		text.setText(GlimmpseWeb.constants.interactionHypothesisPanelText());
		betweenParticipantFactors.setText(GlimmpseWeb.constants.
		        interactionHypothesisPanelBetweenParticipantFactors());
		withinParticipantFactors.setText(GlimmpseWeb.constants.
		        interactionHypothesisPanelWithinParticipantFactors());
		
		//Style Sheets
		text.setStyleName(
		        GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		betweenParticipantFactors.setStyleName(
		        GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		withinParticipantFactors.setStyleName(
		        GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
				
				
		verticalPanel.add(text);
		verticalPanel.add(betweenParticipantFactors);
		verticalPanel.add(betweenParticipantFactorsFlexTable);
		verticalPanel.add(withinParticipantFactors);
		verticalPanel.add(withinParticipantFactorsFlexTable);
		
		initWidget(verticalPanel);
	}
	public void load()
	{
	    for(BetweenParticipantFactor factor : betweenParticipantFactors)
        {
            betweenParticipantFactorDataList.add(factor.getPredictorName());
        }
	    
		int betweenParticipantFactorArrayListSize =
		        betweenParticipantFactorDataList.size();
		
		for(int i = 0; i < betweenParticipantFactorArrayListSize; i++)
		{
			betweenParticipantFactorsFlexTable.setWidget(i, 0,
			        new InteractionVariablePanel(
			                betweenParticipantFactorDataList.get(i)));
		}
		
		for(RepeatedMeasuresNode node : repeatedMeasuresNodes)
        {
            withinParticipantFactorDataList.add(node.getDimension());
        }
		
		int withinParticipantFactorsArrayListSize =
		        withinParticipantFactorDataList.size();
		
		for(int i = 0; i < withinParticipantFactorsArrayListSize; i++)
		{
			withinParticipantFactorsFlexTable.setWidget(i, 0,
			        new InteractionVariablePanel(
			                withinParticipantFactorDataList.get(i)));
			
		}
	}
	
	public Hypothesis getHypothesis()
	{
	    Hypothesis hypothesis = new Hypothesis();
	    
	    List<HypothesisBetweenParticipantMapping> participantList =
	            new ArrayList<HypothesisBetweenParticipantMapping>();
	    
	    HypothesisBetweenParticipantMapping participant =
                new HypothesisBetweenParticipantMapping();
	    
	    InteractionVariablePanel panel = null;
	    for(int i = 0; i < betweenParticipantFactorsFlexTable.getRowCount(); i++)
	    {
	        panel = (InteractionVariablePanel)
                    betweenParticipantFactorsFlexTable.getWidget(i, 0);
            if(panel.isChecked())
            {
                participant.setBetweenParticipantFactor(betweenParticipantFactors.get(i));
                String value = panel.selectedTrend();
                participantList.add(participant);
                EnumHelper enumHelper = new EnumHelper();
                participant.setType(enumHelper.getEnum(value));
                
            }
            participantList.add(participant);
	    }
	    
	    List<HypothesisRepeatedMeasuresMapping> nodeList =
                new ArrayList<HypothesisRepeatedMeasuresMapping>();
        
	    HypothesisRepeatedMeasuresMapping node =
                new HypothesisRepeatedMeasuresMapping();
        
	    for(int i = 0; i < withinParticipantFactorsFlexTable.getRowCount(); i++)
        {
            panel = (InteractionVariablePanel)
                    withinParticipantFactorsFlexTable.getWidget(i, 0);
            if(panel.isChecked())
            {
                node.setRepeatedMeasuresNode(repeatedMeasuresNodes.get(i));
                String value = panel.selectedTrend();
                nodeList.add(node);
                EnumHelper enumHelper = new EnumHelper();
                node.setType(enumHelper.getEnum(value));
            }
            nodeList.add(node);
        }
	    hypothesis.setRepeatedMeasuresMapTree(nodeList);
	    
	    return hypothesis;
	}
}
