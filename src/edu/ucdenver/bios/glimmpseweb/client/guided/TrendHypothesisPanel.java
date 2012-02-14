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

public class TrendHypothesisPanel extends Composite
{
	protected FlexTable betweenParticipantFactorsFlexTable = new FlexTable();
	protected FlexTable withinParticipantFactorsFlexTable = new FlexTable();
	
	public TrendHypothesisPanel()
	{
		VerticalPanel verticalPanel = new VerticalPanel();
		HTML text = new HTML();
		HTML betweenParticipantFactors = new HTML();
		HTML withinParticipantFactors = new HTML();
		
		
		text.setText(GlimmpseWeb.constants.trendHypothesisPanelText());
		betweenParticipantFactors.setText(GlimmpseWeb.constants.trendHypothesisPanelBetweenParticipantFactors());
		withinParticipantFactors.setText(GlimmpseWeb.constants.trendHypothesisPanelWithinParticipantFactors());
		
		//Style Sheets
		text.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		betweenParticipantFactors.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		withinParticipantFactors.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
				
		verticalPanel.add(text);
		verticalPanel.add(betweenParticipantFactors);
		verticalPanel.add(betweenParticipantFactorsFlexTable);
		verticalPanel.add(withinParticipantFactors);
		verticalPanel.add(withinParticipantFactorsFlexTable);
		
		initWidget(verticalPanel);
	}
	public void test()
	{
		List<String> betweenParticipantFactorDataList = new ArrayList<String>();
		betweenParticipantFactorDataList.add("Treatment");
		betweenParticipantFactorDataList.add("Risk Group");
		betweenParticipantFactorDataList.add("Risk Gfjghfdg");
		
		int betweenParticipantFactorArrayListSize = betweenParticipantFactorDataList.size();
		for(int i = 0; i < betweenParticipantFactorArrayListSize; i++)
		{
			betweenParticipantFactorsFlexTable.setWidget(i, 0, new RadioButton("Between Participant Factors Group", betweenParticipantFactorDataList.get(i)));
		}
		
		List<String> withinParticipantFactorDataList = new ArrayList<String>();
		withinParticipantFactorDataList.add("Time");
		withinParticipantFactorDataList.add("Heart Beat");
		
		int withinParticipantFactorsArrayListSize = withinParticipantFactorDataList.size();
		for(int i = 0; i < withinParticipantFactorsArrayListSize; i++)
		{
			withinParticipantFactorsFlexTable.setWidget(i, 0, new RadioButton("Within Participant Factors Group", withinParticipantFactorDataList.get(i)));
		}
	}
}
