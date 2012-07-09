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
import java.util.HashMap;
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
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
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
    private static final String RADIO_GROUP = "fixedPredictorSampleGroup";
    private static final int MIN_CATEGORIES = 2;
	// pointer to the study design context
	StudyDesignContext studyDesignContext = (StudyDesignContext) context;
	
	// radio buttons indicating whether this is a single sample or multi sample design
	protected RadioButton oneSampleRadioButton = new RadioButton(RADIO_GROUP,
	            GlimmpseWeb.constants.predictorsOneSampleButton());
	protected RadioButton multiSampleRadioButton = new RadioButton(RADIO_GROUP,
	        GlimmpseWeb.constants.predictorsMultiSampleButton());
	
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
    
    protected HashMap<String,ArrayList<String>> predictorCategoryMap = 
    	new HashMap<String,ArrayList<String>>();
    
    public FixedPredictorsPanel(WizardContext context)
    {
    	super(context, GlimmpseWeb.constants.navItemFixedPredictors(),
    	        WizardStepPanelState.INCOMPLETE);
        VerticalPanel panel = new VerticalPanel();
        
        // create header/instruction text
        HTML header = new HTML(GlimmpseWeb.constants.predictorsTitle());
        HTML description = new HTML(GlimmpseWeb.constants.predictorsDescription());        


        // disable category text box and delete buttons
        categoryTextBox.setEnabled(false);
        categoryDeleteButton.setEnabled(false);
        predictorDeleteButton.setEnabled(false);

        // add handlers
        oneSampleRadioButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showMultiSamplePanel(false);
                checkComplete();
            }
        });
        // add handlers
        multiSampleRadioButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                showMultiSamplePanel(true);
                checkComplete();
            }
        });
        // hide multi-sample panel by default
        oneSampleRadioButton.setValue(false);
        multiSamplePanel.setVisible(false);
        
        // layout the overall panel
        buildCascadingList();
        panel.add(header);
        panel.add(description);
        panel.add(oneSampleRadioButton);
        panel.add(multiSampleRadioButton);
        panel.add(multiSamplePanel);
        
        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);

        initWidget(panel);
    }    
        
    private void buildCascadingList()
    {
        HTML description = new HTML(GlimmpseWeb.constants.predictorsMultiSampleDescription());       
        
        VerticalPanel panel = new VerticalPanel();
        
    	predictorList.setVisibleItemCount(5);
    	predictorList.setWidth("100%");
    	categoryList.setVisibleItemCount(5);
    	categoryList.setWidth("100%");
    	
    	// add callbacks
    	predictorTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				String value = predictorTextBox.getText();
				if (!value.isEmpty())
				{
					addPredictor(value);
					int selectedIndex = predictorList.getItemCount()-1;
					predictorList.setSelectedIndex(selectedIndex);
					selectPredictor(selectedIndex);
					predictorTextBox.setText("");
				}
			}
    	});
    	predictorList.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				selectPredictor(predictorList.getSelectedIndex());
			}
    	});
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
    	categoryTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				String value = categoryTextBox.getText();
				if (!value.isEmpty())
				{
					String predictor = predictorList.getItemText(predictorList.getSelectedIndex());
					if (predictor != null && !predictor.isEmpty())
					{
						addCategory(predictor, value);
					}
					categoryTextBox.setText("");
				}
			}
    	});
    	categoryList.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event)
			{
				categoryDeleteButton.setEnabled((categoryList.getSelectedIndex() != -1));
			}
    	});
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
    	
    	// set style
    	panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_PANEL);
    	predictorAddButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
    	predictorDeleteButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
    	categoryAddButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
    	categoryDeleteButton.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST_BUTTON);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
    	//grid.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_LIST);
        grid.getRowFormatter().setStylePrimaryName(0, 
        		GlimmpseConstants.STYLE_WIZARD_STEP_LIST_HEADER);
    	
        multiSamplePanel.add(description);
        multiSamplePanel.add(panel);
    }
    
    private void showMultiSamplePanel(boolean show) {
        multiSamplePanel.setVisible(show);
        changed = true;
    }
    
    
    private void showCategories(String predictor)
    {
    	ArrayList<String> categories = predictorCategoryMap.get(predictor);
		categoryList.clear();
    	for(String category: categories)
    	{
    		categoryList.addItem(category);
    	}
    	// disable the delete categories button
    	categoryDeleteButton.setEnabled(false);
    }
    
    private void addPredictor(String name)
    {
    	if (!predictorCategoryMap.containsKey(name))
    	{
    		predictorList.addItem(name);
    		predictorCategoryMap.put(name, new ArrayList<String>());
    	}
        changed = true;
    }
    
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
    
    private void deletePredictor(String name)
    {
    	predictorCategoryMap.remove(name);
        changed = true;
    }
    
    private void addCategory(String predictor, String category)
    {
    	ArrayList<String> categories = predictorCategoryMap.get(predictor);
    	if (categories != null) 
    	{
    		categories.add(category);
			categoryList.addItem(category);
    	}
        changed = true;
		checkComplete();
    }
    
    private void deleteCategory(String predictor, String category)
    {
    	ArrayList<String> categories = predictorCategoryMap.get(predictor);
    	if (categories != null)  
    	{
    		categories.remove(category);
    	}
        changed = true;
		checkComplete();
    }
    
    public void checkComplete()
    {
    	boolean isComplete = false;
    	if (multiSampleRadioButton.getValue()) {
    	    isComplete = !predictorCategoryMap.isEmpty();
    	    for(ArrayList<String> categories: predictorCategoryMap.values())
    	    {
    	        if (categories.size() < MIN_CATEGORIES) 
    	        {
    	            isComplete = false;
    	            break;
    	        }
    	    }
    	} else if (oneSampleRadioButton.getValue()) {
    	    // one sample
    	    isComplete = true;
    	}
    	if (isComplete)
    		changeState(WizardStepPanelState.COMPLETE);
    	else
    		changeState(WizardStepPanelState.INCOMPLETE);
    }
    
    public void reset() 
    {
    	predictorList.clear();
    	categoryList.clear();
    	predictorCategoryMap.clear();
        oneSampleRadioButton.setValue(false);
        multiSamplePanel.setVisible(false);
    	checkComplete();
    }

    /**
     * Resize the beta matrix when the design matrix dimensions change
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

    private void loadFromContext()
    {
        reset();
        List<BetweenParticipantFactor> factorList = 
            studyDesignContext.getStudyDesign().getBetweenParticipantFactorList();
        if (factorList != null) {
            for(BetweenParticipantFactor factor: factorList)
            {
                String predictor = factor.getPredictorName();
                addPredictor(predictor);
                List<Category> categoryList = factor.getCategoryList();
                ArrayList<String> categories = predictorCategoryMap.get(predictor);
                for(Category category: categoryList)
                {
                    categories.add(category.getCategory());
                }
            }
        }
        checkComplete();
    }

    /**
     * Update the between participant factors in the context
     */
    @Override
    public void onExit()
    {
        if (changed) {
            studyDesignContext.setBetweenParticipantFactorList(this, 
                    buildBetweenParticipantFactorList());
            changed = false;
        }
    }
    
    /**
     * Convert the screen information into a between participant factor list
     * @return list of between participant factors
     */
    private List<BetweenParticipantFactor> buildBetweenParticipantFactorList()
    {
        ArrayList<BetweenParticipantFactor> factorList = new ArrayList<BetweenParticipantFactor>();
        if (!oneSampleRadioButton.getValue()) {
            for(String predictor: predictorCategoryMap.keySet())
            {
                BetweenParticipantFactor factor = new BetweenParticipantFactor();
                factor.setPredictorName(predictor);
                List<String> categoryNameList = predictorCategoryMap.get(predictor);
                ArrayList<Category> categoryList = new ArrayList<Category>();
                for(String category: categoryNameList) categoryList.add(new Category(category));
                factor.setCategoryList(categoryList);
                factorList.add(factor);
            }
        } else {
            // build a single predictor
            BetweenParticipantFactor factor = new BetweenParticipantFactor();
            factor.setPredictorName("Sample");
            ArrayList<Category> categoryNameList = new ArrayList<Category>();
            categoryNameList.add(new Category("Sample"));
            factor.setCategoryList(categoryNameList);
            factorList.add(factor);
        }
        return factorList;
    }
	
}
