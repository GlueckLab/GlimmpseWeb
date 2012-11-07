/*
 * User Interface for the GLIMMPSE Software System.  Allows
 * users to perform power, sample size, and detectable difference
 * calculations. 
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.Spacing;
import edu.ucdenver.bios.webservice.common.enums.RepeatedMeasuresDimensionType;

/**
 * Panel to enter information about repeated measures for a single
 * dimension
 * @author Vijay Akula
 * @author Sarah Kreidler
 *
 */
public class RepeatedMeasuresPanelSubPanel extends Composite {
    // index for the repeated measures type listbox
    protected static final int NUMERIC_INDEX = 0;
    protected static final int ORDINAL_INDEX = 1;
    protected static final int NOMINAL_INDEX = 2;

    // name of this dimension of repeated measures
    protected TextBox dimensionTextBox = new TextBox();

    // number of measurements across this dimension
    protected TextBox noOfMeasurementsTextBox = new TextBox();

    // indicates if the repeated measures dimension represents
    // numeric, ordinal, or categorical values
    protected ListBox typeListBox = new ListBox(false);

    // spacing flexTable
    protected Grid spacingGrid = new Grid(2,2);
    protected HTML spacingHTML = 
            new HTML(GlimmpseWeb.constants.repeatedMeasuresNodePanelSpacingLabel());
    protected FlexTable spacingFlexTable = new FlexTable();
    // resets the spacing to even spacing
    protected Button spacingResetButton = new Button(
            GlimmpseWeb.constants.repeatedMeasuresNodePanelEqualSpacingButton(), 
            new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) 
                {
                    setEqualSpacing();
                    notifyParent();
                }
            });

    //html widget to display the error message
    protected HTML errorHTML = new HTML();

    // pointer to the parent panel to manage changes
    protected RepeatedMeasuresPanel parent = null;
    // current depth in the clustering tree
    protected int depth = -1;

    // cache the spacing list
    ArrayList<Spacing> spacingList = new ArrayList<Spacing>();

    /**
     * Helper class for spacing text boxes
     * @author Sarah
     *
     */
    private class SpacingTextBox extends TextBox {
        int index = -1;
        public SpacingTextBox(String text, int index) {
            this.index = index;
            setText(text);
            addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {                
                    SpacingTextBox tb = (SpacingTextBox)event.getSource();
                    int spacingValue = tb.getIndex()+1;
                    String value = tb.getValue();
                    try
                    {
                        spacingValue = TextValidation.parseInteger(value, 1, true);
                        TextValidation.displayOkay(errorHTML, "");
                    }
                    catch (Exception e)
                    {
                        TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidPositiveNumber());
                        tb.setText(Double.toString(spacingValue));
                    }
                    updateSpacingList(tb.getIndex(), spacingValue);
                    notifyParent();   
                }
            });
            // add style
            setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TEXTBOX);
            addStyleDependentName(GlimmpseConstants.STYLE_SHORT);
        }

        public int getIndex() {
            return index;
        }
    }

    /**
     * Constructor.
     * @param dependentStyleName optional parameter to allow different
     * styles for subpanels depending on depth
     */
    public RepeatedMeasuresPanelSubPanel(String dependentStyleName,
            int depth, RepeatedMeasuresPanel parent)
    {
        this.depth = depth;
        this.parent = parent;
        VerticalPanel panel = new VerticalPanel();

        Grid grid = new Grid(3,2);

        // dimension name row
        grid.setWidget(0, 0, 
                new HTML(GlimmpseWeb.constants.repeatedMeasuresNodePanelDimensionLabel()));
        grid.setWidget(0, 1, dimensionTextBox);
        dimensionTextBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                TextBox tb = (TextBox)event.getSource();
                String value = tb.getValue();
                try
                {
                    TextValidation.parseString(value);
                    TextValidation.displayOkay(errorHTML, "");
                }
                catch (Exception e)
                {
                    TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidString());
                    tb.setText("");
                }
                notifyParent();
            }
        });

        // type row
        grid.setWidget(1, 0, 
                new HTML(GlimmpseWeb.constants.repeatedMeasuresNodePanelTypeLabel()));
        grid.setWidget(1, 1, typeListBox);
        // fill list box
        typeListBox.addItem(GlimmpseWeb.constants.repeatedMeasuresNodePanelNumericLabel());
        typeListBox.addItem(GlimmpseWeb.constants.repeatedMeasuresNodePanelOrdinalLabel());
        typeListBox.addItem(GlimmpseWeb.constants.repeatedMeasuresNodePanelCategoricalLabel());
        typeListBox.setItemSelected(0, true);
        typeListBox.addChangeHandler(new ChangeHandler(){
            @Override
            public void onChange(ChangeEvent event) {
                ListBox lb = (ListBox)event.getSource();
                int index = lb.getSelectedIndex();
                showSpacingBar(index == NUMERIC_INDEX);
                if (index != NUMERIC_INDEX) {
                    setEqualSpacing();
                }
                notifyParent();
            }
        });

        // number of measurements row
        grid.setWidget(2, 0, 
                new HTML(GlimmpseWeb.constants.repeatedMeasuresNodePanelNumMeasurementsLabel()));
        grid.setWidget(2, 1, noOfMeasurementsTextBox);
        noOfMeasurementsTextBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) 
            {
                TextBox tb = (TextBox)event.getSource();
                String value = tb.getValue();
                try
                {
                    int numMeasurements = TextValidation.parseInteger(value, 1, true);
                    TextValidation.displayOkay(errorHTML, "");
                    updateSpacingBar(numMeasurements, null);
                }
                catch (Exception e)
                {
                    TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidNumRepeatedMeasures());
                    tb.setText("");
                }
                notifyParent();
            }
        });

        // spacing panel
        spacingGrid.setWidget(0, 0, spacingHTML);
        spacingGrid.setWidget(0, 1, spacingFlexTable);
        spacingGrid.setWidget(1, 0, spacingResetButton);
        showSpacingBar(false);

        // layout overall panel
        panel.add(grid);
        panel.add(spacingGrid);
        panel.add(errorHTML);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TREE_NODE);
        if (dependentStyleName != null && !dependentStyleName.isEmpty()) {
            panel.addStyleDependentName(dependentStyleName);
        }
        spacingResetButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);
        /*Initializing Widget*/
        initWidget(panel);

    }

    /**
     * Synchronize the array list with the text boxes
     * @param index
     * @param spacingValue
     */
    public void updateSpacingList(int index, int spacingValue) {
        if (index >= 0 && index < spacingList.size()) {
            spacingList.get(index).setValue(spacingValue);
        }
    }

    /**
     * Reset the spacing list to equal spacing
     */
    public void setEqualSpacing()
    {
        for(int c = 0; c < spacingFlexTable.getCellCount(0); c++) {
            TextBox tb = (TextBox) spacingFlexTable.getWidget(0, c);
            tb.setText(Integer.toString(c+1));
            spacingList.get(c).setValue(c+1);
        }
    }

    /** 
     * Clear the panel
     */
    public void reset() {
        dimensionTextBox.setText("");
        noOfMeasurementsTextBox.setText("");
        typeListBox.setSelectedIndex(0);
        spacingFlexTable.removeAllRows();
    }

    /**
     * Reset the size of the spacing bar
     * @param numMeasurements
     */
    private void updateSpacingBar(int numMeasurements, List<Spacing> inputSpacingList)
    {
        spacingFlexTable.removeAllRows();
        spacingList.clear();
        if (inputSpacingList == null || inputSpacingList.size() != numMeasurements) {
            for(int i = 0; i < numMeasurements; i++) {
                spacingList.add(new Spacing(i+1));
                spacingFlexTable.setWidget(0, i , new SpacingTextBox(Integer.toString(i+1), i));
            }
        } else {
            int col = 0;
            for(Spacing spacingValue: inputSpacingList) {
                String spacingStr = "";
                // the user can save incomplete information - this will be saved as MIN_VALUE
                // if the text box was empty at time of save.
                if (spacingValue.getValue() != Integer.MIN_VALUE) {
                    spacingStr = Integer.toString(spacingValue.getValue());
                }
                spacingFlexTable.setWidget(0, col ,
                        new SpacingTextBox(spacingStr, col));
                spacingList.add(new Spacing(spacingValue.getValue()));
                col++;
            }
        }
        showSpacingBar(typeListBox.getSelectedIndex() == NUMERIC_INDEX);
    }

    /**
     * Hide the spacing bar
     */
    private void showSpacingBar(boolean show)
    {
        spacingGrid.setVisible(show);
    }

    /**
     * Check if the user has entered all required information
     * @return true if complete, false if incomplete
     */
    public boolean checkComplete()
    {
        String checkDimension = dimensionTextBox.getText();
        String checkNumber = noOfMeasurementsTextBox.getText();
        boolean validNumeric = true;
        boolean validTextBoxes = (checkDimension != null && !checkDimension.isEmpty()
                && checkNumber != null && !checkNumber.isEmpty());

        if (typeListBox.getSelectedIndex() == NUMERIC_INDEX) {
            if (spacingFlexTable.getRowCount() > 0 && spacingFlexTable.getCellCount(0) > 0) {
                for(int c = 0; c < spacingFlexTable.getCellCount(0); c++) {
                    TextBox tb = (TextBox) spacingFlexTable.getWidget(0, c);
                    if (tb.getText() == null || tb.getText().isEmpty()) {
                        validNumeric = false;
                        break;
                    }
                }
            } else {
                validNumeric = false;
            }
        }
        return (validTextBoxes && validNumeric);
    }

    /**
     * Fill in the panel from the repeated measures node
     * @param repeatedMeasuresNode
     */
    public void loadFromRepeatedMeasuresNode(
            RepeatedMeasuresNode repeatedMeasuresNode) {
        reset();
        if (repeatedMeasuresNode != null) {
            // !need to set type first!
            if (repeatedMeasuresNode.getRepeatedMeasuresDimensionType() != null) {
                switch (repeatedMeasuresNode.getRepeatedMeasuresDimensionType()) {
                case NUMERICAL:
                    typeListBox.setSelectedIndex(NUMERIC_INDEX);
                    break;
                case ORDINAL:
                    typeListBox.setSelectedIndex(ORDINAL_INDEX);
                    break;
                case CATEGORICAL:
                    typeListBox.setSelectedIndex(NOMINAL_INDEX);
                    break;
                }
            }
            // set the dimension name
            if (repeatedMeasuresNode.getDimension() != null) {
                dimensionTextBox.setText(repeatedMeasuresNode.getDimension());
            }
            // set the number of measurements and update the spacing bar
            if (repeatedMeasuresNode.getNumberOfMeasurements() != null) {
                noOfMeasurementsTextBox.setText(Integer.toString(repeatedMeasuresNode.getNumberOfMeasurements()));
                updateSpacingBar(repeatedMeasuresNode.getNumberOfMeasurements(),
                        repeatedMeasuresNode.getSpacingList());
            }
        }
    }

    /**
     * get the dimension label
     * @return
     */
    public String getDimensionName() {
        return dimensionTextBox.getText();
    }

    /**
     * Get the spacing list
     * @return
     */
    public List<Spacing> getSpacingList() {
        return spacingList;
    }

    /**
     * get the repeated measures type
     * @return
     */
    public RepeatedMeasuresDimensionType getType() {
        switch (typeListBox.getSelectedIndex()) {
        case NUMERIC_INDEX:
            return RepeatedMeasuresDimensionType.NUMERICAL;
        case ORDINAL_INDEX:
            return RepeatedMeasuresDimensionType.ORDINAL;
        case NOMINAL_INDEX:
            return RepeatedMeasuresDimensionType.CATEGORICAL;
        default:
            return null;
        }
    }

    /**
     * get the number of measurements
     * @return
     */
    public int getNumberOfMeausrements() {
        String numMeasurements = noOfMeasurementsTextBox.getText();
        if (numMeasurements != null && !numMeasurements.isEmpty()) {
            return Integer.parseInt(numMeasurements);
        } else {
            return -1;
        }
    }

    /**
     * Return the tree depth of this node
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Notify the parent panel that a change has occurred
     */
    private void notifyParent() {
        parent.updateRepeatedMeasuresNode(this);
    }
}
