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
package edu.ucdenver.bios.glimmpseweb.context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent.StudyDesignChangeType;
import edu.ucdenver.bios.webservice.common.domain.BetaScale;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Blob2DArray;
import edu.ucdenver.bios.webservice.common.domain.Category;
import edu.ucdenver.bios.webservice.common.domain.ClusterNode;
import edu.ucdenver.bios.webservice.common.domain.ConfidenceIntervalDescription;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.Hypothesis;
import edu.ucdenver.bios.webservice.common.domain.HypothesisBetweenParticipantMapping;
import edu.ucdenver.bios.webservice.common.domain.HypothesisRepeatedMeasuresMapping;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.NominalPower;
import edu.ucdenver.bios.webservice.common.domain.PowerCurveDescription;
import edu.ucdenver.bios.webservice.common.domain.PowerMethod;
import edu.ucdenver.bios.webservice.common.domain.Quantile;
import edu.ucdenver.bios.webservice.common.domain.RelativeGroupSize;
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;
import edu.ucdenver.bios.webservice.common.domain.ResponseNode;
import edu.ucdenver.bios.webservice.common.domain.SampleSize;
import edu.ucdenver.bios.webservice.common.domain.SigmaScale;
import edu.ucdenver.bios.webservice.common.domain.Spacing;
import edu.ucdenver.bios.webservice.common.domain.StandardDeviation;
import edu.ucdenver.bios.webservice.common.domain.StatisticalTest;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;
import edu.ucdenver.bios.webservice.common.domain.TypeIError;
import edu.ucdenver.bios.webservice.common.enums.CovarianceTypeEnum;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTrendTypeEnum;
import edu.ucdenver.bios.webservice.common.enums.HypothesisTypeEnum;
import edu.ucdenver.bios.webservice.common.enums.PowerMethodEnum;
import edu.ucdenver.bios.webservice.common.enums.RepeatedMeasuresDimensionType;
import edu.ucdenver.bios.webservice.common.enums.SolutionTypeEnum;
import edu.ucdenver.bios.webservice.common.enums.StatisticalTestTypeEnum;
import edu.ucdenver.bios.webservice.common.enums.StudyDesignViewTypeEnum;

/**
 * Wizard context for study designs
 * @author Sarah Kreidler
 * @author Vijay Akula
 *
 */
public class StudyDesignContext extends WizardContext
{
    // main study design object
    private StudyDesign studyDesign;

    // cache of all possible between participant effects
    private FactorTable participantGroups = new FactorTable();

    /**
     * Create a new context with an empty study design object
     */
    public StudyDesignContext()
    {
        studyDesign = new StudyDesign();
    }

    /**
     * Get the study design object associated with this context
     * @return StudyDesign object
     */
    public StudyDesign getStudyDesign()
    {
        return studyDesign;
    }

    /**
     * Load the context from an existing StudyDesign object.
     * Fires a wizard context load event to all panels
     * @param design StudyDesign object
     */
    public void loadStudyDesign(StudyDesign design)
    {
        if (design == null) {
            studyDesign = new StudyDesign();
        } else {
            studyDesign = design;
        }
        participantGroups.loadBetweenParticipantFactors(studyDesign.getBetweenParticipantFactorList());
        notifyWizardContextLoad();
        checkComplete();
    }

    /**
     * Get a table with all possible combinations of 
     * between participant factors.
     * @return between participant factor table
     */
    public FactorTable getParticipantGroups()
    {
        return participantGroups;
    }

    /**
     * Determine if we need to clear the hypothesis information when
     * predictors or repeated measures change
     * @param changeType
     */
    private void updateHypothesis(StudyDesignChangeType changeType) {
        Set<Hypothesis> hypothesisSet = studyDesign.getHypothesis();
        if (hypothesisSet != null) {
            Hypothesis primaryHypothesis = hypothesisSet.iterator().next();
            if (primaryHypothesis != null) {
                switch (changeType) {
                case BETWEEN_PARTICIPANT_FACTORS:
                    if (primaryHypothesis.getBetweenParticipantFactorMapList() != null &&
                    primaryHypothesis.getBetweenParticipantFactorMapList().size() > 0) {
                        // the hypothesis involves between participant effects, so we need to
                        // clear it to avoid corruption in the study design
                        hypothesisSet.clear();
                    }
                    break;
                case RESPONSES_LIST:
                    if (primaryHypothesis.getType() == HypothesisTypeEnum.GRAND_MEAN) {
                        // the dimension of grand mean hypotheses will change with the response list
                        // so we clear it to avoid corruption in the study design
                        hypothesisSet.clear();
                        removeMatrixByName(GlimmpseConstants.MATRIX_THETA);
                    }
                    break;
                case REPEATED_MEASURES:
                    if (primaryHypothesis.getRepeatedMeasuresMapTree() != null &&
                    primaryHypothesis.getRepeatedMeasuresMapTree().size() > 0) {
                        // the hypothesis involves between participant effects, so we need to
                        // clear it to avoid corruption in the study design
                        hypothesisSet.clear();
                    }
                    break;
                }
            }
        }
    }

    /**
     * Determine if we need to clear the beta matrix information when
     * predictors or repeated measures change
     * @param changeType
     */
    private void updateMeans(StudyDesignChangeType changeType) {
        NamedMatrix beta = getMatrixByName(GlimmpseConstants.MATRIX_BETA);

        // get new dimensions
        int newRows = participantGroups.getNumberOfRows();
        if (newRows == 0) {
            // if no groups, then assume a one sample design
            newRows = 1;
        }
        int newCols = 1;
        List<RepeatedMeasuresNode> rmList = studyDesign.getRepeatedMeasuresTree();
        if (rmList != null) {
            for(RepeatedMeasuresNode rmNode: rmList) {
                if (rmNode.getNumberOfMeasurements() != null &&
                        rmNode.getNumberOfMeasurements() > 1) {
                    newCols *= rmNode.getNumberOfMeasurements();
                }
            }
        }
        List<ResponseNode> responsesList = studyDesign.getResponseList();
        if (responsesList != null) {
            newCols *= responsesList.size();
        }

        boolean allocateNewBetaMatrix = true;
        switch (changeType) {
        case BETWEEN_PARTICIPANT_FACTORS:
            if (beta != null && beta.getRows() == newRows) {
                allocateNewBetaMatrix = false;
            }
            break;
        case RESPONSES_LIST:
        case REPEATED_MEASURES:
            if (beta != null && beta.getColumns() == newCols) {
                allocateNewBetaMatrix = false;
            }
            break;
        case COVARIATE:
            allocateNewBetaMatrix = false;
            if (!studyDesign.isGaussianCovariate()) {
                removeMatrixByName(GlimmpseConstants.MATRIX_BETA_RANDOM);
            } else {
                NamedMatrix betaRandom = new NamedMatrix();
                double[][] betaRandomData = new double[1][newCols];
                betaRandom.setRows(1);
                betaRandom.setColumns(newCols);
                betaRandom.setDataFromArray(betaRandomData);
                betaRandom.setName(GlimmpseConstants.MATRIX_BETA_RANDOM);
                studyDesign.setNamedMatrix(betaRandom);
            }
            break;
        }

        // create a new beta matrix with appropriate dimensions
        if (allocateNewBetaMatrix && newRows > 0 && newCols > 0) {
            if (beta == null) {
                beta = new NamedMatrix();
                beta.setName(GlimmpseConstants.MATRIX_BETA);
                studyDesign.setNamedMatrix(beta);
            }
            double[][] betaData = new double[newRows][newCols];
            beta.setRows(newRows);
            beta.setColumns(newCols);
            beta.setDataFromArray(betaData);
        }
    }

    /**
     * Determine if we need to clear the covariance information when
     * response variables or repeated measures change
     * @param changeType
     */
    private void updateCovariance(StudyDesignChangeType changeType) {
        switch (changeType) {
        case RESPONSES_LIST:
            List<ResponseNode> responsesList = studyDesign.getResponseList();
            int numResponses = 0;
            if (responsesList != null) {
                numResponses = responsesList.size();
            }
            Set<Covariance> covarianceList = studyDesign.getCovariance();
            if (covarianceList != null) {
                Iterator<Covariance> iter = covarianceList.iterator();
                while (iter.hasNext()) {
                    Covariance covariance = iter.next();
                    if (covariance.getName().equals(
                            GlimmpseConstants.RESPONSES_COVARIANCE_LABEL)) {
                        if (covariance.getRows() != numResponses) {
                            covariance.setRows(numResponses);
                            covariance.setColumns(numResponses);
                            covariance.setBlob(createIdentityMatrix(numResponses));
                        }
                    }
                }
            } else {
                if (numResponses > 0) {
                    Covariance covariance = new Covariance();
                    covariance.setName(
                            GlimmpseConstants.RESPONSES_COVARIANCE_LABEL);
                    covariance.setType(CovarianceTypeEnum.UNSTRUCTURED_COVARIANCE);
                    covariance.setRows(numResponses);
                    covariance.setColumns(numResponses);
                    covariance.setBlob(createIdentityMatrix(numResponses));
                }
            }            
            break;
        case REPEATED_MEASURES:
            // TODO
            break;
        case COVARIATE:
            if (studyDesign.isGaussianCovariate()) {
                // clear the sigmaYG and sigmaG matrices
                removeMatrixByName(GlimmpseConstants.MATRIX_SIGMA_COVARIATE);
                removeMatrixByName(GlimmpseConstants.MATRIX_SIGMA_OUTCOME_COVARIATE);
            } else {

            }
            break;
        }
    }

    /**
     * Utility function to create a new identity matrix.
     * Used primarily for updating covariance matrices
     * @param size
     * @return
     */
    private Blob2DArray createIdentityMatrix(int size) {
        double[][] data = new double[size][size];
        for(int r = 0; r < size; r++) {
            for(int c = 0; c < size; c++) {
                data[r][c] = (r==c ? 1 : 0);
            }
        }
        return new Blob2DArray(data);
    }

    /**
     * Create a matrix of zeros.
     * @param size
     * @return
     */
    private Blob2DArray createZeroMatrix(int rows, int columns) {
        double[][] data = new double[rows][columns];
        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < columns; c++) {
                data[r][c] = 0;
            }
        }
        return new Blob2DArray(data);
    }

    /**
     * Add a Type I error rate to the study design
     * @param panel panel initiating the change
     * @param alpha the Type I error rate
     */
    public void addTypeIErrorRate(WizardStepPanel panel, double alpha) {
        List<TypeIError> alphaList = studyDesign.getAlphaList();
        if (alphaList == null) {
            alphaList = new ArrayList<TypeIError>();
            studyDesign.setAlphaList(alphaList);
        }
        alphaList.add(new TypeIError(alpha));
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.ALPHA_LIST));
    }

    /**
     * Delete the specified type I error rate
     * @param panel panel initiating the change
     * @param alpha the Type I error rate
     * @param the index in the list
     */
    public void deleteTypeIErrorRate(WizardStepPanel panel, 
            double alpha, int index) {
        List<TypeIError> alphaList = studyDesign.getAlphaList();
        if (alphaList != null) {
            if (index >=0 && index < alphaList.size() &&
                    alphaList.get(index).getAlphaValue() == alpha) {
                alphaList.remove(index);
                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.ALPHA_LIST));
            }
        }
    }

    /**
     * Add a beta scale value to the study design
     * @param panel the panel initiating the change
     * @param betaScale the beta scale value
     */
    public void addBetaScale(WizardStepPanel panel, double betaScale) {
        List<BetaScale> betaScaleList = studyDesign.getBetaScaleList();
        if (betaScaleList == null) {
            betaScaleList = new ArrayList<BetaScale>();
            studyDesign.setBetaScaleList(betaScaleList);
        }
        betaScaleList.add(new BetaScale(betaScale));
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.BETA_SCALE_LIST));
    }

    /**
     * Delete the specified beta scale value
     * @param panel the panel initiating the change
     * @param betaScale the beta scale value
     * @param index the index of the value in the list
     */
    public void deleteBetaScale(WizardStepPanel panel, double betaScale, int index) {
        List<BetaScale> betaScaleList = studyDesign.getBetaScaleList();
        if (betaScaleList != null) {
            if (index >=0 && index < betaScaleList.size() &&
                    betaScaleList.get(index).getValue() == betaScale) {
                betaScaleList.remove(index);
                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.BETA_SCALE_LIST));
            }
        }
    }

    /**
     * Add a sigma scale value to the study design
     * @param panel the panel initiating the change
     * @param sigmaScale the sigma scale value
     */
    public void addSigmaScale(WizardStepPanel panel, double sigmaScale) {
        List<SigmaScale> sigmaScaleList = studyDesign.getSigmaScaleList();
        if (sigmaScaleList == null) {
            sigmaScaleList = new ArrayList<SigmaScale>();
            studyDesign.setSigmaScaleList(sigmaScaleList);
        }
        sigmaScaleList.add(new SigmaScale(sigmaScale));
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.SIGMA_SCALE_LIST));
    }

    /**
     * Delete the specified sigma scale value
     * @param panel the panel initiating the change
     * @param sigmaScale the sigma scale value
     * @param index the index of the value in the list
     */
    public void deleteSigmaScale(WizardStepPanel panel, double sigmaScale, int index) {
        List<SigmaScale> sigmaScaleList = studyDesign.getSigmaScaleList();
        if (sigmaScaleList != null) {
            if (index >=0 && index < sigmaScaleList.size() &&
                    sigmaScaleList.get(index).getValue() == sigmaScale) {
                sigmaScaleList.remove(index);
                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.SIGMA_SCALE_LIST));
            }
        }
    }  

    /**
     * Add a nominal power value to the study design
     * @param panel the panel initiating the change
     * @param power the nominal power value
     */
    public void addNominalPower(WizardStepPanel panel, double power) {
        List<NominalPower> powerList = studyDesign.getNominalPowerList();
        if (powerList == null) {
            powerList = new ArrayList<NominalPower>();
            studyDesign.setNominalPowerList(powerList);
        }
        powerList.add(new NominalPower(power));
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.POWER_LIST));
    }

    /**
     * Delete the specified nominal power value
     * @param panel the panel initiating the change
     * @param power the nominal power value
     * @param index the index of the value in the list
     */
    public void deleteNominalPower(WizardStepPanel panel, double power, int index) {
        List<NominalPower> nominalPowerList = studyDesign.getNominalPowerList();
        if (nominalPowerList != null) {
            if (index >=0 && index < nominalPowerList.size() &&
                    nominalPowerList.get(index).getValue() == power) {
                nominalPowerList.remove(index);
                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.POWER_LIST));
            }
        }
    }

    /**
     * Add a power method to the study design
     * @param panel the panel initiating the change
     * @param powerMethod the nominal power value
     */
    public void addPowerMethod(WizardStepPanel panel, PowerMethodEnum powerMethod) {
        List<PowerMethod> powerMethodList = studyDesign.getPowerMethodList();
        if (powerMethodList == null) {
            powerMethodList = new ArrayList<PowerMethod>();
            studyDesign.setPowerMethodList(powerMethodList);
        }
        powerMethodList.add(new PowerMethod(powerMethod));
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.POWER_METHOD_LIST));
    }

    /**
     * Delete the specified power method from the study design
     * @param panel the panel initiating the change
     * @param powerMethod the power method
     * @param index the index of the value in the list
     */
    public void deletePowerMethod(WizardStepPanel panel, 
            PowerMethodEnum powerMethod, int index) {
        List<PowerMethod> powerMethodList = studyDesign.getPowerMethodList();
        if (powerMethodList != null) {
            if (index >=0 && index < powerMethodList.size() &&
                    powerMethodList.get(index).getPowerMethodEnum() == powerMethod) {
                powerMethodList.remove(index);
                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.POWER_METHOD_LIST));
            }
        }
    }

    /**
     * Add a quantile value to the study design
     * @param panel the panel initiating the change
     * @param quantile the quantile value
     */
    public void addQuantile(WizardStepPanel panel, double quantile) {
        List<Quantile> quantileList = studyDesign.getQuantileList();
        if (quantileList == null) {
            quantileList = new ArrayList<Quantile>();
            studyDesign.setQuantileList(quantileList);
        }
        quantileList.add(new Quantile(quantile));
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.QUANTILE_LIST));
    }

    /**
     * Delete the specified quantile from the study design
     * @param panel the panel initiating the change
     * @param quantile the quantile value
     * @param index the index of the value in the list
     */
    public void deleteQuantile(WizardStepPanel panel, double quantile, int index) {
        List<Quantile> quantileList = studyDesign.getQuantileList();
        if (quantileList != null) {
            if (index >=0 && index < quantileList.size() &&
                    quantileList.get(index).getValue() == quantile) {
                quantileList.remove(index);
                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.QUANTILE_LIST));
            }
        }
    }

    /**
     * Add a per group sample size value to the study design
     * @param panel the panel initiating the change
     * @param perGroupSampleSize the quantile value
     */
    public void addPerGroupSampleSize(WizardStepPanel panel, int sampleSize) {
        List<SampleSize> sampleSizeList = studyDesign.getSampleSizeList();
        if (sampleSizeList == null) {
            sampleSizeList = new ArrayList<SampleSize>();
            studyDesign.setSampleSizeList(sampleSizeList);
        }
        sampleSizeList.add(new SampleSize(sampleSize));
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.PER_GROUP_N_LIST));
    }

    /**
     * Delete the specified quantile from the study design
     * @param panel the panel initiating the change
     * @param quantile the quantile value
     * @param index the index of the value in the list
     */
    public void deletePerGroupSampleSize(WizardStepPanel panel, int sampleSize, int index) {
        List<SampleSize> sampleSizeList = studyDesign.getSampleSizeList();
        if (sampleSizeList != null) {
            if (index >=0 && index < sampleSizeList.size() &&
                    sampleSizeList.get(index).getValue() == sampleSize) {
                sampleSizeList.remove(index);
                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.PER_GROUP_N_LIST));
            }
        }
    }

    /**
     * Add the statistical test to the study design
     * @param panel the panel initiating the change
     * @param test the statistical test
     */
    public void addStatisticalTest(WizardStepPanel panel, StatisticalTestTypeEnum test) {
        List<StatisticalTest> testList = studyDesign.getStatisticalTestList();
        if (testList == null) {
            testList = new ArrayList<StatisticalTest>();
            studyDesign.setStatisticalTestList(testList);
        }
        // insert in the same order as the tests are displayed in the GUI
        int testIndex = getStatisticalTestIndex(test);
        if (testIndex > -1) {
            // find the appropriate insertion point
            int insertIndex = 0;
            for(StatisticalTest currentTest: testList) {
                if (testIndex < getStatisticalTestIndex(currentTest.getType())) {
                    break;
                }
                insertIndex++;
            }
            testList.add(insertIndex, new StatisticalTest(test));
            notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                    StudyDesignChangeType.STATISTICAL_TEST_LIST));
        }
    }

    /**
     * Get the order of the statistical tests.  Allows us to insert in order
     * as displayed in the GUI (rather than depending on the enum order)
     *   
     * @param test
     * @return
     */
    private int getStatisticalTestIndex(StatisticalTestTypeEnum test) {
        switch(test)
        {
        case HLT:
            return 0;
        case PBT:
            return 1;
        case WL:
            return 2;
        case UNIREPBOX:
            return 3;
        case UNIREPGG:
            return 4;
        case UNIREPHF:
            return 5;
        case UNIREP:
            return 6;
        default:
            return -1;
        }   
    }

    /**
     * Remove the statistical test from the study design
     * @param panel the panel initiating the change
     * @param test the statistical test
     */
    public void deleteStatisticalTest(WizardStepPanel panel, StatisticalTestTypeEnum test) {
        List<StatisticalTest> testList = studyDesign.getStatisticalTestList();
        if (testList != null) {
            for(int i = 0; i < testList.size(); i++) {
                StatisticalTest currentTest = testList.get(i);
                if (currentTest.getType() == test) {
                    testList.remove(i);
                    notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                            StudyDesignChangeType.STATISTICAL_TEST_LIST));
                    break;
                }
            }
        }
    }

    /**
     * Update the StudyDesign with the solution type (i.e. power or sample size).
     * @param panel wizard panel initiating the change
     * @param solutionType indicates if the user is solving for power or sample size
     */
    public void setSolutionType(WizardStepPanel panel, SolutionTypeEnum solutionType)
    {
        studyDesign.setSolutionTypeEnum(solutionType);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.SOLVING_FOR));
    }

    /**
     * Store the covariate information in the StudyDesign object.
     * @param panel wizard panel initiating the change
     * @param hasCovariate indicates if the design controls for a Gaussian covariate
     */
    public void setCovariate(WizardStepPanel panel, boolean hasCovariate)
    {
        studyDesign.setGaussianCovariate(hasCovariate);
        // remove the Wilk's lambda and Pillai Bartlett for covariate designs
        if (hasCovariate) {
            deleteStatisticalTest(panel, StatisticalTestTypeEnum.PBT);
            deleteStatisticalTest(panel, StatisticalTestTypeEnum.WL);
        }
        // update the variability
        updateCovariance(StudyDesignChangeType.COVARIATE);
        // update the random portion of the beta matrix
        updateMeans(StudyDesignChangeType.COVARIATE);

        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.COVARIATE));



    }

    /**
     * Store the design essence matrix to the StudyDesign.
     * @param panel wizard panel initiating the change
     * @param designFixed the fixed sub-matrix of the design essence matrix
     * @param designRandom for GLMM(F,g) designs, specifies the random
     * portion of the design essence matrix
     */
    public void setDesignEssenceMatrix(WizardStepPanel panel, NamedMatrix designFixed,
            NamedMatrix designRandom)
    {
        if (designFixed != null) {
            studyDesign.setNamedMatrix(designFixed);
        } else {
            removeMatrixByName(GlimmpseConstants.MATRIX_DESIGN);
        }
        if (designRandom != null) {
            studyDesign.setNamedMatrix(designRandom);
        } else {
            removeMatrixByName(GlimmpseConstants.MATRIX_DESIGN_RANDOM);
        }
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.DESIGN_ESSENCE_MATRIX));
    }

    /**
     * Store the between participant contrast matrix to the StudyDesign.
     * @param panel wizard panel initiating the change
     * @param fixed the fixed sub-matrix of the contrast
     * @param random for GLMM(F,g) designs, specifies the random
     * portion of the contrast
     */
    public void setBetweenParticipantContrast(WizardStepPanel panel, 
            NamedMatrix fixed, NamedMatrix random)
    {
        if (fixed != null) {
            studyDesign.setNamedMatrix(fixed);
        } else {
            removeMatrixByName(GlimmpseConstants.MATRIX_BETWEEN_CONTRAST);
        }
        if (random != null) {
            studyDesign.setNamedMatrix(random);
        } else {
            removeMatrixByName(GlimmpseConstants.MATRIX_BETWEEN_CONTRAST_RANDOM);
        }
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.BETWEEN_CONTRAST_MATRIX));
    }

    /**
     * Store the within participant contrast matrix to the StudyDesign.
     * @param panel wizard panel initiating the change
     * @param withinParticipantContrast the contrast matrix
     */
    public void setWithinParticipantContrast(WizardStepPanel panel, NamedMatrix withinParticipantContrast)
    {
        if (withinParticipantContrast != null) {
            studyDesign.setNamedMatrix(withinParticipantContrast);
        } else {
            removeMatrixByName(GlimmpseConstants.MATRIX_WITHIN_CONTRAST);
        }
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.WITHIN_CONTRAST_MATRIX));
    }

    /**
     * Store the beta matrix to the StudyDesign.
     * @param panel wizard panel initiating the change
     * @param fixed the fixed sub-matrix of the beta matrix
     * @param random for GLMM(F,g) designs, specifies the random
     * portion of the beta matrix
     */
    public void setBeta(WizardStepPanel panel, NamedMatrix fixed, NamedMatrix random)
    {
        if (fixed != null) {
            studyDesign.setNamedMatrix(fixed);
        } else {
            removeMatrixByName(GlimmpseConstants.MATRIX_BETA);
        }
        if (random != null) {
            studyDesign.setNamedMatrix(random);
        } else {
            removeMatrixByName(GlimmpseConstants.MATRIX_BETA_RANDOM);
        }
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.BETA_MATRIX));
    }

    /**
     * Store the covariance matrix for the Gaussian covariate  to the StudyDesign.
     * @param panel wizard panel initiating the change
     * @param sigmaCovariate the covariance matrix of the Gaussian covariate (1x1)
     */
    public void setSigmaCovariate(WizardStepPanel panel, NamedMatrix sigmaCovariate)
    {
        if (sigmaCovariate != null) {
            studyDesign.setNamedMatrix(sigmaCovariate);
        } else {
            removeMatrixByName(GlimmpseConstants.MATRIX_SIGMA_COVARIATE);
        }
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.SIGMA_COVARIATE_MATRIX));
    }

    /**
     * Store the covariance matrix for errors  to the StudyDesign.
     * @param panel wizard panel initiating the change
     * @param sigmaCovariate the covariance matrix of errors
     */
    public void setSigmaError(WizardStepPanel panel, NamedMatrix sigmaError)
    {
        if (sigmaError != null) {
            studyDesign.setNamedMatrix(sigmaError);
        } else {
            removeMatrixByName(GlimmpseConstants.MATRIX_SIGMA_ERROR);
        }
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.SIGMA_ERROR_MATRIX));
    }

    /**
     * Store the covariance matrix of the Gaussian covariate and outcomes  to the StudyDesign.
     * @param panel wizard panel initiating the change
     * @param sigmaYG the covariance matrix of the Gaussian covariate and outcomes
     */
    public void setSigmaOutcomesCovariate(WizardStepPanel panel, NamedMatrix sigmaYG)
    {
        if (sigmaYG != null) {
            studyDesign.setNamedMatrix(sigmaYG);
        } else {
            removeMatrixByName(GlimmpseConstants.MATRIX_SIGMA_OUTCOME_COVARIATE);
        }

        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.SIGMA_OUTCOME_COVARIATE_MATRIX));
    }

    /**
     * Store the covariance matrix of outcomes to the StudyDesign (for GLMM(F,g) designs).
     * @param panel wizard panel initiating the change
     * @param sigmaYG the covariance matrix of the outcomes
     */
    public void setSigmaOutcomes(WizardStepPanel panel, NamedMatrix sigmaY)
    {
        if (sigmaY != null) {
            studyDesign.setNamedMatrix(sigmaY);
        } else {
            removeMatrixByName(GlimmpseConstants.MATRIX_SIGMA_OUTCOME);
        }
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.SIGMA_OUTCOME_MATRIX));
    }

    /**
     * Store the null hypothesis matrix to the StudyDesign.
     * @param panel wizard panel initiating the change
     * @param thetaNull the null hypothesis matrix
     */
    public void setThetaNull(WizardStepPanel panel, NamedMatrix thetaNull)
    {
        if (thetaNull != null) {
            studyDesign.setNamedMatrix(thetaNull);
        } else {
            removeMatrixByName(GlimmpseConstants.MATRIX_THETA);
        }

        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.THETA_NULL_MATRIX));
    }

    /**
     * Add a clustering node
     * @param panel
     * @param clusteringNode
     */
    public void addClusteringNode(WizardStepPanel panel, ClusterNode clusteringNode) {
        List<ClusterNode> clusteringTree = studyDesign.getClusteringTree();
        if (clusteringTree == null) {
            clusteringTree = new ArrayList<ClusterNode>();
            studyDesign.setClusteringTree(clusteringTree);
        }
        clusteringTree.add(clusteringNode);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.CLUSTERING));
    }

    /**
     * Delete a clustering node
     * @param panel
     * @param index
     */
    public void deleteClusteringNode(WizardStepPanel panel, int index) {
        List<ClusterNode> clusteringTree = studyDesign.getClusteringTree();
        if (clusteringTree != null) {
            if (index >=0 && index < clusteringTree.size()) {
                clusteringTree.remove(index);
                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.CLUSTERING));
            }
        }
    }

    /**
     * Update the value of a clustering node
     * @param panel
     * @param index
     * @param clusterName
     * @param clusterSize
     * @param icc
     */
    public void updateClusteringNode(WizardStepPanel panel, int index,
            String clusterName, int clusterSize, double icc) {
        List<ClusterNode> clusteringTree = studyDesign.getClusteringTree();
        if (clusteringTree != null) {
            if (index >=0 && index < clusteringTree.size()) {
                ClusterNode clusterNode = clusteringTree.get(index);
                clusterNode.setGroupName(clusterName);
                clusterNode.setGroupSize(clusterSize);
                clusterNode.setIntraClusterCorrelation(icc);
                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.CLUSTERING));
            }
        }
    }

    /**
     * Add a repeated measures node
     * @param panel the panel initiating the change
     * @param rmNode the repeated measures node
     */
    public void addRepeatedMeasuresNode(WizardStepPanel panel, 
            RepeatedMeasuresNode rmNode) {
        List<RepeatedMeasuresNode> rmTree = studyDesign.getRepeatedMeasuresTree();
        if (rmTree == null) {
            rmTree = new ArrayList<RepeatedMeasuresNode>();
            studyDesign.setRepeatedMeasuresTree(rmTree);
        }
        rmTree.add(rmNode);
        // update the beta matrix
        updateMeans(StudyDesignChangeType.REPEATED_MEASURES);
        // update the variability
        updateCovariance(StudyDesignChangeType.REPEATED_MEASURES);

        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.REPEATED_MEASURES));
    }

    /**
     * Delete a repeated measures node
     * @param panel the panel initiating the change
     * @param index the repeated measures depth
     */
    public void deleteRepeatedMeasuresNode(WizardStepPanel panel, int index) {
        List<RepeatedMeasuresNode> rmTree = studyDesign.getRepeatedMeasuresTree();
        if (rmTree != null) {
            if (index >=0 && index < rmTree.size()) {
                rmTree.remove(index);
                // update the hypothesis
                updateHypothesis(StudyDesignChangeType.REPEATED_MEASURES);
                // update the beta matrix
                updateMeans(StudyDesignChangeType.REPEATED_MEASURES);
                // update the variability
                updateCovariance(StudyDesignChangeType.REPEATED_MEASURES);

                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.REPEATED_MEASURES));
            }
        }
    }

    /**
     * Update the value of a repeated measures node
     * @param panel
     * @param index
     * @param clusterName
     * @param clusterSize
     * @param icc
     */
    public void updateRepeatedMeasuresNode(WizardStepPanel panel, int index,
            String dimension, RepeatedMeasuresDimensionType rmDimensionType,
            int numberOfMeasurements, List<Spacing> spacingList) {
        List<RepeatedMeasuresNode> rmTree = studyDesign.getRepeatedMeasuresTree();
        if (rmTree != null) {
            if (index >=0 && index < rmTree.size()) {
                RepeatedMeasuresNode rmNode = rmTree.get(index);
                rmNode.setDimension(dimension);
                rmNode.setRepeatedMeasuresDimensionType(rmDimensionType);
                rmNode.setNumberOfMeasurements(numberOfMeasurements);
                rmNode.setSpacingList(spacingList);
                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.REPEATED_MEASURES));
            }
        }
    }    

    /**
     * Add a between participant factor to the StudyDesign
     * @param panel wizard panel initiating the change
     * @param predictorName the name of the new predictor
     */
    public void addBetweenParticipantFactor(WizardStepPanel panel, 
            String predictorName)
    {
        List<BetweenParticipantFactor> factorList = 
                studyDesign.getBetweenParticipantFactorList();
        if (factorList == null) {
            factorList = new ArrayList<BetweenParticipantFactor>();
            studyDesign.setBetweenParticipantFactorList(factorList);
        }
        BetweenParticipantFactor factor = new BetweenParticipantFactor();
        factor.setPredictorName(predictorName);
        factor.setCategoryList(new ArrayList<Category>());
        // add the factor to the study design
        factorList.add(factor);
        // add to the table of combinations of factors
        participantGroups.loadBetweenParticipantFactors(factorList);
        // update the relative group sizes
        updateRelativeGroupSizeList(participantGroups.getNumberOfRows());
        // update means
        updateMeans(StudyDesignChangeType.BETWEEN_PARTICIPANT_FACTORS);
        // notify the other screens of the change
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.BETWEEN_PARTICIPANT_FACTORS));
    }

    /**
     * Delete a between participant factor to the StudyDesign
     * @param panel wizard panel initiating the change
     * @param predictorName the name of the predictor
     */
    public void deleteBetweenParticipantFactor(WizardStepPanel panel, 
            String predictorName)
    {
        List<BetweenParticipantFactor> factorList = 
                studyDesign.getBetweenParticipantFactorList();
        if (factorList != null) {
            for(int i = 0; i < factorList.size(); i++) {
                BetweenParticipantFactor factor = factorList.get(i);
                if (predictorName.equals(factor.getPredictorName())) {
                    // remove from the study design
                    factorList.remove(i);
                    // update the table of combinations of factors
                    participantGroups.loadBetweenParticipantFactors(factorList);
                    // update the relative group sizes
                    updateRelativeGroupSizeList(participantGroups.getNumberOfRows());
                    // update hypothesis
                    updateHypothesis(StudyDesignChangeType.BETWEEN_PARTICIPANT_FACTORS);
                    // update means
                    updateMeans(StudyDesignChangeType.BETWEEN_PARTICIPANT_FACTORS);
                    // notify other screens of the change
                    notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                            StudyDesignChangeType.BETWEEN_PARTICIPANT_FACTORS));
                }
            }            
        }
    }

    /**
     * Get the specified between participant factor by name
     * @param factorName name of the factor
     * @return the between participant factor object
     */
    public BetweenParticipantFactor getBetweenParticipantFactor(String factorName) {
        List<BetweenParticipantFactor> factorList = 
                studyDesign.getBetweenParticipantFactorList();
        if (factorList != null) {
            for(BetweenParticipantFactor factor: factorList) {
                if (factorName.equals(factor.getPredictorName())) {
                    return factor;
                }
            }
        }
        return null;
    }

    /**
     * Get the index of the specified between participant factor
     * @param factorName name of the factor
     * @return the index of the between participant factor object
     */
    public int getBetweenParticipantFactorIndex(BetweenParticipantFactor factor) {
        List<BetweenParticipantFactor> factorList = 
                studyDesign.getBetweenParticipantFactorList();
        if (factorList != null) {
            int index = 0;
            for(BetweenParticipantFactor currentFactor: factorList) {
                if (currentFactor == factor) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    /**
     * Get the specified repeated measures factor by name
     * @param factorName name of the factor
     * @return the between participant factor object
     */
    public RepeatedMeasuresNode getRepeatedMeasuresNode(String nodeName) {
        List<RepeatedMeasuresNode> nodeList = 
                studyDesign.getRepeatedMeasuresTree();
        if (nodeList != null) {
            for(RepeatedMeasuresNode node: nodeList) {
                if (nodeName.equals(node.getDimension())) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * Get the specified repeated measures factor by name
     * @param factorName name of the factor
     * @return the between participant factor object
     */
    public int getRepeatedMeasuresNodeIndex(RepeatedMeasuresNode node) {
        List<RepeatedMeasuresNode> nodeList = 
                studyDesign.getRepeatedMeasuresTree();
        if (nodeList != null) {
            int index = 0;
            for(RepeatedMeasuresNode currentNode: nodeList) {
                if (currentNode == node) {
                    return index;
                }
            }
        }
        return -1;
    }

    /**
     * Add a category to a between participant factor in the StudyDesign
     * @param panel wizard panel initiating the change
     * @param predictorName the name of the predictor
     * @param categoryName the name of the new category
     */
    public void addBetweenParticipantFactorCategory(WizardStepPanel panel, 
            String predictorName, String categoryName)
    {
        BetweenParticipantFactor factor = getBetweenParticipantFactor(predictorName);
        if (factor != null) {
            List<Category> categoryList = factor.getCategoryList();
            if (categoryList == null) {
                categoryList = new ArrayList<Category>();
                factor.setCategoryList(categoryList);
            }
            // add to the study design
            categoryList.add(new Category(categoryName));
            // add to the table of combinations of factors
            participantGroups.loadBetweenParticipantFactors(studyDesign.getBetweenParticipantFactorList());       
            // update the relative group sizes
            updateRelativeGroupSizeList(participantGroups.getNumberOfRows());
            // update the means
            updateMeans(StudyDesignChangeType.BETWEEN_PARTICIPANT_FACTORS);
            // notify other screens of the change
            notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                    StudyDesignChangeType.BETWEEN_PARTICIPANT_FACTORS));
        }
    }

    /**
     * Remove a category from a between participant factor in the StudyDesign
     * @param panel wizard panel initiating the change
     * @param predictorName the name of the predictor
     * @param categoryName the name of the new category
     */    
    public void deleteBetweenParticipantFactorCategory(WizardStepPanel panel, 
            String predictorName, String categoryName)
    {
        BetweenParticipantFactor factor = getBetweenParticipantFactor(predictorName);
        if (factor != null) {
            List<Category> categoryList = factor.getCategoryList();
            if (categoryList != null) {
                for(int i = 0; i < categoryList.size(); i++) {
                    Category category = categoryList.get(i);
                    if (categoryName.equals(category.getCategory())) {
                        // remove from the study design
                        categoryList.remove(i);
                        // update the table of combinations of factors
                        participantGroups.loadBetweenParticipantFactors(studyDesign.getBetweenParticipantFactorList());
                        // update the relative group sizes
                        updateRelativeGroupSizeList(participantGroups.getNumberOfRows());
                        // update the means
                        updateMeans(StudyDesignChangeType.BETWEEN_PARTICIPANT_FACTORS);
                        // notify other screens
                        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                                StudyDesignChangeType.BETWEEN_PARTICIPANT_FACTORS));
                        break;
                    }
                }
            }
        }
    }



    /**
     * Add or remove values from the relative size list when 
     * the predictor list changes
     * 
     * @param size
     */
    private void updateRelativeGroupSizeList(int size) {
        List<RelativeGroupSize> relativeSizeList = studyDesign.getRelativeGroupSizeList();
        if (relativeSizeList == null) {
            relativeSizeList = new ArrayList<RelativeGroupSize>();
            studyDesign.setRelativeGroupSizeList(relativeSizeList);
        }
        if (size < relativeSizeList.size()) {
            // shrink the list
            for(int i = relativeSizeList.size()-1; i >= size; i--) {
                relativeSizeList.remove(i);
            }
        } else if (size > relativeSizeList.size()) {
            // expand the list
            for(int i = relativeSizeList.size(); i < size; i++) {
                relativeSizeList.add(new RelativeGroupSize(1));
            }
        }
    }

    /**
     * Store the relative group size information to the StudyDesign.
     * @param panel wizard panel initiating the change
     * @param relativeGroupSizeList list of relative group sizes
     */
    public void setRelativeGroupSizeValue(WizardStepPanel panel, 
            int value, int index)
    {
        List<RelativeGroupSize> relativeGroupSizeList = 
                studyDesign.getRelativeGroupSizeList();
        if (relativeGroupSizeList != null && 
                index >= 0 && index < relativeGroupSizeList.size()) {
            RelativeGroupSize relativeGroupSize = relativeGroupSizeList.get(index);
            relativeGroupSize.setValue(value);
            notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                    StudyDesignChangeType.RELATIVE_GROUP_SIZE_LIST));
        }
    }

    /**
     * Add a response variable to the study design
     * @param panel the panel initiating the change
     * @param variable the response variable
     */
    public void addResponseVariable(WizardStepPanel panel, String variable) {
        List<ResponseNode> responseList = studyDesign.getResponseList();
        if (responseList == null) {
            responseList = new ArrayList<ResponseNode>();
            studyDesign.setResponseList(responseList);
        }
        responseList.add(new ResponseNode(variable));
        // update the hypothesis
        updateHypothesis(StudyDesignChangeType.RESPONSES_LIST);
        // update the means
        updateMeans(StudyDesignChangeType.RESPONSES_LIST);
        // update the variability
        updateCovariance(StudyDesignChangeType.RESPONSES_LIST);

        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.RESPONSES_LIST));
    }

    /**
     * Delete the specified response variable from the study design
     * @param panel the panel initiating the change
     * @param variable the response variable
     * @param index the index of the value in the list
     */
    public void deleteResponseVariable(WizardStepPanel panel, String variable, int index) {
        List<ResponseNode> responseList = studyDesign.getResponseList();
        if (responseList != null) {
            if (index >=0 && index < responseList.size() &&
                    responseList.get(index).getName().equals(variable)) {
                responseList.remove(index);
                // update the hypothesis
                updateHypothesis(StudyDesignChangeType.RESPONSES_LIST);
                // update the means
                updateMeans(StudyDesignChangeType.RESPONSES_LIST);
                // update the variability
                updateCovariance(StudyDesignChangeType.RESPONSES_LIST);

                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.RESPONSES_LIST));
            }
        }
    }

    /**
     * Update the type of hypothesis
     * @param panel
     * @param type
     */
    public void setHypothesisType(WizardStepPanel panel, HypothesisTypeEnum type) {
        Set<Hypothesis> hypothesisSet = studyDesign.getHypothesis();
        Hypothesis hypothesis = null;
        if (hypothesisSet != null) {
            hypothesis = hypothesisSet.iterator().next();
        } 
        // create a new hypothesis if we don't have one yet
        if (hypothesis == null) {
            // create a new hypothesis
            hypothesis = new Hypothesis();
            hypothesis.setType(type);
            studyDesign.setHypothesisToSet(hypothesis);
        }
        // update the type
        if (hypothesis.getType() != type) {
            hypothesis.setType(type);
            // clear the factor lists - these will be updated by subsequent
            // calls to add/remove factors
            List<HypothesisBetweenParticipantMapping> betweenFactorMap = 
                    hypothesis.getBetweenParticipantFactorMapList();
            if (betweenFactorMap != null) {
                betweenFactorMap.clear();
            }
            List<HypothesisRepeatedMeasuresMapping> withinFactorMap = 
                    hypothesis.getRepeatedMeasuresMapTree();
            if (withinFactorMap != null) {
                withinFactorMap.clear();
            }
        }
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.HYPOTHESIS));
    }

    /**
     * Add a between participant factor to the hypothesis object
     * @param panel panel initiating the change
     * @param name name of the factor
     */
    public void addHypothesisBetweenParticipantFactor(WizardStepPanel panel, 
            BetweenParticipantFactor factor) {
        // get the current hypothesis
        Set<Hypothesis> hypothesisSet = studyDesign.getHypothesis();
        Hypothesis hypothesis = null;
        if (hypothesisSet != null) {
            hypothesis = hypothesisSet.iterator().next();
        } 
        if (hypothesis != null) {
            List<HypothesisBetweenParticipantMapping> mapList = 
                    hypothesis.getBetweenParticipantFactorMapList();
            if (mapList == null) {
                mapList = new ArrayList<HypothesisBetweenParticipantMapping>();
                hypothesis.setBetweenParticipantFactorMapList(mapList);
            }
            /*
             * Some notes here: we need to make sure that the relative order of the
             * factors stays constant so the final Kronecker product of contrasts is
             * correct.  Thus the following code attempts to enforce said order for
             * any combination of factors.  Note that users may click the check 
             * boxes in any order
             */
            // add the factor object
            int factorIndex = getBetweenParticipantFactorIndex(factor);
            if (factorIndex > -1) {
                // find the appropriate insertion point
                int insertIndex = 0;
                for(HypothesisBetweenParticipantMapping map: mapList) {
                    if (factorIndex < 
                            getBetweenParticipantFactorIndex(map.getBetweenParticipantFactor())) {
                        break;
                    }
                    insertIndex++;
                }
                // build the mapping
                HypothesisBetweenParticipantMapping mapping = new HypothesisBetweenParticipantMapping();
                mapping.setBetweenParticipantFactor(studyDesign.getBetweenParticipantFactorList().get(factorIndex));
                mapping.setType(HypothesisTrendTypeEnum.NONE);
                mapList.add(insertIndex, mapping);
                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.HYPOTHESIS));
            }
        }
    }

    /**
     * Delete a between participant factor from the hypothesis object
     * @param panel panel initiating the change
     * @param name name of the factor
     */
    public void deleteHypothesisBetweenParticipantFactor(WizardStepPanel panel, 
            BetweenParticipantFactor factor) {
        // add it to the hypothesis
        Set<Hypothesis> hypothesisSet = studyDesign.getHypothesis();
        Hypothesis hypothesis = null;
        if (hypothesisSet != null) {
            hypothesis = hypothesisSet.iterator().next();
        } 
        if (hypothesis != null) {
            List<HypothesisBetweenParticipantMapping> mapList = 
                    hypothesis.getBetweenParticipantFactorMapList();
            for(int i = 0; i < mapList.size(); i++) {
                HypothesisBetweenParticipantMapping mapping = mapList.get(i);
                if (mapping.getBetweenParticipantFactor() == factor) {
                    mapList.remove(i);
                    break;
                }
            }
            notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                    StudyDesignChangeType.HYPOTHESIS));
        }
    }

    /**
     * Update the trend information for a between participant factor in the hypothesis object
     * @param panel panel initiating the change
     * @param name name of the factor
     * @param trendType trend type for the factor
     */
    public void updateHypothesisBetweenParticipantFactorTrend(WizardStepPanel panel, 
            BetweenParticipantFactor factor, HypothesisTrendTypeEnum trendType) {
        // add it to the hypothesis
        Set<Hypothesis> hypothesisSet = studyDesign.getHypothesis();
        Hypothesis hypothesis = null;
        if (hypothesisSet != null) {
            hypothesis = hypothesisSet.iterator().next();
        } 
        if (hypothesis != null) {
            List<HypothesisBetweenParticipantMapping> mapList = 
                    hypothesis.getBetweenParticipantFactorMapList();
            for(HypothesisBetweenParticipantMapping mapping : mapList) {
                if (factor == mapping.getBetweenParticipantFactor()) {
                    mapping.setType(trendType);
                    break;
                }
            }
            notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                    StudyDesignChangeType.HYPOTHESIS));
        }
    }

    /**
     * Add a repeated measures factor to the hypothesis object
     * @param panel panel initiating the change
     * @param name name of the factor
     */
    public void addHypothesisRepeatedMeasuresFactor(WizardStepPanel panel, 
            RepeatedMeasuresNode factor) {
        // get the current hypothesis
        Set<Hypothesis> hypothesisSet = studyDesign.getHypothesis();
        Hypothesis hypothesis = null;
        if (hypothesisSet != null) {
            hypothesis = hypothesisSet.iterator().next();
        } 
        if (hypothesis != null) {
            List<HypothesisRepeatedMeasuresMapping> mapList = 
                    hypothesis.getRepeatedMeasuresMapTree();
            if (mapList == null) {
                mapList = new ArrayList<HypothesisRepeatedMeasuresMapping>();
                hypothesis.setRepeatedMeasuresMapTree(mapList);
            }
            /*
             * Some notes here: we need to make sure that the relative order of the
             * factors stays constant so the final Kronecker product of contrasts is
             * correct.  Thus the following code attempts to enforce said order for
             * any combination of factors.  Note that users may click the check 
             * boxes in any order
             */
            // add the factor object
            int factorIndex = getRepeatedMeasuresNodeIndex(factor);
            if (factorIndex > -1) {
                // find the appropriate insertion point
                int insertIndex = 0;
                for(HypothesisRepeatedMeasuresMapping map: mapList) {
                    if (factorIndex < 
                            getRepeatedMeasuresNodeIndex(
                                    map.getRepeatedMeasuresNode())) {
                        break;
                    }
                    insertIndex++;
                }
                // build the mapping
                HypothesisRepeatedMeasuresMapping mapping = new HypothesisRepeatedMeasuresMapping();
                mapping.setRepeatedMeasuresNode(studyDesign.getRepeatedMeasuresTree().get(factorIndex));
                mapping.setType(HypothesisTrendTypeEnum.NONE);
                mapList.add(insertIndex, mapping);
                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.HYPOTHESIS));
            }
        }
    }

    /**
     * Delete a repeated measures factor from the hypothesis object
     * @param panel panel initiating the change
     * @param name name of the factor
     */
    public void deleteHypothesisRepeatedMeasuresFactor(WizardStepPanel panel, 
            RepeatedMeasuresNode factor) {
        // add it to the hypothesis
        Set<Hypothesis> hypothesisSet = studyDesign.getHypothesis();
        Hypothesis hypothesis = null;
        if (hypothesisSet != null) {
            hypothesis = hypothesisSet.iterator().next();
        } 
        if (hypothesis != null) {
            List<HypothesisRepeatedMeasuresMapping> mapList = 
                    hypothesis.getRepeatedMeasuresMapTree();
            for(int i = 0; i < mapList.size(); i++) {
                HypothesisRepeatedMeasuresMapping mapping = mapList.get(i);
                if (factor == mapping.getRepeatedMeasuresNode()) {
                    mapList.remove(i);
                    break;
                }
            }
            notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                    StudyDesignChangeType.HYPOTHESIS));
        }
    }

    /**
     * Update the trend information for a repeated measures factor in the hypothesis object
     * @param panel panel initiating the change
     * @param name name of the factor
     * @param trendType trend type for the factor
     */
    public void updateHypothesisRepeatedMeasuresFactorTrend(WizardStepPanel panel, 
            RepeatedMeasuresNode factor, HypothesisTrendTypeEnum trendType) {
        // add it to the hypothesis
        Set<Hypothesis> hypothesisSet = studyDesign.getHypothesis();
        Hypothesis hypothesis = null;
        if (hypothesisSet != null) {
            hypothesis = hypothesisSet.iterator().next();
        } 
        if (hypothesis != null) {
            List<HypothesisRepeatedMeasuresMapping> mapList = 
                    hypothesis.getRepeatedMeasuresMapTree();
            for(HypothesisRepeatedMeasuresMapping mapping : mapList) {
                if (factor == mapping.getRepeatedMeasuresNode()) {
                    mapping.setType(trendType);
                    break;
                }
            }
            notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                    StudyDesignChangeType.HYPOTHESIS));
        }
    }

    /**
     * Update the value in the theta null matrix
     * @param panel panel initiating the change
     * @param row cell row
     * @param column cell column
     * @param value new cell value
     */
    public void updateHypothesisThetaNullValue(WizardStepPanel panel, 
            int row, int column, double value) {
        // get the theta null matrix
        NamedMatrix thetaNull = 
                this.getMatrixByName(GlimmpseConstants.MATRIX_THETA);
        if (thetaNull != null && thetaNull.getData() != null &&
                row >= 0 && row < thetaNull.getRows() &&
                column >= 0 && column < thetaNull.getColumns()) {
            thetaNull.getData().getData()[row][column] = value;
            notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                    StudyDesignChangeType.HYPOTHESIS));
        }
    }


    /**
     * Add covariance information in the study design.
     * @param panel wizard panel initiating the change
     * @param covariance covariance for a single dimension
     */
    public void clearCovariance(WizardStepPanel panel)
    {
        Set<Covariance> covarianceSet = studyDesign.getCovariance();
        if (covarianceSet != null) {
            covarianceSet.clear();
        }
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.COVARIANCE));
    }

    /**
     * Add covariance information in the study design.
     * @param panel wizard panel initiating the change
     * @param covariance covariance for a single dimension
     */
    public void setCovariance(WizardStepPanel panel, Covariance covariance)
    {
        studyDesign.addCovariance(covariance);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.COVARIANCE));
    }

    /**
     * Add an empty power confidence interval description to the context
     * @param panel wizard panel initiating the change
     * @param confidenceIntervalDescription confidence interval description
     */
    public void addConfidenceIntervalOptions(WizardStepPanel panel)
    {
        studyDesign.setConfidenceIntervalDescriptions(new ConfidenceIntervalDescription());
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.CONFIDENCE_INTERVAL));
    }

    /**
     * Store the power confidence interval description to the context
     * @param panel wizard panel initiating the change
     * @param confidenceIntervalDescription confidence interval description
     */
    public void deleteConfidenceIntervalOptions(WizardStepPanel panel)
    {
        studyDesign.setConfidenceIntervalDescriptions(null);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.CONFIDENCE_INTERVAL));
    }

    /**
     * Store the power confidence interval description to the context
     * @param panel wizard panel initiating the change
     * @param confidenceIntervalDescription confidence interval description
     */
    public void updateConfidenceIntervalOptions(WizardStepPanel panel, 
            boolean betaFixed, boolean sigmaFixed, 
            double lowerTailProbability, double upperTailProbability,
            int sampleSize, int rankOfDesignMatrix)
    {
        ConfidenceIntervalDescription ciDescr = studyDesign.getConfidenceIntervalDescriptions();
        if (ciDescr != null) {
            ciDescr.setBetaFixed(betaFixed);
            ciDescr.setSigmaFixed(sigmaFixed);
            ciDescr.setLowerTailProbability(lowerTailProbability);
            ciDescr.setUpperTailProbability(upperTailProbability);
            ciDescr.setSampleSize(sampleSize);
            ciDescr.setRankOfDesignMatrix(rankOfDesignMatrix);
            notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                    StudyDesignChangeType.CONFIDENCE_INTERVAL));
        }

    }

    /**
     * Store the power curve description to the context
     * @param panel wizard panel initiating the change
     * @param curveDescription power curve description
     */
    public void setPowerCurveDescription(WizardStepPanel panel,
            PowerCurveDescription curveDescription) {
        studyDesign.setPowerCurveDescriptions(curveDescription);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.POWER_CURVE));
    }

    /**
     * Checks if the study design is complete
     */
    @Override
    public void checkComplete() {
        if (StudyDesignViewTypeEnum.MATRIX_MODE == studyDesign.getViewTypeEnum()) {
            checkCompleteMatrixOnly();
        } else {
            checkCompleteGuided();
        }
    }

    /**
     * Checks if a "guided" study design is complete.
     * @return true if complete, false otherwise
     */
    private void checkCompleteGuided() {
        Set<NamedMatrix> matrixSet = studyDesign.getMatrixSet();
        boolean hasCovariate = studyDesign.isGaussianCovariate();

        if (studyDesign.getSolutionTypeEnum() == null) {
            complete =  false;
        } else {
            boolean hasBeta = false;
            boolean hasBetaRandom = !hasCovariate;
            boolean hasSigmaG = !hasCovariate;
            boolean hasSigmaYG = !hasCovariate;
            boolean hasThetaNull = true;
            // check if we have some of the required matrices
            if (matrixSet != null) {
                for(NamedMatrix matrix: matrixSet) {
                    String name = matrix.getName();
                    if (matrix != null) {
                        if  (GlimmpseConstants.MATRIX_BETA.equals(name)) {
                            // do we have means?
                            hasBeta = true;
                        } else if (GlimmpseConstants.MATRIX_THETA.equals(name)) {
                            // do we have a null hypothesis?
                            hasThetaNull = true;
                        }
                        if (hasCovariate) {
                            // check matrices for covariate designs
                            if (GlimmpseConstants.MATRIX_BETA_RANDOM.equals(name)) {
                                hasBetaRandom = true;
                            } else if (GlimmpseConstants.MATRIX_SIGMA_OUTCOME_COVARIATE.equals(name)) {
                                hasSigmaYG = true;
                            } else if (GlimmpseConstants.MATRIX_SIGMA_COVARIATE.equals(name)) {
                                hasSigmaG = true;
                            }
                        } 
                    }
                }
            }

            // do we have fixed predictors?
            // do we have at least 1 response?
            // do we have a hypothesis?
            // if repeated measures are present, are they valid?
            // if clustering is present, is it valid?
            complete = (validBetweenFactors() &&
                    validResponses() &&
                    validHypothesis(hasThetaNull) &&
                    validRepeatedMeasures() &&
                    validClustering() &&
                    validLists() && validOptions() &&
                    (hasBeta && hasBetaRandom && hasSigmaYG && hasSigmaG)
                    );
        }
    }

    /**
     * Returns true if a matrix only study design is complete
     * with all required matrices.
     * @return true if complete, false otherwise
     */
    private void checkCompleteMatrixOnly() {
        Set<NamedMatrix> matrixSet = studyDesign.getMatrixSet();
        boolean gaussianCovariate = studyDesign.isGaussianCovariate();

        // make sure the solution type is set
        if (studyDesign.getSolutionTypeEnum() == null) {
            complete = false;
        } else {

            // flags to make sure all matrices are present.  This avoids
            // multiple calls to hasNamedMatrix so we limit traversal of the 
            // matrix set.
            boolean hasX = false;
            boolean hasBeta = false;
            boolean hasBetaRandom = !gaussianCovariate;
            boolean hasC = false;
            boolean hasCRandom = !gaussianCovariate;
            boolean hasU = false;
            boolean hasThetaNull = false;
            boolean hasSigmaE = gaussianCovariate;
            boolean hasSigmaY = !gaussianCovariate;
            boolean hasSigmaYG = !gaussianCovariate;
            boolean hasSigmaG = !gaussianCovariate;

            // spin over the matrices and make sure all are present
            if (matrixSet != null) {
                for (NamedMatrix matrix: matrixSet) {
                    String name = matrix.getName();
                    if (matrix != null) {
                        // check matrices used for both covariate/fixed designs
                        if (GlimmpseConstants.MATRIX_DESIGN.equals(name)) {
                            hasX = true;
                        } else if  (GlimmpseConstants.MATRIX_BETA.equals(name)) {
                            hasBeta = true;
                        } else if (GlimmpseConstants.MATRIX_BETWEEN_CONTRAST.equals(name)) {
                            hasC = true;
                        } else if  (GlimmpseConstants.MATRIX_WITHIN_CONTRAST.equals(name)) {
                            hasU = true;
                        } else if (GlimmpseConstants.MATRIX_THETA.equals(name)) {
                            hasThetaNull = true;
                        }

                        if (gaussianCovariate) {
                            // check matrices for covariate designs
                            if (GlimmpseConstants.MATRIX_BETA_RANDOM.equals(matrix.getName())) {
                                hasBetaRandom = true;
                            } else if (GlimmpseConstants.MATRIX_BETWEEN_CONTRAST_RANDOM.equals(matrix.getName())) {
                                hasCRandom = true;
                            } else if (GlimmpseConstants.MATRIX_SIGMA_OUTCOME.equals(matrix.getName())) {
                                hasSigmaY = true;
                            } else if (GlimmpseConstants.MATRIX_SIGMA_OUTCOME_COVARIATE.equals(matrix.getName())) {
                                hasSigmaYG = true;
                            } else if (GlimmpseConstants.MATRIX_SIGMA_COVARIATE.equals(matrix.getName())) {
                                hasSigmaG = true;
                            }
                        } else {
                            if (GlimmpseConstants.MATRIX_SIGMA_ERROR.equals(matrix.getName())) {
                                hasSigmaE = true;
                            }
                        }
                    }
                }
            }
            complete = validLists() && validOptions() &&
                    (hasX && hasBeta && hasBetaRandom && hasC && hasCRandom 
                            && hasU && hasThetaNull
                            && hasSigmaE && hasSigmaY && hasSigmaYG && hasSigmaG);
        }
    }

    /**
     * Returns true if the list of factors is valid
     * @param factorList between participant factors
     * @return true if valid, false otherwise
     */
    private boolean validBetweenFactors() {
        List<BetweenParticipantFactor> factorList = 
                studyDesign.getBetweenParticipantFactorList();
        boolean complete = false;
        if (factorList != null && factorList.size() > 0) {
            if (factorList.size() == 1) {
                List<Category> categoryList = factorList.get(0).getCategoryList();
                complete = (categoryList != null && categoryList.size() > 0);
            } else {
                complete = true;
                for(BetweenParticipantFactor factor: factorList) {
                    List<Category> categoryList = factor.getCategoryList();
                    if (categoryList.size() < 2) {
                        complete = false;
                        break;
                    }
                }
            }
        } 

        return complete;
    }

    /**
     * Returns true if the list of factors is valid
     * @param factorList between participant factors
     * @return true if valid, false otherwise
     */
    private boolean validResponses() {
        List<ResponseNode> responseList = studyDesign.getResponseList();
        if (responseList != null && responseList.size() > 0 &&
                validCovariance(
                        studyDesign.getCovarianceFromSet(
                                GlimmpseConstants.RESPONSES_COVARIANCE_LABEL
                                ), responseList.size())) {
            return true;
        } 

        return false;
    }

    /**
     * Checks if all required list inputs have been entered (used to validate both
     * matrix and guided designs).
     * @return true if all lists are valid, false otherwise
     */
    private boolean validLists() {       
        // check for lists required by specific solution types
        if (studyDesign.getSolutionTypeEnum() != null) {
            switch(studyDesign.getSolutionTypeEnum()) {
            case POWER:
                if (studyDesign.getSampleSizeList() == null
                || studyDesign.getSampleSizeList().size() < 1
                || studyDesign.getBetaScaleList() == null 
                || studyDesign.getBetaScaleList().size() < 1) {
                    return false;
                }
                break;
            case SAMPLE_SIZE:
                if (studyDesign.getNominalPowerList() == null
                || studyDesign.getNominalPowerList().size() < 1
                || studyDesign.getBetaScaleList() == null 
                || studyDesign.getBetaScaleList().size() < 1) {
                    return false;
                }
                break;
            case DETECTABLE_DIFFERENCE:
                if (studyDesign.getSampleSizeList() == null
                || studyDesign.getSampleSizeList().size() < 1
                || studyDesign.getNominalPowerList() == null 
                || studyDesign.getNominalPowerList().size() < 1) {
                    return false;
                }
                break;
            default:
                return false; // null or invalid
            }

            if (studyDesign.getAlphaList() == null || studyDesign.getAlphaList().size() < 1
                    ||studyDesign.getSigmaScaleList() == null || studyDesign.getSigmaScaleList().size() < 1
                    ||studyDesign.getStatisticalTestList() == null || studyDesign.getStatisticalTestList().size() < 1) {
                return false;
            }

            if (studyDesign.isGaussianCovariate()) {
                if (studyDesign.getPowerMethodList() == null || studyDesign.getPowerMethodList().size() < 1) {
                    return false;
                } else {
                    for(PowerMethod method : studyDesign.getPowerMethodList()) {
                        if (PowerMethodEnum.QUANTILE ==method.getPowerMethodEnum()
                                && (studyDesign.getPowerMethodList() == null 
                                || studyDesign.getPowerMethodList().size() < 1)){
                            return false;
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }

    }

    /**
     * Indicates if the options are completely specified in the StudyDesign object
     * @return true if complete, false otherwise
     */
    private boolean validOptions() {
        PowerCurveDescription curveDescr = studyDesign.getPowerCurveDescriptions();
        ConfidenceIntervalDescription ciDescr = studyDesign.getConfidenceIntervalDescriptions();
        return (
                (curveDescr == null ||
                (curveDescr.getHorizontalAxisLabelEnum() != null &&
                curveDescr.getDataSeriesList() != null &&
                curveDescr.getDataSeriesList().size() > 0)) &&
                (ciDescr == null ||
                (ciDescr.getLowerTailProbability() >= 0 &&
                ciDescr.getUpperTailProbability() >= 0 &&
                ciDescr.getSampleSize() > 1 &&
                ciDescr.getRankOfDesignMatrix() > 0))
                );
    }

    /**
     * Checks if the context has a valid hypothesis
     * @param hypothesisSet
     * @param hasThetaNull
     * @return
     */
    private boolean validHypothesis(boolean hasThetaNull) {
        Set<Hypothesis> hypothesisSet = studyDesign.getHypothesis();
        if (hypothesisSet != null && hypothesisSet.size() > 0) {
            for(Hypothesis hypothesis: hypothesisSet) {
                switch (hypothesis.getType()) {
                case GRAND_MEAN:
                    return hasThetaNull;
                case MAIN_EFFECT:
                    if ((hypothesis.getBetweenParticipantFactorList() == null ||
                    hypothesis.getBetweenParticipantFactorList().size() <= 0) &&
                    (hypothesis.getRepeatedMeasuresList() == null ||
                    hypothesis.getRepeatedMeasuresList().size() <= 0)) {
                        return false;
                    } else {
                        return true;
                    }
                case TREND:
                    if ((hypothesis.getBetweenParticipantFactorList() == null ||
                    hypothesis.getBetweenParticipantFactorList().size() <= 0 ||
                    hypothesis.getBetweenParticipantFactorMapList() == null ||
                    hypothesis.getBetweenParticipantFactorMapList().size() <= 0) &&
                    (hypothesis.getRepeatedMeasuresList() == null ||
                    hypothesis.getRepeatedMeasuresList().size() <= 0 ||
                    hypothesis.getRepeatedMeasuresMapTree() == null ||
                    hypothesis.getRepeatedMeasuresMapTree().size() <= 0)) {
                        return false;
                    } else {
                        return true;
                    }
                case INTERACTION:
                    int totalFactors = 0;
                    if (hypothesis.getBetweenParticipantFactorMapList()  != null) {
                        totalFactors += hypothesis.getBetweenParticipantFactorMapList().size();
                    }
                    if (hypothesis.getRepeatedMeasuresMapTree() != null) {
                        totalFactors += hypothesis.getRepeatedMeasuresMapTree().size();
                    }
                    if (totalFactors <= 1) {
                        return false;
                    } else {
                        return true;
                    }
                }
                // only one hypothesis so kick out of the loop here
                break;
            }
        }
        return false;
    }

    /**
     * Make sure the repeated measures are valid in the studydesign object
     * @param rmList repeated measures information
     * @return true if valid, false otherwise
     */
    private boolean validRepeatedMeasures() {
        List<RepeatedMeasuresNode> rmList = studyDesign.getRepeatedMeasuresTree();
        Set<Covariance> covarianceSet = studyDesign.getCovariance();
        if (rmList == null &&
                covarianceSet != null &&
                covarianceSet.size() == 1) {
            // NO repeated measures set, expect 1 covariance in set
            return true;
        } else if (rmList != null && 
                covarianceSet != null && 
                covarianceSet.size() > 1) {
            // repeated measures set, make sure we have covariance for each
            for(RepeatedMeasuresNode rmNode: rmList) {
                if (rmNode.getDimension() != null && !rmNode.getDimension().isEmpty()
                        && rmNode.getNumberOfMeasurements() > 1) {
                    if (rmNode.getRepeatedMeasuresDimensionType() == 
                            RepeatedMeasuresDimensionType.NUMERICAL) {
                        if (rmNode.getSpacingList() == null || rmNode.getSpacingList().size() <= 0) {
                            return false;
                        }
                    }
                    if (!validCovariance(studyDesign.getCovarianceFromSet(
                            rmNode.getDimension()), rmNode.getNumberOfMeasurements())) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        } else {
            // repeated measures set but no covariance set
            return false;
        }
    }

    /**
     * Checks if the specified covariance object is valid
     * @param covariance covariance object
     * @return true if valid, false otherwise
     */
    private boolean validCovariance(Covariance covariance, 
            int desiredDimension) {
        if (covariance == null) {
            return false;
        } else {
            if (covariance.getRows() != covariance.getColumns() ||
                    covariance.getRows() != desiredDimension)
                switch (covariance.getType()) {
                case LEAR_CORRELATION:
                    if (covariance.getDelta() <= 0 || 
                    covariance.getRho() < -1 || covariance.getRho() > 1 ||
                    covariance.getStandardDeviationList() == null || 
                    covariance.getStandardDeviationList().size() != 1 ||
                    covariance.getStandardDeviationList().get(0).getValue() <= 0) {
                        return false;
                    }
                    break;
                case UNSTRUCTURED_CORRELATION:
                    if (covariance.getBlob() == null ||
                    covariance.getBlob().getData() == null ||
                    covariance.getStandardDeviationList() == null ||
                    covariance.getStandardDeviationList().size() != covariance.getRows()) {
                        return false;
                    } else {
                        // make sure the std dev list is valid
                        for(StandardDeviation stddev: covariance.getStandardDeviationList()) {
                            if (stddev.getValue() <= 0) {
                                return false;
                            }
                        }
                    }
                    break;
                case UNSTRUCTURED_COVARIANCE:
                    if (covariance.getBlob() == null ||
                    covariance.getBlob().getData() == null) {
                        return false;
                    }
                    break;
                default:
                    return false;
                }
        }
        return true;
    }

    /**
     * Check if clustering information is valid in the context.  Clustering is
     * optional, so null is considered valid.
     * @param clusteringList
     * @return
     */
    private boolean validClustering() {
        List<ClusterNode> clusteringList = studyDesign.getClusteringTree();
        if (clusteringList == null) {
            return true;
        } else {
            for(ClusterNode node: clusteringList) {
                if (node.getGroupName() == null ||
                        node.getGroupName().isEmpty() ||
                        node.getGroupSize() <= 1 ||
                        node.getIntraClusterCorrelation() < -1 ||
                        node.getIntraClusterCorrelation() > 1) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Convenience routine to get a matrix from the study design.
     * 
     * @param name name of matrix to get.
     */
    private NamedMatrix getMatrixByName(String name) {
        Set<NamedMatrix> matrixSet = studyDesign.getMatrixSet();
        if (matrixSet != null) {
            for(NamedMatrix matrix : matrixSet) {
                if (matrix.getName() != null && 
                        matrix.getName().equals(name)) {
                    return matrix;
                }
            }
        }
        return null;
    }

    /**
     * Convenience routine to remove a matrix from the study design.
     * 
     * @param name name of matrix to remove.
     */
    private void removeMatrixByName(String name) {
        // TODO: move this to StudyDesign object in domain layer?
        Set<NamedMatrix> matrixSet = studyDesign.getMatrixSet();
        if (matrixSet != null) {
            for(NamedMatrix matrix : matrixSet) {
                if (matrix.getName() != null && 
                        matrix.getName().equals(name)) {
                    matrixSet.remove(matrix);
                    break;
                }
            }
        }
    }

}
