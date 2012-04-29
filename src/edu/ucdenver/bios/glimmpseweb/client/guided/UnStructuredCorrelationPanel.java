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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.shared.ResizableMatrixPanel;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.StandardDeviation;
/**
 * 
 * @author VIJAY AKULA
 *
 */
public class UnStructuredCorrelationPanel extends Composite implements CovarianceBuilder
{    
    //Grid to construct the Standard Deviation Entry Text boxes
	FlexTable standardDeviationFlexTable = new FlexTable();	
	
//	//Object Initilization for Repeated Measures Node
//	RepeatedMeasuresNode repeatedMeasuresNode;
	
	ResizableMatrixPanel correlationMatrix;
	
	HorizontalPanel horizointalPanel = new HorizontalPanel();
	
	HTML errorHTML = new HTML();
	
	List<String> labelList;
	
	List<Integer> spacingList;
	
	//Constructor for the unStructuredCorrelationPanel class
	public UnStructuredCorrelationPanel(List<String> stringList, List<Integer> integerList)
	{
		//Instance of vertical panel to hold all the widgets created in this class
		VerticalPanel verticalPanel = new VerticalPanel();
		
		labelList = stringList;
		spacingList = integerList;
	
		HTML header = new HTML();
		HTML text = new HTML();
		HTML expectedStandardDeviationText = new HTML();
		HTML expectedCorrelationText = new HTML();
		
		expectedStandardDeviationText.setText(GlimmpseWeb.constants.unstructuredCorrelationEnterExpectedStandardDeviation());
		expectedCorrelationText.setText(GlimmpseWeb.constants.unstructuredCorrelationEnterExpectedCorrelation());
		
		//calling a method to construct the Standard Deviation text boxes 
		//based on the input obtained form RepeatedMeasuresNode object
		constructStandardDeviationGrid();
		
		//calling a method to construct a Correlation matrix based on the standard deviations entered
		constructCorrelationMatrix();
		
		verticalPanel.add(header);
		verticalPanel.add(text);
		verticalPanel.add(expectedStandardDeviationText);
		verticalPanel.add(standardDeviationFlexTable);
		verticalPanel.add(errorHTML);
		verticalPanel.add(expectedCorrelationText);
		verticalPanel.add(horizointalPanel);
		
		//initilizing the vertical pane widgets which holds all the widgets of the class		
		initWidget(verticalPanel);
	}
	
	/**This counstructs the Standard Deviation text boxes based on the input fron
	 * response list 
	 * @return grid
	 */
	public FlexTable constructStandardDeviationGrid()
	{
	for(int i = 0; i <spacingList.size(); i++)
		{
			HTML textLabel = new HTML(labelList.get(i));
			TextBox textBox = new TextBox();
			textBox.addChangeHandler(new ChangeHandler()
			{
				@Override
				public void onChange(ChangeEvent event)
				{
					try
					{
					TextBox tb = (TextBox)event.getSource();

					Double value = TextValidation.parseDouble
					        (tb.getValue(), 0.0, true);
					tb.setValue(value.toString());
					TextValidation.displayOkay(errorHTML, "");
					}
					catch (Exception e)
					{
						GWT.log(e.getMessage());
						TextValidation.displayError(errorHTML, "FIXME");
//						        GlimmpseWeb.constants.
//						        unstructuredCorrelationStandardDeviationError());
					}
				
				}
			});
			standardDeviationFlexTable.setWidget(i, 0, textLabel);
			standardDeviationFlexTable.setWidget(i, 1, textBox);
		}
		return standardDeviationFlexTable;
	}
		
	
	/**
	 * This constructs the Correlation Matrix based on the input entered into the Standard Deviation Text Boxes
	 * @return Correlation Matrix which is a instance of resizable matrix
	 */
	 public void constructCorrelationMatrix()
	 {
		 int size = spacingList.size();
		 correlationMatrix = new ResizableMatrixPanel(size, size, false, false, true, true);
		 correlationMatrix.setRowLabels(labelList);
		 correlationMatrix.setColumnLabels(labelList);
		 
		 for(int i = 0; i < size; i++)
		 {
			 Double value = 1.0;
			 String  stringValue = value.toString();
			 correlationMatrix.setCellValue(i, i, stringValue);
		 }
		 horizointalPanel.add(correlationMatrix);
	 }

    @Override
    public Covariance getCovariance() 
    {
        Covariance covariance = new Covariance();
        List<StandardDeviation> sdList = new ArrayList<StandardDeviation>();
        StandardDeviation sd = new StandardDeviation();
        
        for(int i = 0; i < standardDeviationFlexTable.getRowCount(); i++)
        {
            TextBox tb = (TextBox) standardDeviationFlexTable.getWidget(i, 1);
            sd.setValue(Double.parseDouble(tb.getValue()));
            sdList.add(sd);
        }
        covariance.setStandardDeviationList(sdList);
        return covariance;
    }
}
