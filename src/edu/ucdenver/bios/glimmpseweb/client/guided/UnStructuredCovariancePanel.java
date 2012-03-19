package edu.ucdenver.bios.glimmpseweb.client.guided;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ResizableMatrixPanel;

public class UnStructuredCovariancePanel extends Composite
{
	ResizableMatrixPanel covarianceMatrix;

	HorizontalPanel horizontalPanel = new HorizontalPanel();
	
	List<String> labelList;
	
	List<Integer> spacingList;
	
	public UnStructuredCovariancePanel(List<String> stringList, List<Integer> integerList)
	{
		VerticalPanel verticalPanel = new VerticalPanel();
		
		labelList = stringList;
		spacingList = integerList;
		
		HTML header = new HTML();
		header.setText(GlimmpseWeb.constants.unstructuredCovarianceHeader());
		HTML instructions = new HTML();
		instructions.setText(GlimmpseWeb.constants.unstructuredCovarianceInstructions());
		
		constructCovarianceMatrix();
		
		verticalPanel.add(instructions);
		verticalPanel.add(horizontalPanel);
		initWidget(verticalPanel);
	}
	public void constructCovarianceMatrix()
	{
		int size = spacingList.size();
		covarianceMatrix = new ResizableMatrixPanel(size, size, false, false, true, true);
		covarianceMatrix.setRowLabels(labelList);
		covarianceMatrix.setColumnLabels(labelList);
		horizontalPanel.add(covarianceMatrix);
	}
}
