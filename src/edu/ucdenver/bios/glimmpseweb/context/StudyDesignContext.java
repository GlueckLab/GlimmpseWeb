package edu.ucdenver.bios.glimmpseweb.context;

import java.util.List;

import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesign.SolutionType;
import edu.ucdenver.bios.glimmpseweb.context.StudyDesignChangeEvent.StudyDesignChangeType;

public class StudyDesignContext extends WizardContext
{
	private StudyDesign studyDesign;
	
	public StudyDesignContext()
	{
		studyDesign = new StudyDesign();
	}

	public List<Double> getAlphaList()
	{
		return studyDesign.getAlphaList();
	}

	public void setAlphaList(WizardStepPanel panel, List<Double> alphaList)
	{
		studyDesign.setAlphaList(alphaList);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.ALPHA_LIST));
	}
	
	public List<Double> getBetaScaleList()
	{
		return studyDesign.getBetaScaleList();
	}

	public void setBetaScaleList(WizardStepPanel panel, List<Double> betaScaleList)
	{
		studyDesign.setAlphaList(betaScaleList);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.BETA_SCALE_LIST));
	}
	
	public List<Double> getSigmaScaleList()
	{
		return studyDesign.getSigmaScaleList();
	}

	public void setSigmaScaleList(WizardStepPanel panel, List<Double> sigmaScaleList)
	{
		studyDesign.setAlphaList(sigmaScaleList);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.SIGMA_SCALE_LIST));
	}
	
	public List<Double> getPowerList()
	{
		return studyDesign.getPowerList();
	}

	public void setPowerList(WizardStepPanel panel, List<Double> powerList)
	{
		studyDesign.setPowerList(powerList);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.POWER_LIST));
	}

	public SolutionType getSolutionType()
	{
		return studyDesign.getSolutionType();
	}

	public void setSolutionType(WizardStepPanel panel, SolutionType solutionType)
	{
		studyDesign.setSolutionType(solutionType);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.SOLVING_FOR));
	}

	public boolean hasCovariate()
	{
		return studyDesign.hasCovariate();
	}

	public void setCovariate(WizardStepPanel panel, boolean hasCovariate)
	{
		studyDesign.setCovariate(hasCovariate);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.COVARIATE));
	}

	public NamedMatrix getDesignEssence()
	{
		return studyDesign.getDesignEssence();
	}

	public void setDesignEssence(WizardStepPanel panel, NamedMatrix designEssence)
	{
		studyDesign.setDesignEssence(designEssence);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.DESIGN_ESSENCE_MATRIX));
	}

	public FixedRandomMatrix getBetweenParticipantContrast()
	{
		return studyDesign.getBetweenParticipantContrast();
	}

	public void setBetweenParticipantContrast(WizardStepPanel panel, 
			FixedRandomMatrix betweenParticipantContrast)
	{
		studyDesign.setBetweenParticipantContrast(betweenParticipantContrast);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.BETWEEN_CONTRAST_MATRIX));
	}

	public NamedMatrix getWithinParticipantContrast()
	{
		return studyDesign.getWithinParticipantContrast();
	}

	public void setWithinParticipantContrast(WizardStepPanel panel, NamedMatrix withinParticipantContrast)
	{
		studyDesign.setWithinParticipantContrast(withinParticipantContrast);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.WITHIN_CONTRAST_MATRIX));
	}

	public FixedRandomMatrix getBeta()
	{
		return studyDesign.getBeta();
	}

	public void setBeta(WizardStepPanel panel, FixedRandomMatrix beta)
	{
		studyDesign.setBeta(beta);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.BETA_MATRIX));
	}
	
	public NamedMatrix getSigmaCovariate()
	{
		return studyDesign.getSigmaCovariate();
	}

	public void setSigmaCovariate(WizardStepPanel panel, NamedMatrix sigmaCovariate)
	{
		studyDesign.setSigmaCovariate(sigmaCovariate);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.SIGMA_COVARIATE_MATRIX));
	}
	
	public NamedMatrix getSigmaError()
	{
		return studyDesign.getSigmaError();
	}

	public void setSigmaError(WizardStepPanel panel, NamedMatrix sigmaError)
	{
		studyDesign.setSigmaError(sigmaError);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.SIGMA_ERROR_MATRIX));
	}
	
	public NamedMatrix getSigmaOutcomesCovariate()
	{
		return studyDesign.getSigmaOutcomesCovariate();
	}

	public void setSigmaOutcomesCovariate(WizardStepPanel panel, NamedMatrix sigmaYG)
	{
		studyDesign.setSigmaOutcomesCovariate(sigmaYG);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.SIGMA_OUTCOME_COVARIATE_MATRIX));
	}
	
	public NamedMatrix getSigmaOutcomes()
	{
		return studyDesign.getSigmaOutcomes();
	}

	public void setSigmaOutcomes(WizardStepPanel panel, NamedMatrix sigmaY)
	{
		studyDesign.setSigmaOutcomes(sigmaY);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.SIGMA_OUTCOME_MATRIX));
	}
	
	public NamedMatrix getThetaNull()
	{
		return studyDesign.getThetaNull();
	}

	public void setThetaNull(WizardStepPanel panel, NamedMatrix thetaNull)
	{
		studyDesign.setThetaNull(thetaNull);
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, 
				StudyDesignChangeType.THETA_NULL_MATRIX));
	}

}
