package edu.ucdenver.bios.glimmpseweb.context;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TextBox;

import edu.ucdenver.bios.glimmpseweb.client.matrix.ResizableMatrix;

public class NamedMatrix
{
	private String name;
	private int rows;
	private int columns;
	private Grid matrixData;
	
	public NamedMatrix(String name, int rows, int columns, Grid matrixData)
	{
		this.name = name;
		this.rows = rows;
		this.columns = columns;
		this.matrixData = matrixData;
	}
	
	private void toXML()
	{
		StringBuffer buffer = new StringBuffer();
		
		int start = 0;

		buffer.append("<matrix name='" + name + "' rows='" +  rows + "' columns='" + 
				columns + "'>");

		for(int r = start; r < matrixData.getRowCount(); r++)
		{
			buffer.append("<r>");
			for(int c = start; c < columns; c++)
			{
				TextBox txt = (TextBox) matrixData.getWidget(r, c);
				buffer.append("<c>" + txt.getValue() + "</c>");
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
	
	
	
}
