package edu.ucdenver.bios.glimmpseweb.context;

import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;

public class StudyDesignChangeEvent extends WizardContextChangeEvent
{
    public enum StudyDesignChangeType
    {
        ALPHA_LIST,
        BETA_MATRIX,
        BETA_SCALE_LIST,
        BETWEEN_CONTRAST_MATRIX,
        BETWEEN_PARTICIPANT_FACTORS,
        CLUSTERING,
        CONFIDENCE_INTERVAL,
        COVARIATE,
        COVARIANCE,
        DESIGN_ESSENCE_MATRIX,
        HYPOTHESIS,
        PER_GROUP_N_LIST,
        POWER_CURVE,
        POWER_LIST,
        POWER_METHOD_LIST,
        QUANTILE_LIST,
        RELATIVE_GROUP_SIZE_LIST,
        REPEATED_MEASURES,
        RESPONSES_LIST,
        SIGMA_COVARIATE_MATRIX,
        SIGMA_ERROR_MATRIX,
        SIGMA_OUTCOME_MATRIX,
        SIGMA_OUTCOME_COVARIATE_MATRIX,
        SIGMA_SCALE_LIST,
        SOLVING_FOR,
        STATISTICAL_TEST_LIST,
        THETA_NULL_MATRIX,
        WITHIN_CONTRAST_MATRIX,
  
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
