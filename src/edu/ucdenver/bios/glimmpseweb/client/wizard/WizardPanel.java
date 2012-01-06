package edu.ucdenver.bios.glimmpseweb.client.wizard;

import java.util.List;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;

public class WizardPanel extends Composite implements NavigationListener
{
	// style for tools
	protected static final String STYLE_TOOL_PANEL = "wizardToolsPanel";
	protected static final String STYLE_TOOL_BUTTON = "wizardToolsPanelButton";
	protected static final String STYLE_CONTENT_PANEL = "wizardContentPanel";
	protected static final String STYLE_SAVE = "save";
	protected static final String STYLE_CANCEL = "cancel";
	protected static final String STYLE_HELP = "help";
	// uri for help manual
	protected static final String HELP_URL = "/help/manual.pdf";

	
	// main panel
	protected HorizontalPanel panel = new HorizontalPanel();
	// left navigation / "steps left" panel
    protected WizardLeftNavigationPanel leftNavPanel;
    // nav panel
    protected NextPreviousNavigationPanel navPanel = new NextPreviousNavigationPanel();
    // run panel
    protected FinishPanel finishPanel = new FinishPanel();
	// index of currently visible step
    protected int currentStep = 0;  
    // deck panel containing all steps in the input wizard
    protected DeckPanel wizardDeck = new DeckPanel();
    
	/**
	 * Create an empty matrix panel
	 */
	public WizardPanel(List<WizardStepPanelGroup> wizardPanelGroups)
	{		
		VerticalPanel contentPanel = new VerticalPanel();
		VerticalPanel leftPanel = new VerticalPanel();
		
		// layout the wizard panel
		//contentPanel.add(toolBar);
		leftNavPanel = new WizardLeftNavigationPanel(wizardPanelGroups);
		leftPanel.add(leftNavPanel);
		leftPanel.add(createToolLinks());
		leftNavPanel.setPixelSize(100, 300);
		contentPanel.add(wizardDeck);
		contentPanel.add(navPanel);
		panel.add(leftPanel);		
		panel.add(contentPanel);
		
		// add the panels to the deck
		for(WizardStepPanelGroup panelGroup: wizardPanelGroups)
		{
			for(WizardStepPanel step: panelGroup.getPanelList())
			{
				wizardDeck.add(step);
			}
		}
		
		// add navigtion callbacks amongst subpanels
		leftNavPanel.addNavigationListener(this);

		// add style
		contentPanel.setStyleName(STYLE_CONTENT_PANEL);
		panel.setStyleName(STYLE_CONTENT_PANEL);

		// initialize
		initWidget(panel);
	}
	
	public VerticalPanel createToolLinks()
	{
		VerticalPanel panel = new VerticalPanel();
		
		Button saveButton = new Button(GlimmpseWeb.constants.toolsSaveStudy(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
//				notifyOnSave();
			}
		});
		
		Button cancelButton = new Button(GlimmpseWeb.constants.toolsCancel(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
//				notifyOnCancel();
			}
		});
		Button helpButton = new Button(GlimmpseWeb.constants.toolsHelp(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				openHelpManual();
			}
		});
		panel.add(saveButton);
		panel.add(helpButton);
		panel.add(cancelButton);
		
		// add style
		panel.setStyleName(STYLE_TOOL_PANEL);
		saveButton.setStyleName(STYLE_TOOL_BUTTON);
		saveButton.addStyleDependentName(STYLE_SAVE);
		cancelButton.setStyleName(STYLE_TOOL_BUTTON);
		cancelButton.addStyleDependentName(STYLE_CANCEL);
		helpButton.setStyleName(STYLE_TOOL_BUTTON);
		helpButton.addStyleDependentName(STYLE_HELP);
		return panel;
	}
	
//    /**
//     * Call back when "next" navigation button is clicked
//     * Does nothing if already at end of step list
//     */
//    public void onNext()
//    {
//    	if (currentStep < wizardDeck.getWidgetCount()-1)
//    	{
//    		exitStep();
//			// find the next panel which is not skipped, and is not a separator panel
//    		WizardStepPanel w;
//    		do
//    		{
//    			currentStep++;
//    			w = ((WizardStepPanel) wizardDeck.getWidget(currentStep));
//    			if (w.isSeparator()) stepsLeftPanel.onNext();
//    		} 
//    		while ((w.isSkipped() || w.isSeparator()) && 
//    				currentStep < wizardDeck.getWidgetCount()-1);
//    		enterStep();
//    	}
//    }
//    
//    /**
//     * Call back when "previous" navigation button is clicked
//     * Does nothing if already at start of step list
//     */
//    public void onPrevious()
//    {
//    	if (currentStep > 0)
//    	{
//    		exitStep();
//			// find the next panel which is not skipped, and is not a separator panel
//    		WizardStepPanel w;
//    		do
//    		{
//    			currentStep--;
//    			w = ((WizardStepPanel) wizardDeck.getWidget(currentStep));
//    			if (w.isSeparator()) stepsLeftPanel.onPrevious();
//    		} 
//    		while ((w.isSkipped() || w.isSeparator()) && 
//    				currentStep > 0);
//    		enterStep();
//    	}
//    }
//	
//    /**
//     * Navigate to a specific step
//     */
//    public void onStep(int stepIndex)
//    {
//    	if (stepIndex >= 0 && stepIndex < wizardDeck.getWidgetCount())
//    	{
//    		exitStep();
//    		currentStep = stepIndex;
//    		enterStep();
//    	}
//    }
//    
//    /**
//     * Allow forward navigation when user input to the current step is complete.
//     */
//    public void onStepComplete()
//    {
//    	navPanel.setNext(true);
//    }
//    
//    /**
//     * Disallow forward navigation when user input to the current step is not complete.
//     */
//    public void onStepInProgress()
//    {
//    	navPanel.setNext(false);
//    }
//    
//    /**
//     * Clear data from all panels in the wizard 
//     */
//    public void reset()
//    {
//    	for(int i = 0; i < wizardDeck.getWidgetCount(); i++)
//    	{
//    		WizardStepPanel step = (WizardStepPanel) wizardDeck.getWidget(i);
//    		step.reset();
//    	}
//    	stepsLeftPanel.reset();
//    	onStep(0);
//    }
//
//    /**
//     * Call any exit functions as we leave the current step
//     */
//    private void exitStep()
//    {
//    	WizardStepPanel w = (WizardStepPanel) wizardDeck.getWidget(currentStep);
//    	w.onExit();
//    }
//
//    /**
//     *  Enter the new step, calling any setup routines
//     */
//    private void enterStep()
//    {
//    	WizardStepPanel w = (WizardStepPanel) wizardDeck.getWidget(currentStep);
//    	wizardDeck.showWidget(currentStep);
//    	navPanel.setNext(w.isComplete());
//    	w.onEnter();
//    	navPanel.setPrevious(currentStep != 0);
//    }
//
//	
//
//    
//	/**
//	 * Add a panel to the wizard within the specified subgroup.
//	 * The panel will be added last to the subgroup
//	 */
//	public void addPanel(WizardStepPanel panel, String groupLabel)
//	{
//		wizardDeck.add(panel);
//		leftNavPanel.addNavigationItem(groupLabel, panel);
//	}

	public void setVisiblePanel(WizardStepPanel panel)
	{
		wizardDeck.showWidget(wizardDeck.getWidgetIndex(panel));
	}
	
    /**
     * Open the help manual in a new tab/window
     */
    public void openHelpManual()
    {
		// open manual
		Window.open(HELP_URL, "_blank", null);
    }

	@Override
	public void onNext()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPrevious()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPanel(WizardStepPanel panel)
	{
		// TODO Auto-generated method stub
		setVisiblePanel(panel);
	}
    
}
