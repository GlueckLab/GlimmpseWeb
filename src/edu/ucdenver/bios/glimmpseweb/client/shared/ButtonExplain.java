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

public class ButtonExplain extends Composite 
{
	protected DialogBox dialogBox = new DialogBox();
	protected String okButtonStyle = "buttonStyle";
	public ButtonExplain(String buttontext, String alerttextheader, String alerttext)
	{
		HorizontalPanel horizontalpanel = new HorizontalPanel();
		VerticalPanel verticalPanel = new VerticalPanel();

		String buttonLabel = buttontext;
		String alertTextHeader = alerttextheader;
		String alertText = alerttext;

		/*HTML descriptionText = new HTML();*/
		HTML explainationText = new HTML();
		HTML headerText = new HTML();
		
		headerText.setText(alertTextHeader);
		explainationText.setText(alertText);
		

		ScrollPanel scrollPanel = new ScrollPanel(explainationText);
		scrollPanel.setSize("150px", "100px");


		Button explainButton = new Button( buttonLabel, new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) 
			{
				dialogBox.setAnimationEnabled(true);
				dialogBox.setGlassEnabled(true);
				dialogBox.center();
			}
		});

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
		
		
		horizontalpanel.add(explainButton);
		
		
		initWidget(horizontalpanel);
	}

}
