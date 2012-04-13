package edu.ucdenver.bios.glimmpseweb.client.guided;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ResizableMatrixPanel;
import edu.ucdenver.bios.webservice.common.domain.Covariance;

public class UnStructuredCovariancePanel extends Composite implements CovarianceBuilder
{
	ResizableMatrixPanel covarianceMatrix;

	HorizontalPanel horizontalPanel = new HorizontalPanel();
	
	List<String> labelList;
	
	List<Integer> spacingList;
	
	int size;
	
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
		size = spacingList.size();
		covarianceMatrix = new ResizableMatrixPanel(size, size, false, false, true, true);
		covarianceMatrix.setRowLabels(labelList);
		covarianceMatrix.setColumnLabels(labelList);
		horizontalPanel.add(covarianceMatrix);
	}
	
    @Override
    public Covariance getCovariance() 
    {
        Covariance covariance = new Covariance();
        
        double [][] matrixData = new double[size][size];
        
        for(int i = 0; i < size; i++)
        {
            for( int j = 0; j < size; j++)
            {
                matrixData[i][j] = covarianceMatrix.getCellValue(i, j);
            }
        }
        covariance.setBlobFromArray(matrixData);
        return covariance;
    }
}
