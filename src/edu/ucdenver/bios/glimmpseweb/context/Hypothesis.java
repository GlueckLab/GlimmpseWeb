package edu.ucdenver.bios.glimmpseweb.context;

public class Hypothesis
{
	public enum Type
	{
		MAIN_EFFECT,
		INTERACTION,
		TREND
	};
	
	public enum Trend
	{
		CHANGE_FIRST_TO_LAST,
		POLYNOMIAL_LINEAR,
		POLYNOMIAL_QUADRATIC,
		POLYNOMIAL_CUBIC
	};
	
	public Hypothesis(Type type)
	{
		
	}
	
	public void addFactor(String name)
	{
		
	}
}
