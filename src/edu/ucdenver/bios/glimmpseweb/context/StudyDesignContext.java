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

import com.google.gwt.core.client.GWT;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent.StudyDesignChangeType;
import edu.ucdenver.bios.webservice.common.domain.BetaScale;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Category;
import edu.ucdenver.bios.webservice.common.domain.ClusterNode;
import edu.ucdenver.bios.webservice.common.domain.ConfidenceIntervalDescription;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.Hypothesis;
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
import edu.ucdenver.bios.webservice.common.domain.StatisticalTest;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;
import edu.ucdenver.bios.webservice.common.domain.TypeIError;
import edu.ucdenver.bios.webservice.common.enums.PowerMethodEnum;
import edu.ucdenver.bios.webservice.common.enums.SolutionTypeEnum;
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
        notifyWizardContextLoad();
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
     * Store the list of Type I error values in the StudyDesign object.
     * @param panel wizard panel initiating the change
     * @param alphaList list of Type I error values
     */
    public void setAlphaList(WizardStepPanel panel, ArrayList<TypeIError> alphaList)
    {
        studyDesign.setAlphaList(alphaList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.ALPHA_LIST));
    }

    /**
     * Store the list of beta scale values in the StudyDesign object.
     * @param panel wizard panel initiating the change
     * @param betaScaleList list of beta scale values
     */
    public void setBetaScaleList(WizardStepPanel panel, List<BetaScale> betaScaleList)
    {
        studyDesign.setBetaScaleList(betaScaleList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.BETA_SCALE_LIST));
    }

    /**
     * Store the list of sigma scale values in the StudyDesign object.
     * @param panel wizard panel initiating the change
     * @param sigmaScaleList list of sigma scale values
     */
    public void setSigmaScaleList(WizardStepPanel panel, List<SigmaScale> sigmaScaleList)
    {
        studyDesign.setSigmaScaleList(sigmaScaleList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.SIGMA_SCALE_LIST));
    }

    /**
     * Store the list of nominal power values in the StudyDesign object. Specified
     * when solving for sample size.
     * @param panel wizard panel initiating the change
     * @param powerList list of nominal power values
     */
    public void setNominalPowerList(WizardStepPanel panel, ArrayList<NominalPower> powerList)
    {
        studyDesign.setNominalPowerList(powerList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.POWER_LIST));
    }

    /**
     * Store the list of power methods in the StudyDesign object. 
     * @param panel wizard panel initiating the change
     * @param powerMethodList list of power methods
     */
    public void setPowerMethodList(WizardStepPanel panel, ArrayList<PowerMethod> powerMethodList)
    {
        studyDesign.setPowerMethodList(powerMethodList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.POWER_METHOD_LIST));
    }

    /**
     * Store the list of quantiles in the StudyDesign object. Specified when using 
     * the quantile power method
     * @param panel wizard panel initiating the change
     * @param quantileList list of quantiles
     */
    public void setQuantileList(WizardStepPanel panel, ArrayList<Quantile> quantileList)
    {
        studyDesign.setQuantileList(quantileList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.QUANTILE_LIST));
    }

    /**
     * Store the list of sample size values in the StudyDesign object. Specified when 
     * solving for power.
     * @param panel wizard panel initiating the change
     * @param sampleSizeList list of sample sizes
     */
    public void setPerGroupSampleSizeList(WizardStepPanel panel, 
            ArrayList<SampleSize> sampleSizeList)
    {
        studyDesign.setSampleSizeList(sampleSizeList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.PER_GROUP_N_LIST));
    }

    /**
     * Store the list of statistical tests in the StudyDesign object. 
     * @param panel wizard panel initiating the change
     * @param statisticalTestList list of tests
     */
    public void setStatisticalTestList(WizardStepPanel panel, ArrayList<StatisticalTest> statisticalTestList)
    {
        studyDesign.setStatisticalTestList(statisticalTestList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.STATISTICAL_TEST_LIST));
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
            removeMatrixByName(GlimmpseConstants.MATRIX_DESIGN);
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
     * Store the clustering information to the StudyDesign.
     * @param panel wizard panel initiating the change
     * @param clusteringNodeList tree of clustering information
     */
    public void setClustering(WizardStepPanel panel, ArrayList<ClusterNode> clusteringNodeList)
    {
        studyDesign.setClusteringTree(clusteringNodeList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.CLUSTERING));
    }

    /**
     * Store the repeated measures information to the StudyDesign.
     * @param panel wizard panel initiating the change
     * @param repeatedMeasuresNodeList tree of repeated measures information
     */
    public void setRepeatedMeasures(WizardStepPanel panel, 
            ArrayList<RepeatedMeasuresNode> repeatedMeasuresNodeList)
    {
        studyDesign.setRepeatedMeasuresTree(repeatedMeasuresNodeList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.REPEATED_MEASURES));
    }

    /**
     * Store the between participant factor information to the StudyDesign.
     * @param panel wizard panel initiating the change
     * @param factorList list of between participant factors
     */
    public void setBetweenParticipantFactorList(WizardStepPanel panel, 
            List<BetweenParticipantFactor> factorList)
    {
        this.participantGroups.loadBetweenParticipantFactors(factorList);
        studyDesign.setBetweenParticipantFactorList(factorList);
        // clear the relative group sizes
        studyDesign.setRelativeGroupSizeList(null);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.BETWEEN_PARTICIPANT_FACTORS));
    }

    /**
     * Store the relative group size information to the StudyDesign.
     * @param panel wizard panel initiating the change
     * @param relativeGroupSizeList list of relative group sizes
     */
    public void setRelativeGroupSizeList(WizardStepPanel panel, 
            List<RelativeGroupSize> relativeGroupSizeList)
    {
        studyDesign.setRelativeGroupSizeList(relativeGroupSizeList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.RELATIVE_GROUP_SIZE_LIST));
    }

    /**
     * Store the response variable information to the StudyDesign.
     * @param panel wizard panel initiating the change
     * @param responseList list of response variables
     */
    public void setResponseList(WizardStepPanel panel, String label,
            List<ResponseNode> responseList)
    {
        studyDesign.setResponseList(responseList);
        studyDesign.setParticipantLabel(label);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.RESPONSES_LIST));
    }

    /**
     * Store the hypothesis in the study design
     * @param panel wizard panel initiating the change
     * @param hypothesis hypothesis objects
     */
    public void setHypothesis(WizardStepPanel panel, Hypothesis hypothesis)
    {
        studyDesign.setHypothesisToSet(hypothesis);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.HYPOTHESIS));
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
     * Store the power confidence interval description to the context
     * @param panel wizard panel initiating the change
     * @param confidenceIntervalDescription confidence interval description
     */
    public void setConfidenceIntervalOptions(WizardStepPanel panel, ConfidenceIntervalDescription confidenceIntervalDescription)
    {
        studyDesign.setConfidenceIntervalDescriptions(confidenceIntervalDescription);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.CONFIDENCE_INTERVAL));
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
        boolean gaussianCovariate = studyDesign.isGaussianCovariate();

        boolean hasBetweenFactors = false;
        int totalWithinFactors = 0;
        boolean hasHypothesis = false;
        boolean hasCovariance = false;
        boolean hasThetaNull = false;
        boolean hasBeta = false;
        boolean hasBetaRandom = !gaussianCovariate;

        // do we have fixed predictors?
        List<BetweenParticipantFactor> betweenFactorList = studyDesign.getBetweenParticipantFactorList();
        if (validBetweenFactors(betweenFactorList)) {
            
        } else {
            complete = false;
        }
        hasBetweenFactors = (betweenFactorList != null && betweenFactorList.size() > 0);
        // do we have responses?
        List<ResponseNode> responseList = studyDesign.getResponseList();
        if (responseList != null) {
            totalWithinFactors++;
        }
        // do we have repeated measures (optional) ?
        List<RepeatedMeasuresNode> rmNodeList = studyDesign.getRepeatedMeasuresTree();
        if (rmNodeList != null) {
            totalWithinFactors += rmNodeList.size();
        }

        // do we have a hypothesis?
        Set<Hypothesis> hypothesisSet = studyDesign.getHypothesis();
        hasHypothesis = (hypothesisSet != null && hypothesisSet.size() > 0);

        if (matrixSet != null) {
            Iterator<NamedMatrix> iterator = matrixSet.iterator();
            while (iterator.hasNext()) {
                NamedMatrix matrix = iterator.next();
                if (matrix != null) {
                    if  (GlimmpseConstants.MATRIX_BETA.equals(matrix.getName())) {
                        // do we have means?
                        hasBeta = true;
                    } else if (GlimmpseConstants.MATRIX_THETA.equals(matrix.getName())) {
                        // do we have a null hypothesis?
                        hasThetaNull = true;
                    }

                    if (gaussianCovariate) {
                        // check matrices for covariate designs
                        if (GlimmpseConstants.MATRIX_BETA_RANDOM.equals(matrix.getName())) {
                            hasBetaRandom = true;
                        }
                    } 
                }
            }
        }
        // do we have covariance information?
        Set<Covariance> covarianceSet = studyDesign.getCovariance();
        hasCovariance = (covarianceSet != null && covarianceSet.size() == totalWithinFactors);

        complete = (validLists() && validOptions() &&
                hasBetweenFactors && 
                totalWithinFactors > 0 &&
                hasHypothesis && 
                hasCovariance && 
                hasBeta && 
                hasBetaRandom &&
                hasThetaNull);

        GWT.log("Study design complete? " + complete);
        complete= true;
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
                Iterator<NamedMatrix> iterator = matrixSet.iterator();
                while (iterator.hasNext()) {
                    NamedMatrix matrix = iterator.next();
                    if (matrix != null) {
                        // check matrices used for both covariate/fixed designs
                        if (GlimmpseConstants.MATRIX_DESIGN.equals(matrix.getName())) {
                            hasX = true;
                        } else if  (GlimmpseConstants.MATRIX_BETA.equals(matrix.getName())) {
                            hasBeta = true;
                        } else if (GlimmpseConstants.MATRIX_BETWEEN_CONTRAST.equals(matrix.getName())) {
                            hasC = true;
                        } else if  (GlimmpseConstants.MATRIX_WITHIN_CONTRAST.equals(matrix.getName())) {
                            hasU = true;
                        } else if (GlimmpseConstants.MATRIX_THETA.equals(matrix.getName())) {
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

    private boolean validBetweenFactors(List<BetweenParticipantFactor> factorList) {
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
                                        ((ciDescr.isBetaFixed() || ciDescr.isSigmaFixed()) &&
                                                ciDescr.getLowerTailProbability() >= 0 &&
                                                ciDescr.getUpperTailProbability() >= 0 &&
                                                ciDescr.getSampleSize() > 1 &&
                                                ciDescr.getRankOfDesignMatrix() > 0))
        );
    }

    /**
     * Convenience routine to remove a matrix from the study design.
     * 
     * @param name name of matrix to remove.
     */
    private void removeMatrixByName(String name) {
        // TODO: move this to StudyDesign object in domain layer?
        Set<NamedMatrix> matrixSet = studyDesign.getMatrixSet();
        for(NamedMatrix matrix : matrixSet) {
            if (matrix.getName() != null && 
                    matrix.getName().equals(name)) {
                matrixSet.remove(matrix);
                break;
            }
        }
    }

}
