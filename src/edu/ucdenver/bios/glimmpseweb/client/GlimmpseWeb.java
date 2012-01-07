package edu.ucdenver.bios.glimmpseweb.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

import edu.ucdenver.bios.glimmpseweb.client.guided.GuidedWizardPanel;
import edu.ucdenver.bios.glimmpseweb.client.matrix.MatrixWizardPanel;

/**
 * Entry point class for the GLIMMPSE web interface.
 */
public class GlimmpseWeb implements EntryPoint
{
	/**
	 * This is the entry point method.
	 */
	// string constants for internationalization 
	public static final GlimmpseConstants constants =  
		(GlimmpseConstants) GWT.create(GlimmpseConstants.class); 

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{        
		// add the glimmpse header
		RootPanel glimmpseHeader = RootPanel.get("glimmpseHeader");
		if (glimmpseHeader != null)
		{
			glimmpseHeader.add(new GlimmpseHeader());
			glimmpseHeader.setStyleName(GlimmpseConstants.STYLE_GLIMMPSE_PANEL);
		}
		// add the main wizard
		RootPanel glimmpseMatrixWizard = RootPanel.get("glimmpseMatrixWizard");
		if (glimmpseMatrixWizard != null)
		{
			glimmpseMatrixWizard.add(new MatrixWizardPanel());
			glimmpseMatrixWizard.setStyleName(GlimmpseConstants.STYLE_GLIMMPSE_PANEL);
		}
		// add the main wizard
		RootPanel glimmpseGuidedWizard = RootPanel.get("glimmpseGuidedWizard");
		if (glimmpseGuidedWizard != null)
		{
			glimmpseGuidedWizard.add(new GuidedWizardPanel());
			glimmpseGuidedWizard.setStyleName(GlimmpseConstants.STYLE_GLIMMPSE_PANEL);
		}
		// add the footer
		RootPanel glimmpseFooter = RootPanel.get("glimmpseFooter");
		if (glimmpseFooter != null)
		{
			glimmpseFooter.add(new GlimmpseFooter());
			glimmpseFooter.setStyleName(GlimmpseConstants.STYLE_GLIMMPSE_PANEL);
		}
		// set root style so it recognizes standard css elements like "body"
		RootPanel.get().setStyleName("body");

		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
//		VisualizationUtils.loadVisualizationApi(onLoadCallback, 
//				Table.PACKAGE);
	}
}

