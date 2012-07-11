/*
 * User Interface for the GLIMMPSE Software System.  Processes
 * incoming HTTP requests for power, sample size, and detectable
 * difference
 * 
 * Copyright (C) 2011 Regents of the University of Colorado.  
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
package edu.ucdenver.bios.glimmpseweb.client.guided;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ResizableMatrixPanel;
import edu.ucdenver.bios.webservice.common.domain.Covariance;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.enums.CovarianceTypeEnum;

/**
 * Entry panel for a generic covariance matrix
 * @author Vijay Akula
 * @author Sarah Kreidler
 *
 */
public class UnStructuredCovariancePanel extends Composite 
implements CovarianceBuilder
{
    // matrix display panel
	protected ResizableMatrixPanel covarianceMatrix;
	// name for this covariance component
	protected String name;
    // parent panel
    protected ChangeHandler parent = null;
    
	public UnStructuredCovariancePanel(String name, List<String> labelList, 
	        List<Integer> spacingList, ChangeHandler handler)
	{
		VerticalPanel verticalPanel = new VerticalPanel();
		this.name = name;
        parent = handler;
		HTML header = new HTML();
		header.setText(GlimmpseWeb.constants.unstructuredCovarianceHeader());
		HTML instructions = new HTML();
		instructions.setText(GlimmpseWeb.constants.unstructuredCovarianceInstructions());
		
		int size = spacingList.size();
		covarianceMatrix = new ResizableMatrixPanel(size, size, false, true, true, true);
		covarianceMatrix.setRowLabels(labelList);
		covarianceMatrix.setColumnLabels(labelList);
		
		verticalPanel.add(header);
		verticalPanel.add(instructions);
		verticalPanel.add(covarianceMatrix);
		initWidget(verticalPanel);
	}
	
	/**
	 * Build a covariance object from the panel
	 */
    @Override
    public Covariance getCovariance() 
    {
        NamedMatrix matrix = covarianceMatrix.toNamedMatrix(name);
        Covariance covariance = new Covariance();
        covariance.setName(name);
        covariance.setType(CovarianceTypeEnum.UNSTRUCTURED_COVARIANCE);
        covariance.setColumns(matrix.getColumns());
        covariance.setRows(matrix.getRows());
        covariance.setBlob(matrix.getData());
        return covariance;
    }
    

    /**
     * Indicates if all required information has been entered
     * @return true if the screen is complete
     */
    public boolean checkComplete() {
        return true;
    }
}
