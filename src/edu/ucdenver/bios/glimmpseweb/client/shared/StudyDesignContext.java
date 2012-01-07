package edu.ucdenver.bios.glimmpseweb.client.shared;

import java.util.List;

import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;

public class StudyDesignContext extends WizardContext
{
	// list of alpha values
	private List<Double> alphaList;

	public List<Double> getAlphaList()
	{
		return alphaList;
	}

	public void setAlphaList(List<Double> alphaList)
	{
		this.alphaList = alphaList;
		// TODO: save to Study Design svc
	}
	
	

}
