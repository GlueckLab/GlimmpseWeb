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
import edu.ucdenver.bios.glimmpseweb.client.shared.ResultsDisplayPanel;
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
	protected IntroPanel startIntroPanel = new IntroPanel(context, 
	        GlimmpseWeb.constants.navItemIntro(), GlimmpseWeb.constants.startTitle(),
			GlimmpseWeb.constants.startDescription());
	protected SolvingForPanel solvingForPanel = new SolvingForPanel(context, "guided");
	protected PowerPanel powerPanel = new PowerPanel(context);
	// type I error
	protected IntroPanel alphaIntroPanel = new IntroPanel(context, 
			GlimmpseWeb.constants.navItemIntro(), GlimmpseWeb.constants.alphaIntroTitle(),
			GlimmpseWeb.constants.alphaIntroDescription());
	protected TypeIErrorPanel typeIErrorPanel = new TypeIErrorPanel(context);
	// predictors
	protected IntroPanel predictorIntroPanel = new IntroPanel(context,
			GlimmpseWeb.constants.navItemIntro(), GlimmpseWeb.constants.predictorsIntroTitle(),
			GlimmpseWeb.constants.predictorsIntroDescription());
	protected CategoricalPredictorsPanel catPredictorsPanel = new CategoricalPredictorsPanel(context);
	protected BaselineCovariatePanel covariatePanel = new BaselineCovariatePanel(context);
	protected RelativeGroupSizePanel relativeGroupSizePanel = new RelativeGroupSizePanel(context);
	protected PerGroupSampleSizePanel perGroupSampleSizePanel = new PerGroupSampleSizePanel(context);
	// outcomes
	protected IntroPanel outcomesIntroPanel = new IntroPanel(context,
			GlimmpseWeb.constants.navItemIntro(), GlimmpseWeb.constants.outcomesIntroTitle(),
			GlimmpseWeb.constants.outcomesIntroDescription());
	protected OutcomesPanel outcomesPanel = new OutcomesPanel(context);
	protected RepeatedMeasuresPanel repeatedMeasuresPanel = new RepeatedMeasuresPanel(context);
	//clustering 
	protected ClusteringPanel clusteringPanel = new ClusteringPanel(context);
	// hypotheses
	protected IntroPanel hypothesisIntroPanel = new IntroPanel(context,
			GlimmpseWeb.constants.navItemIntro(), GlimmpseWeb.constants.hypothesisIntroTitle(),
			GlimmpseWeb.constants.hypothesisIntroDescription());
	protected HypothesisPanel hypothesisPanel = new HypothesisPanel(context);

	// mean differences
	protected IntroPanel meanDifferencesIntroPanel = new IntroPanel(context,
			GlimmpseWeb.constants.navItemIntro(), GlimmpseWeb.constants.meanDifferenceIntroTitle(),
			GlimmpseWeb.constants.meanDifferenceIntroDescription());
	protected MeanDifferencesPanel meanDifferencesPanel = new MeanDifferencesPanel(context);
	protected MeanDifferencesScalePanel meanDifferencesScalePanel = 
		new MeanDifferencesScalePanel(context);
	// variability
	protected IntroPanel variabilityIntroPanel = new IntroPanel(context,
			GlimmpseWeb.constants.navItemIntro(), GlimmpseWeb.constants.variabilityIntroTitle(),
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
	// panel to display the results
	protected ResultsDisplayPanel resultsPanel = new ResultsDisplayPanel(context);
	
	protected WizardPanel wizardPanel;
	
	public GuidedWizardPanel()
	{
		VerticalPanel panel = new VerticalPanel();
		// set up the wizard for Guided Mode
		ArrayList<WizardStepPanelGroup> groups = buildPanelGroups();
		wizardPanel = new WizardPanel(groups, resultsPanel);
		wizardPanel.setVisiblePanel(startIntroPanel);
		// layout the overall panel
		panel.add(wizardPanel);
		// set style
		
		// initialize
		initWidget(panel);
	}
	
	private ArrayList<WizardStepPanelGroup> buildPanelGroups()
	{
		ArrayList<WizardStepPanelGroup> groupList = new ArrayList<WizardStepPanelGroup>();
		WizardStepPanelGroup group = 
		    new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupStart());
		group.addPanel(startIntroPanel);
		group.addPanel(solvingForPanel);
		group.addPanel(powerPanel);
		group.addPanel(typeIErrorPanel);
		groupList.add(group);
//		{predictorIntroPanel, catPredictorsPanel, covariatePanel, 
//			relativeGroupSizePanel, perGroupSampleSizePanel}, 
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupPredictors());
		group.addPanel(predictorIntroPanel);
		group.addPanel(catPredictorsPanel);
		group.addPanel(covariatePanel);
		group.addPanel(relativeGroupSizePanel);
		group.addPanel(perGroupSampleSizePanel);
		groupList.add(group);
		// random effects
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupClustering());
		group.addPanel(clusteringPanel);
		groupList.add(group);
		// outcomes
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupResponses());
		group.addPanel(outcomesIntroPanel);
		group.addPanel(outcomesPanel);
		group.addPanel(repeatedMeasuresPanel);
		groupList.add(group);
		// hypotheses
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupHypothesis());
		group.addPanel(hypothesisIntroPanel);
		group.addPanel(hypothesisPanel);
		groupList.add(group);
		// mean differences
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupMeans());
		group.addPanel(meanDifferencesIntroPanel);
		group.addPanel(meanDifferencesPanel);
		group.addPanel(meanDifferencesScalePanel);
		groupList.add(group);
		// variability
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupVariability());
		group.addPanel(variabilityIntroPanel);
		group.addPanel(variabilityWithinParticipantPanel);
		group.addPanel(variabilityCovariatePanel);
		group.addPanel(variabilityCovariateOutcomePanel);
		group.addPanel(variabilityScalePanel);
		groupList.add(group);
		// options panels
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupOptions());
		group.addPanel(optionsTestsPanel);
		group.addPanel(optionsPowerMethodsPanel);
		group.addPanel(optionsCIPanel);
		group.addPanel(optionsDisplayPanel);
		groupList.add(group);

		return groupList;
	}
}
