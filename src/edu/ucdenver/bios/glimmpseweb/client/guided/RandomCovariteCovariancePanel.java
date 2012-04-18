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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.Spacing;
/**
 * 
 * @author VIJAY AKULA
 *
 */
public class RandomCovariteCovariancePanel extends WizardStepPanel
{
	// context object
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;
    
    HorizontalPanel hp = new HorizontalPanel();
    
    FlexTable flexTable = new FlexTable();
    
    int flexTableRows = 1;
    
    boolean CHECKBOXVALUE;
    
    TextBox textBox = new TextBox();
    
    List<RepeatedMeasuresNode> repeatedMeasuresNodeList= new ArrayList<RepeatedMeasuresNode>();
    //this is the List of List of Strings to keep track of
    //all the repeated nodes objects label list
    List<List<String>> dataList = new ArrayList<List<String>>();
    
    HTML errorHtml = new HTML();
    HTML errorHTML = new HTML();
    
    
	@SuppressWarnings("deprecation")
	public RandomCovariteCovariancePanel(WizardContext context, String name) 
	{
		super(context, "Random Covariate Covariance Screen");
		
		VerticalPanel verticalPanel = new VerticalPanel();
		
		HTML header = new HTML();
		header.setText(GlimmpseWeb.constants.randomCovariateCovarianceHeader());
		
		HTML description = new HTML();
		description.setText(GlimmpseWeb.constants.randomCovariateCovarianceDescription());
		
		HTML enterStandardDeviationExpectedInstruction = new HTML();
		enterStandardDeviationExpectedInstruction.setText(GlimmpseWeb.constants.randomCovariateCovarianceEnterStandardDeviationExpectedInstruction());
		
		HTML enterCorrelationYouExpectToObserveInstruction = new HTML();
		enterCorrelationYouExpectToObserveInstruction.setText(GlimmpseWeb.constants.randomCovariateCovarianceEnterCorrelationYouExpectToObserveInstruction());
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();

		HTML label = new HTML("Covarite");
		
		
		textBox.addChangeHandler(new ChangeHandler(){ 
            @Override
            public void onChange(ChangeEvent event)
            {
                TextBox tb = (TextBox)event.getSource();
                try
                {
                    String value = tb.getValue();
                }
                catch (Exception e)
                {
                    
                }
            }
        });
		
		horizontalPanel.add(label);
		horizontalPanel.add(textBox);
		
		
		CheckBox sampleCorrelationCheckBox = new CheckBox("");
		
		sampleCorrelationCheckBox.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				CheckBox cb = (CheckBox)event.getSource();
				CHECKBOXVALUE = cb.isChecked();
				editCorrelationTextBoxes();
				
			}
		});
		sampleCorrelationCheckBox.setHTML("Use the sample correlation for all outcomes");

		constructFlexTable();
		
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		sampleCorrelationCheckBox.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		
		verticalPanel.add(header);
		verticalPanel.add(description);
		verticalPanel.add(enterStandardDeviationExpectedInstruction);
		verticalPanel.add(horizontalPanel);
		verticalPanel.add(enterCorrelationYouExpectToObserveInstruction);
		verticalPanel.add(sampleCorrelationCheckBox);
		verticalPanel.add(errorHtml);
		verticalPanel.add(hp);
		verticalPanel.add(errorHTML);
		initWidget(verticalPanel);

	}

	@Override
	public void reset() 
	{

	}
	
	public void constructFlexTable()
	{
		
		
		
		//getting repeated measures tree form a study design
		repeatedMeasuresNodeList = studyDesignContext.getStudyDesign().getRepeatedMeasuresTree();
		
		//integer value to keep the track of number of repeated measures node objects in the repeated measures tree
		int noOfRepeatedMeasures = repeatedMeasuresNodeList.size();
		
		//Construction of the flexTable
		
		
		//this for loop is used to display the Headings of the flex table
		// which are present in the first row of the tabel
		
		for(int i = 0; i < noOfRepeatedMeasures; i++)
		{
			RepeatedMeasuresNode repeatedMeasuresNode = new RepeatedMeasuresNode();
			
			repeatedMeasuresNode = repeatedMeasuresNodeList.get(i);
			
			List<String> labelList = new ArrayList<String>();
			
			List <Spacing> spacingList = repeatedMeasuresNode.getSpacingList();
			
			String dimension = repeatedMeasuresNode.getDimension();
			
			//code to construct the label list
			for(int j = 0; j < spacingList.size(); j++)
			{
				labelList.add(dimension+" "+spacingList.get(j).toString());
			}
			
			//this is to display the dimension as the heading of the cloumn
			HTML label = new HTML(repeatedMeasuresNode.getDimension());
			label.setSize("60", "20");
			flexTable.setWidget(0, i, label);
			
			dataList.add(labelList);
			
			//this integer variable is to store the total number of rows 
			//to be displayed in the flex table
			flexTableRows = flexTableRows*labelList.size();
		}
		
		//this integer variable is to find the number of lists in the dataList
		//so that Responses and Correlatio n will be displayed as heading in the next columns of the flex table
		int dataListSize = dataList.size();
		
		HTML responses = new HTML("Responses");
		responses.setSize("60", "20");
		HTML correlation = new HTML("Correlation");
		correlation.setSize("60", "20");
		flexTable.setWidget(0, dataListSize, responses);
		flexTable.setWidget(0, dataListSize+1, correlation);
		
		List <String> responseList = studyDesignContext.getStudyDesign().getResponseListNames();

		//adding responseList to the dataList
		dataList.add(responseList);
		
		//Calculating the numbers of rows to be displayed a
		flexTableRows = flexTableRows*responseList.size();
		
		
		int i;
		for( i = 0; i <= dataList.size()-1; i++)
		{
			int numberOfRows = 1;
			for(int x = i+1; x < dataList.size(); x++)
			{
				int c = dataList.get(x).size();
				numberOfRows = numberOfRows*c;
			}
			GWT.log(""+numberOfRows);
			int a = 0;
			String abc="";
			for(int j = 0; j < flexTableRows; j++)
				{
				HTML text = new HTML();
					if(j % numberOfRows == 0)
					{
						abc = dataList.get(i).get(a);
						int check = dataList.get(i).size();
						if(a == check-1)
						{
							a = 0;
						}
						else
						{
							a++;
						}
					}
				text.setText(abc);
				flexTable.setWidget(j+1, i, text);
			}
		}
		for(int t = 1; t <= flexTableRows; t++ )
		{
			TextBox tb = new TextBox();
			tb.addChangeHandler(new ChangeHandler() 
			{
				@Override
				public void onChange(ChangeEvent event) 
				{
					TextBox correlationTextBox = (TextBox)event.getSource();
					try
					{
						String value = correlationTextBox.getValue();
						double d = TextValidation.parseDouble(value, -1.0, 1.0, true);
						correlationTextBox.setValue(""+d);
						TextValidation.displayOkay(errorHtml, "");
						TextValidation.displayOkay(errorHTML, "");
						
					}
					catch(Exception e)
					{
						TextValidation.displayError(errorHtml, GlimmpseWeb.constants.randomCovariateCovarianceCorrelationValueError());
						TextValidation.displayError(errorHTML, GlimmpseWeb.constants.randomCovariateCovarianceCorrelationValueError());
					}	
				}
			});
			tb.setSize("75", "30");
			flexTable.setWidget(t, dataList.size(), tb);
		}
	
		hp.add(flexTable);
	}

	public void editCorrelationTextBoxes()
	{
		if(CHECKBOXVALUE)
		{
			for(int i = 2; i <= flexTableRows; i++)
			{
				TextBox textBox = (TextBox)flexTable.getWidget(i, dataList.size());
				textBox.setEnabled(false);
			}
			
			TextBox tb = (TextBox)flexTable.getWidget(1, dataList.size());
			tb.addChangeHandler(new ChangeHandler() 
			{
				@Override
				public void onChange(ChangeEvent event)
				{
					try
					{
						TextBox t = (TextBox)event.getSource();
						String value = t.getValue();
						double d = TextValidation.parseDouble(value, -1.0, 1.0, true);
						t.setValue(""+d);
						TextValidation.displayOkay(errorHtml, "");
						TextValidation.displayOkay(errorHTML, "");
						for(int i = 2; i <= flexTableRows; i ++)
						{
							TextBox correlationTb = (TextBox)flexTable.getWidget(i, dataList.size());
							correlationTb.setValue(""+d);
						}
					}
					catch(Exception e)
					{
						TextValidation.displayError(errorHtml, GlimmpseWeb.constants.randomCovariateCovarianceCorrelationValueError());
						TextValidation.displayError(errorHTML, GlimmpseWeb.constants.randomCovariateCovarianceCorrelationValueError());
					}
					
				}
			});
		}
		else
		{
			for(int i = 2; i <= flexTableRows; i++)
			{
				TextBox textBox = (TextBox)flexTable.getWidget(i, dataList.size());
				textBox.setEnabled(true);
			}
		}
	}
	
	public  void onExit() 
	{
	    double[][] covariteMatrixData = new double[1][1];
        covariteMatrixData[1][1] = Double.parseDouble(textBox.getValue());
        NamedMatrix sigmaCovariate = new NamedMatrix();
        sigmaCovariate.setColumns(1);
        sigmaCovariate.setRows(1);
        sigmaCovariate.setDataFromArray(covariteMatrixData);
        sigmaCovariate.setName(GlimmpseWeb.constants.MATRIX_SIGMA_COVARIATE);
        studyDesignContext.setSigmaCovariate(this, sigmaCovariate);
        
        double[][] sigmaYGMatrixData = new double[flexTableRows][1];
        int column = repeatedMeasuresNodeList.size()+1;
        for(int t = 0; t < flexTableRows; t++)
        {
            TextBox tb = (TextBox) flexTable.getWidget(flexTableRows+1, column);
            sigmaYGMatrixData [flexTableRows][1] = Double.parseDouble(tb.getValue());
        }
        NamedMatrix sigmaYG = new NamedMatrix();
        sigmaYG.setColumns(1);
        sigmaYG.setRows(flexTableRows-1);
        sigmaYG.setDataFromArray(sigmaYGMatrixData);
        sigmaYG.setName(GlimmpseWeb.constants.MATRIX_SIGMA_OUTCOME_COVARIATE);
        studyDesignContext.setSigmaOutcomesCovariate(this, sigmaYG);
    }
}
