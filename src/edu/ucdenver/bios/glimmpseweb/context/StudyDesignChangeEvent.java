package edu.ucdenver.bios.glimmpseweb.context;

import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;

public class StudyDesignChangeEvent extends WizardContextChangeEvent
{
	public enum StudyDesignChangeType
	{
		SOLVING_FOR,
		ALPHA_LIST,
		TEST_LIST,
		POWER_LIST,
		DESIGN_ESSENCE_MATRIX,
		BETWEEN_CONTRAST_MATRIX,
		WITHIN_CONTRAST_MATRIX,
		BETA_MATRIX,
		THETA_NULL_MATRIX,
		SIGMA_ERROR_MATRIX,
		SIGMA_OUTCOME_MATRIX,
		SIGMA_COVARIATE_MATRIX,
		SIGMA_OUTCOME_COVARIATE_MATRIX,
		CONFIDENCE_INTERVAL,
		POWER_CURVE
	};
	
	protected StudyDesignChangeType type;
	
	public StudyDesignChangeEvent(WizardStepPanel panel, StudyDesignChangeType type)
	{
		super(panel);
		this.type = type;
	}

	public StudyDesignChangeType getType()
	{
		return type;
	}
	
	
}
