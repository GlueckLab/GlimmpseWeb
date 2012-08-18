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
package edu.ucdenver.bios.glimmpseweb.client.shared;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;

/**
 * Comment and Bug reporting form for GLIMMPSE.
 * Depends on formmail.php script from http://www.tectite.com
 * @author Sarah Kreidler
 *
 */
public class GlimmpseFeedbackPanel  extends Composite {

    private static final String FORMMAIL_URI = "/formmail.php";
    
    // form for sending feedback via formmail.php
    protected FormPanel feedbackForm = new FormPanel();
    protected Hidden envReportHidden = new Hidden("env_report");
    protected Hidden goodUrlHidden = new Hidden("good_url");
    protected Hidden badUrlHidden = new Hidden("bad_url");
    protected Hidden recipientsHidden = new Hidden("recipients");
    protected Hidden subjectHidden = new Hidden("subject");
    
    protected TextBox nameTextBox = new TextBox();
    protected TextBox emailAddressTextBox = new TextBox();
    protected ListBox subjectListBox = new ListBox();
    protected RadioButton contactYesRadioButton = new RadioButton("contact",
            GlimmpseWeb.constants.yes());
    protected RadioButton contactNoRadioButton = new RadioButton("contact",
            GlimmpseWeb.constants.no());
    protected ListBox browserListBox = new ListBox();
    protected TextBox browserOtherTextBox = new TextBox();
    protected ListBox versionListBox = new ListBox();
    protected TextArea messageTextArea = new TextArea();
    protected Button submitButton = new Button(
            GlimmpseWeb.constants.submit(), 
            new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    submitFeedback();
                }
    });
    
    public GlimmpseFeedbackPanel() {
        
        /* setup the hidden fields */
        envReportHidden.setValue("REMOTE_HOST,REMOTE_ADDR," +
        		"HTTP_USER_AGENT,AUTH_TYPE,REMOTE_USER");
        goodUrlHidden.setValue("/feedbackSuccess.html");
        badUrlHidden.setValue("/feedbackError.html");
        recipientsHidden.setValue("feedback@glimmpse.samplesizeshop.com");
        
        
        VerticalPanel panel = new VerticalPanel();

        HTML header = 
            new HTML(GlimmpseWeb.constants.feedbackHeader());
        HTML description = 
            new HTML(GlimmpseWeb.constants.feedbackDescription());
        
        // build subject listbox
        subjectListBox.addItem(GlimmpseWeb.constants.feedbackTypeGeneral());
        subjectListBox.addItem(GlimmpseWeb.constants.feedbackTypeFeature());
        subjectListBox.addItem(GlimmpseWeb.constants.feedbackTypeBug());
        
        // build browser listbox
        browserListBox.addItem(GlimmpseWeb.constants.browserChromeLabel());
        browserListBox.addItem(GlimmpseWeb.constants.browserIELabel());
        browserListBox.addItem(GlimmpseWeb.constants.browserFirefoxLabel());
        browserListBox.addItem(GlimmpseWeb.constants.browserOperaLabel());
        browserListBox.addItem(GlimmpseWeb.constants.browserSafariLabel());

        // build version listbox
        versionListBox.addItem("1.1.0");
        versionListBox.addItem("2.0.0");
        
        // build the form
        feedbackForm.setAction(FORMMAIL_URI);
        VerticalPanel formContainer = new VerticalPanel();
        // select feature, bug, or comment
        formContainer.add(buildInputQuestion(
                GlimmpseWeb.constants.feedbackTypeLabel()));
        formContainer.add(subjectListBox);
        // enter name
        formContainer.add(buildInputQuestion(
                GlimmpseWeb.constants.feedbackNameLabel()));
        formContainer.add(nameTextBox);
        // enter email
        formContainer.add(buildInputQuestion(
                GlimmpseWeb.constants.feedbackEmailLabel()));
        formContainer.add(emailAddressTextBox);
        // enter if contact is okay
        formContainer.add(buildInputQuestion(
                GlimmpseWeb.constants.feedbackMayWeContactLabel()));
        Grid yesNoContainer = new Grid(1,2);
        contactYesRadioButton.setValue(true);
        yesNoContainer.setWidget(0, 0, contactYesRadioButton);
        yesNoContainer.setWidget(0, 1, contactNoRadioButton);
        formContainer.add(yesNoContainer);
        // browser select with other
        formContainer.add(buildInputQuestion(
                GlimmpseWeb.constants.feedbackBrowserLabel()));
        formContainer.add(browserListBox);
        formContainer.add(buildInputQuestion(
                GlimmpseWeb.constants.feedbackOtherLabel()));
        formContainer.add(browserOtherTextBox);
        // version dropdown
        formContainer.add(buildInputQuestion(
                GlimmpseWeb.constants.feedbackVersionLabel()));
        formContainer.add(versionListBox);
        // actual message contents
        formContainer.add(buildInputQuestion(
                GlimmpseWeb.constants.feedbackContentsLabel()));
        formContainer.add(messageTextArea);
        formContainer.add(submitButton);
        // add the container to the form
        feedbackForm.setWidget(formContainer);
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(feedbackForm);
        
        // set style
        header.setStyleName(GlimmpseConstants.STYLE_FEEDBACK_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_FEEDBACK_DESCRIPTION);
        subjectListBox.setStyleName(GlimmpseConstants.STYLE_FEEDBACK_RESPONSE);
        nameTextBox.setStyleName(GlimmpseConstants.STYLE_FEEDBACK_RESPONSE);
        emailAddressTextBox.setStyleName(GlimmpseConstants.STYLE_FEEDBACK_RESPONSE);
        yesNoContainer.setStyleName(GlimmpseConstants.STYLE_FEEDBACK_RESPONSE);
        browserListBox.setStyleName(GlimmpseConstants.STYLE_FEEDBACK_RESPONSE);
        browserOtherTextBox.setStyleName(GlimmpseConstants.STYLE_FEEDBACK_RESPONSE);
        versionListBox.setStyleName(GlimmpseConstants.STYLE_FEEDBACK_RESPONSE);
        messageTextArea.setStyleName(GlimmpseConstants.STYLE_FEEDBACK_MESSAGE);
        submitButton.setStyleName(GlimmpseConstants.STYLE_FEEDBACK_SUBMIT);
        
        initWidget(panel);
    }
    
    /**
     * Convenience routine to build the html label and set style
     * @param value string value
     * @return HTML widget
     */
    private HTML buildInputQuestion(String value) {
        HTML valueHTML = new HTML(value);
        valueHTML.setStyleName(GlimmpseConstants.STYLE_FEEDBACK_QUESTION);
        return valueHTML;
    }
    
    
    /**
     * submit the form to the formmail script
     */
    private void submitFeedback() {
        feedbackForm.submit();
        feedbackForm.reset();
    }
}
