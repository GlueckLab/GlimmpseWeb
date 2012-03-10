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
import edu.ucdenver.bios.glimmpseweb.client.guided.GuidedWizardPanel;
import edu.ucdenver.bios.glimmpseweb.client.matrix.MatrixWizardPanel;

/**
 * Main application panel for Glimmpse. 
 * 
 * @author Sarah Kreidler
 */
public class GlimmpseApplicationPanel extends Composite
implements ModeSelectionHandler
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
//        if (uploadedStudy != null)
//        {
//        	try
//        	{
////        		// for debug only - removes crap GWT wraps on ajax requests
////        		Window.alert("before ["+uploadedStudy+"]");
//        		uploadedStudy = uploadedStudy.replaceFirst("<pre>", "");
//        		uploadedStudy = uploadedStudy.replaceFirst("</pre>", "");
//        		uploadedStudy = uploadedStudy.replaceAll("&lt;", "<");
//        		uploadedStudy = uploadedStudy.replaceAll("&gt;", ">");
////        		Window.alert("after ["+uploadedStudy+"]");
//        		
//           		Document doc = XMLParser.parse(uploadedStudy);
//        		Node studyNode = doc.getElementsByTagName(GlimmpseConstants.TAG_STUDY).item(0);
//        		if (studyNode == null) throw new DOMException(DOMException.SYNTAX_ERR, "no study tag specified");
//        		Node mode = studyNode.getAttributes().getNamedItem(GlimmpseConstants.ATTR_MODE);
//        		if (mode != null && GlimmpseConstants.MODE_MATRIX.equals(mode.getNodeValue()))
//        		{
//        			matrixWizardPanel.reset();
//        			matrixWizardPanel.loadFromXML(doc);
//        			onMatrixMode();
//        		}
//        		else
//        		{
//        			guidedWizardPanel.reset();
//        			guidedWizardPanel.loadFromXML(doc);
//        			onGuidedMode();
//        		}
//        	}
//        	catch (DOMException e)
//        	{
//        		Window.alert(Glimmpse.constants.errorUploadInvalidStudyFile() + " [" + e.getMessage() + "]");
//        	}
//        }
//        else
//        {
//        	Window.alert(Glimmpse.constants.errorUploadFailed());
//        }
    }

//    /**
//     * Reset the panels when the user selects "Clear", "All" from one of the wizard
//     * toolbar menus
//     */
//	@Override
//	public void onCancel()
//	{
//		matrixWizardPanel.reset();
//		guidedWizardPanel.reset();
//		modeSelectionPanel.reset();
//		deckPanel.showWidget(START_INDEX);
//	}
}
