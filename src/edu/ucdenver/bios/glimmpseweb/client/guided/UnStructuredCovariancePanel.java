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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.shared.ResizableMatrixChangeHandler;
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
implements CovarianceBuilder, ResizableMatrixChangeHandler
{
    // matrix display panel
    protected ResizableMatrixPanel covarianceMatrix;
    // name for this covariance component
    protected String name;
    // parent panel
    protected CovarianceSetManager manager = null;

    public UnStructuredCovariancePanel(CovarianceSetManager manager, 
            String name, List<String> labelList, 
            List<Integer> spacingList)
    {
        VerticalPanel verticalPanel = new VerticalPanel();
        this.name = name;
        this.manager = manager;
        HTML header = new HTML();
        header.setText(GlimmpseWeb.constants.unstructuredCovarianceHeader());
        HTML instructions = new HTML();
        instructions.setText(GlimmpseWeb.constants.unstructuredCovarianceInstructions());

        int size = spacingList.size();
        covarianceMatrix = new ResizableMatrixPanel(size, size, false, true, true, true);
        covarianceMatrix.setRowLabels(labelList);
        covarianceMatrix.setColumnLabels(labelList);
        covarianceMatrix.addChangeHandler(this);

        verticalPanel.add(header);
        verticalPanel.add(instructions);
        verticalPanel.add(covarianceMatrix);
        initWidget(verticalPanel);
    }

    /**
     * Build a covariance object from the panel
     */
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

    /**
     * Load the panel from the specified covariance object
     * @param covariance covariance object
     */
    @Override
    public void loadCovariance(Covariance covariance) {
        if (covariance != null && 
                CovarianceTypeEnum.UNSTRUCTURED_COVARIANCE == covariance.getType()) {
            // load the data
            if (covariance.getBlob() != null) {
                covarianceMatrix.loadMatrixData(covariance.getRows(), 
                        covariance.getColumns(), covariance.getBlob().getData());
            }
            manager.setComplete(name, checkComplete());
        }
    }

    /**
     * Event handler for row resize events in the covariance matrix
     */
    @Override
    public void onRowDimension(int rows) {
        // nothing to do since user can't change dimension of 
        // covariance from this screen
    }

    /**
     * Event handler for column resize events in the covariance matrix
     */
    @Override
    public void onColumnDimension(int columns) {
        // nothing to do since user can't change dimension of 
        // covariance from this screen
    }

    /**
     * Handler for changes to the resizable matrix contents
     */
    @Override
    public void onCellChange(int row, int column, double value) {
        manager.setCovarianceCellValue(name, row, column, value);
        manager.setComplete(name, checkComplete());
    }

    /**
     * Sync the GUI view with the context
     */
    @Override
    public void syncCovariance() 
    {
        NamedMatrix matrix = covarianceMatrix.toNamedMatrix(name);
        // sync the type
        manager.setType(name, CovarianceTypeEnum.UNSTRUCTURED_COVARIANCE);
        // sync the standard deviation values
        for(int i = 0; i < matrix.getRows(); i++)
        {
            manager.setStandardDeviationValue(name, i, 1);
        }
        // sync the actual covariance values
        double[][] data = matrix.getData().getData();
        for(int row = 0; row < matrix.getRows(); row++) {
            for(int column = 0; column <= row; column++) {
                manager.setCovarianceCellValue(name, row, column, data[row][column]);
            }
        }
    }

}
