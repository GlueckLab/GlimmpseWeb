package edu.ucdenver.bios.glimmpseweb.client.guided;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.HtmlTextExplainPanel;

public class StructuredCorrelationPanel extends Composite 
{
	/**
	 * Constructor to the clas
	 */
	public StructuredCorrelationPanel()
	{
		//instance of the VerticalPanel Class which is used to 
		//hold the widgets created in this particular class
		VerticalPanel verticalPanel = new VerticalPanel();
		
		HTML header = new HTML();
		HTML text = new HTML();
		
		header.setText(GlimmpseWeb.constants.structuredCorrelationPanelHeader());
		text.setText(GlimmpseWeb.constants.structuredCorrelationPanelText());
		
		
		
		HtmlTextExplainPanel standardDeviation = new HtmlTextExplainPanel(GlimmpseWeb.constants.standardDeviationLabel(),
				GlimmpseWeb.constants.standardDeviationExplinationHeader(), GlimmpseWeb.constants.standardDeviationExplinationText());
		HtmlTextExplainPanel strongestCorrelation = new HtmlTextExplainPanel(GlimmpseWeb.constants.strongestCorrelationLabel(),
				GlimmpseWeb.constants.strongestCorrelationExplinationHeader(), GlimmpseWeb.constants.strongestCorrelationExplinationText());
		HtmlTextExplainPanel rateOfDecayOfCorrelation = new HtmlTextExplainPanel(GlimmpseWeb.constants.rateOfDecayOfCorrelationLabel(),
				GlimmpseWeb.constants.rateOfDecayOfCorrelationExplinationHeader(), GlimmpseWeb.constants.rateOfDecayOfCorrelationExplinationText());
		
		Grid grid = new Grid(3,2);
		
		TextBox standardDeviationTextBox = new TextBox();
		TextBox strongestCorrelationTextBox = new TextBox();
		TextBox rateOfDecayOfCorrelationTextBox = new TextBox();
		
		grid.setWidget(0, 0, standardDeviation);
		grid.setWidget(0, 1, standardDeviationTextBox);
		grid.setWidget(1, 0, strongestCorrelation);
		grid.setWidget(1, 1, strongestCorrelationTextBox);
		grid.setWidget(2, 0, rateOfDecayOfCorrelation);
		grid.setWidget(2, 1, rateOfDecayOfCorrelationTextBox);
		
		verticalPanel.add(grid);
		initWidget(verticalPanel);
		
	}
}
