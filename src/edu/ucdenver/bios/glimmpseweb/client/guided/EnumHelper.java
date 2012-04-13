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

import edu.ucdenver.bios.webservice.common.enums.HypothesisTrendTypeEnum;

/**
 * 
 * @author VIJAY AKULA
 *
 */

public class EnumHelper {
    public HypothesisTrendTypeEnum getEnum(String value)
    {
        HypothesisTrendTypeEnum enumValue = null;
        
        if(HypothesisTrendTypeEnum.NONE.equals(value))
        {
            enumValue = HypothesisTrendTypeEnum.NONE;
        }
        else if(HypothesisTrendTypeEnum.CHANGE_FROM_BASELINE.equals(value))
        {
            enumValue = HypothesisTrendTypeEnum.CHANGE_FROM_BASELINE;
        }
        else if(HypothesisTrendTypeEnum.ALL_POYNOMIAL.equals(value))
        {
            enumValue = HypothesisTrendTypeEnum.ALL_POYNOMIAL;
        }
        else if(HypothesisTrendTypeEnum.LINEAR.equals(value))
        {
            enumValue = HypothesisTrendTypeEnum.LINEAR;
        }
        else if(HypothesisTrendTypeEnum.QUADRATIC.equals(value))
        {
            enumValue = HypothesisTrendTypeEnum.QUADRATIC;
        }
        else if(HypothesisTrendTypeEnum.CUBIC.equals(value))
        {
            enumValue = HypothesisTrendTypeEnum.CUBIC;
        }
        return enumValue;
    }
}
