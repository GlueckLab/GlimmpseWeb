package edu.ucdenver.bios.glimmpseweb.client.guided;

import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;

public class VariabilityWithinParticipantPanel extends WizardStepPanel
{

	public VariabilityWithinParticipantPanel()
	{
		super("Within Participant Variability");
		VerticalPanel panel = new VerticalPanel();
		initWidget(panel);
	}
	@Override
	public void reset()
	{
		// TODO Auto-generated method stub
		
	}

}
