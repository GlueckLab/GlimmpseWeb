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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.shared.ResizableMatrixPanel;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.StandardDeviation;
import edu.ucdenver.bios.webservice.common.enums.CovarianceTypeEnum;

/**
 * 
 * @author VIJAY AKULA
 * @author Sarah Kreidler
 *
 */
public class UnStructuredCorrelationPanel extends Composite implements CovarianceBuilder
{    
    // name of this covariance piece
    protected String name;
    
    //Grid to construct the Standard Deviation Entry Text boxes
    protected FlexTable standardDeviationFlexTable = new FlexTable();	

    // error display for standard deviation list
    protected HTML errorHTML = new HTML();

    // matrix panel for correlation matrix
    protected ResizableMatrixPanel correlationMatrix;

    /**
     * Constructor for the unStructuredCorrelationPanel class
     * @param stringList
     * @param integerList
     */
    public UnStructuredCorrelationPanel(String covarianceName,
            List<String> labelList, List<Integer> spacingList)
    {
        // store the covariance name
        name = covarianceName;
        
        //Instance of vertical panel to hold all the widgets created in this class
        VerticalPanel verticalPanel = new VerticalPanel();

        HTML expectedStandardDeviationText = 
            new HTML(GlimmpseWeb.constants.unstructuredCorrelationEnterExpectedStandardDeviation());
        HTML expectedCorrelationText = 
            new HTML(GlimmpseWeb.constants.unstructuredCorrelationEnterExpectedCorrelation());

        //calling a method to construct the Standard Deviation text boxes 
        //based on the input obtained form RepeatedMeasuresNode object
        constructStandardDeviationGrid(labelList);

        //calling a method to construct a Correlation matrix based on the standard deviations entered
        int size = spacingList.size();
        correlationMatrix = new ResizableMatrixPanel(size, size, false, false, true, true);
        correlationMatrix.setRowLabels(labelList);
        correlationMatrix.setColumnLabels(labelList);
        // only show the correlation when the matrix is not 1x1
        correlationMatrix.setVisible(size != 1);
        
        verticalPanel.add(expectedStandardDeviationText);
        verticalPanel.add(standardDeviationFlexTable);
        verticalPanel.add(errorHTML);
        verticalPanel.add(expectedCorrelationText);
        verticalPanel.add(correlationMatrix);

        //initilizing the vertical pane widgets which holds all the widgets of the class		
        initWidget(verticalPanel);
    }

    /**This counstructs the Standard Deviation text boxes based on the input fron
     * response list 
     * @return grid
     */
    public void constructStandardDeviationGrid(List<String> labelList)
    {
        int row = 0;
        for(String label: labelList)
        {
            TextBox textBox = new TextBox();
            textBox.addChangeHandler(new ChangeHandler()
            {
                @Override
                public void onChange(ChangeEvent event)
                {
                    TextBox tb = (TextBox)event.getSource();
                    try
                    {
                        TextValidation.parseDouble(tb.getText(), 0.0, true);
                        TextValidation.displayOkay(errorHTML, "");
                    }
                    catch (Exception e)
                    {
                        tb.setText("");
                        TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidStandardDeviation());
                    }
                }
            });
            standardDeviationFlexTable.setWidget(row, 0, new HTML(label));
            standardDeviationFlexTable.setWidget(row, 1, textBox);
            row++;
        }
    }

    /**
     * Indicates if all required information has been entered
     * @return
     */
    public boolean checkComplete() {
        boolean complete = true;
        for(int row = 0; row < standardDeviationFlexTable.getRowCount(); row++)
        {
            TextBox tb = (TextBox) standardDeviationFlexTable.getWidget(row, 1);
            String value = tb.getText();
            if (value == null || value.isEmpty()) {
                complete = false;
                break;
            }
        }
        return complete;
    }

    /**
     * Create a covariance domain object
     */
    @Override
    public Covariance getCovariance() 
    {
        Covariance covariance = new Covariance();
        covariance.setName(name);
        covariance.setType(CovarianceTypeEnum.UNSTRUCTURED_CORRELATION);
        List<StandardDeviation> sdList = new ArrayList<StandardDeviation>();
        for(int i = 0; i < standardDeviationFlexTable.getRowCount(); i++)
        {
            TextBox tb = (TextBox) standardDeviationFlexTable.getWidget(i, 1);
            String value = tb.getValue();
            StandardDeviation sd = new StandardDeviation();
            if (value != null && !value.isEmpty()) {
                sd.setValue(Double.parseDouble(tb.getValue()));
            } else {
                sd.setValue(Double.NaN);
            }
            sdList.add(sd);
        }
        covariance.setStandardDeviationList(sdList);

        NamedMatrix matrix = correlationMatrix.toNamedMatrix(name);
        covariance.setRows(matrix.getRows());
        covariance.setColumns(matrix.getColumns());
        covariance.setBlob(matrix.getData());
        return covariance;
    }
}
