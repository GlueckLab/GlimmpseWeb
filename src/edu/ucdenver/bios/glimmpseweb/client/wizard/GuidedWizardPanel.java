package edu.ucdenver.bios.glimmpseweb.client.wizard;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.shared.IntroPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.TypeIErrorPanel;

public class GuidedWizardPanel extends Composite
{
	protected IntroPanel i1 = new IntroPanel("1","1", "Intro Panel 1");
	protected IntroPanel i2 = new IntroPanel("2", "2", "Intro Panel 2");
	protected IntroPanel i3 = new IntroPanel("3", "3", "Intro Panel 3");
	protected IntroPanel i4 = new IntroPanel("4", "4","Intro Panel 4");
	protected IntroPanel i5 = new IntroPanel("5","5", "Intro Panel 5");
	protected TypeIErrorPanel typeIErrorPanel = new TypeIErrorPanel();
	
	protected WizardPanel wizardPanel;
	
	public GuidedWizardPanel()
	{
		VerticalPanel panel = new VerticalPanel();
		// set up the wizard for Guided Mode
		ArrayList<WizardStepPanelGroup> groups = buildPanelGroups();
		wizardPanel = new WizardPanel(groups);
		wizardPanel.setVisiblePanel(typeIErrorPanel);
		// layout the overall panel
		panel.add(wizardPanel);
		// set style
		
		// initialize
		initWidget(panel);
	}
	
	private ArrayList<WizardStepPanelGroup> buildPanelGroups()
	{
		ArrayList<WizardStepPanelGroup> groups = new ArrayList<WizardStepPanelGroup>();
		WizardStepPanelGroup group = new WizardStepPanelGroup("Start");
		group.addPanel(typeIErrorPanel);
		group.addPanel(i1);
		group.addPanel(i2);
		groups.add(group);
		group = new WizardStepPanelGroup("My Group");
		group.addPanel(i3);
		groups.add(group);
		group = new WizardStepPanelGroup("My Group2");
		group.addPanel(i4);
		group.addPanel(i5);
		groups.add(group);
		
		return groups;
	}
}
