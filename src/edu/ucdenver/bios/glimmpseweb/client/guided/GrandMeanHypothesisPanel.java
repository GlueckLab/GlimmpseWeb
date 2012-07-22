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
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.Hypothesis;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTypeEnum;

/**
 * Panel to build one sample hypothesis comparing against
 * a known mean
 * @author Sarah Kreidler
 *
 */
public class GrandMeanHypothesisPanel extends Composite  
implements HypothesisBuilder {
    
    // column indices for the flex table
    private static final int LABEL_COLUMN = 0;
    private static final int TEXTBOX_COLUMN = 1;

    // parent panel, change handler
    protected ChangeHandler parent = null;
        
    // table of theta null inputs
    protected FlexTable comparisonMeanTable = new FlexTable();
    
    //html widget to display the error message
    protected HTML errorHTML = new HTML();
    
    /**
     * Constructor
     * @param studyDesignContext
     */
    public GrandMeanHypothesisPanel(ChangeHandler handler)
    {
        this.parent = handler;

        VerticalPanel panel = new VerticalPanel();
        HTML text = new HTML(GlimmpseWeb.constants.grandMeanHypothesisPanelText());

        //Style Sheets
        text.setStyleName(
                GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        
        //Add individual widgets to vertical panel
        panel.add(text);
        panel.add(comparisonMeanTable);
        panel.add(errorHTML);

        initWidget(panel);
    }
    
    /**
     * Returns true if the user has selected sufficient information
     */
    @Override
    public boolean checkComplete() {
        for(int i = 0; i < comparisonMeanTable.getRowCount(); i++) {
            TextBox tb = (TextBox) comparisonMeanTable.getWidget(i, TEXTBOX_COLUMN);
            if (tb.getValue() == null || tb.getValue().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Load the responses from the context
     */
    public void loadResponseList(List<ResponseNode> responseList) {
        comparisonMeanTable.removeAllRows();
        if (responseList != null) {
            int row = 0;
            for(ResponseNode response: responseList) {
                comparisonMeanTable.setWidget(row, LABEL_COLUMN, new HTML(response.getName()));
                comparisonMeanTable.setWidget(row, TEXTBOX_COLUMN, createTextBox());
                row++;
            }
        }
    }
    
    /**
     * Load the hypothesis information.  Should be called after loadResponseList.
     */
    public void loadHypothesis(NamedMatrix thetaNull) {
        if (thetaNull != null) {
            if (thetaNull.getColumns() == comparisonMeanTable.getRowCount() &&
                    thetaNull.getRows() == 1 &&
                    thetaNull.getData() != null) {
                double[][] data =  thetaNull.getData().getData();
                for(int col = 0; col < thetaNull.getColumns(); col++) {
                    // the visual presentation of theta null here is transposed, 
                    // hence the swapping of row/column indices
                    TextBox tb = 
                        (TextBox) comparisonMeanTable.getWidget(col, 
                                TEXTBOX_COLUMN);
                    tb.setText(Double.toString(data[0][col]));
                }
            }
        }
    }
    
    /**
     * Add an entry row to the flex table
     */
    private TextBox createTextBox() {
        TextBox tb = new TextBox();
        tb.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                // make sure the user entered a number
                TextBox tb = (TextBox)event.getSource();
                try
                {
                    Double.parseDouble(tb.getValue());
                    TextValidation.displayOkay(errorHTML, "");
                }
                catch (Exception e)
                {
                    TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidMean());
                    tb.setText("");
                }
            }
        });
        tb.addChangeHandler(parent);
        return tb;
    }
    
    /**
     * Create a grand mean hypothesis object
     */
    @Override
    public Hypothesis buildHypothesis() {
        Hypothesis hypothesis = new Hypothesis();
        hypothesis.setType(HypothesisTypeEnum.GRAND_MEAN);
        return hypothesis;
    }

    /**
     * Create a theta null matrix
     */
    @Override
    public NamedMatrix buildThetaNull() {
        NamedMatrix thetaNull = new NamedMatrix();
        thetaNull.setName(GlimmpseConstants.MATRIX_THETA);
        thetaNull.setRows(1);
        thetaNull.setColumns(comparisonMeanTable.getRowCount());
        double[][] data = new double[1][comparisonMeanTable.getRowCount()];
        for(int row = 0; row < comparisonMeanTable.getRowCount(); row++) {
            TextBox tb = (TextBox) comparisonMeanTable.getWidget(row, TEXTBOX_COLUMN);
            String value = tb.getValue();
            if (value != null && !value.isEmpty()) {
                data[0][row] = Double.parseDouble(value);
            } else {
                data[0][row] = Double.NaN;
            }
        }
        thetaNull.setDataFromArray(data);
        return thetaNull;
        
    }
}
