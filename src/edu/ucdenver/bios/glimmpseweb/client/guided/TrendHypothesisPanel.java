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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
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
import edu.ucdenver.bios.webservice.common.enums.HypothesisTypeEnum;

public class TrendHypothesisPanel extends Composite
{
	protected FlexTable betweenParticipantFactorsFlexTable = new FlexTable();
	protected FlexTable withinParticipantFactorsFlexTable = new FlexTable();
	
	StudyDesign studyDesign;
	
	List<BetweenParticipantFactor> betweenParticipantFactors = new ArrayList<BetweenParticipantFactor>();
            
    List<String> betweenParticipantFactorDataList = new ArrayList<String>();
    
    List<RepeatedMeasuresNode> repeatedMeasuresNodes = new ArrayList<RepeatedMeasuresNode>();
    List<String> withinParticipantFactorDataList = new ArrayList<String>();
    
    EditTrendPanel editTrendPanel = new EditTrendPanel();
	
	public TrendHypothesisPanel(StudyDesign studyDesign)
	{
		VerticalPanel verticalPanel = new VerticalPanel();
		
		this.studyDesign = studyDesign;
		
		betweenParticipantFactors = studyDesign.getBetweenParticipantFactorList();
		
		repeatedMeasuresNodes = studyDesign.getRepeatedMeasuresTree();
		
		HTML text = new HTML();
		HTML betweenParticipantFactors = new HTML();
		HTML withinParticipantFactors = new HTML();
		final HTML selectTypeOfTrend = new HTML(
                GlimmpseWeb.constants.hypothesisPanelSelectTypeOfTrend());
		
		
		text.setText(GlimmpseWeb.constants.trendHypothesisPanelText());
		betweenParticipantFactors.setText(GlimmpseWeb.constants.trendHypothesisPanelBetweenParticipantFactors());
		withinParticipantFactors.setText(GlimmpseWeb.constants.trendHypothesisPanelWithinParticipantFactors());
		
		//Style Sheets
		text.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		betweenParticipantFactors.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		withinParticipantFactors.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		selectTypeOfTrend.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		
		HorizontalPanel editTrendHorizontalPanel = new HorizontalPanel();
		editTrendHorizontalPanel.add(editTrendPanel);
		verticalPanel.add(text);
		verticalPanel.add(betweenParticipantFactors);
		verticalPanel.add(betweenParticipantFactorsFlexTable);
		verticalPanel.add(withinParticipantFactors);
		verticalPanel.add(withinParticipantFactorsFlexTable);
		verticalPanel.add(selectTypeOfTrend);
		verticalPanel.add(editTrendPanel);
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
			        new RadioButton("RadioButtonsGroup",
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
			        new RadioButton("RadioButtonsGroup",
			                withinParticipantFactorDataList.get(i)));
		}
	}
	
	
	public Hypothesis getHypothesis()
    {
        Hypothesis hypothesis = new Hypothesis();
        BetweenParticipantFactor participant = getBetweenParticipant();
        RepeatedMeasuresNode node = getRepeatedMeasuresNode();
        hypothesis.setType(HypothesisTypeEnum.TREND);
        String value = editTrendPanel.getSelectedTrend();
        EnumHelper enumHelper = new EnumHelper();
        if(participant == null)
        {
           HypothesisRepeatedMeasuresMapping mappingNode =
                   new HypothesisRepeatedMeasuresMapping();
           mappingNode.setRepeatedMeasuresNode(node);
           List<HypothesisRepeatedMeasuresMapping> mappingList =
                   new ArrayList<HypothesisRepeatedMeasuresMapping>();
           
           mappingNode.setType(enumHelper.getEnum(value));
           mappingList.add(mappingNode);
           hypothesis.setRepeatedMeasuresMapTree(mappingList);
        }
        else
        {
            HypothesisBetweenParticipantMapping mappingParticipant =
                    new HypothesisBetweenParticipantMapping();
            mappingParticipant.setBetweenParticipantFactor(participant);
            mappingParticipant.setType(null);
            mappingParticipant.setType(enumHelper.getEnum(value));
            List<HypothesisBetweenParticipantMapping> mappingList = 
                    new ArrayList<HypothesisBetweenParticipantMapping>();
            mappingList.add(mappingParticipant);
            hypothesis.setBetweenParticipantFactorMapList(mappingList);
        }
        
        return hypothesis;
    }
	
	
	public BetweenParticipantFactor getBetweenParticipant()
	{
	    BetweenParticipantFactor participant = new BetweenParticipantFactor();
	    
        int size = betweenParticipantFactorsFlexTable.getRowCount();
        for(int i = 0; i < size; i++ )
        {
            RadioButton radioButton = (RadioButton)
                    betweenParticipantFactorsFlexTable.getWidget(i, 0);
            if(radioButton.isChecked())
            {
                participant = betweenParticipantFactors.get(i);
                break;
            }   
        }
	    return participant;
	}
	
	public RepeatedMeasuresNode getRepeatedMeasuresNode()
	{
	    RepeatedMeasuresNode node =
	            new RepeatedMeasuresNode();
	    int size = withinParticipantFactorsFlexTable.getRowCount();
        for(int i = 0; i < size; i++ )
        {
            RadioButton radioButton = (RadioButton)
                    withinParticipantFactorsFlexTable.getWidget(i, 0);
            if(radioButton.isChecked())
            {
                node = repeatedMeasuresNodes.get(i);
                break;
            }   
        }
	    return node;
	}
	
	public String getSelectedTrend()
	{
	    String selectedTrend = editTrendPanel.getSelectedTrend();
	    return selectedTrend;
	}
	
}
