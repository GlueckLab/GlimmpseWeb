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
import java.util.List;
import java.util.Set;

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
import edu.ucdenver.bios.webservice.common.domain.StandardDeviation;
import edu.ucdenver.bios.webservice.common.domain.StatisticalTest;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;
import edu.ucdenver.bios.webservice.common.domain.TypeIError;
import edu.ucdenver.bios.webservice.common.enums.PowerMethodEnum;
import edu.ucdenver.bios.webservice.common.enums.RepeatedMeasuresDimensionType;
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
            if (index >=0 && index < alphaList.size()-1 &&
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
            if (index >=0 && index < betaScaleList.size()-1 &&
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
            if (index >=0 && index < sigmaScaleList.size()-1 &&
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
            if (index >=0 && index < nominalPowerList.size()-1 &&
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
    public void addNominalPower(WizardStepPanel panel, PowerMethodEnum powerMethod) {
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
    public void deleteNominalPower(WizardStepPanel panel, 
            PowerMethodEnum powerMethod, int index) {
        List<PowerMethod> powerMethodList = studyDesign.getPowerMethodList();
        if (powerMethodList != null) {
            if (index >=0 && index < powerMethodList.size()-1 &&
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
            if (index >=0 && index < quantileList.size()-1 &&
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
            if (index >=0 && index < sampleSizeList.size()-1 &&
                    sampleSizeList.get(index).getValue() == sampleSize) {
                sampleSizeList.remove(index);
                notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                        StudyDesignChangeType.PER_GROUP_N_LIST));
            }
        }
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
            for(BetweenParticipantFactor factor: factorList) {
                if (predictorName.equals(factor.getPredictorName())) {
                    // remove from the study design
                    factorList.remove(factor);
                    // update the table of combinations of factors
                    participantGroups.loadBetweenParticipantFactors(factorList);
                    // update the relative group sizes
                    updateRelativeGroupSizeList(participantGroups.getNumberOfRows());
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
                for(Category category: categoryList) {
                    if (categoryName.equals(category.getCategory())) {
                        // remove from the study design
                        categoryList.remove(category);
                        // update the table of combinations of factors
                        participantGroups.loadBetweenParticipantFactors(studyDesign.getBetweenParticipantFactorList());
                        // update the relative group sizes
                        updateRelativeGroupSizeList(participantGroups.getNumberOfRows());
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
        if (hypothesis != null) { 
            // clear any prior hypothesis
            studyDesign.setHypothesis(null);
            // now add the new one
            studyDesign.setHypothesisToSet(hypothesis);
        } else {
            studyDesign.setHypothesis(null);
        }
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.HYPOTHESIS));
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
