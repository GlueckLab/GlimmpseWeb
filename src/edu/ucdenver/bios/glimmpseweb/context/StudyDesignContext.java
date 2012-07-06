package edu.ucdenver.bios.glimmpseweb.context;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent.StudyDesignChangeType;
import edu.ucdenver.bios.webservice.common.domain.BetaScale;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
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

public class StudyDesignContext extends WizardContext
{
    // main study design object
    private StudyDesign studyDesign;

    // cache of all possible between participant effects
    private FactorTable participantGroups = new FactorTable();

    // cache of all possible combinations of within participant effects
    private FactorTable withinParticipantMeasures = new FactorTable();

    public StudyDesignContext()
    {
        studyDesign = new StudyDesign();
    }

    public StudyDesign getStudyDesign()
    {
        return studyDesign;
    }

    public void loadStudyDesign(StudyDesign design)
    {
        if (design == null) {
            studyDesign = new StudyDesign();
        } else {
            studyDesign = design;
        }
        notifyWizardContextLoad();
    }

    public FactorTable getParticipantGroups()
    {
        return participantGroups;
    }

    public FactorTable getWithinParticipantMeasures()
    {
        return withinParticipantMeasures;
    }

    public void setAlphaList(WizardStepPanel panel, ArrayList<TypeIError> alphaList)
    {
        studyDesign.setAlphaList(alphaList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.ALPHA_LIST));
    }

    public void setBetaScaleList(WizardStepPanel panel, List<BetaScale> betaScaleList)
    {
        studyDesign.setBetaScaleList(betaScaleList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.BETA_SCALE_LIST));
    }

    public void setSigmaScaleList(WizardStepPanel panel, List<SigmaScale> sigmaScaleList)
    {
        studyDesign.setSigmaScaleList(sigmaScaleList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.SIGMA_SCALE_LIST));
    }

    public void setNominalPowerList(WizardStepPanel panel, ArrayList<NominalPower> powerList)
    {
        studyDesign.setNominalPowerList(powerList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.POWER_LIST));
    }

    public void setPowerMethodList(WizardStepPanel panel, ArrayList<PowerMethod> powerMethodList)
    {
        studyDesign.setPowerMethodList(powerMethodList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.POWER_METHOD_LIST));
    }

    public void setQuantileList(WizardStepPanel panel, ArrayList<Quantile> quantileList)
    {
        studyDesign.setQuantileList(quantileList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.QUANTILE_LIST));
    }

    public void setPerGroupSampleSizeList(WizardStepPanel panel, ArrayList<SampleSize> sampleSizeList)
    {
        studyDesign.setSampleSizeList(sampleSizeList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.PER_GROUP_N_LIST));
    }

    public void setStatisticalTestList(WizardStepPanel panel, ArrayList<StatisticalTest> statisticalTestList)
    {
        studyDesign.setStatisticalTestList(statisticalTestList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.STATISTICAL_TEST_LIST));
    }

    public void setSolutionType(WizardStepPanel panel, SolutionTypeEnum solutionType)
    {
        studyDesign.setSolutionTypeEnum(solutionType);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.SOLVING_FOR));
    }

    public void setCovariate(WizardStepPanel panel, boolean hasCovariate)
    {
        studyDesign.setGaussianCovariate(hasCovariate);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.COVARIATE));
    }

    public void setDesignEssenceMatrix(WizardStepPanel panel, NamedMatrix designFixed,
            NamedMatrix designRandom)
    {
        studyDesign.setNamedMatrix(designFixed);
        if (designRandom != null) studyDesign.setNamedMatrix(designRandom);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.DESIGN_ESSENCE_MATRIX));
    }

    public void setBetweenParticipantContrast(WizardStepPanel panel, 
            NamedMatrix fixed, NamedMatrix random)
    {
        studyDesign.setNamedMatrix(fixed);
        if (random != null) studyDesign.setNamedMatrix(random);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.BETWEEN_CONTRAST_MATRIX));
    }

    public void setWithinParticipantContrast(WizardStepPanel panel, NamedMatrix withinParticipantContrast)
    {
        studyDesign.setNamedMatrix(withinParticipantContrast);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.WITHIN_CONTRAST_MATRIX));
    }

    public void setBeta(WizardStepPanel panel, NamedMatrix fixed, NamedMatrix random)
    {
        studyDesign.setNamedMatrix(fixed);
        if (random != null) studyDesign.setNamedMatrix(random);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.BETA_MATRIX));
    }

    public void setSigmaCovariate(WizardStepPanel panel, NamedMatrix sigmaCovariate)
    {
        studyDesign.setNamedMatrix(sigmaCovariate);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.SIGMA_COVARIATE_MATRIX));
    }

    public void setSigmaError(WizardStepPanel panel, NamedMatrix sigmaError)
    {
        studyDesign.setNamedMatrix(sigmaError);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.SIGMA_ERROR_MATRIX));
    }

    public void setSigmaOutcomesCovariate(WizardStepPanel panel, NamedMatrix sigmaYG)
    {
        studyDesign.setNamedMatrix(sigmaYG);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.SIGMA_OUTCOME_COVARIATE_MATRIX));
    }

    public void setSigmaOutcomes(WizardStepPanel panel, NamedMatrix sigmaY)
    {
        studyDesign.setNamedMatrix(sigmaY);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.SIGMA_OUTCOME_MATRIX));
    }

    public void setThetaNull(WizardStepPanel panel, NamedMatrix thetaNull)
    {
        if (thetaNull != null) {
            studyDesign.setNamedMatrix(thetaNull);
        } else {
            Set<NamedMatrix> matrixSet = studyDesign.getMatrixSet();
            // TODO
//            if (matrixSet != null) {
//                matrixSet.remove(arg0)
//            }
        }

        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.THETA_NULL_MATRIX));
    }

    public void setClustering(WizardStepPanel panel, ArrayList<ClusterNode> clusteringNodeList)
    {
        studyDesign.setClusteringTree(clusteringNodeList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.CLUSTERING));
    }

    public void setRepeatedMeasures(WizardStepPanel panel, 
            ArrayList<RepeatedMeasuresNode> repeatedMeasuresNodeList)
    {
        studyDesign.setRepeatedMeasuresTree(repeatedMeasuresNodeList);
        this.withinParticipantMeasures.loadRepeatedMeasures(repeatedMeasuresNodeList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.REPEATED_MEASURES));
    }

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

    public void setRelativeGroupSizeList(WizardStepPanel panel, 
            List<RelativeGroupSize> relativeGroupSizeList)
    {
        studyDesign.setRelativeGroupSizeList(relativeGroupSizeList);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.RELATIVE_GROUP_SIZE_LIST));
    }

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
     * @param panel
     * @param hypothesis
     */
    public void setHypothesis(WizardStepPanel panel, Hypothesis hypothesis)
    {
        studyDesign.setHypothesisToSet(hypothesis);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.HYPOTHESIS));
    }

    /**
     * Store the covariance information in the study design
     * @param panel
     * @param covariance
     */
    public void setCovariance(WizardStepPanel panel, Covariance covariance)
    {
        studyDesign.addCovariance(covariance);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.COVARIANCE));
    }

    public void setConfidenceIntervalOptions(WizardStepPanel panel, ConfidenceIntervalDescription confidenceIntervalDescription)
    {
        studyDesign.setConfidenceIntervalDescriptions(confidenceIntervalDescription);
        notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
                StudyDesignChangeType.CONFIDENCE_INTERVAL));
    }

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

        complete = (validLists() && 
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
        complete = validLists() && (hasX && hasBeta && hasBetaRandom && hasC && hasCRandom 
                && hasU && hasThetaNull
                && hasSigmaE && hasSigmaY && hasSigmaYG && hasSigmaG);
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


}
