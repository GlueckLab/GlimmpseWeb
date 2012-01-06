package edu.ucdenver.bios.glimmpseweb.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GlimmpseHeader extends Composite
{
	private static final String STYLE_HEADER = "header";
	private static final String STYLE_TITLE_BAR = "headerTitleBar";
	private static final String STYLE_NAME = "headerName";
	private static final String STYLE_LOGO = "headerLogo";
	private static final String STYLE_OWNER = "headerOwner";
	private static final String STYLE_DESCRIPTION = "headerDescription";
	private static final String STYLE_NAV_BAR = "headerNavBar";
	private static final String STYLE_NAV_BAR_LINK = "headerNavBarLink";
	
	public GlimmpseHeader()
	{
//	  	<!-- Glimmpse logo and title text -->
//		<div id="header">
//			<div id="glimmpse-logo"><img src="images/glimmpse.png" alt="GLIMMPSE" /></div>
//			<div id="glimmpse-name">GLIMMPSE</div>
//			<div id="glimmpse-description">Power and Sample Size for the<br/>General Linear Multivariate Model</div>
//			<div id="glimmpse-owner"><a href="http://www.cudenver.edu/Academics/Colleges/PublicHealth">Colorado School of Public Health, Department of Biostatistics &amp; Informatics</a></div>
//		</div>
//	    </td></tr>
//	    <tr><td>
//		<!-- top navigation bar  -->
//		<div id="horizontal-navbar">
//			<a href="index.html">Home</a>
//			<a href="about.html">About Us</a>
//			<a href="publications.html">Related Publications</a>
//			<a href="documentation.html">Documentation</a>
//			<a href="download.html">Download</a>
//		</div>
		// layout widgets
		final VerticalPanel panel = new VerticalPanel();
		final HorizontalPanel titleBar = new HorizontalPanel();
		final HorizontalPanel navBar = new HorizontalPanel();
		
		// content widgets for title bar
		final Image logo = new Image();
		logo.setUrl("images/glimmpse.png");
		final HTML name = new HTML("GLIMMPSE");
		final HTML description = new HTML("Power and Sample Size for the<br/>General Linear Multivariate Model");
		final Hyperlink ownerLink =
			new Hyperlink("Colorado School of Public Health, Department of Biostatistics &amp; Informatics", 
				true, "http://www.cudenver.edu/Academics/Colleges/PublicHealth");
		// content widgets for navigation bar
		final Hyperlink homeLink = new Hyperlink("Home", true, "index.html");
		final Hyperlink aboutLink = new Hyperlink("About Us", true, "about.html");
		final Hyperlink pubsLink = new Hyperlink("Related Publications", true, "publications.html");
		final Hyperlink docsLink = new Hyperlink("Documentation", true, "documentation.html");
		final Hyperlink downloadLink = new Hyperlink("Download", true, "download.html");
		
		// build the title bar
		titleBar.add(logo);
		titleBar.add(name);
		titleBar.add(description);
		// build the nav bar
		navBar.add(homeLink);
		navBar.add(aboutLink);
		navBar.add(pubsLink);
		navBar.add(docsLink);
		navBar.add(downloadLink);
		// layout the panel
		panel.add(titleBar);
		panel.add(ownerLink);
		panel.add(navBar);
		
		// set style
		panel.setStyleName(STYLE_HEADER);
		titleBar.setStyleName(STYLE_TITLE_BAR);
		logo.setStyleName(STYLE_LOGO);
		name.setStyleName(STYLE_NAME);
		description.setStyleName(STYLE_DESCRIPTION);
		ownerLink.setStyleName(STYLE_OWNER);
		navBar.setStyleName(STYLE_NAV_BAR);
		homeLink.setStyleName(STYLE_NAV_BAR_LINK);
		aboutLink.setStyleName(STYLE_NAV_BAR_LINK);
		pubsLink.setStyleName(STYLE_NAV_BAR_LINK);
		docsLink.setStyleName(STYLE_NAV_BAR_LINK);
		downloadLink.setStyleName(STYLE_NAV_BAR_LINK);
		initWidget(panel);
	}
}
