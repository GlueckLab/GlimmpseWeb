package edu.ucdenver.bios.glimmpseweb.client.shared;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implemented a tab panel using FlexTable since TabLayout
 * cannot add/remove tabs on the fly
 * 
 * @author Sarah
 *
 */
public class DynamicTabPanel extends Composite {
    
    private static final String STYLE_TAB_HEADER_PANEL = "tabHeaderPanel";
    private static final String STYLE_TAB_HEADER = "tabHeader";
    private static final String STYLE_TAB_CONTENT_PANEL = "tabContentPanel";
    
    protected int tabCount = 0;
    
    // header bar simulating tabs since you can't dynamically add stuff to a tab panel
    protected FlexTable tabPanel = new FlexTable();
    // deck containing covariance panels
    protected DeckPanel tabDeck = new DeckPanel();
    // helps to simulate tabs
    private class IndexedButton extends Button {
        public int index = -1;
        public IndexedButton(int i, String label, ClickHandler handler) {
            super(label, handler);
            index = i;
        }
    }
    
    public DynamicTabPanel() {
        VerticalPanel panel = new VerticalPanel();
        
        panel.add(tabPanel);
        panel.add(tabDeck);
        
        // set style
        tabPanel.setStyleName(STYLE_TAB_HEADER_PANEL);
        tabDeck.setStyleName(STYLE_TAB_CONTENT_PANEL);
        
        initWidget(panel);
    }
    
    public void insert(int position, String label, Widget tab) {
        if (position >= 0) {
            IndexedButton button = new IndexedButton(tabCount, label,
                    new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    IndexedButton button = (IndexedButton) event.getSource();
                    showWidget(button.index);
                }
            });
            button.setStyleName(STYLE_TAB_HEADER);
//            tabPanel.insertCell(1, position);
            tabPanel.setWidget(0, tabCount, button);
            tabDeck.add(tab);
            tabCount++;
        }
    }
    
    public void add(String label, Widget tab) {
        insert(tabCount, label, tab);
    }
    
    public void clear() {
        tabPanel.removeAllRows();
        tabDeck.clear();
        tabCount = 0;
    }
        
    private void showWidget(int index) {
        tabDeck.showWidget(index);
    }
    
    public int  getTabCount() {
        return tabCount;
    }
    
    public Widget getWidget(int i) {
        if (i >=0 && i < tabCount) {
            return tabDeck.getWidget(i);
        } else {
            return null;
        }
    }
    
    


}
