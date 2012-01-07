package edu.ucdenver.bios.glimmpseweb.context;

import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;

public class StudyDesignChangeEvent extends WizardContextChangeEvent
{
	public enum StudyDesignChangeType
	{
		SOLVING_FOR,
		ALPHA_LIST,
		TEST_LIST,
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
	
	public StudyDesignChangeEvent(StudyDesignChangeType type)
	{
		this.type = type;
	}

	public StudyDesignChangeType getType()
	{
		return type;
	}
	
	
}
