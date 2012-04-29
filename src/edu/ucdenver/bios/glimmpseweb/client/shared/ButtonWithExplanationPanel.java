package edu.ucdenver.bios.glimmpseweb.client.shared;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class ButtonWithExplanationPanel extends Composite 
{
	Button button = new Button();
	public ButtonWithExplanationPanel(String name, String explainbuttontext, String alerttextheader, String alerttext)
	{
		String buttonName = name;
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		
		button.setText(buttonName);
		
		ButtonExplain explainButton = new ButtonExplain(explainbuttontext, alerttextheader, alerttext);
		
		horizontalPanel.add(button);
		horizontalPanel.add(explainButton);
		
		initWidget(horizontalPanel);
	}
	public void addClickHandler(ClickHandler handler)
	{
		button.addClickHandler(handler);
	}

}
