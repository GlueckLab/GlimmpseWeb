/*
 * Power Service for the GLIMMPSE Software System.  Processes
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

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;

public class EditTrendPanel extends Composite
{
	final String radioButtonGroup = "Various Trend Types";
	RadioButton noTrendTestRadioButton = new RadioButton(radioButtonGroup, GlimmpseWeb.constants.editTrendNoTrend());
	RadioButton changeFromBaseLineRadiButton = new RadioButton(radioButtonGroup, GlimmpseWeb.constants.editTrendChangeFromBaseline());
	RadioButton allPolynomialTrendsRadioButton = new RadioButton(radioButtonGroup, GlimmpseWeb.constants.editTrendAllPolynomialTrends());
	RadioButton linearTrendOnlyRadioButton = new RadioButton(radioButtonGroup, GlimmpseWeb.constants.editTrendLinearTrendOnly());
	RadioButton qudraticTrendOnlyRadioButton = new RadioButton(radioButtonGroup, GlimmpseWeb.constants.editTrendQudraticTrendOnly());
	RadioButton cubicTrendOnlyRadioButton = new RadioButton(radioButtonGroup, GlimmpseWeb.constants.editTrendCubicTrendOnly());
	
	Grid grid = new Grid(6,2);
	
	public EditTrendPanel(boolean NO_TREND_DISPLAY_FLAG)
	{
		
		VerticalPanel verticalPanel = new VerticalPanel();
		
		
		//Creating Radio Buttons
				
		noTrendTestRadioButton.setVisible(false);
		
		
		//Creating Image Widget
		Image changeFromBaseLineImage = new Image(
		        GlimmpseWeb.constants.editTrendChangeFromBaselineImage());
		Image allPolynomialTrendsImage = new Image(
		        GlimmpseWeb.constants.editTrendAllPolynomialTrendsImage());
		Image linearTrendOnlyImage = new Image(
		        GlimmpseWeb.constants.editTrendLinearTrendOnlyImage());
		Image qudraticTrendOnlyImage = new Image(
		        GlimmpseWeb.constants.editTrendQudraticTrendOnlyImage());
		Image cubicTrendOnlyImage = new Image(
		        GlimmpseWeb.constants.editTrendCubicTrendOnlyImage());
		
		
		/*Adding Radio Button to Grid*/
		grid.setWidget(0, 0, noTrendTestRadioButton);
		grid.setWidget(1, 0, changeFromBaseLineRadiButton);
		grid.setWidget(2, 0, allPolynomialTrendsRadioButton);
		grid.setWidget(3, 0, linearTrendOnlyRadioButton);
		grid.setWidget(4, 0, qudraticTrendOnlyRadioButton);
		grid.setWidget(5, 0, cubicTrendOnlyRadioButton);
		
		
		/*Adding Image Widget to grid*/
		grid.setWidget(1, 1, changeFromBaseLineImage);
		grid.setWidget(2, 1, allPolynomialTrendsImage);
		grid.setWidget(3, 1, linearTrendOnlyImage);
		grid.setWidget(4, 1, qudraticTrendOnlyImage);
		grid.setWidget(5, 1, cubicTrendOnlyImage);
		
		if(NO_TREND_DISPLAY_FLAG == true)
		{
			noTrendTestRadioButton.setVisible(true);
		}
		else
		{
			noTrendTestRadioButton.setValue(false);
		}
		
		verticalPanel.add(grid);
		
		initWidget(verticalPanel);
	}
	public void addClickHandler(ClickHandler handler)
	{
		noTrendTestRadioButton.addClickHandler(handler);
		changeFromBaseLineRadiButton.addClickHandler(handler);
		allPolynomialTrendsRadioButton.addClickHandler(handler);
		linearTrendOnlyRadioButton.addClickHandler(handler);
		qudraticTrendOnlyRadioButton.addClickHandler(handler);
		cubicTrendOnlyRadioButton.addClickHandler(handler);
	}
	public String getSelectedTrend()
	{
	    String selectedTrend = null;
	    for(int i = 0; i <= 5; i++)
	    {
	        RadioButton radioButton = (RadioButton) grid.getWidget(i, 0);
	        if(radioButton.isChecked())
	        {
	            selectedTrend = radioButton.getText();
	        }
	    }
	    return selectedTrend;
	}
}
