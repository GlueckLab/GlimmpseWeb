package edu.ucdenver.bios.glimmpseweb.client.matrix;

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

public class MatrixWizardPanel extends Composite
{
	// create a study design context
	protected StudyDesignContext context = new StudyDesignContext();
	// content panels 
	protected IntroPanel introPanel = new IntroPanel(context, 
			GlimmpseWeb.constants.navItemIntro(), GlimmpseWeb.constants.matrixIntroTitle(),
			GlimmpseWeb.constants.matrixIntroDescription());
	protected SolvingForPanel solvingForPanel = new SolvingForPanel(context, "matrix");
	protected PowerPanel powerPanel = new PowerPanel(context);
	protected TypeIErrorPanel typeIErrorPanel = new TypeIErrorPanel(context);
    protected DesignPanel designPanel = new DesignPanel(context);
	protected BaselineCovariatePanel covariatePanel = new BaselineCovariatePanel(context);
	protected PerGroupSampleSizePanel perGroupSampleSizePanel = 
		new PerGroupSampleSizePanel(context);
	
    protected BetweenSubjectContrastPanel betweenContrastPanel = 
    	new BetweenSubjectContrastPanel(context);
    protected WithinSubjectContrastPanel withinContrastPanel = 
    	new WithinSubjectContrastPanel(context);

    protected BetaPanel betaPanel = new BetaPanel(context);
    protected BetaScalePanel betaScalePanel = new BetaScalePanel(context);
    protected ThetaNullPanel thetaPanel = new ThetaNullPanel(context);
    
    protected SigmaErrorMatrixPanel sigmaErrorPanel = new SigmaErrorMatrixPanel(context);
    protected SigmaOutcomesMatrixPanel sigmaOutcomesPanel = new SigmaOutcomesMatrixPanel(context);
    protected SigmaOutcomeCovariateMatrixPanel sigmaOutcomeCovariatePanel = 
    	new SigmaOutcomeCovariateMatrixPanel(context);
    protected SigmaCovariateMatrixPanel sigmaCovariatePanel = new SigmaCovariateMatrixPanel(context);
    protected SigmaScalePanel sigmaScalePanel = new SigmaScalePanel(context);
	// options
	protected OptionsTestsPanel optionsTestsPanel = new OptionsTestsPanel(context, "matrix");
	protected OptionsPowerMethodsPanel optionsPowerMethodsPanel = 
		new OptionsPowerMethodsPanel(context, "matrix");
	protected OptionsDisplayPanel optionsDisplayPanel = new OptionsDisplayPanel(context, "matrix");
	protected OptionsConfidenceIntervalsPanel optionsCIPanel =
		new OptionsConfidenceIntervalsPanel(context, "matrix");
	// panel to display the results
    protected ResultsDisplayPanel resultsPanel = new ResultsDisplayPanel(context);
	// the wizard panel widget
	protected WizardPanel wizardPanel;
	
	public MatrixWizardPanel()
	{
		VerticalPanel panel = new VerticalPanel();
		// set up the wizard for Guided Mode
		ArrayList<WizardStepPanelGroup> groups = buildPanelGroups();
		wizardPanel = new WizardPanel(context, groups, resultsPanel);
		wizardPanel.setVisiblePanel(introPanel);
		// layout the overall panel
		panel.add(wizardPanel);
		// set style

		// initialize
		initWidget(panel);
	}
	
	private ArrayList<WizardStepPanelGroup> buildPanelGroups()
	{
		ArrayList<WizardStepPanelGroup> groupList = new ArrayList<WizardStepPanelGroup>();
		WizardStepPanelGroup group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupStart());
		group.addPanel(introPanel);
		group.addPanel(solvingForPanel);
		group.addPanel(powerPanel);
		group.addPanel(typeIErrorPanel);
		groupList.add(group);
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupDesign());
		group.addPanel(designPanel);
		group.addPanel(covariatePanel);
		group.addPanel(perGroupSampleSizePanel);
		groupList.add(group);
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupCoefficients());
		group.addPanel(betaPanel);
		group.addPanel(betaScalePanel);
		groupList.add(group);
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupHypothesis());
		group.addPanel(betweenContrastPanel);
		group.addPanel(withinContrastPanel);
		group.addPanel(thetaPanel);
		groupList.add(group);
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupVariability());
		group.addPanel(sigmaErrorPanel);
		group.addPanel(sigmaOutcomesPanel);
		group.addPanel(sigmaCovariatePanel);
		group.addPanel(sigmaOutcomeCovariatePanel);
		group.addPanel(sigmaScalePanel);
		groupList.add(group);
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupOptions());
		group.addPanel(optionsTestsPanel);
		group.addPanel(optionsPowerMethodsPanel);
		group.addPanel(optionsCIPanel);
		group.addPanel(optionsDisplayPanel);
		groupList.add(group);
		return groupList;
	}
}
