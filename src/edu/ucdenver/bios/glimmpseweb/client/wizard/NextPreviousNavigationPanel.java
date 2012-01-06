package edu.ucdenver.bios.glimmpseweb.client.wizard;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;

public class NextPreviousNavigationPanel extends Composite
{
    ArrayList<NavigationListener> listeners = new ArrayList<NavigationListener>();
    protected Button next;
    protected Button previous;
    
    public NextPreviousNavigationPanel()
    {
        HorizontalPanel panel = new HorizontalPanel();
       
        next = new Button(GlimmpseWeb.constants.buttonNext(), new ClickHandler() {
            public void onClick(ClickEvent event) {
                notifyOnNext();
            }
        });
        previous = new Button(GlimmpseWeb.constants.buttonPrevious(), new ClickHandler() {
            public void onClick(ClickEvent event) {
                notifyOnPrevious();
            }
        });

        panel.add(previous);
        panel.add(next);
        
        // add style
        previous.setStyleName("wizardNavigationPanelButton");
        next.setStyleName("wizardNavigationPanelButton");
        panel.setStyleName("wizardNavigationPanel");
                
        initWidget(panel);
    }
    
    protected void notifyOnNext()
    {
        for(NavigationListener listener: listeners)
            listener.onNext();
    }
    
    protected void notifyOnPrevious()
    {
        for(NavigationListener listener: listeners)
            listener.onPrevious();
    }
    
    public void setNext(boolean enabled)
    {
        next.setEnabled(enabled);
    }
    
    public void setPrevious(boolean enabled)
    {
        previous.setEnabled(enabled);
    }

    public void addNavigationListener(NavigationListener listener)
    {
        listeners.add(listener);
    }
}
