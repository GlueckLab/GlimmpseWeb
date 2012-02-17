package edu.ucdenver.bios.glimmpseweb.client.guided;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.matrix.ResizableMatrix;

public class UnStructuredCovariancePanel extends Composite
{
	ResizableMatrix covarianceMatrix = new ResizableMatrix("name", 1, 1, "defaultValue", "title");
	Grid navigationGrid = new Grid(3,2);

	public UnStructuredCovariancePanel()
	{
		VerticalPanel verticalPanel = new VerticalPanel();
		
		HTML header = new HTML();
		header.setText(GlimmpseWeb.constants.unstructuredCorrelationHeader());
		HTML instructions = new HTML();
		instructions.setText(GlimmpseWeb.constants.unstructuredCorrelationInstructions());
		
		constructCovarianceMatrix();
		
		verticalPanel.add(instructions);
		
		
		
		
		initWidget(verticalPanel);
	}
	public ResizableMatrix constructCovarianceMatrix()
	{
		return covarianceMatrix;
	}
}
