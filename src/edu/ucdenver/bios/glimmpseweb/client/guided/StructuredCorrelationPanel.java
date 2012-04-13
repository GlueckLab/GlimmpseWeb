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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.LearCorrelation;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.shared.HtmlTextExplainPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.ResizableMatrixPanel;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.StandardDeviation;
/**
 * 
 * @author VIJAY AKULA
 *
 */
public class StructuredCorrelationPanel extends Composite implements CovarianceBuilder
{
	boolean strongestCorrelationFlag = false;
	boolean rateofDecayFlag = false;
	boolean standardDeviationFlag = false;
	
	VerticalPanel verticalPanel = new VerticalPanel();
	HorizontalPanel horizontalPanel = new HorizontalPanel();
	
	TextBox standardDeviationTextBox = new TextBox();
	TextBox strongestCorrelationTextBox = new TextBox();
	TextBox rateOfDecayOfCorrelationTextBox = new TextBox();
	
	HTML errorHTML = new HTML();
	
	List<String> labelList;
	
	List<Integer> spacingList;
	
	ResizableMatrixPanel resizableMatrix;
	/**
	 * Constructor to the clas
	 */
	public StructuredCorrelationPanel(List<String> stringList, List<Integer> integerList)
	{
		//instance of the VerticalPanel Class which is used to 
		//hold the widgets created in this particular class
		labelList = stringList;
		
		spacingList = integerList;
		
		HTML header = new HTML();
		HTML text = new HTML();
		
		
		header.setText(GlimmpseWeb.constants.structuredCorrelationPanelHeader());
		text.setText(GlimmpseWeb.constants.structuredCorrelationPanelText());
		
		
		
		HtmlTextExplainPanel standardDeviation = new HtmlTextExplainPanel(GlimmpseWeb.constants.standardDeviationLabel(),
				GlimmpseWeb.constants.standardDeviationExplinationHeader(), GlimmpseWeb.constants.standardDeviationExplinationText());
		HtmlTextExplainPanel strongestCorrelation = new HtmlTextExplainPanel(GlimmpseWeb.constants.strongestCorrelationLabel(),
				GlimmpseWeb.constants.strongestCorrelationExplinationHeader(), GlimmpseWeb.constants.strongestCorrelationExplinationText());
		HtmlTextExplainPanel rateOfDecayOfCorrelation = new HtmlTextExplainPanel(GlimmpseWeb.constants.rateOfDecayOfCorrelationLabel(),
				GlimmpseWeb.constants.rateOfDecayOfCorrelationExplinationHeader(), GlimmpseWeb.constants.rateOfDecayOfCorrelationExplinationText());
		
		Grid grid = new Grid(3,2);
		
		standardDeviationTextBox.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event) 
			{
				try
				{
				Double standardDeviationvalue = TextValidation.parseDouble(standardDeviationTextBox.getValue());
				standardDeviationTextBox.setValue(""+standardDeviationvalue);
				poplutateMatrix();
				TextValidation.displayOkay(errorHTML, "");
				standardDeviationFlag = true;
				}
				catch(Exception e)
				{
					TextValidation.displayError(errorHTML, "Enter correct Standard Deviation Value");
					standardDeviationFlag = false;
					
				}
				}
				
			});
		
		strongestCorrelationTextBox.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event) 
			{
				try
				{
				Double	strongestCorrelationvalue =TextValidation.parseDouble(strongestCorrelationTextBox.getValue(), -1.0, 1.0, true);
				strongestCorrelationFlag = true;
				poplutateMatrix();
				TextValidation.displayOkay(errorHTML, "");
				strongestCorrelationTextBox.setValue(""+strongestCorrelationvalue);
				}
				catch(Exception e)
				{
					TextValidation.displayError(errorHTML, "Strongest Correlation Value must be between -1 and +1");
					strongestCorrelationFlag = false;
				}
				
			}
		});
		
		rateOfDecayOfCorrelationTextBox.addChangeHandler(new ChangeHandler()
		{
			@Override
			public void onChange(ChangeEvent event) 
			{
				try
				{
				Double rateOfDecayvalue = TextValidation.parseDouble(rateOfDecayOfCorrelationTextBox.getValue());
				rateofDecayFlag = true;
				poplutateMatrix();
				rateOfDecayOfCorrelationTextBox.setValue(""+rateOfDecayvalue);
				TextValidation.displayOkay(errorHTML, "");
				}
				catch(Exception e)
				{
					TextValidation.displayError(errorHTML, "Enter Rate of Decay value" + e.getMessage());
					rateofDecayFlag = false;
				}
				}
		});
		
		grid.setWidget(0, 0, standardDeviation);
		grid.setWidget(0, 1, standardDeviationTextBox);
		grid.setWidget(1, 0, strongestCorrelation);
		grid.setWidget(1, 1, strongestCorrelationTextBox);
		grid.setWidget(2, 0, rateOfDecayOfCorrelation);
		grid.setWidget(2, 1, rateOfDecayOfCorrelationTextBox);
		
		addResizableMatrix();
		
		verticalPanel.add(grid);
		verticalPanel.add(errorHTML);
		verticalPanel.add(horizontalPanel);
		initWidget(verticalPanel);
	}
	
	public void addResizableMatrix()
	{
		int size = spacingList.size();		
		resizableMatrix = new ResizableMatrixPanel(size, size, false, false, true, true);
		resizableMatrix.setRowLabels(labelList);
		resizableMatrix.setColumnLabels(labelList);
		horizontalPanel.add(resizableMatrix);
	}

	public void poplutateMatrix()
	{
		if(strongestCorrelationFlag && rateofDecayFlag)
		{
			int size = spacingList.size();
			LearCorrelation learCorrelation = new LearCorrelation(spacingList);
			Double strongestCorrelation = Double.parseDouble(strongestCorrelationTextBox.getValue());
			Double rateOfDecay = Double.parseDouble(rateOfDecayOfCorrelationTextBox.getValue());
			for(int i = 1; i <= size; i++)
			{
				for(int j = 1; j <= size; j++)
				{
					if(i != j)
					{
						Double value = learCorrelation.getRho(i-1, j-1, strongestCorrelation, rateOfDecay);
						resizableMatrix.setCellValue(i, j, value.toString());
					}
					else
					{
						Double value = 1.0;
						String abc = value.toString();
						resizableMatrix.setCellValue(i, j, abc);
					}
				}
			}
		}
		else
		{
			
		}
	}
	
	public Covariance getCovariance()
	{
	    Covariance covariance = new Covariance();
	    List<StandardDeviation> sdList = new ArrayList<StandardDeviation>();
	    StandardDeviation sd = new StandardDeviation();
	    
	    sd.setValue(Double.parseDouble(standardDeviationTextBox.getValue()));
	    sdList.add(sd);
	    
	    covariance.setDelta(Double.parseDouble(rateOfDecayOfCorrelationTextBox.getValue()));
	    
	    Double rho = Math.pow(Double.parseDouble(strongestCorrelationTextBox.getValue()), 2);
	    covariance.setRho(rho);
	    
	    return covariance;
	}
}
