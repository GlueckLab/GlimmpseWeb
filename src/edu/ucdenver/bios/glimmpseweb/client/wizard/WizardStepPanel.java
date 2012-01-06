package edu.ucdenver.bios.glimmpseweb.client.wizard;

import com.google.gwt.user.client.ui.Composite;

//import edu.cudenver.bios.glimmpse.client.listener.StepStatusListener;

public abstract class WizardStepPanel extends Composite
{
	protected boolean skip = false;
	protected boolean complete = false;
	protected String name = "name";
	
	public WizardStepPanel(String name)
	{
		this.name = name;
	}
	
	abstract public void reset();
	
    /**
     * Notify listeners that this step is complete and forward navigation
     * is allowed.
     */
    public void notifyComplete()
    {
    	complete = true;
//		for(StepStatusListener listener: stepStatusListeners) listener.onStepComplete();
    }
    
    /**
     * Notify listeners that this step is in-progress and forward navigation
     * is not allowed.
     */
    public void notifyInProgress()
    {
//    	complete = false;
//		for(StepStatusListener listener: stepStatusListeners) listener.onStepInProgress();
    }
    
    /**
     * Perform any setup when first entering this step in the wizard
     */
    public void onEnter() {}
    
    /**
     * Perform any cleanup when first exiting this step in the wizard
     */
    public void onExit() {}
    
    public String getName()
    {
    	return name;
    }
    
}
