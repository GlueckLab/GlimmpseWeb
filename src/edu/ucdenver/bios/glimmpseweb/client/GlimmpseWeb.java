package edu.ucdenver.bios.glimmpseweb.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.Table;

import edu.ucdenver.bios.glimmpseweb.client.shared.GlimmpseApplicationPanel;

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
	 * Load the GLIMMPSE application
	 */
    Runnable onLoadCallback = new Runnable() {
        public void run() {         
            // add the main application
            RootPanel glimmpseApp = RootPanel.get("glimmpseApp");
            if (glimmpseApp != null)
            {
                glimmpseApp.add(new GlimmpseApplicationPanel());
                glimmpseApp.setStyleName(GlimmpseConstants.STYLE_GLIMMPSE_PANEL);
            }

            // set root style so it recognizes standard css elements like "body"
            RootPanel.get().setStyleName("body");
        }
    };
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad()
	{        
		// Load the visualization api, passing the onLoadCallback to be called
		// when loading is done.
		VisualizationUtils.loadVisualizationApi(onLoadCallback, 
				Table.PACKAGE);
	}
}

