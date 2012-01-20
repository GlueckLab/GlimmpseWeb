package edu.ucdenver.bios.glimmpseweb.client.shared;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;

public class CancelDialogBox extends DialogBox
{
	private static final String STYLE_BUTTON = "buttonStyle";
public Widget cancelDialogBox()
{
	DialogBox dialogBox = new DialogBox();
	dialogBox.setGlassEnabled(true);
    dialogBox.setAnimationEnabled(true);
    HTML query = new HTML(GlimmpseWeb.constants.cancelDialogBoxQuery());
    Button saveButton = new Button("Save", new ClickHandler()
    {
		@Override
		public void onClick(ClickEvent event) 
		{
			// TODO Auto-generated method stub
			
		}
    	
    });
    Button discardChangesButton = new Button("Discard Changes", new ClickHandler()
    {
		@Override
		public void onClick(ClickEvent event) 
		{
			// TODO Auto-generated method stub
			
		}
    	
    });
    Button continueButton = new Button("Continue", new ClickHandler()
    {
		@Override
		public void onClick(ClickEvent event) 
		{
			// TODO Auto-generated method stub
			
		}
    	
    });
    
    

    /*saveButton.setWidth("30%");
    discardChangesButton.setWidth("30%");
    continueButton.setWidth("30%");*/
    
    saveButton.setStyleName(STYLE_BUTTON);
    discardChangesButton.setStyleName(STYLE_BUTTON);
    continueButton.setStyleName(STYLE_BUTTON);
    HorizontalPanel horizontalPanel = new HorizontalPanel();
    horizontalPanel.add(saveButton);
    horizontalPanel.add(discardChangesButton);
    horizontalPanel.add(continueButton);
    horizontalPanel.setSpacing(10);
    
    
    VerticalPanel verticalPanel = new VerticalPanel();
    verticalPanel.add(query);
    verticalPanel.add(horizontalPanel);
    
    /*dialogBox.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_PANEL);*/
    dialogBox.setText("Cancel Dialog Box");
    dialogBox.add(verticalPanel);
    dialogBox.show();
    /*dialogBox.add(horizontalPanel);*/
	return dialogBox;
}
}
