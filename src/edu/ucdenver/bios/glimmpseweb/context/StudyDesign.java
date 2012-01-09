package edu.ucdenver.bios.glimmpseweb.context;

import java.util.List;

public class StudyDesign
{
	public enum SolutionType
	{
		POWER,
		TOTAL_N,
		DETECTABLE_DIFFERENCE
	};
	
	// indicates whether the user is solving for power, sample size,
	// or detectable difference
	private SolutionType solutionType;
	// indicates if the design includes a baseline covariate
	private boolean hasCovariate;
	
	/*
	 * matrices which define the study design
	 */
	private NamedMatrix designEssence;
	private FixedRandomMatrix betweenParticipantContrast;
	private NamedMatrix withinParticipantContrast;
	private FixedRandomMatrix beta;
	private NamedMatrix thetaNull;
	private NamedMatrix sigmaError;
	// variance for covariate designs
	private NamedMatrix sigmaOutcomes;
	private NamedMatrix sigmaOutcomesCovariate;
	private NamedMatrix sigmaCovariate;
	
	/*
	 * Matrices which are subcomponents of the overall
	 * covariance
	 */
	
	/*
	 * The following lists allow the user to request multiple
	 * power values at once
	 */
	// for sample size and detectable difference calculations,
	// a list of nominal power values must be specified
	private List<Double> powerList;
	// list of alpha values
	private List<Double> alphaList;
	// list of beta scale values
	private List<Double> betaScaleList;
	// list of sigma scale values
	private List<Double> sigmaScaleList;
	// list of statistical tests values
	private List<String> testList;
	
	/*** Getters and Setters ***/
	
	public List<Double> getAlphaList()
	{
		return alphaList;
	}

	public void setAlphaList(List<Double> alphaList)
	{
		this.alphaList = alphaList;
		// TODO: save to Study Design svc
	}

	public SolutionType getSolutionType()
	{
		return solutionType;
	}

	public void setSolutionType(SolutionType solutionType)
	{
		this.solutionType = solutionType;
	}

	public boolean hasCovariate()
	{
		return hasCovariate;
	}

	public void setCovariate(boolean hasCovariate)
	{
		this.hasCovariate = hasCovariate;
	}

	public NamedMatrix getDesignEssence()
	{
		return designEssence;
	}

	public void setDesignEssence(NamedMatrix designEssence)
	{
		this.designEssence = designEssence;
	}

	public FixedRandomMatrix getBetweenParticipantContrast()
	{
		return betweenParticipantContrast;
	}

	public void setBetweenParticipantContrast(FixedRandomMatrix betweenParticipantContrast)
	{
		this.betweenParticipantContrast = betweenParticipantContrast;
	}

	public NamedMatrix getWithinParticipantContrast()
	{
		return withinParticipantContrast;
	}

	public void setWithinParticipantContrast(NamedMatrix withinParticipantContrast)
	{
		this.withinParticipantContrast = withinParticipantContrast;
	}

	public FixedRandomMatrix getBeta()
	{
		return beta;
	}

	public void setBeta(FixedRandomMatrix beta)
	{
		this.beta = beta;
	}

	public NamedMatrix getThetaNull()
	{
		return thetaNull;
	}

	public void setThetaNull(NamedMatrix thetaNull)
	{
		this.thetaNull = thetaNull;
	}

	public NamedMatrix getSigmaError()
	{
		return sigmaError;
	}

	public void setSigmaError(NamedMatrix sigmaError)
	{
		this.sigmaError = sigmaError;
	}

	public NamedMatrix getSigmaOutcomes()
	{
		return sigmaOutcomes;
	}

	public void setSigmaOutcomes(NamedMatrix sigmaOutcomes)
	{
		this.sigmaOutcomes = sigmaOutcomes;
	}

	public NamedMatrix getSigmaOutcomesCovariate()
	{
		return sigmaOutcomesCovariate;
	}

	public void setSigmaOutcomesCovariate(NamedMatrix sigmaOutcomesCovariate)
	{
		this.sigmaOutcomesCovariate = sigmaOutcomesCovariate;
	}

	public NamedMatrix getSigmaCovariate()
	{
		return sigmaCovariate;
	}

	public void setSigmaCovariate(NamedMatrix sigmaCovariate)
	{
		this.sigmaCovariate = sigmaCovariate;
	}

	public List<Double> getPowerList()
	{
		return powerList;
	}

	public void setPowerList(List<Double> powerList)
	{
		this.powerList = powerList;
	}

	public List<String> getTestList()
	{
		return testList;
	}

	public void setTestList(List<String> testList)
	{
		this.testList = testList;
	}

	public boolean isHasCovariate()
	{
		return hasCovariate;
	}

	public void setHasCovariate(boolean hasCovariate)
	{
		this.hasCovariate = hasCovariate;
	}

	public List<Double> getBetaScaleList()
	{
		return betaScaleList;
	}

	public void setBetaScaleList(List<Double> betaScaleList)
	{
		this.betaScaleList = betaScaleList;
	}

	public List<Double> getSigmaScaleList()
	{
		return sigmaScaleList;
	}

	public void setSigmaScaleList(List<Double> sigmaScaleList)
	{
		this.sigmaScaleList = sigmaScaleList;
	}
	
	
}
