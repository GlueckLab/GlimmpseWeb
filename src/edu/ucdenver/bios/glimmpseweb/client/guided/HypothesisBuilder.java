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
package edu.ucdenver.bios.glimmpseweb.client.guided;

import edu.ucdenver.bios.webservice.common.domain.Hypothesis;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;

/**
 * Common interface for generating hypothesis objects
 * @author Sarah Kreidler
 *
 */
public interface HypothesisBuilder {

    /**
     * Create a Hypothesis domain object from the current panel
     * @return Hypothesis object
     */
    public Hypothesis buildHypothesis();
    
    /**
     * Create a Hypothesis domain object from the current panel
     * @return Hypothesis object
     */
    public NamedMatrix buildThetaNull();
    
    /**
     * Indicates if the panel has sufficient information to build a hypothesis
     * @return true if complete, false otherwise
     */
    public boolean checkComplete();

}
