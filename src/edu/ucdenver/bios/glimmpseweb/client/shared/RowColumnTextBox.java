/*
 * Web Interface for the GLIMMPSE Software System.  Allows
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

import com.google.gwt.user.client.ui.TextBox;

/**
 * Text box which knows its row/column position in the matrix
 */
public class RowColumnTextBox extends TextBox 
{
    public int row;
    public int column;
    private String defaultValue = null;

    /**
     * Create an empty row/column text box
     * @param row the row
     * @param column the column
     */
    public RowColumnTextBox(int row, int column)
    {
        super();
        this.row = row;
        this.column = column;
    }
    
    /**
     * Create an empty row/column text box
     * @param row the row
     * @param column the column
     */
    public RowColumnTextBox(int row, int column, String defaultValue)
    {
        super();
        this.row = row;
        this.column = column;
        this.defaultValue = defaultValue;
        this.setText(defaultValue);
    }

    /**
     * Reset the text box
     */
    public void reset()
    {
        if (defaultValue != null) {
            this.setText(defaultValue);
        } else {
            this.setText("");
        }
    }
};
