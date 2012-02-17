package edu.ucdenver.bios.glimmpseweb.client.guided;



import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.matrix.ResizableMatrix;

public class UnStructuredCorrelationPanel extends Composite 
{
	//Grid to construct the Standard Deviation Entry Text boxes
	Grid standardDeviationGrid = new Grid();	
	
	//Constructor for the unStructuredCoveriancePanel class
	public UnStructuredCorrelationPanel()
	{
		//Instance of vertical panel to hold all the widgets created in this class
		VerticalPanel verticalPanel = new VerticalPanel();
	 
		HTML header = new HTML();
		HTML text = new HTML();
		HTML expectedStandardDeviationText = new HTML();
		HTML expectedCorrelationText = new HTML();
		
		//calling a method to construct the Standard Deviation text boxes 
		//based on the input obtained form Resposes List
		constructStandardDeviationGrid();
		
		
		//calling a method to construct a Correlation matrix based on the standard deviations entered
		constructCorrelationMatrix();
		
		
		verticalPanel.add(header);
		verticalPanel.add(text);
		verticalPanel.add(expectedStandardDeviationText);
		verticalPanel.add(standardDeviationGrid);
		verticalPanel.add(expectedCorrelationText);
		verticalPanel.add(correlationMatrix);
	 
		
		//initilizing the vertical pane widgets which holds all the widgets of the class		
		initWidget(verticalPanel);
	}
	
	/**This counstructs the Standard Deviation text boxes based on the input fron
	 * response list 
	 * @return grid
	 */
	public Grid constructStandardDeviationGrid()
	{
		List<String> stringList = new ArrayList<String>();
		stringList.add("HR");
		stringList.add("SBP");
		stringList.add("DBP");
		
		int size = stringList.size();
		
		for(int i = 0; i < size; i++)
		{
			HTML textLabel = new HTML(stringList.get(i));
			TextBox textBox = new TextBox();
			standardDeviationGrid.setWidget(i, 0, textLabel);
			standardDeviationGrid.setWidget(i, 1, textBox);
		}
		
		return standardDeviationGrid;
	}
	
	//Instance of the resizable matrix to create correlation matrix based on the 
	//inputs entered in the Expected Standard Deviation textboxes
	ResizableMatrix correlationMatrix = new ResizableMatrix("name", 1, 1,"   ", "title");
	/**
	 * This constructs the Correlation Matrix based on the input entered into the Standard Deviation Text Boxes
	 * @return Correlation Matrix which is a instance of resizable matrix
	 */
	 public ResizableMatrix constructCorrelationMatrix()
	 {
		 return correlationMatrix;
	 }
}
