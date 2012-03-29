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
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.HtmlTextExplainPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;

public class HypothesisPanel extends WizardStepPanel {

	DeckPanel deckPanel = new DeckPanel();
	DeckPanel editTrendDeckPanel = new DeckPanel();
	VerticalPanel verticalPanel = new VerticalPanel();
	MainEffectHypothesisPanel mainEffectHypothesisPanelInstance = new MainEffectHypothesisPanel();
	InteractionHypothesisPanel interactionHypothesisPanelInstance = new InteractionHypothesisPanel();
	TrendHypothesisPanel trendHypothesisPanelInstance = new TrendHypothesisPanel();

	public HypothesisPanel(WizardContext context)
	{		
		super(context, "Hypothesis");
		
		HTML title = new HTML(GlimmpseWeb.constants.hypothesisPanelTitle());
		HTML description = new HTML(GlimmpseWeb.constants.hypothesisPanelDescription());
		HTML instructions = new HTML(GlimmpseWeb.constants.hypothesisPanelInstructions());
		final HTML selectTypeOfTrend = new HTML(GlimmpseWeb.constants.hypothesisPanelSelectTypeOfTrend());
		
		RadioButton mainEffectRadioButton = new RadioButton("HypothesisRadioButtonGroup", GlimmpseWeb.constants.hypothesisPanelMainEffect()); 
		RadioButton interactionRadioButton = new RadioButton("HypothesisRadioButtonGroup", GlimmpseWeb.constants.hypothesisPanelInteraction()); 
		RadioButton trendRadioButton = new RadioButton("HypothesisRadioButtonGroup", GlimmpseWeb.constants.hypothesisPanelTrend());
		
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		
		HtmlTextExplainPanel mainEffect = new HtmlTextExplainPanel("", GlimmpseWeb.constants.hypothesisPanelMainEffect(), GlimmpseWeb.constants.hypothesisPanelMainEffectExplination());
		horizontalPanel.add(mainEffectRadioButton);
		horizontalPanel.add(mainEffect);
		HtmlTextExplainPanel interaction = new HtmlTextExplainPanel("", GlimmpseWeb.constants.hypothesisPanelInteraction(), GlimmpseWeb.constants.hypothesisPanelInteractionExplination());
		horizontalPanel.add(interactionRadioButton);
		horizontalPanel.add(interaction);
		HtmlTextExplainPanel trend = new HtmlTextExplainPanel("",GlimmpseWeb.constants.hypothesisPanelTrend(), GlimmpseWeb.constants.hypothesisPanelTrendExplination());
		horizontalPanel.add(trendRadioButton);
		horizontalPanel.add(trend);
	
		
		
				
		selectTypeOfTrend.setVisible(false);
		
		VerticalPanel emptyPanel = new VerticalPanel();
		deckPanel.add(emptyPanel);
		deckPanel.add(mainEffectHypothesisPanelInstance);
		deckPanel.add(interactionHypothesisPanelInstance);
		deckPanel.add(trendHypothesisPanelInstance);
		
		/*EditTrendPanel showNoTrendOption = new EditTrendPanel(true);*/
		EditTrendPanel hideNoTrendOption = new EditTrendPanel(false);
		editTrendDeckPanel.add(emptyPanel);
		/*editTrendDeckPanel.add(showNoTrendOption);*/
		editTrendDeckPanel.add(hideNoTrendOption);
		
		
		mainEffectRadioButton.addClickHandler(new ClickHandler() 
		{
			@Override
			public void onClick(ClickEvent event)
			{			
				deckPanel.showWidget(0);
				selectTypeOfTrend.setVisible(false);
				editTrendDeckPanel.showWidget(0);
				mainEffectHypothesisPanelInstance.test();
			}
		});
		interactionRadioButton.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				selectTypeOfTrend.setVisible(false);
				deckPanel.showWidget(1);
				editTrendDeckPanel.showWidget(0);
				interactionHypothesisPanelInstance.test();
				
			}
			
		});
		trendRadioButton.addClickHandler(new ClickHandler() 
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				selectTypeOfTrend.setVisible(true);
				deckPanel.showWidget(2);
				editTrendDeckPanel.showWidget(1);
				trendHypothesisPanelInstance.test();
			}
		});
		
		title.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		instructions.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
		selectTypeOfTrend.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
				
		verticalPanel.add(title);
		verticalPanel.add(description);
		verticalPanel.add(instructions);
		verticalPanel.add(horizontalPanel);
		verticalPanel.add(deckPanel);
		verticalPanel.add(selectTypeOfTrend);
		verticalPanel.add(editTrendDeckPanel);
		
		initWidget(verticalPanel);
		
	}
	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Respond to a change in the context object
	 */
    @Override
	public void onWizardContextChange(WizardContextChangeEvent e) 
    {
    	
    };
    
    /**
     * Response to a context load event
     */
    @Override
	public void onWizardContextLoad() 
    {
    	
    }
    
    public void onExit()
    {
        
    }

}
