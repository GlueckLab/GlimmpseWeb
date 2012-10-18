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

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.connector.DomainObjectSerializer;
import edu.ucdenver.bios.glimmpseweb.client.guided.GuidedWizardPanel;
import edu.ucdenver.bios.glimmpseweb.client.matrix.MatrixWizardPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardActionListener;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;

/**
 * Main application panel for Glimmpse. 
 * 
 * @author Sarah Kreidler
 */
public class GlimmpseApplicationPanel extends Composite
implements ModeSelectionHandler, WizardActionListener
{
    private static final int START_INDEX = 0;
    private static final int GUIDED_INDEX = 1;
    private static final int MATRIX_INDEX = 2;

    protected DeckPanel deckPanel = new DeckPanel();
    protected ModeSelectionPanel modeSelectionPanel = new ModeSelectionPanel();
    protected MatrixWizardPanel matrixWizardPanel = new MatrixWizardPanel();
    protected GuidedWizardPanel guidedWizardPanel = new GuidedWizardPanel();

    /**
     * Constructor.  The main panel has a deck with three entries:
     * <ul>
     * <li>Start panel - main entry screen for user to select either guided/matrix mode</li>
     * <li>Guided panel - wizard panel for guided mode</li>
     * <li>Matrix panel - wizard panel for matrix mode</li>
     * </ul>
     */
    public GlimmpseApplicationPanel()
    {
        // listen for events on the wizard panels
        guidedWizardPanel.addWizardActionListener(this);
        matrixWizardPanel.addWizardActionListener(this);
        // add the start panel and wizard panels to the deck
        deckPanel.add(modeSelectionPanel);
        deckPanel.add(guidedWizardPanel);
        deckPanel.add(matrixWizardPanel);
        // show start screen first
        deckPanel.showWidget(START_INDEX);
        // add style
        deckPanel.setStyleName(GlimmpseConstants.STYLE_GLIMMPSE_PANEL);

        // set up listener relationships
        modeSelectionPanel.addModeSelectionHandler(this);

        // initialize
        initWidget(deckPanel);

    }

    /**
     * Display the guided mode wizard
     * @see ModeSelectionHandler
     */
    @Override
    public void onGuidedMode()
    {
        deckPanel.showWidget(GUIDED_INDEX);
    }

    /**
     * Display the matrix mode wizard
     * @see ModeSelectionHandler
     */
    @Override
    public void onMatrixMode()
    {
        deckPanel.showWidget(MATRIX_INDEX);
    }

    /**
     * Parse the uploaded file to determine whether the user was
     * using matrix or guided mode, then load the appropriate 
     * wizard from the XML document
     */
    public void onStudyUpload(String uploadedStudy)
    {
        if (uploadedStudy != null)
        {
            try
            {
                // REMOVE
//                uploadedStudy = uploadedStudy.substring(5,uploadedStudy.length()-6);
                // END REMOVE
                // the domain layer changed between GLIMMPSE Web 2.0.0beta and 
                // GLIMMPSE 2.0.0 official.  We try to correct for the previous format
                boolean studyFormatWarn = uploadedStudy.contains("\"id\":");
                if (studyFormatWarn) {
                    uploadedStudy = uploadedStudy.replace("\"id\":", "\"idx\":");
                }

                StudyDesign design = 
                    DomainObjectSerializer.getInstance().studyDesignFromJSON(uploadedStudy);
                // TODO: clear
                if (design != null && design.getViewTypeEnum() != null) {
                    if (studyFormatWarn) {
                        Window.alert("The format for GLIMMPSE study designs had been updated.  " +
                                "To update to the latest format, please save a new copy of your study design.");
                    }
                    switch (design.getViewTypeEnum()) {
                    case MATRIX_MODE:
                        matrixWizardPanel.loadStudyDesign(design);
                        deckPanel.showWidget(MATRIX_INDEX);
                        break;
                    case GUIDED_MODE:
                        guidedWizardPanel.loadStudyDesign(design);
                        deckPanel.showWidget(GUIDED_INDEX);
                        break;
                    }

                } else {
                    Window.alert(GlimmpseWeb.constants.errorUploadInvalidStudyFile());
                }
            } catch (Exception e) {
                Window.alert(GlimmpseWeb.constants.errorUploadInvalidStudyFile() + e.getMessage());
            }
        } else {
            Window.alert(GlimmpseWeb.constants.errorUploadFailed());
        }
    }

    @Override
    public void onNext() {
        // no action
    }

    @Override
    public void onPrevious() {
        // no action
    }

    @Override
    public void onPanel(WizardStepPanel panel) {
        // no action
    }

    @Override
    public void onFinish() {
        // no action
    }

    @Override
    public void onHelp() {
        // no action
    }

    @Override
    public void onSave() {
        // no action
    }

    /**
     * Reset the panels when the user selects "cancel" from one of the wizard
     * toolbar menus
     */
    @Override
    public void onCancel()
    {
        boolean cancel = Window.confirm(GlimmpseWeb.constants.confirmClearAll());
        if (cancel) {
            matrixWizardPanel.reset();
            guidedWizardPanel.reset();
            modeSelectionPanel.reset();
            deckPanel.showWidget(START_INDEX);
        }
    }
}
