/*
 * User Interface for the GLIMMPSE Software System.  Allows
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
package edu.ucdenver.bios.glimmpseweb.client.matrix;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.shared.GridTextBox;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;

/**
 * Resizable matrix widget
 * @author Sarah Kreidler
 * @author Jonathan Cohen added functionality to allow users to enter information
 * in the lower triangle, and have it propagate to the upper triangle for a symmetric
 * matrix.  These changes also disable editing for the upper triangle of elements 
 * for the user. 
 *
 */
public class ResizableMatrix extends Composite 
{
	protected static final int DEFAULT_MAX_ROWS = 50;
	protected static final int MIN_ROW_COL = 1;
	protected static final int DEFAULT_MAX_COLS = 50;
	protected int maxRows = DEFAULT_MAX_ROWS;
	protected int maxCols = DEFAULT_MAX_COLS;
	protected Grid matrixData;
	protected TextBox rowTextBox;
	protected TextBox columnTextBox;
	protected boolean symmetric = false;
	protected HTML title = new HTML("");
	protected String defaultValue = "1";
	protected HTML errorHTML = new HTML();
	protected String name = null;
	
	/**
	 * This constructor should be used to construct a symmetric ResizableMatrix by setting symmetric
	 * to "true".
	 * 
	 * @param name
	 * @param rows
	 * @param cols
	 * @param defaultValue
	 * @param title
	 * @param symmetric
	 */
	public ResizableMatrix(String name, int rows, int cols, String defaultValue, String title, boolean symmetric) 
	{
		this.name = name;
	    if (title != null && !title.isEmpty()) 
	    	this.title = new HTML(title);
	    else
	    	this.title = new HTML();
	    
	    if (defaultValue != null && !defaultValue.isEmpty()) this.defaultValue = defaultValue;
	    
		// overall layout panel    
	    VerticalPanel matrixPanel = new VerticalPanel();

		// matrix dimensions
		rowTextBox = new TextBox();
		rowTextBox.setValue(Integer.toString(rows), false);
		rowTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent e)
			{
				try
				{
					int newRows = 
						TextValidation.parseInteger(rowTextBox.getText(), MIN_ROW_COL, maxRows);
					setRowDimension(newRows);
					// notify listeners of row change
					TextValidation.displayOkay(errorHTML, "");
				}
				catch (NumberFormatException nfe)
				{
					TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidMatrixDimension());
					rowTextBox.setText(Integer.toString(matrixData.getRowCount()));
				}
			}
		});
		columnTextBox = new TextBox();
		columnTextBox.setValue(Integer.toString(cols), false);		
		columnTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent e)
			{
				try
				{
					int newCols = TextValidation.parseInteger(columnTextBox.getText(), MIN_ROW_COL, maxCols);
					setColumnDimension(newCols);
					// notify listeners of row change
					TextValidation.displayOkay(errorHTML, "");
				}
				catch (NumberFormatException nfe)
				{
					columnTextBox.setText(Integer.toString(matrixData.getCellCount(0)));
					TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidMatrixDimension());
				}
			}
		});
		
		// layout the matrix dimensions
		HorizontalPanel matrixDimensions = new HorizontalPanel();
		matrixDimensions.add(rowTextBox);
		matrixDimensions.add(new HTML(GlimmpseWeb.constants.matrixDimensionSeparator()));
		matrixDimensions.add(columnTextBox);
	    
		// set symmetry BEFORE building matrix
		this.symmetric = symmetric;
		
		// build matrix itself
		matrixData = new Grid(rows, cols);
		initMatrixData();
		
		// add the widgets to the vertical panel
		matrixPanel.add(this.title);
		matrixPanel.add(matrixDimensions);
		matrixPanel.add(matrixData);
		matrixPanel.add(errorHTML);
		
		// set up styles
		matrixPanel.setStyleName(GlimmpseConstants.STYLE_MATRIX_PANEL);
		matrixDimensions.setStyleName(GlimmpseConstants.STYLE_MATRIX_DIMENSION);
		matrixData.setStyleName(GlimmpseConstants.STYLE_MATRIX_DATA);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
		
        // initialize the widget
		initWidget(matrixPanel);
	}
	
	/**
	 * This constructor will create a ResizableMatrix with the attribute @see symmetric
	 * set to false.
	 * @param name
	 * @param rows
	 * @param cols
	 * @param defaultValue
	 * @param title
	 */
	public ResizableMatrix(String name, int rows, int cols, String defaultValue, String title) 
	{	
		this(name, rows, cols, defaultValue, title, false);
	}
    	
	public boolean isSymmetric(){
		return symmetric;
	}
	
	public void setRowDimension(int newRows)
	{
		if (newRows >= MIN_ROW_COL && newRows <= maxRows)
		{
			int oldRows = matrixData.getRowCount();
			if (oldRows != newRows)
			{
			    
	             if (symmetric)
	                    matrixData.resize(newRows, newRows);
	                else
	                    matrixData.resizeRows(newRows);
				if (newRows > oldRows)
				{
				    if (symmetric) 
				        for(int c = oldRows; c < newRows; c++) fillColumn(c, defaultValue);
					for(int r = oldRows; r < newRows; r++) fillRow(r, defaultValue);
				} 
			}
		}
		
		rowTextBox.setText(Integer.toString(matrixData.getRowCount()));
        if (symmetric) columnTextBox.setText(Integer.toString(matrixData.getColumnCount()));

	}
	
	public void setColumnDimension(int newCols)
	{
		if (newCols >= MIN_ROW_COL && newCols <= maxCols)
		{
			int oldCols = matrixData.getColumnCount();
			if (oldCols != newCols)
			{
			    if (symmetric)
			        matrixData.resize(newCols, newCols);
			    else
			        matrixData.resizeColumns(newCols);
			    
				if (newCols > oldCols)
				{
                    if (symmetric) 
                        for(int r = oldCols; r < oldCols; r++) fillRow(r, defaultValue);
					for(int c = oldCols; c < newCols; c++) fillColumn(c, defaultValue);
				} 
			}
			
			columnTextBox.setText(Integer.toString(matrixData.getColumnCount()));
			if (symmetric) rowTextBox.setText(Integer.toString(matrixData.getRowCount()));
		}

	}
	
	public int getRowDimension()
	{
		return matrixData.getRowCount();
	}
	
	public int getColumnDimension()
	{
		return matrixData.getCellCount(0);
	}
	
	private void initMatrixData()
	{
		for(int r = 0; r < matrixData.getRowCount(); r++)
		{
			fillRow(r, defaultValue);
		}
	}
		
	private void setData(int row, int col, String value, boolean enabled)
	{
		GridTextBox textBox = new GridTextBox(row, col);
		textBox.setValue(value);
		textBox.setEnabled(enabled);
		textBox.setStyleName(GlimmpseConstants.STYLE_MATRIX_CELL);
        if (!enabled)
    	{
        	textBox.addStyleDependentName(GlimmpseConstants.STYLE_DISABLED);
    	}
        
        textBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				GridTextBox source = (GridTextBox) event.getSource();
				int col = source.getColumn();
				int row = source.getRow();
				try
				{
					TextValidation.parseDouble(source.getText(), Double.NEGATIVE_INFINITY, 
							Double.POSITIVE_INFINITY, false);
					TextValidation.displayOkay(errorHTML, "");
					
					//copy the value to the upper triangle from the lower 
					//if matrix is symmetric
					if(symmetric)
					{	
						if (col >= 0 && col < matrixData.getRowCount() &&
								row >= 0 && row < matrixData.getColumnCount())
						{
							TextBox tb = (TextBox) matrixData.getWidget(col, row);
							if(tb != null){
								tb.setText(source.getText());
								//if the text box is on the diagonal, (r==c), or below (r>c), 
								//we want it enabled. Only top triangle tb's should be disabled.
								if( row < col){
									tb.setEnabled(false);
								}
								matrixData.setWidget(col, row, tb);
							}
						}
					}
				}
				catch (NumberFormatException nfe)
				{
					TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidNumber());
					source.setText(getDefaultValue());
				}
			}
		});
		matrixData.setWidget(row, col, textBox);
	}
	
	private String getDefaultValue()
	{
		return this.defaultValue;
	}
	
	public double getData(int row, int col)
	{
		double value = Double.NaN;	
		if (row >= 0 && row < matrixData.getRowCount() &&
				col >= 0 && col < matrixData.getColumnCount())
		{
			TextBox tb = (TextBox) matrixData.getWidget(row, col);
			value = Double.parseDouble(tb.getValue());
		}
		return value;
	}

    private void fillRow(int row, String diagonalValue)
	{
		for (int col = 0; col < matrixData.getColumnCount(); col++)
		{
			//diagnal elements
			if (col == row)
			{
				setData(row, col, diagonalValue, true);
			}
			//lower triangle elements
			else if (col < row)
			{
				setData(row, col, "0", true);
			}
			//disable upper triangle if symmetric
			else if(col > row && symmetric)
			{
				setData(row, col, "0", false);
			}
			//upper triangle, but not symmetric
			else
			{
				setData(row, col, "0", true);	
			}
		}
	}

    private void fillColumn(int col, String diagonalValue)
	{
		for (int row = 0; row < matrixData.getRowCount(); row++) 
		{
			//diagnal elements
			if (col == row)
			{
				setData(row, col, diagonalValue, true);
			}
			//lower triangle elements
			else if (col < row)
			{
				setData(row, col, "0", true);
			}
			//disable upper triangle if symmetric
			else if(col > row && symmetric)
			{
				setData(row, col, "0", false);
			}
			//upper triangle, but not symmetric
			else
			{
				setData(row, col, "0", true);	
			}
		}
	}
		
	public String toXML()
	{
		return toXML(name);
	}
	
	public String toXML(String matrixName)
	{
		StringBuffer buffer = new StringBuffer();
		
		int start = 0;
		int rows = matrixData.getRowCount();
		int cols = matrixData.getCellCount(0);

		buffer.append("<matrix name='" + matrixName + "' rows='" +  rows + "' columns='" + 
		        cols + "'>");

		for(int r = start; r < matrixData.getRowCount(); r++)
		{
			buffer.append("<r>");
			for(int c = start; c < cols; c++)
			{
				TextBox txt = (TextBox) matrixData.getWidget(r, c);
				buffer.append("<c>" + txt.getValue() + "</c>");
			}
			buffer.append("</r>");
		}
		buffer.append("</matrix>");

		return buffer.toString();
	}

        
    public void loadFromNamedMatrix(NamedMatrix matrix)
    {
        if (matrix != null)
        {           
//        	int rows = matrix.getRows();
//        	int columns = matrix.getColumns();
//        	matrixData.resize(rows, columns);
//        	rowTextBox.setText(Integer.toString(rows));
//        	columnTextBox.setText(Integer.toString(columns));
//        	
//            for(int r = 0; r < rows; r++)
//            {
//                for(int c = 0; c <  columns; c++)
//                {
//                	setData(r,c,matrix.getCellData(r, c).toString(),true);
//                }
//            }
        }
    }   
    
	public NamedMatrix toNamedMatrix()
	{
//		int rows = matrixData.getRowCount();
//		int columns = matrixData.getColumnCount();
//		NamedMatrix namedMatrix = new NamedMatrix(name, rows, columns);
//		for(int r = 0; r < rows; r++)
//		{
//			for(int c = 0; c < columns; c++)
//			{
//				namedMatrix.setCellData(r, c, this.getData(r, c));
//			}
//		}
//		return namedMatrix;
		return null;
	}

    public void reset(int newRows, int newColumns)
    {
        matrixData.resize(newRows, newColumns);
        rowTextBox.setText(Integer.toString(newRows));
        columnTextBox.setText(Integer.toString(newColumns));
        TextValidation.displayOkay(errorHTML, "");
        for(int r = 0; r < matrixData.getRowCount(); r++)
        {
            fillRow(r, defaultValue);
        }
    }
    
    public void setEnabledRowDimension(boolean enabled)
    {
    	rowTextBox.setEnabled(enabled);
    }
    
    public void setEnabledColumnDimension(boolean enabled)
    {
    	columnTextBox.setEnabled(enabled);
    }
    
    public void setMaxRows(int rows)
    {
    	if (rows >= MIN_ROW_COL)
    	{
        	maxRows = rows;
    	}
    }
    
    public void setMaxColumns(int columns)
    {
    	if (columns >= MIN_ROW_COL)
    	{
        	maxCols = columns;
    	}
    }
    
    public void reset()
    {
    	reset(this.getRowDimension(), this.getColumnDimension());
    }

}