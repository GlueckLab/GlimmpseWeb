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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.webservice.common.domain.PowerResultList;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;

public class PowerSvcConnector {
    
    private static final String MEDIA_TYPE = "application/json";

    // JSON encoder/decoder class
    private static final DomainObjectSerializer serializer = new DomainObjectSerializer();
    
    /**
     * Create a new connector to the power service
     */
    public PowerSvcConnector() {}   
    
    /**
     * Send a request to the power service to calculate power
     * @param studyDesign The study design object
     * @param callback handler for AJAX request to power service
     */
    public void getPower(StudyDesign studyDesign, RequestCallback callback) 
    throws RequestException {

        String entity = serializer.toJSON(studyDesign);   
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, GlimmpseWeb.constants.powerSvcHostPower());

        builder.setHeader("Content-Type", MEDIA_TYPE);
        builder.setHeader("Accept", MEDIA_TYPE);
        builder.sendRequest(entity, callback);

    }  
    
    /**
     * Send a request to the power service to calculate power
     * @param studyDesign The study design object
     * @param callback handler for AJAX request to power service
     */
    public void getSampleSize(StudyDesign studyDesign, RequestCallback callback) 
    throws RequestException {

        String entity = serializer.toJSON(studyDesign);   
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, GlimmpseWeb.constants.powerSvcHostSampleSize());

        builder.setHeader("Content-Type", MEDIA_TYPE);
        builder.setHeader("Accept", MEDIA_TYPE);
        builder.sendRequest(entity, callback);

    }  
    
    /**
     * Parse an entity body into a power result list
     * @param entity JSON encoded entity body
     * @return PowerResultList
     */
    public PowerResultList parsePowerResultList(String entity) {
        return serializer.powerResultListFromJSON(entity);
    }
    
    
}
