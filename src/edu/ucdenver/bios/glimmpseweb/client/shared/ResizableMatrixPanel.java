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

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.webservice.common.domain.NamedMatrix;

/**
 * Generic resizable matrix display panel.
 * 
 * @author Vijay Chander-Akula
 *
 */
public class ResizableMatrixPanel extends Composite
{
	// maximum allowed rows and columns
	protected static final int DEFAULT_MAX_ROWS = 50;
	protected static final int DEFAULT_MAX_COLS = 50;
	// minimum allowed rows/columns
	protected static final int MIN_ROW_COL = 1;
	// max rows/columns - allows calling application to control this value
	protected int maxRows = DEFAULT_MAX_ROWS;
	protected int maxCols = DEFAULT_MAX_COLS;
	// default value for off diagonal elements
	protected String defaultOffDiagonalValue = "0";
	protected String defaultDiagonalValue = "1";
	// row dimension of the matrix
	protected int rows;
	// column dimension of the matrix
	protected int columns;
	// indicates the matrix is symmetric
	// if true, the matrix will only allow editing of the lower triangle and will auto-populate
	// the corresponding cells in the upper triangle
	protected boolean isSymmetric;
	// indicates that the off diagonals of the matrix are editable
	protected boolean allowEditOffDiagonal;
	// indicates that the diagonal elements of the matrix are editable
	protected boolean allowEditDiagonal;
	// indicates if the matrix dimensions should be displayed
	protected boolean showDimensions;
	// container for matrix cells and row/column labels
	protected FlexTable matrixCellAndLabelTable = new FlexTable();
	// panel containing dimensions
	protected HorizontalPanel dimensionPanel = new HorizontalPanel();
	// text box for row dimension
	protected TextBox rowTextBox = new TextBox();
	// text box for column dimension
	protected TextBox columnTextBox = new TextBox();

	// error html
	HTML errorHTML = new HTML();

	/**
	 * Text box which knows its row/column position in the matrix
	 * Used to manage symmetric matrices
	 */
	private class RowColumnTextBox extends TextBox 
	{
		public int row;
		public int column;
		private String defaultValue = "0";
		
		public RowColumnTextBox(int row, int column, String defaultValue)
		{
			super();
			this.row = row;
			this.column = column;
			this.defaultValue = defaultValue;
		}
		
		public void reset()
		{
			this.setText(defaultValue);
		}
	};
	
	/**
	 * Create a ResizableMatrixPanel with the specified dimensions.  By default, dimensions
	 * are displayed, and all cells are editable
	 * 
	 * @param row row dimension
	 * @param column column dimension
	 * @param showDimensions if true, the row x column dimensions will be displayed in the panel
	 * @param allowEditDiagonal if true, diagonal elements will be editable
	 * @param allowEditOffDiagonal if true, off diagonal elements will be editable
	 * @param isSymmetric indicates if the matrix is symmetric
	 */
	public ResizableMatrixPanel(int rows, int columns)
	{
		this(rows, columns, true, true, true, false);
	}

	/**
	 * Create a ResizableMatrixPanel with the specified restrictions
	 * 
	 * @param row row dimension
	 * @param column column dimension
	 * @param showDimensions if true, the row x column dimensions will be displayed in the panel
	 * @param allowEditDiagonal if true, diagonal elements will be editable
	 * @param allowEditOffDiagonal if true, off diagonal elements will be editable
	 * @param isSymmetric indicates if the matrix is symmetric
	 */
	public ResizableMatrixPanel(int rows, int columns, boolean showDimensions, boolean allowEditDiagonal,
			boolean allowEditOffDiagonal, boolean isSymmetric)
	{
		VerticalPanel verticalPanel = new VerticalPanel();

		this.rows = rows;
		this.columns = columns;
		this.isSymmetric = isSymmetric;
		this.allowEditOffDiagonal = allowEditOffDiagonal;
		this.allowEditDiagonal = allowEditDiagonal;
		this.showDimensions = showDimensions;

		// build the matrix data
		buildMatrixCellsAndLabels();

		// build the dimension display
		buildDimensions();

		// layout the panel
		verticalPanel.add(dimensionPanel);
		verticalPanel.add(matrixCellAndLabelTable);
		verticalPanel.add(errorHTML);

		// add style
		verticalPanel.setStyleName(GlimmpseConstants.STYLE_MATRIX_PANEL);
		dimensionPanel.setStyleName(GlimmpseConstants.STYLE_MATRIX_DIMENSION);
		matrixCellAndLabelTable.setStyleName(GlimmpseConstants.STYLE_MATRIX_DATA);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);

		// initialize
		initWidget(verticalPanel);
	}

	/**
	 * Change the row dimension of the matrix display
	 * @param newRows new row dimension
	 */
	public void setRowDimension(int newRows)
	{
		if (newRows != rows && 
				newRows >= MIN_ROW_COL && newRows <= maxRows)
		{
			int oldRows = rows;			
			if (newRows > oldRows)
			{
				// add new rows if the new row dimension is larger than the old
				for(int row = matrixCellAndLabelTable.getRowCount(); 
				row <= newRows && row < maxRows; row++)
				{
					addRow(row);
				}
				if (isSymmetric)
				{
					for(int column = matrixCellAndLabelTable.getCellCount(0);
							column <= newRows && columns <= maxCols; column++)
					{
						addColumn(column);
					}
				}

			}
			else if (newRows < oldRows)
			{
				for(int row = matrixCellAndLabelTable.getRowCount()-1; 
				row > newRows && row > 0; row--)
				{
					matrixCellAndLabelTable.removeRow(row);
					if (isSymmetric) removeColumn(row);
				}
			}
			
			// update the dimension value and display text
			rows = newRows;
			rowTextBox.setText(Integer.toString(rows));
			if (isSymmetric)
			{
				columns = newRows;
				columnTextBox.setText(Integer.toString(columns));
			}
			
		}
	}

	/**
	 * Change the column dimension of the matrix
	 * @param newCols new column dimension
	 */
	public void setColumnDimension(int newCols)
	{
		if (newCols != columns &&
				newCols >= MIN_ROW_COL && newCols <= maxCols)
		{
			int oldCols = columns;			
			if (newCols > oldCols)
			{
				// add new columns if the new row dimension is larger than the old
				for(int column = matrixCellAndLabelTable.getCellCount(0);
				column <= newCols && column <= maxCols; column++)
				{
					addColumn(column);
				}
				if (isSymmetric)
				{
					// for symmetric matrices, add columns to maintain equal row/col dimensions
					for(int row = matrixCellAndLabelTable.getRowCount();
					row <= newCols && row <= maxRows; row++)
					{
						addRow(row);
					}
				}
			}
			else if (newCols < oldCols)
			{
				// delete columns when new dimension is smaller than the old
				for(int column = matrixCellAndLabelTable.getCellCount(0)-1; 
				column > newCols && column > 0; column--)
				{
					removeColumn(column);
					// for symmetric matrices, remove the corresponding rows as well
					if (isSymmetric) matrixCellAndLabelTable.removeRow(column);
				}
			}
			// update the dimension value and display text
			columns = newCols;
			columnTextBox.setText(Integer.toString(columns));
			if (isSymmetric)
			{
				rows = newCols;
				rowTextBox.setText(Integer.toString(rows));
			}
		}
	}

	/**
	 * Add a new row to the matrix table
	 * @param row row number
	 */
	private void addRow(int row)
	{
		int numColumns = matrixCellAndLabelTable.getCellCount(0);
		for(int column = 0; column < numColumns; column++)
		{
			if (column == 0)
			{
				// add a label widget
				matrixCellAndLabelTable.setWidget(row, column, createMatrixLabel(""));
			}
			else
			{
				// add a cell widget
				matrixCellAndLabelTable.setWidget(row, column, createMatrixCellWidget(row, column));
			}
		}
	}

	/**
	 * Add a new column to the matrix data
	 * @param column column number
	 */
	public void addColumn(int column)
	{
		int numRows = matrixCellAndLabelTable.getRowCount();
		for(int row = 0; row < numRows; row++)
		{
			if (row == 0)
			{
				// add a label widget
				matrixCellAndLabelTable.setWidget(row, column, createMatrixLabel(""));
			}
			else
			{
				// add a cell widget
				matrixCellAndLabelTable.setWidget(row, column, createMatrixCellWidget(row, column));
			}
		}
	}

	/**
	 * Add row labels.  The number of labels must exactly match the number
	 * of rows in the matrix
	 * @param labelList list of labels
	 */
	public void setRowLabels(List<String> labelList)
	{
		if (labelList.size() == rows)
		{
			int row = 1;
			for(String label: labelList)
			{
				matrixCellAndLabelTable.setWidget(0, row, createMatrixLabel(label));
				row++;
			}
		}
	}

	/**
	 * Add column labels to the panel.  The list length must be exactly the
	 * number of columns currently in the matrix
	 * @param labelList list of labels.
	 */
	public void setColumnLabels(List<String> labelList)
	{
		if (labelList.size() == columns)
		{
			int column = 1;
			for(String label: labelList)
			{
				matrixCellAndLabelTable.setWidget(0, column, createMatrixLabel(label));
				column++;
			}
		}
	}

	/**
	 * Set the value of a specified cell in the matrix
	 * @param row cell's row index
	 * @param column cell's column index
	 * @param value new cell value
	 */
	public void setCellValue(int row, int column, String value)
	{	
		TextBox tb = (TextBox) matrixCellAndLabelTable.getWidget(row+1, column+1);
		tb.setValue(value);
	}

	/**
	 * Get the value of a specified cell in the matrix
	 * @param row cell's row index
	 * @param column cell's column index
	 */
	public String getCellValue(int row, int column)
	{
		String value;
		TextBox tb = (TextBox) matrixCellAndLabelTable.getWidget(row+1, column+1);
		value = tb.getValue();
		return value;
	}

	/**
	 * Specify whether editing of diagonal elements is allowed
	 * @param editable if true, the diagonal cells can be edited
	 */
	public void setDiagonalsEditable(boolean editable)
	{
		this.allowEditDiagonal = editable;
		for(int i = 0; i < rows; i++)
		{
			TextBox tb = (TextBox) matrixCellAndLabelTable.getWidget(i+1, i+1);
			tb.setEnabled(true);
		}
	}

	/**
	 * Build the panel of matrix cells and surrounding labels.  Labels appear in
	 * the left-most column and top row.
	 */
	private void buildMatrixCellsAndLabels()
	{
		// fill in the cells and labels.  Note that labels appear in row 0, and column 0
		for(int row = 0; row < rows+1; row++)
		{
			for(int column = 0; column < columns+1; column++)
			{
				if (row == 0 && column == 0)
				{
					// add an empty widget in the upper left corned
					matrixCellAndLabelTable.setWidget(row, column, new HTML(""));
				}
				else if (row == 0 || column == 0)
				{
					// add a label widget
					matrixCellAndLabelTable.setWidget(row, column, createMatrixLabel(""));
				}
				else
				{
					// add the widget to the grid
					matrixCellAndLabelTable.setWidget(row, column, createMatrixCellWidget(row, column));
				}
			}
		}
		
	}

	/**
	 * Build the row/column dimension panel
	 */
	private void buildDimensions()
	{
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
					rowTextBox.setText(Integer.toString(rows));
				}
			}
		});
		columnTextBox = new TextBox();
		columnTextBox.setValue(Integer.toString(columns), false);		
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
					columnTextBox.setText(Integer.toString(columns));
					TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidMatrixDimension());
				}
			}
		});

		// layout the matrix dimensions
		dimensionPanel.add(rowTextBox);
		dimensionPanel.add(new HTML(GlimmpseWeb.constants.matrixDimensionSeparator()));
		dimensionPanel.add(columnTextBox);
	}


	/**
	 * Create a matrix label widget
	 * @param label the display label
	 * @return matrix label widget
	 */
	private HTML createMatrixLabel(String label)
	{
		return new HTML(label);
	}

	/**
	 * Create a text box widget for the matrix
	 * @param isDiagonalCell if true, the cell is on the diagonal
	 * @return text box
	 */
	private TextBox createMatrixCellWidget(int row, int column)
	{
		String defaultValue = ((row == column) ? defaultDiagonalValue : defaultOffDiagonalValue);
		// the row/column associated with the text box indicate the true position 
		// in the matrix, so we subtract 1 here to adjust for the label row/column
		RowColumnTextBox cellTextBox = 
			new RowColumnTextBox(row-1, column-1, defaultValue);
		cellTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event)
			{
				RowColumnTextBox tb = (RowColumnTextBox) event.getSource();
				try
				{
					String value = tb.getValue();
					TextValidation.parseDouble(value);
					TextValidation.displayOkay(errorHTML, "");
					if (isSymmetric && tb.row != tb.column) setCellValue(tb.column, tb.row, value);
					
				}
				catch(Exception e)
				{
					TextValidation.displayError(errorHTML, "The value should not contain special characters");
					tb.reset();
					if (isSymmetric && tb.row != tb.column)  setCellValue(tb.column, tb.row, tb.getValue());
				}
			}
		});

		cellTextBox.setEnabled(isCellEditAllowed(row, column));		
		cellTextBox.setValue((row == column) ? defaultDiagonalValue : defaultOffDiagonalValue);
		
		// set style
		cellTextBox.setStyleName(GlimmpseConstants.STYLE_MATRIX_CELL);
		if (!cellTextBox.isEnabled()) cellTextBox.addStyleDependentName(GlimmpseConstants.STYLE_DISABLED);
		
		return cellTextBox;
	}

	/**
	 * Determines whether a cell is editable.<p>
	 *  A diagonal cell is editable if any of the following conditions hold
	 *  <ol>
	 * <li>non-symmetric, but editing allowed on diagonal</li>
	 * <li>symmetric matrix</li>
	 * </ol>
	 * </p><p>
	 * An off diagonal cell is editable if any of the following conditions hold
	 * <ol>
	 * <li>non-symmetric, but editing allowed on off-diagonal</li>
	 * <li>symmetric, and current cell in the lower triangle</li>
	 *</p>
	 *
	 * @param row
	 * @param column
	 * @return
	 */
	private boolean isCellEditAllowed(int row, int column)
	{
		boolean isDiagonal = (row == column);
		return ((!isSymmetric && ((isDiagonal && allowEditDiagonal) || 
				(!isDiagonal && allowEditOffDiagonal))) ||
				(isSymmetric && row <= column));
	}

	/**
	 * GWT's flex table doesn't provide a function to
	 * remove a column, so here is it
	 */
	private void removeColumn(int column)
	{
		int tableRows = matrixCellAndLabelTable.getRowCount();
		for(int row = 0; row < tableRows; row++) 
			matrixCellAndLabelTable.removeCell(row, column);
	}

	/**
	 * Create a NamedMatrix object from the ResizableMatrixPanel
	 * widgets
	 * @param name name of the matrix
	 */
	public NamedMatrix toNamedMatrix(String name)
	{
		NamedMatrix namedMatrix = new NamedMatrix(name);
		namedMatrix.setRows(rows);
		namedMatrix.setColumns(columns);
		double[][] data = new double[rows][columns];
		for(int row = 0; row < rows; row++)
		{
			for(int column = 0; column < columns; column++)
			{
				TextBox tb = (TextBox) matrixCellAndLabelTable.getWidget(row+1, column+1);
				data[row][column] = Double.parseDouble(tb.getValue());
			}
		}
		namedMatrix.setData(data);
		return namedMatrix;
	}

	/**
	 * Reset to the default cell value and remove all labels
	 * 
	 * @param rows row dimension
	 * @param columns column dimension
	 */
	public void reset(int rows, int columns)
	{

	}

	/**
	 * Specify whether the row dimension of the matrix is editable
	 * @param enabled if true, edits are allowed in the row dimension
	 */
	public void setEnabledRowDimension(boolean enabled)
	{
		rowTextBox.setEnabled(enabled);
	}

	/**
	 * Specify whether the column dimension of the matrix is editable
	 * @param enabled if true, edits are allowed in the column dimension
	 */
	public void setEnabledColumnDimension(boolean enabled)
	{
		columnTextBox.setEnabled(enabled);
	}

	/**
	 * Set maximum number of rows allowed in the matrix
	 * @param rows max allowed rows
	 */
	public void setMaxRows(int rows)
	{
		if (rows >= MIN_ROW_COL)
		{
			maxRows = rows;
			if (isSymmetric) maxCols = rows;
		}
	}

	/**
	 * Set maximum number of columns allowed in the matrix
	 * @param columns max allowed columns
	 */
	public void setMaxColumns(int columns)
	{
		if (columns >= MIN_ROW_COL)
		{
			maxCols = columns;
			if (isSymmetric) maxRows = columns;
		}
	}

	/**
	 * FIll in the matrix widgets from a NamedMatrix object
	 * @param matrix NamedMatrix object
	 */
	public void loadFromNamedMatrix(NamedMatrix matrix)
	{
		if (matrix != null)
		{
			setRowDimension(matrix.getRows());
			setColumnDimension(matrix.getColumns());
			double[][] data = matrix.getDataFromBlob();
			for(int row = 0; row < rows; row++)
			{
				for(int column = 0; column < columns; column++)
				{
					setCellValue(row, column, Double.toString(data[row][column]));
				}
			}
			rowTextBox.setValue(Integer.toString(rows));
			columnTextBox.setValue(Integer.toString(columns));
		}
	}

	/**
	 * Get row dimension of the matrix
	 * @return row dimension
	 */
	public int getRowDimension()
	{
		return rows;
	}

	/**
	 * Get column dimension of the matrix
	 * @return row dimension
	 */
	public int getColumnDimension()
	{
		return columns;
	}
}
