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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;


/**
 * Panel for displaying matrices used in power calculations.
 * This is a pop-up on the results screen.
 *
 */
public class MatrixDisplayPanel extends Composite
{
	protected FlexTable tableOfMatrices = new FlexTable();
	
	public MatrixDisplayPanel()
	{
		VerticalPanel panel = new VerticalPanel();

		panel.add(tableOfMatrices);
		
		// style 
		tableOfMatrices.setBorderWidth(1);
		
		// initialize
		initWidget(panel);
	}
	
	public void loadFromMatrixList(List<NamedMatrix> matrixList)
	{
	    int row = 0;
	    for(NamedMatrix matrix: matrixList) {
	        tableOfMatrices.setWidget(row, 0, new HTML(prettyMatrixName(matrix.getName())));
	        ResizableMatrixPanel panel = new ResizableMatrixPanel(matrix.getRows(), matrix.getColumns(), true, false, false, false);
	        panel.setEnabledColumnDimension(false);
	        panel.setEnabledRowDimension(false);
	        panel.loadFromNamedMatrix(matrix);
            tableOfMatrices.setWidget(row, 1, panel);
            row++;
	    }
	}
	
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
	
	public void reset()
	{
		tableOfMatrices.removeAllRows();
	}
	

	
}
