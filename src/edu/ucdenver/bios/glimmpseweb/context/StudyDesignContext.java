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
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, StudyDesignChangeType.ALPHA_LIST));
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
		notifyWizardContextChanged(new StudyDesignChangeEvent(panel, StudyDesignChangeType.SOLVING_FOR));
	}

	public boolean hasCovariate()
	{
		return studyDesign.hasCovariate();
	}

	public void setCovariate(boolean hasCovariate)
	{
		studyDesign.setCovariate(hasCovariate);
		// notifyChanged();
	}

	public NamedMatrix getDesignEssence()
	{
		return studyDesign.getDesignEssence();
	}

	public void setDesignEssence(NamedMatrix designEssence)
	{
		studyDesign.setDesignEssence(designEssence);
		// notifyChanged();
	}

	public FixedRandomMatrix getBetweenParticipantContrast()
	{
		return studyDesign.getBetweenParticipantContrast();
	}

	public void setBetweenParticipantContrast(FixedRandomMatrix betweenParticipantContrast)
	{
		studyDesign.setBetweenParticipantContrast(betweenParticipantContrast);
		//notifyChanged();
	}

	public NamedMatrix getWithinParticipantContrast()
	{
		return studyDesign.getWithinParticipantContrast();
	}

	public void setWithinParticipantContrast(NamedMatrix withinParticipantContrast)
	{
		studyDesign.setWithinParticipantContrast(withinParticipantContrast);
		// notifyChanged();
	}

	public FixedRandomMatrix getBeta()
	{
		return studyDesign.getBeta();
	}

	public void setBeta(FixedRandomMatrix beta)
	{
		studyDesign.setBeta(beta);
		// notifyChanged();
	}

}
