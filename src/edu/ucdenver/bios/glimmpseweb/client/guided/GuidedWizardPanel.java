/*
 * Web Interface for the GLIMMPSE Software System.  Allows
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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.connector.FileSvcConnector;
import edu.ucdenver.bios.glimmpseweb.client.shared.BaselineCovariatePanel;
import edu.ucdenver.bios.glimmpseweb.client.shared.DynamicTabPanelTest;
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
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardActionListener;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelGroup;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.BetaScale;
import edu.ucdenver.bios.webservice.common.domain.SigmaScale;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;
import edu.ucdenver.bios.webservice.common.enums.StudyDesignViewTypeEnum;

/**
 * Main wizard panel for Guided Mode
 * @author Sarah Kreidler
 *
 */
public class GuidedWizardPanel extends Composite
implements WizardActionListener
{
    // connector to File service for save
    protected FileSvcConnector fileSvcConnector = new FileSvcConnector();
    
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
			GlimmpseWeb.constants.navItemIntro(), GlimmpseWeb.constants.participantsIntroTitle(),
			GlimmpseWeb.constants.participantsIntroDescription());
	protected FixedPredictorsPanel fixedPredictorsPanel = new FixedPredictorsPanel(context);
	protected BaselineCovariatePanel covariatePanel = new BaselineCovariatePanel(context);
	protected RelativeGroupSizePanel relativeGroupSizePanel = new RelativeGroupSizePanel(context);
	protected PerGroupSampleSizePanel perGroupSampleSizePanel = new PerGroupSampleSizePanel(context);
	//clustering 
    protected ClusteringPanel clusteringPanel = new ClusteringPanel(context);
	// outcomes
	protected IntroPanel outcomesIntroPanel = new IntroPanel(context,
			GlimmpseWeb.constants.navItemIntro(), GlimmpseWeb.constants.responsesIntroTitle(),
			GlimmpseWeb.constants.responsesIntroDescription());
	protected ResponseVariablesPanel outcomesPanel = new ResponseVariablesPanel(context);
	protected RepeatedMeasuresPanel repeatedMeasuresPanel = new RepeatedMeasuresPanel(context);

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
	protected WithinParticipantCovariancePanel withinParticipantCovariancePanel = 
		new WithinParticipantCovariancePanel(context);
	protected GaussianCovariateCovariancePanel gaussianCovariateCovariancePanel = 
	    new GaussianCovariateCovariancePanel(context);
	protected VariabilityScalePanel variabilityScalePanel = new VariabilityScalePanel(context);
	// options
	protected IntroPanel optionsIntroPanel = new IntroPanel(context, 
	        GlimmpseWeb.constants.navItemIntro(), 
	        GlimmpseWeb.constants.optionsIntroTitle(), 
	        GlimmpseWeb.constants.optionsIntroDescription());
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
        // indicate that this is a guided study design
        context.getStudyDesign().setViewTypeEnum(StudyDesignViewTypeEnum.GUIDED_MODE);
        setStudyDesignDefaults();
		VerticalPanel panel = new VerticalPanel();

		// set up the wizard for Guided Mode
		ArrayList<WizardStepPanelGroup> groups = buildPanelGroups();
		wizardPanel = new WizardPanel(context, groups, resultsPanel);
		wizardPanel.setVisiblePanel(startIntroPanel);
		wizardPanel.addWizardActionListener(this);
		// layout the overall panel
		panel.add(wizardPanel);
		panel.add(fileSvcConnector);
		// set style
		// initialize
		initWidget(panel);
	}
	
	private void setStudyDesignDefaults() {
	    ArrayList<BetaScale> betaScaleList = new ArrayList<BetaScale>();
	    ArrayList<SigmaScale> sigmaScaleList = new ArrayList<SigmaScale>();
	    betaScaleList.add(new BetaScale(1));
        sigmaScaleList.add(new SigmaScale(1));
        context.setSigmaScaleList(variabilityScalePanel, sigmaScaleList);
        context.setBetaScaleList(meanDifferencesScalePanel, betaScaleList);
	}
	
	private ArrayList<WizardStepPanelGroup> buildPanelGroups()
	{
		ArrayList<WizardStepPanelGroup> groupList = new ArrayList<WizardStepPanelGroup>();
		WizardStepPanelGroup group = 
		    new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupStart(), startIntroPanel);
//		group.addPanel(new DynamicTabPanelTest(context));
		group.addPanel(solvingForPanel);
		group.addPanel(powerPanel);
		group.addPanel(typeIErrorPanel);
		groupList.add(group);
		// predictors
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupPredictors(),predictorIntroPanel);
		group.addPanel(fixedPredictorsPanel);
		group.addPanel(covariatePanel);
	    group.addPanel(clusteringPanel);
		group.addPanel(relativeGroupSizePanel);
		group.addPanel(perGroupSampleSizePanel);
		groupList.add(group);
		// outcomes
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupResponses(),outcomesIntroPanel);
		group.addPanel(outcomesPanel);
		group.addPanel(repeatedMeasuresPanel);
		groupList.add(group);
		// hypotheses
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupHypothesis(),hypothesisIntroPanel);
		group.addPanel(hypothesisPanel);
		groupList.add(group);
		// mean differences
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupMeans(),meanDifferencesIntroPanel);
		group.addPanel(meanDifferencesPanel);
		group.addPanel(meanDifferencesScalePanel);
		groupList.add(group);
		// variability
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupVariability(),variabilityIntroPanel);
		group.addPanel(withinParticipantCovariancePanel);
		group.addPanel(gaussianCovariateCovariancePanel);
		group.addPanel(variabilityScalePanel);
		groupList.add(group);
		// options panels
		group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupOptions(), optionsIntroPanel);
		group.addPanel(optionsTestsPanel);
		group.addPanel(optionsPowerMethodsPanel);
		group.addPanel(optionsCIPanel);
		group.addPanel(optionsDisplayPanel);
		groupList.add(group);

		return groupList;
	}

    @Override
    public void onNext() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPrevious() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onPanel(WizardStepPanel panel) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onFinish() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onHelp() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSave() {
        // TODO Auto-generated method stub
        fileSvcConnector.saveStudyDesign(context.getStudyDesign(), null);
    }

    @Override
    public void onCancel() {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * Clear the context
     */
    public void reset() {
        wizardPanel.setVisiblePanel(startIntroPanel);
        context.loadStudyDesign(null);
        setStudyDesignDefaults();
    }
    
    /**
     * Load the specified study design into the wizard
     * @param design
     */
    public void loadStudyDesign(StudyDesign design) {
        context.loadStudyDesign(design);
    }
    
    /**
     * Add a listener for wizard actions - mainly to allow the main application panel
     * to perform a cancel.  Really need to rework this into separate HTML files someday
     * @param listener
     */
    public void addWizardActionListener(WizardActionListener listener) {
        wizardPanel.addWizardActionListener(listener);
    }
}
