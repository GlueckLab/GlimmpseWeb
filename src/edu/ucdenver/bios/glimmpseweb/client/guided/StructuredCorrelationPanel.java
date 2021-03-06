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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.LearCorrelation;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.shared.HtmlTextWithExplanationPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.ResizableMatrixPanel;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.StandardDeviation;
import edu.ucdenver.bios.webservice.common.enums.CovarianceTypeEnum;

/**
 * Lear correlation structure entry panel
 * @author VIJAY AKULA
 * @author Sarah Kreidler
 *
 */
public class StructuredCorrelationPanel extends Composite 
implements CovarianceBuilder
{
    // name of this covariance component
    protected String name;
    // parent panel
    protected CovarianceSetManager manager = null;
    
    // input boxes for Lear parameters
    protected TextBox strongestCorrelationTextBox = new TextBox();
    protected TextBox rateOfDecayTextBox = new TextBox();
    // values from the above list boxes
    protected double standardDeviation = 1;
    protected double strongestCorrelation = Double.NaN;
    protected double rateOfDecay = Double.NaN;
    // error html
    protected HTML errorHTML = new HTML();
    // display matrix
    protected ResizableMatrixPanel resizableMatrix;
    // lear correlation calculator
    protected LearCorrelation learCorrelation = null;
    // cache the covariance size
    protected int size = 0;
    // standard deviation list
    protected List<StandardDeviation> sdList = new ArrayList<StandardDeviation>();

    /**
     * Constructor 
     */
    public StructuredCorrelationPanel(CovarianceSetManager manager,
            String name, List<String> labelList, List<Integer> spacingList)
    {
        VerticalPanel verticalPanel = new VerticalPanel();
        this.name = name;
        this.manager = manager;
        // create a lear calculator for the given spacing
        if (spacingList.size() > 1) {
            learCorrelation = new LearCorrelation(spacingList);
        }

        HTML header = new HTML(GlimmpseWeb.constants.structuredCorrelationPanelHeader());
        HTML description = new HTML(GlimmpseWeb.constants.structuredCorrelationPanelText());

//        HtmlTextWithExplanationPanel standardDeviation = 
//                new HtmlTextWithExplanationPanel(GlimmpseWeb.constants.standardDeviationLabel(),
//                GlimmpseWeb.constants.standardDeviationExplinationHeader(), 
//                GlimmpseWeb.constants.standardDeviationExplinationText());
        HtmlTextWithExplanationPanel strongestCorrelation = 
                new HtmlTextWithExplanationPanel(GlimmpseWeb.constants.strongestCorrelationLabel(),
                GlimmpseWeb.constants.strongestCorrelationExplinationHeader(), 
                GlimmpseWeb.constants.strongestCorrelationExplinationText());
        HtmlTextWithExplanationPanel rateOfDecayOfCorrelation = 
                new HtmlTextWithExplanationPanel(GlimmpseWeb.constants.rateOfDecayOfCorrelationLabel(),
                GlimmpseWeb.constants.rateOfDecayOfCorrelationExplinationHeader(), 
                GlimmpseWeb.constants.rateOfDecayOfCorrelationExplinationText());

        Grid grid = new Grid(2,2);

        
        strongestCorrelationTextBox.addChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(ChangeEvent event) 
            {
                TextBox tb = (TextBox) event.getSource();
                try
                {
                    Double strongestCorrelationValue =TextValidation.parseDouble(tb.getText(), -1.0, 1.0, true);
                    setStrongestCorrelation(strongestCorrelationValue);
                    populateMatrix();
                    TextValidation.displayOkay(errorHTML, "");
                }
                catch(Exception e)
                {
                    TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidCorrelation());
                    tb.setText("");
                    setStrongestCorrelation(Double.NaN);
                }
                notifyLearParameters();
            }
        });
        
        rateOfDecayTextBox.addChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(ChangeEvent event) 
            {
                TextBox tb = (TextBox) event.getSource();
                try
                {

                    Double rateOfDecayvalue = TextValidation.parseDouble(tb.getText(),
                            0,true);
                    setRateOfDecay(rateOfDecayvalue);
                    populateMatrix();
                    TextValidation.displayOkay(errorHTML, "");
                }
                catch(Exception e)
                {
                    TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidPositiveNumber());
                    tb.setText("");
                    setRateOfDecay(Double.NaN);
                }
                notifyLearParameters();
            }
        });
        
        grid.setWidget(0, 0, strongestCorrelation);
        grid.setWidget(0, 1, strongestCorrelationTextBox);
        grid.setWidget(1, 0, rateOfDecayOfCorrelation);
        grid.setWidget(1, 1, rateOfDecayTextBox);


        int size = spacingList.size();      
        resizableMatrix = new ResizableMatrixPanel(size, size, false, false, false, true);
        resizableMatrix.setRowLabels(labelList);
        resizableMatrix.setColumnLabels(labelList);

        verticalPanel.add(header);
        verticalPanel.add(description);
        verticalPanel.add(grid);
        verticalPanel.add(errorHTML);
        verticalPanel.add(resizableMatrix);

        // set style
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);

        initWidget(verticalPanel);
    }

    private void setStrongestCorrelation(double value) {
        strongestCorrelation = value;
    }

    private void setRateOfDecay(double value) {
        rateOfDecay = value;
    }

    private boolean checkComplete() {
        return (!Double.isNaN(standardDeviation) &&
                !Double.isNaN(strongestCorrelation) &&
                !Double.isNaN(rateOfDecay));
    }

    private void populateMatrix()
    {
        int rows = resizableMatrix.getRowDimension();
        int columns = resizableMatrix.getColumnDimension();

        if(checkComplete())
        {
            for(int r = 0; r < rows; r++)
            {
                for(int c = 0; c < columns; c++)
                {
                    if(r != c)
                    {
                        Double value = learCorrelation.getRho(r, c, strongestCorrelation, rateOfDecay);
                        resizableMatrix.setCellValue(r, c, value.toString());
                    }
                    else
                    {
                        resizableMatrix.setCellValue(r, c, "1.0");
                    }
                }
            }
        } else {
            for(int r = 0; r < rows; r++)
            {
                for(int c = 0; c < columns; c++)
                {
                    if(r != c)
                    {
                        resizableMatrix.setCellValue(r, c, "0");
                    }
                    else
                    {
                        resizableMatrix.setCellValue(r, c, "1");
                    }
                }
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
                CovarianceTypeEnum.LEAR_CORRELATION == covariance.getType()) {
            size = covariance.getRows();
            if (covariance.getDelta() >= 0) {
                rateOfDecay = covariance.getDelta();
                rateOfDecayTextBox.setText(Double.toString(rateOfDecay));
            }
            if (covariance.getRho() >= 0) {
                strongestCorrelation = covariance.getRho();
                strongestCorrelationTextBox.setText(Double.toString(strongestCorrelation));
            }
            populateMatrix();
            manager.setComplete(name, checkComplete());
        }
    }
    
    /**
     * Notify the covariance manager of the change in lear parameters
     */
    private void notifyLearParameters() {
        manager.setLearParameters(name, strongestCorrelation, rateOfDecay);
        manager.setComplete(name, checkComplete());
    }

    /**
     * Sync the current GUI view with the context
     */
    @Override
    public void syncCovariance() {
        manager.setType(name, CovarianceTypeEnum.LEAR_CORRELATION);
        // reset the std deviation values to 1
        for(int i = 0; i < size; i++) {
            manager.setStandardDeviationValue(name, i, 1);
        }
        // clear the covariance matrix - this is regenerated based on the Lear params
        for(int row = 0; row < size; row++) {
            for(int col = 0; col <= row; col++) {
                manager.setCovarianceCellValue(name, row, col, 0);
            }
        }
        // sync the lear parameters
        notifyLearParameters();
    }

}
