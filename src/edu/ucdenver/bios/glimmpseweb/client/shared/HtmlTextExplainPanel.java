package edu.ucdenver.bios.glimmpseweb.client.shared;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;


public class HtmlTextExplainPanel extends Composite
{
	protected String okButtonStyle = "buttonStyle";
	protected String explainButtonStyle = "buttonHyperLinkStyle";
	protected DialogBox dialogBox = new DialogBox();
	public HtmlTextExplainPanel(String descriptiontext, String headertext, String explinationtext)
	{
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		VerticalPanel verticalPanel = new VerticalPanel();
		HTML descriptionText = new HTML();
		HTML explainationText = new HTML();
		HTML headerText = new HTML();
		
		descriptionText.setText(descriptiontext);
		explainationText.setText(explinationtext);
		headerText.setText(headertext);
		
		ScrollPanel scrollPanel = new ScrollPanel(explainationText);
		scrollPanel.setSize("150px", "100px");
		
		Button explainButton = new Button("explain", new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) 
			{
				dialogBox.setAnimationEnabled(true);
				dialogBox.setGlassEnabled(true);
				dialogBox.center();
			}
		});
		
		explainButton.setStyleName(explainButtonStyle);
		horizontalPanel.add(descriptionText);
		horizontalPanel.add(explainButton);
		
	Button dialogOK = new Button("OK", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				dialogBox.hide();
			}
		});
	dialogOK.setStyleName(okButtonStyle);
		
	
	headerText.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
	verticalPanel.add(headerText);
	verticalPanel.add(scrollPanel);
	verticalPanel.add(dialogOK);
	dialogBox.add(verticalPanel);
		
		initWidget(horizontalPanel);
	}
}
