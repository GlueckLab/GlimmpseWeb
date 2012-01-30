package edu.ucdenver.bios.glimmpseweb.context;

import java.util.ArrayList;
import java.util.List;

import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent.StudyDesignChangeType;
import edu.ucdenver.bios.webservice.common.domain.FixedRandomMatrix;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign.SolutionType;
import edu.ucdenver.bios.webservice.common.domain.StudyDesignNamedMatrix;

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

	public void setSolutionType(WizardStepPanel panel, SolutionType solutionType)
	{
		studyDesign.setSolutionType(solutionType);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.SOLVING_FOR));
	}

	public void setCovariate(WizardStepPanel panel, boolean hasCovariate)
	{
		studyDesign.setGaussianCovariate(hasCovariate);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.COVARIATE));
	}

	public void setDesignEssence(WizardStepPanel panel, NamedMatrix designEssence)
	{
//		studyDesign.setDesignEssence(designEssence);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.DESIGN_ESSENCE_MATRIX));
	}

	public void setBetweenParticipantContrast(WizardStepPanel panel, 
			FixedRandomMatrix betweenParticipantContrast)
	{
//		studyDesign.setBetweenParticipantContrast(betweenParticipantContrast);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.BETWEEN_CONTRAST_MATRIX));
	}

	public NamedMatrix getWithinParticipantContrast()
	{
//		return studyDesign.getWithinParticipantContrast();
		return null;
	}

	public void setWithinParticipantContrast(WizardStepPanel panel, NamedMatrix withinParticipantContrast)
	{
//		studyDesign.setWithinParticipantContrast(withinParticipantContrast);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.WITHIN_CONTRAST_MATRIX));
	}

	public void setBeta(WizardStepPanel panel, FixedRandomMatrix beta)
	{
//		studyDesign.setBeta(beta);
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

}
