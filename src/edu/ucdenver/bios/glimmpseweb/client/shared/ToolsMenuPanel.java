package edu.ucdenver.bios.glimmpseweb.client.shared;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.MenuItemSeparator;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;

public class ToolsMenuPanel extends Composite
{
	// menu commands
    // Create a command that will execute on menu item selection
    Command menuCommand = new Command() {

      public void execute() {
    	  Window.alert("Testing");
      }
    };
	
	
	public ToolsMenuPanel()
	{
		DockPanel panel = new DockPanel();
		MenuBar menu = new MenuBar();
	    menu.setAutoOpen(true);
	    menu.setAnimationEnabled(true);
	    
	    // add the tools dropdown
	    MenuBar toolsMenuBar = new MenuBar(true);
	    toolsMenuBar.addItem(new MenuItem("Save Study Design", menuCommand));
	    toolsMenuBar.addItem(new MenuItem("Save Results", menuCommand));
	    toolsMenuBar.addItem(new MenuItem("Save Curve", menuCommand));
	    toolsMenuBar.addSeparator();
	    toolsMenuBar.addItem(new MenuItem("Help", menuCommand));
	    toolsMenuBar.addItem(new MenuItem("New", menuCommand));
	    MenuItem toolsMenu = 
	    	new MenuItem(GlimmpseWeb.constants.toolsMenu(), toolsMenuBar);
	    menu.addItem(toolsMenu);
	    
	    panel.add(menu, DockPanel.EAST);
	    
	    // set style
	    
	    panel.setStyleName(GlimmpseConstants.STYLE_TOOLS_MENU_PANEL);
	    toolsMenu.setStyleName(GlimmpseConstants.STYLE_TOOLS_MENU_HEADER);
	    initWidget(panel);
	}
}
