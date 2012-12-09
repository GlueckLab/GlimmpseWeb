package edu.ucdenver.bios.glimmpseweb.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GlimmpseFooter extends Composite
{
	public GlimmpseFooter()
	{
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(new HTML(GlimmpseWeb.constants.fundingStatement()));
		panel.setStyleName("footer");
		initWidget(panel);
	}
}
