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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.shared.ResizableMatrixChangeHandler;
import edu.ucdenver.bios.glimmpseweb.client.shared.ResizableMatrixPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.RowColumnTextBox;
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
public class UnStructuredCorrelationPanel extends Composite 
implements CovarianceBuilder, ResizableMatrixChangeHandler
{    
    // name of this covariance piece
    protected String name;
    // parent panel
    protected CovarianceSetManager manager = null;
    // indicates if this is a correlation only panel, or includes the ability
    // to edit standard deviation values.  If non-editable, all std dev
    // values are forced to 1.
    protected boolean showStandardDeviation = true;
    // panel containing vector of standard deviation values
    protected VerticalPanel standardDeviationPanel = new VerticalPanel();
    //Grid to construct the Standard Deviation Entry Text boxes
    protected FlexTable standardDeviationFlexTable = new FlexTable();	

    // error display for standard deviation list
    protected HTML errorHTML = new HTML();

    // panel containing correlation related widgets
    protected VerticalPanel correlationPanel = new VerticalPanel();
    // matrix panel for correlation matrix
    protected ResizableMatrixPanel correlationMatrix;

    /**
     * Constructor for the unStructuredCorrelationPanel class
     * @param covarianceName
     * @param stringList
     * @param integerList
     * @param showStdDev
     * @param handler
     */
    public UnStructuredCorrelationPanel(CovarianceSetManager manager,
            String covarianceName,
            List<String> labelList, List<Integer> spacingList, 
            boolean showStdDev)
    {
        // store the covariance name
        name = covarianceName;
        this.manager = manager;
        showStandardDeviation = showStdDev;
        //Instance of vertical panel to hold all the widgets created in this class
        VerticalPanel verticalPanel = new VerticalPanel();

        // build the standard deviation grid
        HTML expectedStandardDeviationText = 
            new HTML(GlimmpseWeb.constants.unstructuredCorrelationExpectedStandardDeviation());
        // construct the Standard Deviation text boxes using the specified labels
        constructStandardDeviationGrid(labelList);
        // add to the standard deviation subpanel
        standardDeviationPanel.add(expectedStandardDeviationText);
        standardDeviationPanel.add(standardDeviationFlexTable);
        standardDeviationPanel.add(errorHTML);
        
        // build the correlation sub-panel
        HTML expectedCorrelationText = new HTML();
        // TODO: add function to change text or add to constructor 
        if (showStdDev) {
            expectedCorrelationText.setText(GlimmpseWeb.constants.unstructuredCorrelationExpectedCorrelationMultivariate());
        } else {
            expectedCorrelationText.setText(GlimmpseWeb.constants.unstructuredCorrelationExpectedCorrelationRepeatedMeasures());
        }
        //calling a method to construct a Correlation matrix based on the standard deviations entered
        int size = spacingList.size();
        correlationMatrix = new ResizableMatrixPanel(size, size, false, false, true, true);
        correlationMatrix.setRowLabels(labelList);
        correlationMatrix.setColumnLabels(labelList);
        correlationMatrix.setMaxCellValue(1.0);
        correlationMatrix.setMinCellValue(-1.0);
        correlationMatrix.setCellErrorMessage(GlimmpseWeb.constants.errorInvalidCorrelation());
        correlationMatrix.setColumnLabels(labelList);
        correlationMatrix.addChangeHandler(this);
        // add to the correlation sub panel
        correlationPanel.add(expectedCorrelationText);
        correlationPanel.add(correlationMatrix);
        
        // layout the overall panel
        verticalPanel.add(standardDeviationPanel);
        verticalPanel.add(correlationPanel);

        // hide the standard deviation 
        // TODO: the showStdDev=false should only be used when we are guaranteed
        // a correlation matrix 2x2 or larger (i.e. repeated measures).  Otherwise the panel
        // will end up empty!
        standardDeviationPanel.setVisible(showStdDev);
        // only show the correlation panel when the matrix is not 1x1
        correlationPanel.setVisible(size != 1);
        
        // add styles
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        expectedStandardDeviationText.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        expectedCorrelationText.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        
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
            RowColumnTextBox textBox = new RowColumnTextBox(row, 0);
            textBox.addChangeHandler(new ChangeHandler()
            {
                @Override
                public void onChange(ChangeEvent event)
                {
                    RowColumnTextBox tb = (RowColumnTextBox)event.getSource();
                    double value = Double.NaN;
                    try
                    {
                        value = TextValidation.parseDouble(tb.getText(), 0.0, true);
                        TextValidation.displayOkay(errorHTML, "");
                    }
                    catch (Exception e)
                    {
                        tb.setText("");
                        TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidStandardDeviation());
                    }
                    notifyStandardDeviation(tb.row, value);
                }
            });
//            textBox.addChangeHandler(parent);
            standardDeviationFlexTable.setWidget(row, 0, new HTML(label));
            standardDeviationFlexTable.setWidget(row, 1, textBox);
            row++;
        }
    }

    /**
     * Indicates if all required information has been entered
     * @return true if the screen is complete
     */
    public boolean checkComplete() {
        boolean complete = true;
        if (showStandardDeviation) {
            for(int row = 0; row < standardDeviationFlexTable.getRowCount(); row++)
            {
                TextBox tb = (TextBox) standardDeviationFlexTable.getWidget(row, 1);
                String value = tb.getText();
                if (value == null || value.isEmpty()) {
                    complete = false;
                    break;
                }
            }
        }
        return complete;
    }

    /**
     * Sync the GUI view with the context
     */
    @Override
    public void syncCovariance() 
    {
        // sync the type
        manager.setType(name, CovarianceTypeEnum.UNSTRUCTURED_CORRELATION);
        // sync the standard deviation values
        if (showStandardDeviation) {
            for(int i = 0; i < standardDeviationFlexTable.getRowCount(); i++)
            {
                TextBox tb = (TextBox) standardDeviationFlexTable.getWidget(i, 1);
                double value = Double.NaN;
                try {
                    value = Double.parseDouble(tb.getValue());
                } catch (Exception e) {
                    // no action needed
                }
                manager.setStandardDeviationValue(name, i, value);
            }
        } else {
            for(int i = 0; i < standardDeviationFlexTable.getRowCount(); i++)
            {
                manager.setStandardDeviationValue(name, i, 1);
            }
        }
        // sync the actual correlation values
        NamedMatrix matrix = correlationMatrix.toNamedMatrix(name);
        double[][] data = matrix.getData().getData();
        for(int row = 0; row < matrix.getRows(); row++) {
            for(int column = 0; column <= row; column++) {
                manager.setCovarianceCellValue(name, row, column, data[row][column]);
            }
        }
    }
    
    /**
     * Load the panel from the specified covariance object
     * @param covariance covariance object
     */
    @Override
    public void loadCovariance(Covariance covariance) {
        if (covariance != null && 
                CovarianceTypeEnum.UNSTRUCTURED_CORRELATION == covariance.getType()) {
            List<StandardDeviation> stdDevList = covariance.getStandardDeviationList();
            if (stdDevList != null) {
                int row = 0;
                for(StandardDeviation stdDev : stdDevList) {
                    if (row >= standardDeviationFlexTable.getRowCount()) {
                        break;
                    }
                    TextBox tb = (TextBox) standardDeviationFlexTable.getWidget(row, 1);
                    if (stdDev.getValue() > 0) {
                        tb.setText(Double.toString(stdDev.getValue()));
                    }
                    row++;
                }
            }
            // load the data
            if (covariance.getBlob() != null) {
                correlationMatrix.loadMatrixData(covariance.getRows(), 
                        covariance.getColumns(), covariance.getBlob().getData());
            }
            manager.setComplete(name, checkComplete());
        }
    }

    /**
     * Alert the manager class of the changed standard deviation value
     * @param row
     * @param stddev
     */
    private void notifyStandardDeviation(int row, double stddev) {
        manager.setStandardDeviationValue(name, row, stddev);
        manager.setComplete(name, checkComplete());
    }
    
    /**
     * Event handler for row resize events in the covariance matrix
     */
    @Override
    public void onRowDimension(int rows) {
        // nothing to do since user can't change dimension of 
        // covariance from this screen
    }

    /**
     * Event handler for column resize events in the covariance matrix
     */
    @Override
    public void onColumnDimension(int columns) {
        // nothing to do since user can't change dimension of 
        // covariance from this screen
    }

    /**
     * Handler for changes to the resizable matrix contents
     */
    @Override
    public void onCellChange(int row, int column, double value) {
        manager.setCovarianceCellValue(name, row, column, value);
        manager.setComplete(name, checkComplete());
    }
    
}
