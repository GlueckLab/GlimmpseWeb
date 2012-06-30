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
    // current number of tabs
    protected int tabCount = 0;
    
    // currently selected tab
    protected int selectedIndex = -1;
    
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
        tabPanel.setCellPadding(0);
        tabPanel.setCellSpacing(0);
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
            // create the new tab
            IndexedDecoratorPanel panel = 
                    new IndexedDecoratorPanel(position, tabHeader);
            panel.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    IndexedDecoratorPanel panel = (IndexedDecoratorPanel) event.getSource();
                    openTab(panel.index);
                }
            });
            // dependent style depends on position
            panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TAB);
            if (position == 0) {
                if (tabCount == 0) {
                    panel.addStyleDependentName(GlimmpseConstants.STYLE_SINGLE);
                } else {
                    panel.addStyleDependentName(GlimmpseConstants.STYLE_LEFT);
                }
            } else if (position == tabCount) {
                IndexedDecoratorPanel rightmost = (IndexedDecoratorPanel) tabPanel.getWidget(0, tabCount-1);
                rightmost.removeStyleDependentName(GlimmpseConstants.STYLE_RIGHT);
                panel.addStyleDependentName(GlimmpseConstants.STYLE_RIGHT);
            }
            // add the new tab to the header bar
            tabPanel.insertCell(0, position);
            tabPanel.setWidget(0, position, panel);
            // add the new contents to the deck panel
            tabDeck.insert(tabContents, position);
            tabCount++;
            // adjust the index for the remaining tabs to keep the deck panel and
            // tabs synchronized
            for(int col = position+1; col < tabCount; col++) {
                IndexedDecoratorPanel current = (IndexedDecoratorPanel) tabPanel.getWidget(0, col);
                current.index++;
            }
            if (position < selectedIndex) {
                selectedIndex++;
            }
            updateStyles();
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
     * Open the tab at the specified index
     * @param index
     */
    public void openTab(int index) {
        if (index >= 0 && index < tabCount) {
            selectedIndex = index;
            tabDeck.showWidget(index);
            updateStyles();
        }
    }
    
    /**
     * Open the tab with the specified header widget
     * @param tabHeader
     */
    public void openTab(Widget tabHeader) {
        int index = -1;
        for(int col = 0; col < tabPanel.getCellCount(0); col++) {
            IndexedDecoratorPanel current = (IndexedDecoratorPanel) tabPanel.getWidget(0, col);
            if ((HasClickHandlers) current.getWidget() == tabHeader) {
                index = col;
                break;
            }
        }
        if (index != -1) {
            openTab(index);
        }
    }
    
    private void clearStyles(Widget w) {
        // clear all previous styles
        w.removeStyleDependentName(GlimmpseConstants.STYLE_LEFT);
        w.removeStyleDependentName(GlimmpseConstants.STYLE_LEFT_ACTIVE);
        w.removeStyleDependentName(GlimmpseConstants.STYLE_LEFT_NEXT_TO_ACTIVE);
        w.removeStyleDependentName(GlimmpseConstants.STYLE_MIDDLE);
        w.removeStyleDependentName(GlimmpseConstants.STYLE_MIDDLE_ACTIVE);
        w.removeStyleDependentName(GlimmpseConstants.STYLE_MIDDLE_NEXT_TO_ACTIVE);
        w.removeStyleDependentName(GlimmpseConstants.STYLE_RIGHT);
        w.removeStyleDependentName(GlimmpseConstants.STYLE_RIGHT_ACTIVE);
        w.removeStyleDependentName(GlimmpseConstants.STYLE_SINGLE);
    }
    
    /**
     * Update the selection styles
     * @param selectedIndex
     */
    private void updateStyles() {
        if (tabCount == 1) {
            IndexedDecoratorPanel current = (IndexedDecoratorPanel) tabPanel.getWidget(0, 0);
            clearStyles(current);
            current.addStyleDependentName(GlimmpseConstants.STYLE_SINGLE);
        } else {
            for(int i = 0; i < tabCount; i++) {
                IndexedDecoratorPanel current = (IndexedDecoratorPanel) tabPanel.getWidget(0, i);
                clearStyles(current);
                String newStyle = "";
                // set style of leftmost tab
                if (i == 0) {
                    if (selectedIndex == i+1) {
                        newStyle = GlimmpseConstants.STYLE_LEFT_NEXT_TO_ACTIVE;
                    } else if (selectedIndex == i) {
                        newStyle = GlimmpseConstants.STYLE_LEFT_ACTIVE;
                    } else {
                        newStyle = GlimmpseConstants.STYLE_LEFT;
                    }
                } else if (i == tabCount-1) {
                    // set style of rightmost tab
                    if (selectedIndex == i) {
                        newStyle = GlimmpseConstants.STYLE_RIGHT_ACTIVE;
                    } else {
                        newStyle = GlimmpseConstants.STYLE_RIGHT;
                    }
                } else {
                    // set style for middle tabs
                    if (selectedIndex == i+1) {
                        newStyle = GlimmpseConstants.STYLE_MIDDLE_NEXT_TO_ACTIVE;
                    } else if (selectedIndex == i) {
                        newStyle = GlimmpseConstants.STYLE_MIDDLE_ACTIVE;
                    } else {
                        newStyle = GlimmpseConstants.STYLE_MIDDLE;
                    }
                }
                current.addStyleDependentName(newStyle);
            }
        }
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
    public void setVisible(Widget tabHeader, boolean visible) {
        for(int col = 0; col < tabPanel.getCellCount(0); col++) {
            IndexedDecoratorPanel current = (IndexedDecoratorPanel) tabPanel.getWidget(0, col);
            if (current.getWidget() == tabHeader) {
                current.setVisible(visible);
                if (!visible) {
                    
                }
                
                // select the leftmost tab
                
                // update styles
                break;
            }
        }
        

    }
    
    /**
     * Removed the tab matching the specified widget header
     * @param tabHeader
     */
    public void remove(Widget tabHeader) {
        int index = -1;
        for(int col = 0; col < tabPanel.getCellCount(0); col++) {
            IndexedDecoratorPanel current = (IndexedDecoratorPanel) tabPanel.getWidget(0, col);
            if ((HasClickHandlers) current.getWidget() == tabHeader) {
                index = col;
                break;
            }
        }
        if (index != -1) {
            remove(index);
            updateStyles();
        }
    }
    
    /**
     * Remove the tab at the specified index
     * @param index
     */
    public void remove(int index) {
        if (index >= 0 && index < tabCount) {
            tabDeck.remove(index);
            tabPanel.removeCell(0, index);
            tabCount--;
            for(int i = index; i < tabCount; i++) {
                IndexedDecoratorPanel current = (IndexedDecoratorPanel) tabPanel.getWidget(0, i);
                current.index--;
            }
            if (index < selectedIndex) {
                selectedIndex--;
            }
            updateStyles();
        }
    }
    
        
        
    
    


}
