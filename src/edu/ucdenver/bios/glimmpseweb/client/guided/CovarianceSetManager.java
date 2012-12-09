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

import edu.ucdenver.bios.webservice.common.enums.CovarianceTypeEnum;

/**
 * Interface for classes which aggregate the results of
 * several CovarianceBuilder classes into a coherent
 * set of covariance objects
 * 
 * @author Sarah Kreidler
 *
 */
public interface CovarianceSetManager {
    
    /**
     * Mark the named covariance object as complete or 
     * incomplete
     */
    public void setComplete(String name, boolean complete);
    
    /**
     * Reset the type in the named covariance object
     * @param name
     * @param type
     */
    public void setType(String name, CovarianceTypeEnum type);
    
    /**
     * Update the values of the Lear parameters in the named covariance object
     * @param name
     * @param rho
     * @param delta
     */
    public void setLearParameters(String name, double rho, double delta);
    
    /**
     * Set the value of the specified cell in the named covariance matrix
     * @param name
     * @param row
     * @param column
     * @param value
     */
    public void setCovarianceCellValue(String name, int row, int column, double value);
    
    /**
     * Set the value of the specified standard deviation in the named covariance matrix
     * @param name
     * @param index
     * @param value
     */
    public void setStandardDeviationValue(String name, int index, double value);
}
