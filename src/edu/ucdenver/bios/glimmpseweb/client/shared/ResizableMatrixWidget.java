package edu.ucdenver.bios.glimmpseweb.client.shared;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.ucdenver.bios.glimmpseweb.client.TextValidation;

public class ResizableMatrixWidget extends Composite
{
	int numberOfRows;
	int numberOfColumns;
	boolean symmetricMatrix;
	boolean editableMatrix;
	boolean diagonalsEditableMatrix;
	boolean matrixDimensionDisplay;
	
	FlexTable flexTable = new FlexTable();
	HTML errorHTML = new HTML();
	
	
	public ResizableMatrixWidget(int row, int column, boolean matrixDimension, boolean editable, 
			boolean symmetric)
	{
		VerticalPanel verticalPanel = new VerticalPanel();
		
		numberOfRows = row;
		numberOfColumns = column;
		symmetricMatrix = symmetric;
		editableMatrix = editable;
		matrixDimensionDisplay = matrixDimension;
		
		for(int i = 0; i < numberOfRows; i++)
		{
			for(int j = 0; j < numberOfColumns; j++)
			{
				TextBox textBox = new TextBox();
				textBox.setSize("50", "25");
				flexTable.setWidget(i+1, j+1, textBox);
				if(!editableMatrix)
				{
					textBox.setEnabled(false);
				}
				if(symmetricMatrix)
				{
					try
					{
						final int rowNumber = i+1;
						final int columnNumber = j+1;
						textBox.addChangeHandler(new ChangeHandler() 
						{
							@Override
							public void onChange(ChangeEvent event) 
							{
								try
								{
								TextBox tb = (TextBox)flexTable.getWidget(rowNumber, columnNumber);
								String value = tb.getValue();
								Double doubleValue = TextValidation.parseDouble(value);
								TextBox tb1 = (TextBox)flexTable.getWidget(columnNumber, rowNumber);
								tb.setValue(doubleValue.toString());
								tb1.setValue(doubleValue.toString());
								TextValidation.displayOkay(errorHTML, "");
								}
								catch(Exception e)
								{
									TextValidation.displayError(errorHTML, "The value should not contain special characters");
								}
							}
						});
						if(i >= j)
						{
							textBox.setEnabled(false);
						}
					}
					catch(Exception e)
					{
						
					}
				}
			}
		}
		
		verticalPanel.add(flexTable);
		verticalPanel.add(errorHTML);
		initWidget(verticalPanel);
	}
	
	
	public void setLabels(List<String> list)
	{
		GWT.log("Setting Labels");
		/*flexTable.clearCell(0, 0);*/
		List<String> labelList = list;
		int size = labelList.size();
		for(int i = 0; i < size; i++)
		{
			HTML label = new HTML();
			label.setText(labelList.get(i));
			/*flexTable.clearCell(0, i+1);*/
			flexTable.setWidget(0, i+1, label);
			
		}
		for(int i=0; i < size; i++)
		{
			HTML label = new HTML();
			label.setText(labelList.get(i));
			/*flexTable.clearCell(i+1, 0);*/
			flexTable.setWidget(i+1, 0, label);
		}
	}
	
	
	
	public void setRowLabels(List<String> labelString)
	{
		List<String> labelList = labelString;
		int size = labelList.size();
		for(int i = 1; i <= size; i++)
		{
			HTML label = new HTML();
			label.setText(labelList.get(i-1));
			flexTable.setWidget(0, i, label);
		}
	}
	
	
	
	public void setColumnLabels(List<String> labelString)
	{
		List<String> labelList = labelString;
		int size = labelList.size();
		for(int i=1; i <= size; i++)
		{
			HTML label = new HTML();
			label.setText(labelList.get(i-1));
			flexTable.setWidget(i, 0, label);
		}
	}
	
	
	public void setCellValue(int row, int column, String value)
	{
		String labelValue = value;
		int rowNumber = row;
		int columnNumber = column;
		
		TextBox tb = (TextBox)flexTable.getWidget(rowNumber, columnNumber);
		tb.setValue(labelValue);
		
	}

	public String getCell(int row, int column)
	{
		int rowNumber = row;
		int columnNumber = column;
		String value;
		TextBox tb = (TextBox)flexTable.getWidget(rowNumber, columnNumber);
		value = tb.getValue();
		return value;
	}
	
	public void diagonalsEditable()
	{
		for(int i = 0; i < numberOfRows; i++)
		{
				TextBox tb = (TextBox)flexTable.getWidget(i+1, i+1);
				tb.setEnabled(true);
		}
	}
	public void isSymmentric()
	{
		for(int i = 0; i < numberOfRows; i++)
		{
			for( int j = 0; j < numberOfColumns; i++)
			{
				try
				{
					final int rowNumber = i+1;
					final int columnNumber = j+1;
					TextBox tb = (TextBox)flexTable.getWidget(rowNumber, columnNumber);
					tb.addChangeHandler(new ChangeHandler() 
					{
						@Override
						public void onChange(ChangeEvent event) 
						{
							try
							{
							TextBox tb1 = (TextBox)event.getSource();
							String value = tb1.getValue();
							Double doubleValue = TextValidation.parseDouble(value);
							TextBox tb2 = (TextBox)flexTable.getWidget(columnNumber, rowNumber);
							tb1.setValue(doubleValue.toString());
							tb2.setValue(doubleValue.toString());
							TextValidation.displayOkay(errorHTML, "");
							}
							catch(Exception e)
							{
								TextValidation.displayError(errorHTML, "The value should not contain special characters");
							}
						}
					});
					if(i >= j)
					{
						tb.setEnabled(false);
					}
				}
				catch(Exception e)
				{
					
				}
			}
		}
	}
}
