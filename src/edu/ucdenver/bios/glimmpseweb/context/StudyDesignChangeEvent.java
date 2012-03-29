package edu.ucdenver.bios.glimmpseweb.context;

import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;

public class StudyDesignChangeEvent extends WizardContextChangeEvent
{
	public enum StudyDesignChangeType
	{
		SOLVING_FOR,
		COVARIATE,
		ALPHA_LIST,
		STATISTICAL_TEST_LIST,
		POWER_LIST,
		PER_GROUP_N_LIST,
		BETA_SCALE_LIST,
		SIGMA_SCALE_LIST,
		POWER_METHOD_LIST,
		QUANTILE_LIST,
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
		RESPONSES_LIST,
		POWER_CURVE,
		CLUSTERING,
		REPEATED_MEASURES,
		HYPOTHESIS,
		BETWEEN_PARTICIPANT_FACTORS,
		RELATIVE_GROUP_SIZE_LIST
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
