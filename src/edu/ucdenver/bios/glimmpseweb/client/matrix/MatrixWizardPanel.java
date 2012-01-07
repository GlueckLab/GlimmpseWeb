package edu.ucdenver.bios.glimmpseweb.client.matrix;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.IntroPanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.TypeIErrorPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelGroup;

public class MatrixWizardPanel extends Composite
{
	// content panels 
	protected IntroPanel introPanel = new IntroPanel("Intro", GlimmpseWeb.constants.matrixIntroTitle(),
			GlimmpseWeb.constants.matrixIntroDescription());
//	protected SolvingForPanel solvingForPanel = new SolvingForPanel(getModeName());
//	protected PowerPanel powerPanel = new PowerPanel();
	protected TypeIErrorPanel typeIErrorPanel = new TypeIErrorPanel();
    protected DesignPanel designPanel = new DesignPanel();
//	protected BaselineCovariatePanel covariatePanel = new BaselineCovariatePanel();
//	protected PerGroupSampleSizePanel perGroupSampleSizePanel = new PerGroupSampleSizePanel();
	
    protected BetweenSubjectContrastPanel betweenContrastPanel = new BetweenSubjectContrastPanel();
    protected WithinSubjectContrastPanel withinContrastPanel = new WithinSubjectContrastPanel();

    protected BetaPanel betaPanel = new BetaPanel();
    protected BetaScalePanel betaScalePanel = new BetaScalePanel();
    protected ThetaPanel thetaPanel = new ThetaPanel();
    
    protected SigmaErrorMatrixPanel sigmaErrorPanel = new SigmaErrorMatrixPanel();
    protected SigmaOutcomesMatrixPanel sigmaOutcomesPanel = new SigmaOutcomesMatrixPanel();
    protected SigmaOutcomeCovariateMatrixPanel sigmaOutcomeCovariatePanel = 
    	new SigmaOutcomeCovariateMatrixPanel();
    protected SigmaCovariateMatrixPanel sigmaCovariatePanel = new SigmaCovariateMatrixPanel();
    protected SigmaScalePanel sigmaScalePanel = new SigmaScalePanel();
	// options
//	protected OptionsTestsPanel optionsTestsPanel = new OptionsTestsPanel(getModeName());
//	protected OptionsPowerMethodsPanel optionsPowerMethodsPanel = 
//		new OptionsPowerMethodsPanel(getModeName());
//	protected OptionsDisplayPanel optionsDisplayPanel = new OptionsDisplayPanel(getModeName());
//	protected OptionsConfidenceIntervalsPanel optionsCIPanel =
//		new OptionsConfidenceIntervalsPanel(getModeName());
	// results
//	protected ResultsDisplayPanel resultsPanel = new ResultsDisplayPanel(this);
    // list of panels for the wizard
//	WizardStepPanel[][] panelList = {
//			{introPanel, solvingForPanel, powerPanel},
//			{alphaPanel},
//			{designPanel, covariatePanel, perGroupSampleSizePanel},
//			{betaPanel, betaScalePanel},
//			{betweenContrastPanel, withinContrastPanel},
//			{thetaPanel},
//			{sigmaErrorPanel, sigmaOutcomesPanel, sigmaCovariatePanel, 
//				sigmaOutcomeCovariatePanel, sigmaScalePanel},
//			{optionsTestsPanel, optionsPowerMethodsPanel, optionsCIPanel, optionsDisplayPanel},
//			{resultsPanel}
//	};
	
	String[] groupLabels = {
		GlimmpseWeb.constants.stepsLeftStart(),
		GlimmpseWeb.constants.stepsLeftAlpha(),
		GlimmpseWeb.constants.stepsLeftDesign(),
		GlimmpseWeb.constants.stepsLeftBeta(),
		GlimmpseWeb.constants.stepsLeftContrast(),
		GlimmpseWeb.constants.stepsLeftTheta(),
		GlimmpseWeb.constants.stepsLeftVariability(),
		GlimmpseWeb.constants.stepsLeftOptions(),
		GlimmpseWeb.constants.stepsLeftResults()
	};


	
	protected WizardPanel wizardPanel;
	
	public MatrixWizardPanel()
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
		group.addPanel(introPanel);
		groups.add(group);
		group = new WizardStepPanelGroup("Type I Error");
		group.addPanel(typeIErrorPanel);
		groups.add(group);
		group = new WizardStepPanelGroup("Design");
		group.addPanel(this.designPanel);
		groups.add(group);
		group = new WizardStepPanelGroup("Coefficients");
		group.addPanel(this.betaPanel);
		group.addPanel(this.betaScalePanel);
		groups.add(group);
		
		return groups;
	}
}
