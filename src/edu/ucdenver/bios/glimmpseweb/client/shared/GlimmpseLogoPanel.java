package edu.ucdenver.bios.glimmpseweb.client.shared;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;

public class GlimmpseLogoPanel extends Composite
{
	private static final String STYLE = "glimmpseLogo";
	
	public GlimmpseLogoPanel()
	{
    	VerticalPanel panel = new VerticalPanel();
    	
    	HTML name = new HTML("");
    	
    	// layout
    	panel.add(name);
    	
    	// set style
    	name.setStyleName(STYLE);
    	
    	initWidget(panel);
	}
}
