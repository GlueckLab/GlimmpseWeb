/*
 * Web Interface for the GLIMMPSE Software System.  Allows
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

/**
 * Handler for changes to the matrix dimension or contents
 * @author Sarah Kreidler
 *
 */
public interface ResizableMatrixChangeHandler {
    /**
     * Handle changes in the row dimension of the matrix
     * @param rows new row dimension
     */
    public void onRowDimension(int rows);
    
    /**
     * Handle changes in the column dimension of the matrix
     * @param columns new column dimensions
     */
    public void onColumnDimension(int columns);
    
    /**
     * Handle changes in the contents of the specified cell
     * @param row cell row
     * @param column cell column
     * @param value new cell value
     */
    public void onCellChange(int row, int column, double value);
}
