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

import java.util.List;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;
import edu.ucdenver.bios.webservice.common.domain.PowerResult;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;

public class PowerSvcConnector extends Composite {
    
    private static final String MEDIA_TYPE = "application/json";
    private static final String MATRIX_DISPLAY_WINDOW = "matrixDisplay";
    // JSON encoder/decoder class
    private static final DomainObjectSerializer serializer = 
        DomainObjectSerializer.getInstance();
    
    // form for saving the study design
    protected FormPanel matrixDisplayForm = 
            new FormPanel(MATRIX_DISPLAY_WINDOW);
    protected Hidden studyDesignHidden = new Hidden("studydesign");
    
    /**
     * Create a new connector to the power service
     */
    public PowerSvcConnector() {
        VerticalPanel panel = new VerticalPanel();
        
        // set up the form for saving study designs
        VerticalPanel matrixFormContainer = new VerticalPanel();
        matrixFormContainer.add(studyDesignHidden);
        matrixDisplayForm.setAction(GlimmpseWeb.constants.powerSvcHostMatricesAsHTML());
        matrixDisplayForm.setMethod(FormPanel.METHOD_POST);
        matrixDisplayForm.addSubmitHandler(new SubmitHandler() {
            @Override
            public void onSubmit(SubmitEvent event) {
                Window.open("", MATRIX_DISPLAY_WINDOW, 
                        "width=600,height=500,location=no,toolbars=no,menubar=no,status=yes,resizable=yes,scrollbars=yes");
            }  
        });
        matrixDisplayForm.add(matrixFormContainer);
        panel.add(matrixDisplayForm);
        
        initWidget(panel);
    }   
    
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
     * Send a request to the power service to calculate power
     * @param studyDesign The study design object
     * @param callback handler for AJAX request to power service
     */
    public void getMatrices(StudyDesign studyDesign, RequestCallback callback) 
    throws RequestException {

        String entity = serializer.toJSON(studyDesign);   
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, GlimmpseWeb.constants.powerSvcHostMatrices());

        builder.setHeader("Content-Type", MEDIA_TYPE);
        builder.setHeader("Accept", MEDIA_TYPE);
        builder.sendRequest(entity, callback);
    }  
    
    /**
     * Send a request to the power service to calculate power
     * @param studyDesign The study design object
     * @param callback handler for AJAX request to power service
     */
    public void getMatricesAsHTML(StudyDesign studyDesign, RequestCallback callback) 
    throws RequestException {

        String entity = serializer.toJSON(studyDesign);   
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, 
                GlimmpseWeb.constants.powerSvcHostMatricesAsHTML());

        builder.setHeader("Content-Type", MEDIA_TYPE);
        builder.setHeader("Accept", MEDIA_TYPE);
        builder.sendRequest(entity, callback);
    } 
    
    /**
     * Send a form request to the power service to display the
     * matrices associated with the study design.
     * @param studyDesign The study design object
     */
    public void getMatricesAsHTML(StudyDesign studyDesign) {
        if (studyDesign != null) {
            String jsonEncoded = serializer.toJSON(studyDesign);
            studyDesignHidden.setValue(jsonEncoded);
            matrixDisplayForm.submit();
        }
        matrixDisplayForm.reset();
    } 
    
    /**
     * Parse an entity body into a power result list
     * @param entity JSON encoded entity body
     * @return list of PowerResult objects
     */
    public List<PowerResult> parsePowerResultList(String entity) {
        return serializer.powerResultListFromJSON(entity);
    }
    
    /**
     * Parse an entity body into a list of power matrices
     * @param entity JSON encoded entity body
     * @return NamedMatrix list
     */
    public List<NamedMatrix> parseMatrixList(String entity) {
        return serializer.matrixListFromJSON(entity);
    }
    
    
}
