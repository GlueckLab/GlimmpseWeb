package edu.ucdenver.bios.glimmpseweb.client.guided;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InteractionVariablePanel extends Composite
{
	Button editTrend = new Button("Edit Trend", new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			editTrendClickEvent();
		}
	});


	HorizontalPanel horizontalPanel = new HorizontalPanel();

	HorizontalPanel buttonGroupHorizontalPanel = new HorizontalPanel();
	
	HorizontalPanel selectedTrendHorizontalPanel = new HorizontalPanel();
	
	HTML trendSelected = new HTML();
	
	HTML selectedTrendLabel = new HTML("Selected Trend");
	
	public InteractionVariablePanel(String label)
	{

		String button_Style = "buttonStyle";
		
		VerticalPanel verticalPanel = new VerticalPanel();
		
		Grid grid = new Grid(1, 3);
		grid.setCellPadding(5);
		
		
		selectedTrendHorizontalPanel.setSpacing(10);
		
		
		
		/*selectedTrendLabel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);*/
		
		selectedTrendHorizontalPanel.add(selectedTrendLabel);
		selectedTrendHorizontalPanel.add(trendSelected);
		
		/*hideTrend.setVisible(false);*/
		
		editTrend.setEnabled(false);
		buttonGroupHorizontalPanel.add(editTrend);
		/*buttonGroupHorizontalPanel.add(hideTrend);*/

		CheckBox checkBox = new CheckBox();
		checkBox.setText(label);
		checkBox.addClickHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
				boolean checked = ((CheckBox) event.getSource()).isChecked();
				if(checked)
				{
					editTrend.setEnabled(true);
					editTrend.setVisible(true);
					trendSelected.setVisible(true);
					String abc = trendSelected.getHTML();
					if(abc == null)
					{
						selectedTrendHorizontalPanel.setVisible(false);
					}
					else
					{
						selectedTrendHorizontalPanel.setVisible(true);
					}
					
				}
				else
				{
					editTrend.setVisible(true);
					editTrend.setEnabled(false);
					horizontalPanel.setVisible(false);
					selectedTrendHorizontalPanel.setVisible(false);
				}
			}
			
		});

		grid.setWidget(0, 0, checkBox);
		grid.setWidget(0, 1, buttonGroupHorizontalPanel);
		grid.setWidget(0, 2, selectedTrendHorizontalPanel);

		EditTrendPanel editTrendPanel = new EditTrendPanel(true);
		horizontalPanel.add(editTrendPanel);
		editTrendPanel.addClickHandler(new ClickHandler ()
		{
			@Override
			public void onClick(ClickEvent event) 
			{
					RadioButton radioButton = (RadioButton) event.getSource();
					trendSelected.setText(radioButton.getHTML());
					selectedTrendLabel.setVisible(true);
					selectedTrendHorizontalPanel.setVisible(true);
					horizontalPanel.setVisible(false);
					editTrend.setVisible(true);
			}
			
		});
		
		editTrend.setStyleName(button_Style);
		selectedTrendLabel.setVisible(false);
		selectedTrendHorizontalPanel.setVisible(false);
		horizontalPanel.setVisible(false);

		verticalPanel.add(grid);
		verticalPanel.add(horizontalPanel);
		
		initWidget(verticalPanel);
	}
	public void editTrendClickEvent()
	{
		/*hideTrend.setVisible(true);*/
		editTrend.setVisible(false);
		horizontalPanel.setVisible(true);

	}
	public void hideTrendClickEvent()
	{
		editTrend.setVisible(true);
		/*hideTrend.setVisible(false);*/
		horizontalPanel.setVisible(false);
	}
	
}
