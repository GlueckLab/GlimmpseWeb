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
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Hypothesis;
import edu.ucdenver.bios.webservice.common.domain.HypothesisBetweenParticipantMapping;
import edu.ucdenver.bios.webservice.common.domain.HypothesisRepeatedMeasuresMapping;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTypeEnum;

/**
 * 
 * @author VIJAY AKULA
 *
 */
public class MainEffectHypothesisPanel extends Composite  {

    StudyDesign studyDesign;
    
    protected FlexTable betweenParticipantFactorsFlexTable = new FlexTable();
	protected FlexTable withinParticipantFactorsFlexTable = new FlexTable();
	
	List<BetweenParticipantFactor> betweenParticipantFactors = null;
    List<String> betweenParticipantFactorDataList = new ArrayList<String>();
    
    List<RepeatedMeasuresNode> repeatedMeasuresNodes = null;
    List<String> withinParticipantFactorDataList = new ArrayList<String>();
    
	public MainEffectHypothesisPanel(StudyDesign studyDesign)
	{
	    this.studyDesign = studyDesign;
	    
	    betweenParticipantFactors = studyDesign.getBetweenParticipantFactorList();
	    
	    repeatedMeasuresNodes = studyDesign.getRepeatedMeasuresTree();
	    
		VerticalPanel verticalPanel = new VerticalPanel();
		HTML text = new HTML();
		HTML betweenParticipantFactors = new HTML();
		HTML withinParticipantFactors = new HTML();
		
		
		
		text.setText(GlimmpseWeb.constants.mainEffectPanelText());
		betweenParticipantFactors.setText(
		        GlimmpseWeb.constants.mainEffectPanelBetweenParticipantFactors());
		withinParticipantFactors.setText(
		        GlimmpseWeb.constants.mainEffectPanelWithinParticipantFactors());
	
		
		
		//Style Sheets
		text.setStyleName(
		        GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		betweenParticipantFactors.setStyleName(
		        GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		withinParticipantFactors.setStyleName(
		        GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		
		
		//Add individual widgets to vertical panel
		verticalPanel.add(text);
		verticalPanel.add(betweenParticipantFactors);
		verticalPanel.add(betweenParticipantFactorsFlexTable);
		verticalPanel.add(withinParticipantFactors);
		
		verticalPanel.add(withinParticipantFactorsFlexTable);
		
		verticalPanel.setWidth("100%");
		
		initWidget(verticalPanel);
	}
	
	/**
	 * Respond to a change in the context object
	 */
   /* @Override*/
	public void onWizardContextChange(WizardContextChangeEvent e) 
    {
    	
    };
    
    /**
     * Response to a context load event
     */
    /*@Override*/
	public void onWizardContextLoad() 
    {
	    load();
    }
	
	public void load()
	{
	    for(BetweenParticipantFactor factor : betweenParticipantFactors)
	    {
	        betweenParticipantFactorDataList.add(factor.getPredictorName());
	    }
	    
		int size = betweenParticipantFactorDataList.size();
		
		for(int i = 0; i < size; i++)
		{
			betweenParticipantFactorsFlexTable.setWidget(
			        i, 0, new RadioButton(
			                "RadioButtonsGroup",
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
			withinParticipantFactorsFlexTable.setWidget(
			        i, 0, new RadioButton("RadioButtonsGroup",
			                withinParticipantFactorDataList.get(i)));
		}
	}
	
	public Hypothesis getHypothesis()
	{
	    Hypothesis hypothesis = new Hypothesis();
	    BetweenParticipantFactor participant = getBetweenParticipant();
	    RepeatedMeasuresNode node = getRepeatedMeasuresNode();
	    hypothesis.setType(HypothesisTypeEnum.MAIN_EFFECT);
	    if(participant == null)
	    {
	       HypothesisRepeatedMeasuresMapping mappingNode =
	               new HypothesisRepeatedMeasuresMapping();
	       mappingNode.setRepeatedMeasuresNode(node);
	       List<HypothesisRepeatedMeasuresMapping> mappingList =
	               new ArrayList<HypothesisRepeatedMeasuresMapping>();
	       mappingList.add(mappingNode);
	       hypothesis.setRepeatedMeasuresMapTree(mappingList);
	    }
	    else
	    {
	        HypothesisBetweenParticipantMapping mappingParticipant =
	                new HypothesisBetweenParticipantMapping();
	        mappingParticipant.setBetweenParticipantFactor(participant);
	        List<HypothesisBetweenParticipantMapping> mappingList = 
	                new ArrayList<HypothesisBetweenParticipantMapping>();
	        mappingList.add(mappingParticipant);
	        hypothesis.setBetweenParticipantFactorMapList(mappingList);
	    }
	    
	    return hypothesis;
	}
	
	public BetweenParticipantFactor getBetweenParticipant()
	{
	    BetweenParticipantFactor betweenParticipantFactor = null;
	    int size = betweenParticipantFactorsFlexTable.getRowCount();
	    for(int i = 0; i < size; i++ )
	    {
	        RadioButton radioButton = (RadioButton)
	                betweenParticipantFactorsFlexTable.getWidget(i, 0);
	        if(radioButton.isChecked())
	        {
	            betweenParticipantFactor = betweenParticipantFactors.get(i);
	        }   
	    }
	    return betweenParticipantFactor;
	}
	
	public RepeatedMeasuresNode getRepeatedMeasuresNode()
    {
        RepeatedMeasuresNode repeatedMeasuresNode = null;
        int size = withinParticipantFactorsFlexTable.getRowCount();
        for(int i = 0; i < size; i++ )
        {
            RadioButton radioButton = (RadioButton)
                    withinParticipantFactorsFlexTable.getWidget(i, 0);
            if(radioButton.isChecked())
            {
                repeatedMeasuresNode = repeatedMeasuresNodes.get(i);
            }   
        }
        return repeatedMeasuresNode;
    }

}
