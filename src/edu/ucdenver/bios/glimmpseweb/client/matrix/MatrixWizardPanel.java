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
package edu.ucdenver.bios.glimmpseweb.client.matrix;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.connector.FileSvcConnector;
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
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardActionListener;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelGroup;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignContext;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;
import edu.ucdenver.bios.webservice.common.enums.StudyDesignViewTypeEnum;

/**
 * Main wizard panel for Matrix Mode
 * @author Sarah Kreidler
 *
 */
public class MatrixWizardPanel extends Composite
implements WizardActionListener
{
    // create a study design context
    protected StudyDesignContext context = new StudyDesignContext();

    // connector to File service for save
    protected FileSvcConnector fileSvcConnector = new FileSvcConnector();

    // content panels 
    protected IntroPanel introPanel = new IntroPanel(context, 
            GlimmpseWeb.constants.navItemIntro(), GlimmpseWeb.constants.matrixIntroTitle(),
            GlimmpseWeb.constants.matrixIntroDescription());
    protected SolvingForPanel solvingForPanel = new SolvingForPanel(context, "matrix");
    protected PowerPanel powerPanel = new PowerPanel(context);
    protected TypeIErrorPanel typeIErrorPanel = new TypeIErrorPanel(context);

    // design panels
    protected IntroPanel designIntroPanel = new IntroPanel(context, 
            "Design INtro", "", "");
    protected DesignPanel designPanel = new DesignPanel(context);
    protected BaselineCovariatePanel covariatePanel = new BaselineCovariatePanel(context);
    protected PerGroupSampleSizePanel perGroupSampleSizePanel = 
        new PerGroupSampleSizePanel(context);

    // contrast panels
    protected IntroPanel contrastIntroPanel = new IntroPanel(context, 
            "Contrast INtro", "", "");
    protected BetweenSubjectContrastPanel betweenContrastPanel = 
        new BetweenSubjectContrastPanel(context);
    protected WithinSubjectContrastPanel withinContrastPanel = 
        new WithinSubjectContrastPanel(context);

    // regression parameter panels
    protected IntroPanel parameterIntroPanel = new IntroPanel(context, 
            "Parameter INtro", "", "");
    protected BetaPanel betaPanel = new BetaPanel(context);
    protected BetaScalePanel betaScalePanel = new BetaScalePanel(context);
    protected ThetaNullPanel thetaPanel = new ThetaNullPanel(context);

    // variability panels
    protected IntroPanel variabilityIntroPanel = new IntroPanel(context, 
            "Design INtro", "", "");
    protected SigmaErrorMatrixPanel sigmaErrorPanel = new SigmaErrorMatrixPanel(context);
    protected SigmaOutcomesMatrixPanel sigmaOutcomesPanel = new SigmaOutcomesMatrixPanel(context);
    protected SigmaOutcomeCovariateMatrixPanel sigmaOutcomeCovariatePanel = 
        new SigmaOutcomeCovariateMatrixPanel(context);
    protected SigmaCovariateMatrixPanel sigmaCovariatePanel = new SigmaCovariateMatrixPanel(context);
    protected SigmaScalePanel sigmaScalePanel = new SigmaScalePanel(context);
    // options
    protected IntroPanel optionsIntroPanel = new IntroPanel(context, 
            "Options Intro", "", "");
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

    /**
     * Constructor
     */
    public MatrixWizardPanel()
    {
        // indicate that this is a matrix only study design
        context.getStudyDesign().setViewTypeEnum(StudyDesignViewTypeEnum.MATRIX_MODE);
        VerticalPanel panel = new VerticalPanel();
        // set up the wizard for Guided Mode
        ArrayList<WizardStepPanelGroup> groups = buildPanelGroups();
        wizardPanel = new WizardPanel(context, groups, resultsPanel);
        wizardPanel.setVisiblePanel(introPanel);
        wizardPanel.addWizardActionListener(this);
        // layout the overall panel
        panel.add(wizardPanel);
        // set style

        // initialize
        initWidget(panel);
    }

    /**
     * Create a hierarchy of panels in Matrix mode
     * @return panel group list
     */
    private ArrayList<WizardStepPanelGroup> buildPanelGroups()
    {
        ArrayList<WizardStepPanelGroup> groupList = new ArrayList<WizardStepPanelGroup>();
        WizardStepPanelGroup group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupStart(),
                introPanel);
        group.addPanel(solvingForPanel);
        group.addPanel(powerPanel);
        group.addPanel(typeIErrorPanel);
        groupList.add(group);
        group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupDesign(), designIntroPanel);
        group.addPanel(designPanel);
        group.addPanel(covariatePanel);
        group.addPanel(perGroupSampleSizePanel);
        groupList.add(group);
        group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupCoefficients(), parameterIntroPanel);
        group.addPanel(betaPanel);
        group.addPanel(betaScalePanel);
        groupList.add(group);
        group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupHypothesis(), contrastIntroPanel);
        group.addPanel(betweenContrastPanel);
        group.addPanel(withinContrastPanel);
        group.addPanel(thetaPanel);
        groupList.add(group);
        group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupVariability(), variabilityIntroPanel);
        group.addPanel(sigmaErrorPanel);
        group.addPanel(sigmaOutcomesPanel);
        group.addPanel(sigmaCovariatePanel);
        group.addPanel(sigmaOutcomeCovariatePanel);
        group.addPanel(sigmaScalePanel);
        groupList.add(group);
        group = new WizardStepPanelGroup(GlimmpseWeb.constants.navGroupOptions(), optionsIntroPanel);
        group.addPanel(optionsTestsPanel);
        group.addPanel(optionsPowerMethodsPanel);
        group.addPanel(optionsCIPanel);
        group.addPanel(optionsDisplayPanel);
        groupList.add(group);
        return groupList;
    }

    /**
     * Clear the context
     */
    public void reset() {
        StudyDesign design = new StudyDesign();
        design.setViewTypeEnum(StudyDesignViewTypeEnum.MATRIX_MODE);
        context.loadStudyDesign(design);
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




}
