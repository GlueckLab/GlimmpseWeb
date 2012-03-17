package edu.ucdenver.bios.glimmpseweb.context;

import java.util.ArrayList;
import java.util.List;

import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent.StudyDesignChangeType;
import edu.ucdenver.bios.webservice.common.domain.ClusterNode;
import edu.ucdenver.bios.webservice.common.domain.FixedRandomMatrix;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;
import edu.ucdenver.bios.webservice.common.enums.SolutionTypeEnum;

public class StudyDesignContext extends WizardContext
{
	private StudyDesign studyDesign;

	public StudyDesignContext()
	{
		studyDesign = new StudyDesign();
	}

	public StudyDesign getStudyDesign()
	{
		return studyDesign;
	}

	public void setAlphaList(WizardStepPanel panel, ArrayList<Double> alphaList)
	{
		//		studyDesign.setAlphaList(alphaList);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.ALPHA_LIST));
	}

	public void setBetaScaleList(WizardStepPanel panel, List<Double> betaScaleList)
	{
		//		studyDesign.setAlphaList(betaScaleList);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.BETA_SCALE_LIST));
	}

	public void setSigmaScaleList(WizardStepPanel panel, List<Double> sigmaScaleList)
	{
		//		studyDesign.setAlphaList(sigmaScaleList);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.SIGMA_SCALE_LIST));
	}

	public void setPowerList(WizardStepPanel panel, ArrayList<Double> powerList)
	{
		//		studyDesign.setPowerList(powerList);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.POWER_LIST));
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
		//		studyDesign.setSigmaCovariate(sigmaCovariate);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.SIGMA_COVARIATE_MATRIX));
	}

	public void setSigmaError(WizardStepPanel panel, NamedMatrix sigmaError)
	{
		//		studyDesign.setSigmaError(sigmaError);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.SIGMA_ERROR_MATRIX));
	}

	public void setSigmaOutcomesCovariate(WizardStepPanel panel, NamedMatrix sigmaYG)
	{
		//		studyDesign.setSigmaOutcomesCovariate(sigmaYG);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.SIGMA_OUTCOME_COVARIATE_MATRIX));
	}

	public void setSigmaOutcomes(WizardStepPanel panel, NamedMatrix sigmaY)
	{
		//		studyDesign.setSigmaOutcomes(sigmaY);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.SIGMA_OUTCOME_MATRIX));
	}

	public void setThetaNull(WizardStepPanel panel, NamedMatrix thetaNull)
	{
		//		studyDesign.setThetaNull(thetaNull);
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
}
