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
	    DockPanel dock = new DockPanel();
	    dock.setStyleName(STYLE_PANEL);
	    dock.setSpacing(4);
	    dock.setHorizontalAlignment(DockPanel.ALIGN_CENTER);
	    
		finishButton = new Button("Calculate", handler);
		dock.add(finishButton, DockPanel.CENTER);
		
		// set style
		finishButton.setStyleName(STYLE_BUTTON);
		
		initWidget(dock);
		
	}
	
	
}
