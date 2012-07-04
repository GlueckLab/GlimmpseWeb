package edu.ucdenver.bios.glimmpseweb.client.shared;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContextChangeEvent;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanelState;

public class DynamicTabPanelTest extends WizardStepPanel {

    protected DynamicTabPanel tabPanel = new DynamicTabPanel();
    
    public DynamicTabPanelTest(WizardContext context) {
        super(context, "Test Panel for DynamicTabPanel - not for use in application",
                WizardStepPanelState.COMPLETE);
        
        VerticalPanel panel = new VerticalPanel();
        
        Button addFirstButton = new Button("Add first tab", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                addFirstTab();
            }
            
        });
        Button addLastButton = new Button("Add last tab", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                addLastTab();
            }
            
        });
        Button removeFirstButton = new Button("Remove first tab", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                removeFirstTab();
            }
            
        });
        Button removeLastButton = new Button("Remove last tab", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                removeLastTab();
            }
            
        });
        
        
        panel.add(tabPanel);
        panel.add(addFirstButton);
        panel.add(addLastButton);
        panel.add(removeFirstButton);
        panel.add(removeLastButton);
        
        initWidget(panel);
    }
    
    private void addFirstTab() {
        tabPanel.insert(0, new HTML("Tab " + tabPanel.getTabCount()), makeTabContents());

    }
    
    private void addLastTab() {
        tabPanel.add(new HTML("Tab " + tabPanel.getTabCount()), makeTabContents());

    }
    
    private void removeLastTab() {
        if (tabPanel.getTabCount() > 0) {
            tabPanel.remove(tabPanel.getTabCount()-1);
        }
    }
    
    private void removeFirstTab() {
        if (tabPanel.getTabCount() > 0) {
            tabPanel.remove(0);
        }
    }
    
    
    private VerticalPanel makeTabContents() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(new HTML("Here is the contents for tab " + tabPanel.getTabCount()));
        return panel;
    }
    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onWizardContextChange(WizardContextChangeEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onWizardContextLoad() {
        // TODO Auto-generated method stub

    }

}
