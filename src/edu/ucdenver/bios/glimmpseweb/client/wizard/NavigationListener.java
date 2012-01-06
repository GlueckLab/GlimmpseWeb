package edu.ucdenver.bios.glimmpseweb.client.wizard;

public interface NavigationListener
{
	public void onNext();
	
	public void onPrevious();
	
	public void onPanel(WizardStepPanel panel);
}
