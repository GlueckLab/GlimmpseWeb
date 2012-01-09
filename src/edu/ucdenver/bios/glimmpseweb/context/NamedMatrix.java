package edu.ucdenver.bios.glimmpseweb.context;

import edu.ucdenver.bios.glimmpseweb.client.matrix.ResizableMatrix;

public class NamedMatrix
{
	private String name;
	private int rows;
	private int columns;
	private double[][] matrixData;
	
	public NamedMatrix(String name)
	{
		this.name = name;
		this.rows = -1;
		this.columns = -1;
		matrixData = null;
	}
	
	public NamedMatrix(String name, ResizableMatrix matrixWidget)
	{
		this.name = name;
		this.rows = matrixWidget.getRowDimension();
		this.columns = matrixWidget.getColumnDimension();
		this.matrixData = new double[rows][columns];
		loadMatrixData(matrixWidget);
	}
	
	private void loadMatrixData(ResizableMatrix matrixWidget)
	{
		if (matrixWidget != null) 
		{
			for(int r = 0; r < rows; r++)
			{
				for(int c = 0; c < columns; c++)
				{
					matrixData[r][c] = matrixWidget.getData(r, c);
				}
			}
		}
	}
	
	private void toXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		int start = 0;

		buffer.append("<matrix name='" + name + "' rows='" +  rows + "' columns='" + 
				columns + "'>");

		for(int r = start; r < rows; r++)
		{
			buffer.append("<r>");
			for(int c = start; c < columns; c++)
			{

				buffer.append("<c>" + matrixData[r][c] + "</c>");
			}
			buffer.append("</r>");
		}
		buffer.append("</matrix>");
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getRows()
	{
		return rows;
	}

	public void setRows(int rows)
	{
		this.rows = rows;
	}

	public int getColumns()
	{
		return columns;
	}

	public void setColumns(int columns)
	{
		this.columns = columns;
	}
	
	public Double getCellData(int row, int column)
	{
		return matrixData[row][column];
	}
	
}
