package edu.ucdenver.bios.glimmpseweb.client.wizard;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;

public class WizardFinishPanel extends Composite
{
	private static final String STYLE_BUTTON = "finishButton";
	private static final String STYLE_PANEL = "finishPanel";
	
	protected Button finishButton; 
	
	public WizardFinishPanel(ClickHandler handler)
	{
		DockPanel panel = new DockPanel();
		
		finishButton = new Button("Calculate", handler);
		panel.add(finishButton, DockPanel.CENTER);
		
		// set style
		panel.setStyleName(STYLE_PANEL);
		finishButton.setStyleName(STYLE_BUTTON);
		
		initWidget(panel);
		
	}
	
	
}
