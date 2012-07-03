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

/**
 * Lear correlation structure entry panel
 * @author VIJAY AKULA
 * @author Sarah Kreidler
 *
 */
public class StructuredCorrelationPanel extends Composite implements CovarianceBuilder
{
    // name of this covariance component
    protected String name;
    
    // input boxes for Lear parameters
    protected TextBox standardDeviationTextBox = new TextBox();
    protected TextBox strongestCorrelationTextBox = new TextBox();
    protected TextBox rateOfDecayOfCorrelationTextBox = new TextBox();
    // values from the above list boxes
    protected double standardDeviation = Double.NaN;
    protected double strongestCorrelation = Double.NaN;
    protected double rateOfDecay = Double.NaN;
    // error html
    protected HTML errorHTML = new HTML();
    // display matrix
    protected ResizableMatrixPanel resizableMatrix;
    // lear correlation calculator
    LearCorrelation learCorrelation = null;
    // standard deviation list
    List<StandardDeviation> sdList = new ArrayList<StandardDeviation>();

    /**
     * Constructor 
     */
    public StructuredCorrelationPanel(String name, List<String> labelList, List<Integer> spacingList)
    {
        VerticalPanel verticalPanel = new VerticalPanel();
        this.name = name;
        // create a lear calculator for the given spacing
        if (spacingList.size() > 1) {
            learCorrelation = new LearCorrelation(spacingList);
        }

        HTML header = new HTML(GlimmpseWeb.constants.structuredCorrelationPanelHeader());
        HTML description = new HTML(GlimmpseWeb.constants.structuredCorrelationPanelText());

        HtmlTextWithExplanationPanel standardDeviation = 
                new HtmlTextWithExplanationPanel(GlimmpseWeb.constants.standardDeviationLabel(),
                GlimmpseWeb.constants.standardDeviationExplinationHeader(), 
                GlimmpseWeb.constants.standardDeviationExplinationText());
        HtmlTextWithExplanationPanel strongestCorrelation = 
                new HtmlTextWithExplanationPanel(GlimmpseWeb.constants.strongestCorrelationLabel(),
                GlimmpseWeb.constants.strongestCorrelationExplinationHeader(), 
                GlimmpseWeb.constants.strongestCorrelationExplinationText());
        HtmlTextWithExplanationPanel rateOfDecayOfCorrelation = 
                new HtmlTextWithExplanationPanel(GlimmpseWeb.constants.rateOfDecayOfCorrelationLabel(),
                GlimmpseWeb.constants.rateOfDecayOfCorrelationExplinationHeader(), 
                GlimmpseWeb.constants.rateOfDecayOfCorrelationExplinationText());

        Grid grid = new Grid(3,2);

        standardDeviationTextBox.addChangeHandler(new ChangeHandler()
        {
            @Override
            public void onChange(ChangeEvent event) 
            {
                TextBox tb = (TextBox) event.getSource();
                try
                {
                    Double value = TextValidation.parseDouble(tb.getText(),0,true,false);
                    setStandardDeviation(value);
                    populateMatrix();
                    TextValidation.displayOkay(errorHTML, "");
                }
                catch(Exception e)
                {
                    TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidStandardDeviation());
                    tb.setText("");
                    setStandardDeviation(Double.NaN);
                }
            }

        });

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

            }
        });

        rateOfDecayOfCorrelationTextBox.addChangeHandler(new ChangeHandler()
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
            }
        });

        grid.setWidget(0, 0, standardDeviation);
        grid.setWidget(0, 1, standardDeviationTextBox);
        grid.setWidget(1, 0, strongestCorrelation);
        grid.setWidget(1, 1, strongestCorrelationTextBox);
        grid.setWidget(2, 0, rateOfDecayOfCorrelation);
        grid.setWidget(2, 1, rateOfDecayOfCorrelationTextBox);


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

    private void setStandardDeviation(double value) {
        standardDeviation = value;
    }

    private void setStrongestCorrelation(double value) {
        strongestCorrelation = value;
    }

    private void setRateOfDecay(double value) {
        rateOfDecay = value;
    }

    public boolean checkComplete() {
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

    public Covariance getCovariance()
    {
        Covariance covariance = new Covariance();
        covariance.setName(name);
        
        if (checkComplete()) {
            covariance = new Covariance();
            sdList.clear();
            sdList.add(new StandardDeviation(standardDeviation));
            covariance.setStandardDeviationList(sdList);
            covariance.setDelta(rateOfDecay);
            covariance.setRho(strongestCorrelation);
        }
        return covariance;
    }

}
