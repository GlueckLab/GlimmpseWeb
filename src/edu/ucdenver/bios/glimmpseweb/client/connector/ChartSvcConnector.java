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

import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.visualization.client.DataTable;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.webservice.common.domain.PowerCurveDataSeries;
import edu.ucdenver.bios.webservice.common.domain.PowerCurveDescription;
import edu.ucdenver.bios.webservice.common.domain.PowerResult;
import edu.ucdenver.bios.webservice.common.enums.HorizontalAxisLabelEnum;
import edu.ucdenver.bios.webservice.common.enums.StratificationVariableEnum;

/**
 * Creates a chart request, typically stored to an image widget
 * @author Sarah Kreidler
 *
 */
public class ChartSvcConnector {
    private static final String SERIES_DELIM = "|";
    private static final String CURVE_URL = GlimmpseWeb.constants.chartSvcHostScatter();
    private static final String LEGEND_URL = GlimmpseWeb.constants.chartSvcHostLegend();
    private NumberFormat doubleFormatter = NumberFormat.getFormat("0.0000");

    public ChartSvcConnector() {}

    /**
     * Create a power curve from the specified description and results
     * @param queryStr
     */
    public String buildScatterURL(String queryStr) {
        return CURVE_URL + "?" + queryStr;
    }
    
    /**
     * Create a power curve from the specified description and results
     * @param results
     * @param description
     * @param curveImageWidget
     * @param legendImageWidget
     */
    public String buildLegendURL(String queryStr) {
        return LEGEND_URL + "?" + queryStr;
    }
    
    /**
     * Create a power curve from the specified description and results
     * @param results
     * @param description
     * @param curveImageWidget
     * @param legendImageWidget
     */
    public String buildScatterURL(List<PowerResult> results,
            PowerCurveDescription curveDescription) {
        StringBuffer url = new StringBuffer();
        url.append(CURVE_URL + "?");
        url.append(buildQueryString(results, curveDescription));
        return url.toString();
    }

    /**
     * Create a power curve from the specified description and results
     * @param results
     * @param description
     * @param curveImageWidget
     * @param legendImageWidget
     */
    public String buildLegendURL(List<PowerResult> results,
            PowerCurveDescription curveDescription) {
        StringBuffer url = new StringBuffer();
        url.append(LEGEND_URL + "?");
        url.append(buildQueryString(results, curveDescription));
        return url.toString();
    }

    /**
     * Build the query string for a Chart svc scatter plot
     * @param resultList
     * @param curveDescription
     * @return
     */
    public String buildQueryString(List<PowerResult> resultList,
            PowerCurveDescription curveDescription) {
        StringBuffer queryStr = new StringBuffer();
        boolean first = true;
        // add the chart title
        if (curveDescription.getTitle() != null) {
            queryStr.append("chtt=" + curveDescription.getTitle());
            first = false;
        }
        
        // add axis labels
        if (!first) {
            queryStr.append("&");
        }
        switch(curveDescription.getHorizontalAxisLabelEnum()) {
        case TOTAL_SAMPLE_SIZE:
            queryStr.append("chxl=" + URL.encode(GlimmpseWeb.constants.curveOptionsSampleSizeLabel()));
            break;
        case REGRESSION_COEEFICIENT_SCALE_FACTOR:
            queryStr.append("chxl=" + URL.encode(GlimmpseWeb.constants.curveOptionsBetaScaleLabel()));
            break;
        case VARIABILITY_SCALE_FACTOR:
            queryStr.append("chxl=" + URL.encode(GlimmpseWeb.constants.curveOptionsSigmaScaleLabel()));
            break;
        }
        
        // add the width/height parameters
        queryStr.append(buildSizeQueryString(curveDescription.getWidth(),
                curveDescription.getHeight()));
        
        // add data series
        List<PowerCurveDataSeries> dataSeriesList = curveDescription.getDataSeriesList();
        boolean firstSeries = false;
        if (dataSeriesList != null) {
            StringBuffer seriesLabels = new StringBuffer();
            seriesLabels.append("chdl=");
            for(PowerCurveDataSeries dataSeries: dataSeriesList) {
                StringBuffer lowerCI = new StringBuffer();
                StringBuffer upperCI = new StringBuffer();
                StringBuffer main = new StringBuffer();
                StringBuffer xValues = new StringBuffer();
                
                boolean firstMatch = true;
                
                for(PowerResult result: resultList) {
                    if (isMatch(result, dataSeries, curveDescription.getHorizontalAxisLabelEnum())) {
                        if (!firstMatch) {
                            main.append(",");
                            xValues.append(",");
                        }
                        main.append(result.getActualPower());
                        
                        if (dataSeries.isConfidenceLimits() && 
                                result.getConfidenceInterval() != null) {
                            if (!firstMatch) {
                                lowerCI.append(",");
                                upperCI.append(",");
                            }
                            lowerCI.append(result.getConfidenceInterval().getLowerLimit());
                            upperCI.append(result.getConfidenceInterval().getUpperLimit());
                        }
                        firstMatch = false;
                    }
                }
                
                if (!firstSeries) {
                    queryStr.append(SERIES_DELIM);
                    seriesLabels.append(SERIES_DELIM);
                }
                queryStr.append(xValues.toString() + SERIES_DELIM + main.toString());
                if (lowerCI.length() > 0) {
                    queryStr.append(SERIES_DELIM + xValues.toString() + 
                            SERIES_DELIM + lowerCI.toString());
                }
                if (upperCI.length() > 0) {
                    queryStr.append(SERIES_DELIM + xValues.toString() + 
                            SERIES_DELIM + upperCI.toString());
                }
                
                firstSeries = false;
            }
            
            // append the series labels 
            queryStr.append(seriesLabels);
        }
        return queryStr.toString();
    }

    /**
     * Check if the current result matches the specified data series
     * @param result
     * @param dataSeries
     * @param axisType
     * @return
     */
    private boolean isMatch(PowerResult result, PowerCurveDataSeries dataSeries, 
            HorizontalAxisLabelEnum axisType) {
        switch(axisType)
        {
        case TOTAL_SAMPLE_SIZE:
            return (
                    (result.getBetaScale() != null &&
                            result.getBetaScale().getValue() == 
                                dataSeries.getBetaScale()) &&
                                (result.getSigmaScale() != null &&
                                        result.getSigmaScale().getValue() == 
                                            dataSeries.getSigmaScale()) &&
                                            (result.getPowerMethod() != null && 
                                                    result.getPowerMethod().getPowerMethodEnum() == 
                                                        dataSeries.getPowerMethod()) &&
                                                        (result.getQuantile() != null && 
                                                                result.getQuantile().getValue() ==
                                                                    dataSeries.getQuantile()) &&
                                                                    (result.getTest() != null &&
                                                                            result.getTest().getType() == 
                                                                                dataSeries.getStatisticalTestTypeEnum()) &&   
                                                                                (result.getAlpha() != null &&
                                                                                        result.getAlpha().getAlphaValue() == 
                                                                                            dataSeries.getTypeIError()));
        case REGRESSION_COEEFICIENT_SCALE_FACTOR:
            return (
                    result.getTotalSampleSize() == 
                        dataSeries.getSampleSize()) &&
                        (result.getSigmaScale() != null &&
                                result.getSigmaScale().getValue() == 
                                    dataSeries.getSigmaScale()) &&
                                    (result.getPowerMethod() != null && 
                                            result.getPowerMethod().getPowerMethodEnum() == 
                                                dataSeries.getPowerMethod()) &&
                                                (result.getQuantile() != null && 
                                                        result.getQuantile().getValue() ==
                                                            dataSeries.getQuantile()) &&
                                                            (result.getTest() != null &&
                                                                    result.getTest().getType() == 
                                                                        dataSeries.getStatisticalTestTypeEnum()) &&   
                                                                        (result.getAlpha() != null &&
                                                                                result.getAlpha().getAlphaValue() == 
                                                                                    dataSeries.getTypeIError());
        case VARIABILITY_SCALE_FACTOR:
            return (
                    result.getTotalSampleSize() == 
                        dataSeries.getSampleSize()) &&
                        (result.getBetaScale() != null &&
                                result.getBetaScale().getValue() == 
                                    dataSeries.getBetaScale()) &&
                                    (result.getPowerMethod() != null && 
                                            result.getPowerMethod().getPowerMethodEnum() == 
                                                dataSeries.getPowerMethod()) &&
                                                (result.getQuantile() != null && 
                                                        result.getQuantile().getValue() ==
                                                            dataSeries.getQuantile()) &&
                                                            (result.getTest() != null &&
                                                                    result.getTest().getType() == 
                                                                        dataSeries.getStatisticalTestTypeEnum()) &&   
                                                                        (result.getAlpha() != null &&
                                                                                result.getAlpha().getAlphaValue() == 
                                                                                    dataSeries.getTypeIError());       
        default:
            return false;
        }
    }


    private void appendPipeDelimitedList(StringBuffer buffer, String qParam, ArrayList<String> labels)
    {
        if (labels.size() > 0)
        {
            boolean first = true;
            for(String label: labels)
            {
                if (first)
                {
                    buffer.append(qParam);
                    first = false;
                }
                else
                {
                    buffer.append("|");
                }
                buffer.append(URL.encode(label));
            }
        }   
    }

    private String buildSizeQueryString(int width, int height)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append("&chs=");
        buffer.append(width);
        buffer.append("x");
        buffer.append(height);
        return buffer.toString();
    }
}
