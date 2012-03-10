package edu.ucdenver.bios.glimmpseweb.client.shared;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;

public class GlimmpseTitleBarPanel extends Composite
{
	private static final String STYLE = "titleBar";
	public GlimmpseTitleBarPanel()
	{
		VerticalPanel panel = new VerticalPanel();
		
		HTML title = new HTML(GlimmpseWeb.constants.titleBar());
		
		panel.add(title);
		
		// set style
		panel.setStyleName(STYLE);
		
		initWidget(panel);
	}
}
