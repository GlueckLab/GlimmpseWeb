package edu.ucdenver.bios.glimmpseweb.client.guided;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.BaselineCovariatePanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.IntroPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.OptionsConfidenceIntervalsPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.OptionsDisplayPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.OptionsPowerMethodsPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.OptionsTestsPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.PerGroupSampleSizePanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.PowerPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.SolvingForPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.TypeIErrorPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelGroup;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;

public class GuidedWizardPanel extends Composite
{
	// create a study design context
	protected StudyDesignContext context = new StudyDesignContext();
	// content panels 
	protected IntroPanel startIntroPanel = new IntroPanel(context, "Intro", GlimmpseWeb.constants.startTitle(),
			GlimmpseWeb.constants.startDescription());
	protected SolvingForPanel solvingForPanel = new SolvingForPanel(context, "guided");
	protected PowerPanel powerPanel = new PowerPanel(context);
	// type I error
	protected IntroPanel alphaIntroPanel = new IntroPanel(context, 
			"Intro", GlimmpseWeb.constants.alphaIntroTitle(),
			GlimmpseWeb.constants.alphaIntroDescription());
	protected TypeIErrorPanel typeIErrorPanel = new TypeIErrorPanel(context);
	// predictors
	protected IntroPanel predictorIntroPanel = new IntroPanel(context,
			"Intro", GlimmpseWeb.constants.predictorsIntroTitle(),
			GlimmpseWeb.constants.predictorsIntroDescription());
	protected CategoricalPredictorsPanel catPredictorsPanel = new CategoricalPredictorsPanel(context);
	protected BaselineCovariatePanel covariatePanel = new BaselineCovariatePanel(context);
	protected RelativeGroupSizePanel relativeGroupSizePanel = new RelativeGroupSizePanel(context);
	protected PerGroupSampleSizePanel perGroupSampleSizePanel = new PerGroupSampleSizePanel(context);
	// outcomes
	protected IntroPanel outcomesIntroPanel = new IntroPanel(context,
			"Intro", GlimmpseWeb.constants.outcomesIntroTitle(),
			GlimmpseWeb.constants.outcomesIntroDescription());
	protected OutcomesPanel outcomesPanel = new OutcomesPanel(context);
	protected RepeatedMeasuresPanel repeatedMeasuresPanel = new RepeatedMeasuresPanel(context);
	// hypotheses
	protected IntroPanel hypothesisIntroPanel = new IntroPanel(context,
			"Intro", GlimmpseWeb.constants.hypothesisIntroTitle(),
			GlimmpseWeb.constants.hypothesisIntroDescription());

	// mean differences
	protected IntroPanel meanDifferencesIntroPanel = new IntroPanel(context,
			"Intro", GlimmpseWeb.constants.meanDifferenceIntroTitle(),
			GlimmpseWeb.constants.meanDifferenceIntroDescription());
	protected MeanDifferencesPanel meanDifferencesPanel = new MeanDifferencesPanel(context);
	protected MeanDifferencesScalePanel meanDifferencesScalePanel = 
		new MeanDifferencesScalePanel(context);
	// variability
	protected IntroPanel variabilityIntroPanel = new IntroPanel(context,
			"Intro", GlimmpseWeb.constants.variabilityIntroTitle(),
			GlimmpseWeb.constants.variabilityIntroDescription());
	protected VariabilityWithinParticipantPanel variabilityWithinParticipantPanel = 
		new VariabilityWithinParticipantPanel(context);
	protected VariabilityCovariatePanel variabilityCovariatePanel = new VariabilityCovariatePanel(context);
	protected VariabilityCovariateOutcomePanel variabilityCovariateOutcomePanel = 
		new VariabilityCovariateOutcomePanel(context);
	protected VariabilityScalePanel variabilityScalePanel = new VariabilityScalePanel(context);
	// options
	protected OptionsTestsPanel optionsTestsPanel = new OptionsTestsPanel(context, "guided");
	protected OptionsPowerMethodsPanel optionsPowerMethodsPanel = 
		new OptionsPowerMethodsPanel(context, "guided");
	protected OptionsDisplayPanel optionsDisplayPanel = new OptionsDisplayPanel(context, "guided");
	protected OptionsConfidenceIntervalsPanel optionsCIPanel =
		new OptionsConfidenceIntervalsPanel(context, "guided");
	// results
//	protected ResultsDisplayPanel resultsPanel = new ResultsDisplayPanel(this);
	
//    // list of panels for the wizard
//	WizardStepPanel[][] panelList = {
//			{startIntroPanel, solvingForPanel, powerPanel},
//			{alphaPanel}, 
//			{predictorIntroPanel, catPredictorsPanel, covariatePanel, 
//				relativeGroupSizePanel, perGroupSampleSizePanel}, 
//			{outcomesIntroPanel, outcomesPanel, repeatedMeasuresPanel}, 
//			{hypothesisIntroPanel, hypothesisIndependentPanel, hypothesisRepeatedPanel,
//				hypothesisDoublyRepeatedPanel},
//			{meanDifferencesIntroPanel, meanDifferencesPanel,
//				meanDifferencesRepeatedPanel, meanDifferencesScalePanel},
//			{variabilityIntroPanel, variabilityIndependentPanel, variabilityRepeatedPanel,
//					variabilityCovariatePanel, variabilityCovariateOutcomePanel, variabilityScalePanel},
//			{optionsTestsPanel, optionsPowerMethodsPanel, optionsDisplayPanel},
//			{resultsPanel}
//	};
	
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
		ArrayList<WizardStepPanelGroup> groupList = new ArrayList<WizardStepPanelGroup>();
		WizardStepPanelGroup group = new WizardStepPanelGroup("Start");
		group.addPanel(startIntroPanel);
		group.addPanel(solvingForPanel);
		group.addPanel(powerPanel);
		groupList.add(group);
		group = new WizardStepPanelGroup("Type I Error");
		group.addPanel(typeIErrorPanel);
		groupList.add(group);
//		{predictorIntroPanel, catPredictorsPanel, covariatePanel, 
//			relativeGroupSizePanel, perGroupSampleSizePanel}, 
		group = new WizardStepPanelGroup("Predictors");
		group.addPanel(predictorIntroPanel);
		group.addPanel(catPredictorsPanel);
		group.addPanel(covariatePanel);
		group.addPanel(relativeGroupSizePanel);
		group.addPanel(perGroupSampleSizePanel);
		groupList.add(group);
		group = new WizardStepPanelGroup("Clustering");
		groupList.add(group);
		
		group = new WizardStepPanelGroup("Responses");
		group.addPanel(outcomesIntroPanel);
		group.addPanel(outcomesPanel);
		group.addPanel(repeatedMeasuresPanel);
		group.addPanel(relativeGroupSizePanel);
		group.addPanel(perGroupSampleSizePanel);
		groupList.add(group);

		group = new WizardStepPanelGroup("Hypothesis");
		group.addPanel(hypothesisIntroPanel);
		groupList.add(group);
		
		group = new WizardStepPanelGroup("Means");
		group.addPanel(meanDifferencesIntroPanel);
		group.addPanel(meanDifferencesPanel);
		group.addPanel(meanDifferencesScalePanel);
		groupList.add(group);

		group = new WizardStepPanelGroup("Variability");
		group.addPanel(variabilityIntroPanel);
		group.addPanel(variabilityWithinParticipantPanel);
		group.addPanel(variabilityCovariatePanel);
		group.addPanel(variabilityCovariateOutcomePanel);
		group.addPanel(variabilityScalePanel);
		groupList.add(group);

		group = new WizardStepPanelGroup("Options");
		group.addPanel(optionsTestsPanel);
		group.addPanel(optionsPowerMethodsPanel);
		group.addPanel(optionsCIPanel);
		group.addPanel(optionsDisplayPanel);
		groupList.add(group);

		return groupList;
	}
}
