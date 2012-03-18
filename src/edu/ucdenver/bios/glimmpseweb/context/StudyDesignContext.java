package edu.ucdenver.bios.glimmpseweb.context;

import java.util.ArrayList;
import java.util.List;

import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent.StudyDesignChangeType;
import edu.ucdenver.bios.webservice.common.domain.BetaScale;
import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.ClusterNode;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.NominalPower;
import edu.ucdenver.bios.webservice.common.domain.PowerMethod;
import edu.ucdenver.bios.webservice.common.domain.Quantile;
import edu.ucdenver.bios.webservice.common.domain.SigmaScale;
import edu.ucdenver.bios.webservice.common.domain.StatisticalTest;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;
import edu.ucdenver.bios.webservice.common.domain.TypeIError;
import edu.ucdenver.bios.webservice.common.enums.SolutionTypeEnum;

public class StudyDesignContext extends WizardContext
{
	// main study design object
	private StudyDesign studyDesign;

	/* connectors to the web service layer */
	// power service connector
	
	// chart service connector
	
	// matrix service connector
	
	// study design service connector
	
	public StudyDesignContext()
	{
		studyDesign = new StudyDesign();
	}

	public StudyDesign getStudyDesign()
	{
		return studyDesign;
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

	public void setPowerList(WizardStepPanel panel, ArrayList<NominalPower> powerList)
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
	
	public void setPerGroupSampleSizeList(WizardStepPanel panel, ArrayList<Double> sampleSizeList)
	{
		studyDesign.setPerGroupSampleSizeList(sampleSizeList);
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
		studyDesign.setNamedMatrix(thetaNull);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.THETA_NULL_MATRIX));
	}

	/**
	 * Store clustering information to the local context and the study design service, and
	 * notify all wizard panels of the change.
	 * 
	 * @param panel the panel which initiated the change
	 * @param clusteringNodeList the updated list of clustering nodes.
	 */
	public void setClustering(WizardStepPanel panel, ArrayList<ClusterNode> clusteringNodeList)
	{
		studyDesign.setClusteringTree(clusteringNodeList);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.CLUSTERING));
	}
	
	/**
	 * Set the between participant factor list
	 * @param panel the panel changing the between participant factor list
	 * @param factorList the list of between participant factors
	 */
	public void setBetweenParticipantFactorList(WizardStepPanel panel, 
			List<BetweenParticipantFactor> factorList)
	{
		studyDesign.setBetweenParticipantFactorList(factorList);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.BETWEEN_PARTICIPANT_FACTORS));
	}
}
