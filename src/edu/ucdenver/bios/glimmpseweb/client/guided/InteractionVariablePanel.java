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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTrendTypeEnum;

/** 
 * Sub panel to select interaction variables and edit trends
 * associated with each variable
 * 
 * @author Vijay Akula
 * @author Sarah Kreidler
 *
 */
public class InteractionVariablePanel extends Composite
{   
    // label information
    protected String label = null;

    // selection checkbox
    protected CheckBox checkBox;

    // button to allow editing of trends associated with this variable
    protected Button editTrendButton = 
            new Button(GlimmpseWeb.constants.editTrendLabel(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    showTrendPanel();
                }
            });

    // trend editing panel
    protected HorizontalPanel trendPanel = new HorizontalPanel();

    // short display of currently selected trend
    protected HorizontalPanel selectedTrendPanel = new HorizontalPanel();
    protected HTML selectedTrendHTML = 
            new HTML(GlimmpseWeb.constants.editTrendNoTrend());
    // sub panel to select a trend type
    protected EditTrendPanel editTrendPanel = null;

    /**
     * Constructor.
     * @param label display for the checkbox
     * @param numLevels number of levels for the variable associated with this
     * panel.  Used to display valid trend tests.
     * @param handler click event handler
     */
    public InteractionVariablePanel(String label, int numLevels)
    {
        this.label = label;

        // overall composite panel
        VerticalPanel panel = new VerticalPanel();

        // main layout container
        Grid grid = new Grid(1,2);

        // create the selection box for this variable
        checkBox = new CheckBox(label);
        checkBox.addClickHandler(new ClickHandler()
        {
            @Override
            public void onClick(ClickEvent event) 
            {
                boolean checked = ((CheckBox) event.getSource()).getValue();
                showTrendInformation(checked);
            }
        });

        // build the selected trend display panel
        selectedTrendPanel.add(editTrendButton);
        selectedTrendPanel.add(new HTML(GlimmpseWeb.constants.editTrendSelectedTrendPrefix()));
        selectedTrendPanel.add(selectedTrendHTML);

        // create the edit trend panel
        editTrendPanel = new EditTrendPanel(label, numLevels);
        trendPanel.add(editTrendPanel);
        editTrendPanel.addClickHandler(new ClickHandler ()
        {
            @Override
            public void onClick(ClickEvent event) 
            {
                RadioButton radioButton = (RadioButton) event.getSource();
                // update the selected trend information
                selectedTrendHTML.setText(radioButton.getHTML());
                trendPanel.setVisible(false);
            }
        });

        // layout the top panel for the checkbox and trend display
        grid.setWidget(0, 0, checkBox);
        grid.setWidget(0, 1, selectedTrendPanel);
        // layout the overall panel
        panel.add(grid);
        panel.add(trendPanel);

        // add style
        editTrendButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_BUTTON);

        // hide trend info to start
        reset();

        initWidget(panel);
    }

    /**
     * Add a handler for checkbox clicks
     * @param handler
     */
    public void addVariableClickHandler(ClickHandler handler) {
        checkBox.addClickHandler(handler);
    }

    /**
     * Add a handler for trend selection clicks
     * @param handler
     */
    public void addTrendClickHandler(ClickHandler handler) {
        editTrendPanel.addClickHandler(handler);
    }

    /**
     * Reset to default state which is unchecked
     */
    public void reset() {
        checkBox.setValue(false);
        selectedTrendPanel.setVisible(false);
        trendPanel.setVisible(false);
    }

    /**
     * Show / hide the trend information.
     * @param show
     */
    private void showTrendInformation(boolean show) {
        selectedTrendPanel.setVisible(show);
        if (!show) {
            // box has been unchecked, so reset the trend panel
            editTrendPanel.selectTrend(HypothesisTrendTypeEnum.NONE);
            selectedTrendHTML.setText(GlimmpseWeb.constants.editTrendNoTrend());
        }
    }

    /**
     * Show / hide the trend editing panel.
     * @param show
     */
    private void showTrendPanel() {
        trendPanel.setVisible(true);
    }

    /**
     * Get the selected trend.
     * @return trend type.
     */
    public HypothesisTrendTypeEnum getSelectedTrend()
    {
        return editTrendPanel.getSelectedTrend();
    }

    /**
     * Get the checkbox value.
     * @return true if checked, false if not checked.
     */
    public boolean isChecked()
    {
        return checkBox.getValue();
    }

    /**
     * Get the label for this panel
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set the value of the checkbox.
     * @param checked indicates if the box should be checked or unchecked.
     */
    public void setChecked(boolean checked) {
        checkBox.setValue(checked);
        showTrendInformation(checked);
    }

    /**
     * Set the selected trend.
     * @param trendType the trend type
     */
    public void setTrend(HypothesisTrendTypeEnum trendType) {
        editTrendPanel.selectTrend(trendType);
        selectedTrendHTML.setText(editTrendPanel.getSelectedTrendText());
    }
}
