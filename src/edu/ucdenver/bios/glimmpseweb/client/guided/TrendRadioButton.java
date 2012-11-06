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

import com.google.gwt.user.client.ui.RadioButton;

import edu.ucdenver.bios.webservice.common.enums.HypothesisTrendTypeEnum;

/**
 * Radio button associated with a hypothesis trend type
 * @author Sarah Kreidler
 *
 */
public class TrendRadioButton extends RadioButton {
    private HypothesisTrendTypeEnum trendType;
    
    /**
     * Create a radio button with the specified trend as a value
     * @param group
     * @param label
     * @param trendType
     */
    public TrendRadioButton(String group, String label,
                HypothesisTrendTypeEnum trendType) {
        super(group, label);
        this.trendType = trendType;
    }
    
    /**
     * Get the trend value associated with this button
     * @return
     */
    public HypothesisTrendTypeEnum getTrend() {
        return trendType;
    }
}
