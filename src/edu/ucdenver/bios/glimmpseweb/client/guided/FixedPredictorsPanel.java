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

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Category;

/**
 * Fixed predictor entry screen
 *
 */
public class FixedPredictorsPanel extends WizardStepPanel
{
    private static final int MIN_CATEGORIES = 2;
    // pointer to the study design context
    StudyDesignContext studyDesignContext = (StudyDesignContext) context;

    // subpanel for mult-sample designs
    protected VerticalPanel multiSamplePanel = new VerticalPanel();
    // list box displaying predictors
    protected ListBox predictorList = new ListBox();
    protected ListBox categoryList = new ListBox();
    // text boxes for entering predictor / category values
    protected TextBox predictorTextBox = new TextBox();
    protected TextBox categoryTextBox = new TextBox();
    // control buttons for predictor / category add
    protected Button predictorAddButton = new Button(GlimmpseWeb.constants.buttonAdd());
    protected Button categoryAddButton = new Button(GlimmpseWeb.constants.buttonAdd());
    // control buttons for predictor/category deletion
    protected Button predictorDeleteButton = new Button(GlimmpseWeb.constants.buttonDelete());
    protected Button categoryDeleteButton = new Button(GlimmpseWeb.constants.buttonDelete());

    // error message display
    protected HTML errorHTML = new HTML();

    /**
     * Constructor
     * @param context the study design context
     */
    public FixedPredictorsPanel(WizardContext context)
    {
        super(context, GlimmpseWeb.constants.navItemFixedPredictors(),
                WizardStepPanelState.COMPLETE);
        VerticalPanel panel = new VerticalPanel();

        // create header/instruction text
        HTML header = new HTML(GlimmpseWeb.constants.predictorsTitle());
        HTML description = new HTML(GlimmpseWeb.constants.predictorsDescription());        

        // disable category text box and delete buttons
        categoryTextBox.setEnabled(false);
        categoryDeleteButton.setEnabled(false);
        predictorDeleteButton.setEnabled(false);

        // layout the overall panel
        buildCascadingList();
        panel.add(header);
        panel.add(description);
        panel.add(multiSamplePanel);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
    }    

    private void buildCascadingList()
    {    
        VerticalPanel panel = new VerticalPanel();

        predictorList.setVisibleItemCount(5);
        predictorList.setWidth("100%");
        categoryList.setVisibleItemCount(5);
        categoryList.setWidth("100%");

        // add callbacks
        // hitting enter in the predictor text box adds the name to the predictor list
        predictorTextBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event)
            {
                try {
                    String value = TextValidation.parseString(predictorTextBox.getText().trim());
                    if (inList(predictorList, value)) {
                        TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorNonUniqueValue());
                    } else {
                        addPredictor(value);
                        int selectedIndex = predictorList.getItemCount()-1;
                        predictorList.setSelectedIndex(selectedIndex);
                        selectPredictor(selectedIndex);
                        TextValidation.displayOkay(errorHTML, "");
                    }
                } catch (Exception e) {
                    TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidString());
                }
                predictorTextBox.setText("");
                predictorTextBox.setFocus(true);
            }
        });
        // clicking the name of a predictor in the predictor list
        // will select the predictor and populate the categories list
        predictorList.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event)
            {
                selectPredictor(predictorList.getSelectedIndex());
            }
        });
        // clicking delete removes the predictor and updates the context
        predictorDeleteButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                int selectedIndex = predictorList.getSelectedIndex();
                if (selectedIndex != -1)
                {
                    deletePredictor(predictorList.getItemText(selectedIndex));
                    predictorList.removeItem(selectedIndex);
                    categoryList.clear();
                }
            }
        });
        // hitting enter in the category box adds the category to the list
        categoryTextBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event)
            {
                try {
                    String predictor = predictorList.getItemText(predictorList.getSelectedIndex());
                    String value = TextValidation.parseString(categoryTextBox.getText().trim());
                    if (inList(categoryList, value)) {
                        TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorNonUniqueValue());
                    } else {
                        addCategory(predictor, value);
                        TextValidation.displayOkay(errorHTML, "");
                    }
                } catch (Exception e) {
                    TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidString());
                }
                categoryTextBox.setText("");
                categoryTextBox.setFocus(true);
            }
        });
        // clicking add will add the category to the list and update the
        // context for the selected predictor
        categoryList.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event)
            {
                categoryDeleteButton.setEnabled((categoryList.getSelectedIndex() != -1));
            }
        });
        // clicking delete removes the category
        categoryDeleteButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event)
            {
                int predictorIndex = predictorList.getSelectedIndex();
                if (predictorIndex != -1)
                {
                    String predictor = predictorList.getItemText(predictorIndex);
                    int categoryIndex = categoryList.getSelectedIndex();
                    if (categoryIndex != -1)
                    {
                        String category = categoryList.getItemText(categoryIndex);
                        deleteCategory(predictor, category);
                        categoryList.removeItem(categoryIndex);
                    }
                }
            }
        });
        // build text entry panels
        HorizontalPanel predictorPanel = new HorizontalPanel();
        predictorPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
        VerticalPanel predictorTextContainer = new VerticalPanel();
        predictorTextContainer.add(new HTML(GlimmpseWeb.constants.predictorsTableColumn()));
        predictorTextContainer.add(predictorTextBox);
        predictorPanel.add(predictorTextContainer);
        predictorPanel.add(predictorAddButton);
        predictorPanel.add(predictorDeleteButton);

        HorizontalPanel categoryPanel = new HorizontalPanel();
        categoryPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
        VerticalPanel categoryTextContainer = new VerticalPanel();
        categoryTextContainer.add(new HTML(GlimmpseWeb.constants.categoriesTableColumn()));
        categoryTextContainer.add(categoryTextBox);
        categoryPanel.add(categoryTextContainer);
        categoryPanel.add(categoryAddButton);
        categoryPanel.add(categoryDeleteButton);

        // layout the panels
        Grid grid = new Grid(2,2);
        grid.setWidget(0, 0, predictorPanel);
        grid.setWidget(0, 1, categoryPanel);
        grid.setWidget(1, 0, predictorList);
        grid.setWidget(1, 1, categoryList);
        panel.add(grid);
        panel.add(errorHTML);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_PANEL);
        predictorAddButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
        predictorDeleteButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
        categoryAddButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
        categoryDeleteButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
        //grid.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST);
        grid.getRowFormatter().setStylePrimaryName(0, 
                GlimmpseConstants.STYLE_WIZARD_STEP_LIST_HEADER);

        multiSamplePanel.add(panel);
    }

    /**
     * Populate the categories list when a user selects a predictor
     * @param predictor name of the predictor
     */
    private void showCategories(String predictor)
    {
        BetweenParticipantFactor factor = 
            studyDesignContext.getBetweenParticipantFactor(predictor);
        if (factor != null) {
            List<Category> factorCategoryList = factor.getCategoryList();
            categoryList.clear();
            if (factorCategoryList != null) {
                for(Category category: factorCategoryList)
                {
                    categoryList.addItem(category.getCategory());
                }
            }
            // disable the delete categories button
            categoryDeleteButton.setEnabled(false);
        }
    }

    /**
     * Add a predictor and update the context
     * @param name name of the new predictor
     */
    private void addPredictor(String name)
    {
        predictorList.addItem(name);
        studyDesignContext.addBetweenParticipantFactor(this, name);
        checkComplete();
    }

    /**
     * Update the view to show categories for the selected predictor
     * @param selectedIndex
     */
    private void selectPredictor(int selectedIndex)
    {
        predictorDeleteButton.setEnabled((selectedIndex != -1));
        categoryTextBox.setEnabled((selectedIndex != -1));
        if (selectedIndex != -1)
        {
            String predictorName = predictorList.getItemText(selectedIndex);
            showCategories(predictorName);
        }
    }

    /**
     * Remove a predictor from the study design
     * @param name
     */
    private void deletePredictor(String name)
    {
        studyDesignContext.deleteBetweenParticipantFactor(this, name);
        checkComplete();
    }

    /**
     * Add a category to the study design for the specified predictor
     * @param predictor
     * @param category
     */
    private void addCategory(String predictor, String category)
    {
        categoryList.addItem(category);
        studyDesignContext.addBetweenParticipantFactorCategory(this, predictor, category);
        checkComplete();
    }

    /**
     * Remove a category for the specified predictor
     * @param predictor
     * @param category
     */
    private void deleteCategory(String predictor, String category)
    {
        studyDesignContext.deleteBetweenParticipantFactorCategory(this, predictor, category);
        checkComplete();
    }

    /**
     * Check if the user has entered a valid set of predictors.
     * Either no predictors, or 1+ predictors each with at least 
     * two categories
     */
    public void checkComplete()
    {
        boolean isComplete = true;
        List<BetweenParticipantFactor> factorList = 
            studyDesignContext.getStudyDesign().getBetweenParticipantFactorList();
        if (factorList != null) {
            // multi-sample - each factor must have at least two categories
            for(BetweenParticipantFactor factor: factorList) {
                List<Category> factorCategoryList = factor.getCategoryList();
                if (factorCategoryList == null || factorCategoryList.size() < MIN_CATEGORIES) {
                    isComplete = false;
                    break;
                }
            }
        }
        if (isComplete)
            changeState(WizardStepPanelState.COMPLETE);
        else
            changeState(WizardStepPanelState.INCOMPLETE);
    }

    /**
     * Clear the lists
     */
    public void reset() 
    {
        predictorList.clear();
        categoryList.clear();
        checkComplete();
    }

    /**
     * Respond to context change events. At present no action needed
     */
    @Override
    public void onWizardContextChange(WizardContextChangeEvent e)
    {
        // no action needed
    }

    /**
     * Load the between participant factor information from the context
     */
    @Override
    public void onWizardContextLoad()
    {
        loadFromContext();
    }

    /**
     * Load the predictors when
     */
    private void loadFromContext()
    {
        reset();
        List<BetweenParticipantFactor> factorList = 
            studyDesignContext.getStudyDesign().getBetweenParticipantFactorList();
        if (factorList != null) {                
            // load the lists of predictors
            for(BetweenParticipantFactor factor: factorList)
            {
                predictorList.addItem(factor.getPredictorName());
            }
            // select the first predictor
            if (predictorList.getItemCount() > 0) {
                predictorList.setSelectedIndex(0);
                selectPredictor(0);
            }
        }
        checkComplete();
    }

    /**
     * Utility function to see if the listbox contains a value
     * @param lb
     * @param value
     * @returns true if value appears in the listbox
     */
    private boolean inList(ListBox lb, String value) {
        for(int i = 0; i < lb.getItemCount(); i++) {
            if (value != null && value.equals(lb.getItemText(i))) {
                return true;
            }
        }
        return false;
    }
}
