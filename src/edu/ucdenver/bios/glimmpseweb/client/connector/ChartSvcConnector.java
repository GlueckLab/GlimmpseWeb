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
package edu.ucdenver.bios.glimmpseweb.client.connector;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.visualization.client.DataTable;

import edu.ucdenver.bios.webservice.common.domain.PowerCurveDescription;
import edu.ucdenver.bios.webservice.common.domain.PowerResult;

/**
 * Creates a chart request, typically stored to an image widget
 * @author Sarah Kreidler
 *
 */
public class ChartSvcConnector {

    protected String queryString = null;
    protected List<PowerResult> powerResults = null;
    
    public ChartSvcConnector() {}
    
    public void loadData(List<PowerResult> results,
            PowerCurveDescription description) {
        queryString = "";
//        this.powerResults = buildSubset(results);
//        defineGroups();
//        queryString = buildQueryString(true);
//        queryStringNoLegend = buildQueryString(false);
    }
    
    public void loadData(DataTable data)
    {

    }
    
    /**
     * Create a power curve from the specified description and results
     * @param results
     * @param description
     * @param curveImageWidget
     * @param legendImageWidget
     */
    public void buildChartRequest(List<PowerResult> results,
            PowerCurveDescription description,
            Image curveImageWidget,
            Image legendImageWidget) {
        ArrayList<PowerResult> subset = new ArrayList<PowerResult>();
    }
    
}
