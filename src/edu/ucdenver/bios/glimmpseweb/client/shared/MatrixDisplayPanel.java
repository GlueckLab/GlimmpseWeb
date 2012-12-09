/*
 * User Interface for the GLIMMPSE Software System.  Allows
 * users to perform power, sample size, and detectable difference
 * calculations. 
 * 
 * Copyright (C) 2010 Regents of the University of Colorado.  
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package edu.ucdenver.bios.glimmpseweb.client.shared;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.webservice.common.domain.Blob2DArray;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;


/**
 * Panel for displaying matrices used in power calculations.
 * This is a pop-up on the results screen.
 *
 */
public class MatrixDisplayPanel extends Composite
{
    protected final static String MATH_AREA_ID = "matrixMathML";
    protected final static String TABLE_OPEN = "<table border='1'>";
    protected final static String TABLE_CLOSE = "</table>";
    protected final static String TR_OPEN = "<tr>";
    protected final static String TR_CLOSE = "</tr>";
    protected final static String TD_OPEN = "<td>";
    protected final static String TD_CLOSE = "</td>";
    
//	protected FlexTable tableOfMatrices = new FlexTable();
	protected HTML matrixDisplayHTML = new HTML();
    
	public MatrixDisplayPanel()
	{
		VerticalPanel panel = new VerticalPanel();
		ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setSize("600px", "500px");
		scrollPanel.add(matrixDisplayHTML);
		panel.add(scrollPanel);
		
		// style 
//		tableOfMatrices.setBorderWidth(1);
		
		// initialize
		initWidget(panel);
	}
	
	/**
	 * Display an error 
	 * @param error
	 */
	public void showError(String error) {
	    matrixDisplayHTML.setText(error);
	}
	
	   /**
     * Display the specified HTML in the panel
     * @param matrixList list of matrices
     */
    public void loadFromMatrixHTML(String matrixHTML)
    {
        matrixDisplayHTML.setHTML(matrixHTML);
        typesetMathJax();
    }
	
	/**
	 * Display the list of matrices in the panel.
	 * @param matrixList list of matrices
	 */
	public void loadFromMatrixList(List<NamedMatrix> matrixList)
	{
	    StringBuffer buffer = new StringBuffer();
	    if (matrixList != null) {
	        buffer.append(TABLE_OPEN);
	        for(NamedMatrix matrix: matrixList) {
	            buffer.append(TR_OPEN);
	            buffer.append(TD_OPEN);
	            buffer.append(prettyMatrixName(matrix.getName()) + 
	                    "<br/>(" + matrix.getRows() + " x " +  matrix.getColumns() + ")");
	            buffer.append(TD_CLOSE);
	            buffer.append(TD_OPEN);
	            appendMatrix(buffer, matrix);
	            buffer.append(TD_CLOSE);
	            buffer.append(TR_CLOSE);
	        }
	        buffer.append(TABLE_CLOSE);
	        matrixDisplayHTML.setHTML(buffer.toString());
	    } else {
	        showError("Matrix information unavailable");
	    }
	}
	
	/**
	 * Append the matrix to the specified buffer as an HTML table
	 * @param name name of the matrix
	 * @return display name of the matrix
	 */
	private void appendMatrix(StringBuffer buffer, NamedMatrix matrix) {
	    int rows = matrix.getRows();
	    int columns = matrix.getColumns();
	    Blob2DArray blob = matrix.getData();
	    if (blob != null) {
	        double[][] data = blob.getData();
	        buffer.append(TABLE_OPEN);
	        for(int row = 0; row < rows; row++) {
	            buffer.append(TR_OPEN);
	            for(int col = 0; col < columns; col++) {
	                buffer.append(TD_OPEN);
	                buffer.append(Double.toString(data[row][col]));
	                buffer.append(TD_CLOSE);
	            }
	            buffer.append(TR_CLOSE);
	        }
	        buffer.append(TABLE_CLOSE);
	    }
	}
	
	/**
	 * Return the display name of the matrix
	 * @param name name of the matrix
	 * @return display name of the matrix
	 */
	private String prettyMatrixName(String name)
	{
		if (GlimmpseConstants.MATRIX_DESIGN.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.matrixCategoricalEffectsLabel();
		else if (GlimmpseConstants.MATRIX_WITHIN_CONTRAST.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.withinSubjectContrastMatrixName();
		else if (GlimmpseConstants.MATRIX_SIGMA_ERROR.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.sigmaErrorMatrixName();
		else if (GlimmpseConstants.MATRIX_SIGMA_OUTCOME.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.sigmaOutcomeMatrixName();
		else if (GlimmpseConstants.MATRIX_SIGMA_OUTCOME_COVARIATE.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.sigmaOutcomeCovariateMatrixName();
		else if (GlimmpseConstants.MATRIX_SIGMA_COVARIATE.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.sigmaCovariateMatrixName();
		else if (GlimmpseConstants.MATRIX_THETA.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.thetaNullMatrixName();
		else if (GlimmpseConstants.MATRIX_BETA.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.betaFixedMatrixName();
		else if (GlimmpseConstants.MATRIX_BETWEEN_CONTRAST.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.betweenSubjectContrastMatrixName();
		else
			return "";
	}
	
	/**
	 * Clear the screen
	 */
	public void reset()
	{
		matrixDisplayHTML.setText("");
	}

    /**
     * Native javascript call to run the MathJax typesetter
     */
    public native void typesetMathJax() /*-{
        MathJax.Hub.Queue(["Typeset",MathJax.Hub]);
    }-*/;
    
}
