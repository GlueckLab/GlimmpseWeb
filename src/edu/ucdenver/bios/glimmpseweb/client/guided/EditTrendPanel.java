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
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTrendTypeEnum;

/**
 * Panel to select trend types for hypothesis
 * @author Vijay Akula
 *
 */
public class EditTrendPanel extends Composite
{
	protected static final String radioButtonGroup = "hypothesisTrendGroup";
	
	protected static final String STYLE_EDIT_TREND_PANEL = "editTrendPanel";
	protected static final String STYLE_EDIT_TREND_BUTTON = "editTrendPanelButton";
	protected static final String STYLE_NO_TREND = "noTrend";
	protected static final String STYLE_LINEAR = "linear";
	protected static final String STYLE_QUADRATIC = "quadratic";
    protected static final String STYLE_CUBIC = "cubic";
    protected static final String STYLE_ALL_TRENDS = "allTrends";
    protected static final String STYLE_CHANGE_FROM_BASELINE = "changeFromBaseline";
    
    // radio selection buttons for different trends
	RadioButton noTrendTestRadioButton;
	RadioButton changeFromBaselineRadioButton;
	RadioButton allPolynomialTrendsRadioButton;
	RadioButton linearTrendOnlyRadioButton;
	RadioButton quadraticTrendOnlyRadioButton;
	RadioButton cubicTrendOnlyRadioButton;
	
	// panels containing the radio buttons - makes it easier to hide/show buttons
	// depending on the type of study design
	HorizontalPanel noTrendPanel = new HorizontalPanel();
    HorizontalPanel changeFromBaselinePanel = new HorizontalPanel();
    HorizontalPanel allPolynomialTrendsPanel = new HorizontalPanel();
    HorizontalPanel linearTrendOnlyPanel = new HorizontalPanel();
    HorizontalPanel quadraticTrendOnlyPanel = new HorizontalPanel();
    HorizontalPanel cubicTrendOnlyPanel = new HorizontalPanel();

	/**
	 * Constructor.
	 * @param prefix prefix to ensure uniqueness of radio button group
	 */
	public EditTrendPanel(String prefix, int dataSize)
	{
		VerticalPanel panel = new VerticalPanel();
		
		// create the radio buttons
		noTrendTestRadioButton = 
		        new RadioButton(prefix+radioButtonGroup, 
		                GlimmpseWeb.constants.editTrendNoTrend());
		changeFromBaselineRadioButton = 
		        new RadioButton(prefix+radioButtonGroup, 
		                GlimmpseWeb.constants.editTrendChangeFromBaseline());
		allPolynomialTrendsRadioButton = 
		        new RadioButton(prefix+radioButtonGroup, 
		                GlimmpseWeb.constants.editTrendAllPolynomialTrends());
		linearTrendOnlyRadioButton = 
		        new RadioButton(prefix+radioButtonGroup, 
		                GlimmpseWeb.constants.editTrendLinearTrendOnly());
		quadraticTrendOnlyRadioButton = 
		        new RadioButton(prefix+radioButtonGroup, 
		                GlimmpseWeb.constants.editTrendQudraticTrendOnly());
		cubicTrendOnlyRadioButton = 
		        new RadioButton(prefix+radioButtonGroup, 
		                GlimmpseWeb.constants.editTrendCubicTrendOnly());
		// select no trend by default
		noTrendTestRadioButton.setValue(true);
	        
		// add radio buttons to panels - needed for stylesheets to work
		noTrendPanel.add(noTrendTestRadioButton);
		changeFromBaselinePanel.add(changeFromBaselineRadioButton);
        allPolynomialTrendsPanel.add(allPolynomialTrendsRadioButton);
        linearTrendOnlyPanel.add(linearTrendOnlyRadioButton);       
        quadraticTrendOnlyPanel.add(quadraticTrendOnlyRadioButton);
        cubicTrendOnlyPanel.add(cubicTrendOnlyRadioButton);

		/*Adding Radio Button to Grid*/
		panel.add(noTrendPanel);
		panel.add(changeFromBaselinePanel);
		panel.add(allPolynomialTrendsPanel);
		panel.add(linearTrendOnlyPanel);
		panel.add(quadraticTrendOnlyPanel);
		panel.add(cubicTrendOnlyPanel);
		
		// show/hide panels depending on number of available data points
		if (dataSize > -1) {
		    changeFromBaselinePanel.setVisible(dataSize > 1);
		    allPolynomialTrendsPanel.setVisible(dataSize > 2);
		    linearTrendOnlyPanel.setVisible(dataSize > 1);
		    quadraticTrendOnlyPanel.setVisible(dataSize > 2);
		    cubicTrendOnlyPanel.setVisible(dataSize > 3);
		}
		
		// set style
		panel.setStyleName(STYLE_EDIT_TREND_PANEL);
		noTrendPanel.setStyleName(STYLE_EDIT_TREND_BUTTON);
		noTrendPanel.addStyleDependentName(STYLE_NO_TREND);
		changeFromBaselinePanel.setStyleName(STYLE_EDIT_TREND_BUTTON);
		changeFromBaselinePanel.addStyleDependentName(STYLE_CHANGE_FROM_BASELINE);
		allPolynomialTrendsPanel.setStyleName(STYLE_EDIT_TREND_BUTTON);
		allPolynomialTrendsPanel.addStyleDependentName(STYLE_ALL_TRENDS);
		linearTrendOnlyPanel.setStyleName(STYLE_EDIT_TREND_BUTTON);
		linearTrendOnlyPanel.addStyleDependentName(STYLE_LINEAR);
		quadraticTrendOnlyPanel.setStyleName(STYLE_EDIT_TREND_BUTTON);
		quadraticTrendOnlyPanel.addStyleDependentName(STYLE_QUADRATIC);
		cubicTrendOnlyPanel.setStyleName(STYLE_EDIT_TREND_BUTTON);
		cubicTrendOnlyPanel.addStyleDependentName(STYLE_CUBIC);

		
		initWidget(panel);
	}
	
	/**
	 * Add an external click handler to the buttons
	 * @param handler click handler
	 */
	public void addClickHandler(ClickHandler handler)
	{
	    noTrendTestRadioButton.addClickHandler(handler);
	    changeFromBaselineRadioButton.addClickHandler(handler);
	    allPolynomialTrendsRadioButton.addClickHandler(handler);
	    linearTrendOnlyRadioButton.addClickHandler(handler);
	    quadraticTrendOnlyRadioButton.addClickHandler(handler);
	    cubicTrendOnlyRadioButton.addClickHandler(handler);
	}
	
	/**
	 * Return the selected trend from the panel
	 * @return
	 */
	public HypothesisTrendTypeEnum getSelectedTrend()
	{
	    if (noTrendTestRadioButton.getValue()) {
	        return HypothesisTrendTypeEnum.NONE;
	    } else if (changeFromBaselineRadioButton.getValue()) {
	        return HypothesisTrendTypeEnum.CHANGE_FROM_BASELINE;
	    } else if (allPolynomialTrendsRadioButton.getValue()) {
	        return HypothesisTrendTypeEnum.ALL_POLYNOMIAL;
	    } else if (linearTrendOnlyRadioButton.getValue()) {
	        return HypothesisTrendTypeEnum.LINEAR;
	    } else if (quadraticTrendOnlyRadioButton.getValue()) {
	        return HypothesisTrendTypeEnum.QUADRATIC;
	    } else if (cubicTrendOnlyRadioButton.getValue()) {
	        return HypothesisTrendTypeEnum.CUBIC;
	    } else {
	        return null;
	    }
	}
	
	
	/**
	 * Return the selected trend text label from the panel
	 * @return text label for selected trend
	 */
    public String getSelectedTrendText()
    {
        if (noTrendTestRadioButton.getValue()) {
            return noTrendTestRadioButton.getText();
        } else if (changeFromBaselineRadioButton.getValue()) {
            return changeFromBaselineRadioButton.getText();
        } else if (allPolynomialTrendsRadioButton.getValue()) {
            return allPolynomialTrendsRadioButton.getText();
        } else if (linearTrendOnlyRadioButton.getValue()) {
            return linearTrendOnlyRadioButton.getText();
        } else if (quadraticTrendOnlyRadioButton.getValue()) {
            return quadraticTrendOnlyRadioButton.getText();
        } else if (cubicTrendOnlyRadioButton.getValue()) {
            return cubicTrendOnlyRadioButton.getText();
        } else {
            return "";
        }
    }
	
	/**
	 * Select the specified trend
	 * @param trendType type of trend
	 */
	public void selectTrend(HypothesisTrendTypeEnum trendType) {
	    if (trendType != null) {
	        switch (trendType) {
	        case NONE:
	            noTrendTestRadioButton.setValue(true);
	            break;
	        case CHANGE_FROM_BASELINE:
	            changeFromBaselineRadioButton.setValue(true);
	            break;
	        case ALL_POLYNOMIAL:
	            allPolynomialTrendsRadioButton.setValue(true);
	            break;
	        case LINEAR:
	            linearTrendOnlyRadioButton.setValue(true);
	            break;
	        case QUADRATIC:
	            quadraticTrendOnlyRadioButton.setValue(true);
	            break;
	        case CUBIC:
	            cubicTrendOnlyRadioButton.setValue(true);
	            break;
	        default:
	            break;
	        }
	    }
	}
	
}
