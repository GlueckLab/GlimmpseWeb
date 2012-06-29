/*
 * User Interface for the GLIMMPSE Software System.  Processes
 * incoming HTTP requests for power, sample size, and detectable
 * difference
 * 
 * Copyright (C) 2011 Regents of the University of Colorado.  
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;

/**
 * Implemented a tab panel using FlexTable since TabLayout
 * cannot add/remove tabs on the fly
 * 
 * @author Sarah Kreidler
 *
 */
public class DynamicTabPanel extends Composite {
    
    protected int tabCount = 0;
    
    // header bar simulating tabs since you can't dynamically add stuff to a tab panel
    protected FlexTable tabPanel = new FlexTable();
    // deck containing covariance panels
    protected DeckPanel tabDeck = new DeckPanel();
    // helps to simulate tabs
    private class IndexedDecoratorPanel extends DecoratorPanel 
    implements HasClickHandlers {
        public int index;
        public IndexedDecoratorPanel(int i, Widget widget) {
            super();
            index = i;
            add(widget);
        }
        
        @Override
        public HandlerRegistration addClickHandler(ClickHandler handler) {
            return addDomHandler(handler, ClickEvent.getType());
        }
    }
    
    /**
     * Create an empty dynamic tab panel
     */
    public DynamicTabPanel() {
        VerticalPanel panel = new VerticalPanel();

        panel.add(tabPanel);
        panel.add(tabDeck);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TAB_PANEL);
        tabPanel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TAB_HEADER);
        tabPanel.getRowFormatter().setStyleName(0, GlimmpseConstants.STYLE_WIZARD_STEP_TAB_HEADER);
        tabDeck.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TAB_CONTENT);
        
        initWidget(panel);
    }
    
    /**
     * Insert a tab at the specified position
     * @param position position
     * @param tabHeader header widget
     * @param tabContents contents
     */
    public void insert(int position, Widget tabHeader, Widget tabContents) {
        if (position >= 0 && position <= tabCount) {
            IndexedDecoratorPanel panel = 
                    new IndexedDecoratorPanel(position, tabHeader);
            panel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    IndexedDecoratorPanel panel = (IndexedDecoratorPanel) event.getSource();
                    showWidget(panel.index);
                }
            });
            panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TAB);
            if (position == 0) {
                panel.addStyleDependentName(GlimmpseConstants.STYLE_LEFT);
            } else if (position == tabCount) {
                IndexedDecoratorPanel rightmost = (IndexedDecoratorPanel) tabPanel.getWidget(0, tabCount-1);
                rightmost.removeStyleDependentName(GlimmpseConstants.STYLE_RIGHT);
                panel.addStyleDependentName(GlimmpseConstants.STYLE_RIGHT);
            }
            tabPanel.setWidget(0, position, panel);
            tabDeck.add(tabContents);
            tabCount++;
            for(int col = position+1; col < tabCount; col++) {
                IndexedDecoratorPanel current = (IndexedDecoratorPanel) tabPanel.getWidget(0, col);
                current.index++;
            }
        }
    }
    
    /**
     * Add a tab to the panel
     * @param tabHeader widget for the tab
     * @param tabContents contents of the tab
     */
    public void add(Widget tabHeader, Widget tabContents) {
        insert(tabCount, tabHeader, tabContents);
    }
    
    /**
     * Clear all tabs
     */
    public void clear() {
        //tabPanel.removeAllRows();
        tabDeck.clear();
        tabCount = 0;
    }
        
    /**
     * Show the specified tab
     * @param index
     */
    private void showWidget(int index) {
        tabDeck.showWidget(index);
    }
    
    /**
     * Get the total number of tabs
     * @return number of tabs
     */
    public int  getTabCount() {
        return tabCount;
    }
    
    /**
     * Get the 
     * @param i
     * @return
     */
    public Widget getWidget(int i) {
        if (i >=0 && i < tabCount) {
            return tabDeck.getWidget(i);
        } else {
            return null;
        }
    }
    
    /**
     * Get the 
     * @param i
     * @return
     */
    public Widget getVisibleWidget() {
        if (tabDeck.getVisibleWidget() >= 0) {
            return tabDeck.getWidget(tabDeck.getVisibleWidget());
        }
        return null;
    }
    
    
    
    /**
     * Hide the tab at the specified index
     * @param i index
     */
    public void setVisible(HasClickHandlers tabHeader, boolean visible) {
        for(int col = 0; col < tabPanel.getCellCount(0); col++) {
            IndexedDecoratorPanel current = (IndexedDecoratorPanel) tabPanel.getWidget(0, col);
            if ((HasClickHandlers) current.getWidget() == tabHeader) {
                current.setVisible(visible);
                if (!visible) {
                    
                }
                
                // select the leftmost tab
                
                // update styles
                break;
            }
        }
        

    }
    
    public void remove(HasClickHandlers tabHeader) {
        for(int col = 0; col < tabPanel.getCellCount(0); col++) {
            IndexedDecoratorPanel current = (IndexedDecoratorPanel) tabPanel.getWidget(0, col);
            if ((HasClickHandlers) current.getWidget() == tabHeader) {


                // select the leftmost tab

                // update styles
                break;
            }
        }
    }
    
        
        
    
    


}
