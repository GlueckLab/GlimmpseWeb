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
package edu.ucdenver.bios.glimmpseweb.client.guided;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.XMLUtilities;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;

/**
 * Entry screen for relative group sizes
 * @author Sarah Kreidler
 *
 */
public class RelativeGroupSizePanel extends WizardStepPanel
{
	protected static final int MAX_RELATIVE_SIZE = 10;
    // data table to display possible groups
    protected FlexTable groupSizesTable = new FlexTable();

	public RelativeGroupSizePanel(WizardContext context)
	{
		super(context, "Relative Group Sizes");
		complete = true;
        VerticalPanel panel = new VerticalPanel();
        
        // create header/instruction text
        HTML header = new HTML(GlimmpseWeb.constants.relativeGroupSizeTitle());
        HTML description = new HTML(GlimmpseWeb.constants.relativeGroupSizeDescription());     
        
        // layout the overall panel
        panel.add(header);
        panel.add(description);
        panel.add(groupSizesTable);

        // set style
        panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
        header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
        description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        groupSizesTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE);
        
        initWidget(panel);
	}

	@Override
	public void reset()
	{
		groupSizesTable.removeAllRows();
		complete = true;
	}

//	@Override
//	public void loadFromNode(Node node)
//	{
//		if (GlimmpseConstants.TAG_RELATIVE_GROUP_SIZE_LIST.equalsIgnoreCase(node.getNodeName()))
//		{
//			NodeList children = node.getChildNodes();
//			for(int i = 0, row = 1; i < children.getLength(); i++, row++)
//			{
//				Node child = children.item(i);
//				Node valueNode = child.getFirstChild();
//				if (valueNode != null)
//				{
//					Widget w = groupSizesTable.getWidget(row, 0);
//					if (w != null)
//					{
//						try
//						{
//							Integer value = Integer.parseInt(valueNode.getNodeValue());
//							if (value > 0 && value <= MAX_RELATIVE_SIZE)
//							{
//								((ListBox) w).setSelectedIndex(value-1);
//							}
//						}
//						catch (NumberFormatException e)
//						{
//							// catch, but ignore
//						}
//					}
//				}
//			}
//		}
//	}

//	@Override
//	public void onPredictors(HashMap<String, ArrayList<String>> predictorMap,
//			DataTable groups)
//	{
//    	reset();
//    	if (predictorMap.size() > 0)
//    	{
//    		groupSizesTable.getRowFormatter().setStyleName(0, 
//    				GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_HEADER);
//    		groupSizesTable.setWidget(0, 0, new HTML(GlimmpseWeb.constants.relativeGroupSizeTableColumn()));
//    		for(int col = 0; col < groups.getNumberOfColumns(); col++)
//    		{
//    			groupSizesTable.setWidget(0, col+1, new HTML(groups.getColumnLabel(col)));
//    		}
//    		for(int row = 0; row < groups.getNumberOfRows(); row++)
//    		{
//    			groupSizesTable.setWidget(row+1, 0, createGroupSizeListBox());
//    			groupSizesTable.getRowFormatter().setStyleName(row+1, GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
//    			for(int col = 0; col < groups.getNumberOfColumns(); col++)
//    			{
//    				groupSizesTable.setWidget(row+1, col+1, new HTML(groups.getValueString(row, col)));
//    			}
//    		}
//    	}
//	}

	private ListBox createGroupSizeListBox()
	{
		ListBox lb = new ListBox();
		for(int i = 1; i <= MAX_RELATIVE_SIZE; i++) lb.addItem(Integer.toString(i));
		return lb;
	}
	
//	public void addRelativeGroupSizeListener(RelativeGroupSizeListener listener)
//	{
//		listeners.add(listener);
//	}
	
	public String toRequestXML()
	{
		StringBuffer buffer = new StringBuffer();

		// build the "fixed" cell means matrix.  Essentially, this is an identity matrix
		// with rows & columns equal to the number of study sub groups
		int size = groupSizesTable.getRowCount() -1;  // skip header

		XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_DESIGN, size, size);

		// identity matrix with repeats for unequal group sizes as needed
		for(int row = 0; row < size; row++)
		{
			ListBox lb = (ListBox) groupSizesTable.getWidget(row+1, 0); // skip header
			int groupReps = lb.getSelectedIndex() + 1;
			for(int rep = 0; rep < groupReps; rep++)
			{
				buffer.append("<r>");
				for(int col = 0; col < size; col++)
				{
					buffer.append("<c>");
					if (row == col)
						buffer.append(1);
					else
						buffer.append(0);
					buffer.append("</c>");
				}
				buffer.append("</r>");
			}
		}
		
		// close tag
		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);		
		return buffer.toString();
	}
	
	
	public String toStudyXML()
	{
		StringBuffer buffer = new StringBuffer();
		int numRows = groupSizesTable.getRowCount();
		// convert the relative group size table to XML
		XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_RELATIVE_GROUP_SIZE_LIST);
		// add the list of relative sizes (skip header row)
		for(int row = 1; row < numRows; row++)
		{
			ListBox lb = (ListBox) groupSizesTable.getWidget(row, 0);
			if (lb != null) 
			{
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_VALUE);
				buffer.append(lb.getItemText(lb.getSelectedIndex()));
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_VALUE);
			}
		}

		XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_RELATIVE_GROUP_SIZE_LIST);
		
		return buffer.toString();
	}
	
	@Override
	public void onExit()
	{
		ArrayList<Integer> relativeSizes = new ArrayList<Integer>();
		for(int i = 1; i < groupSizesTable.getRowCount(); i++)
		{
			ListBox lb = (ListBox) groupSizesTable.getWidget(i, 0);
			relativeSizes.add(lb.getSelectedIndex()+1);
		}
//		for(RelativeGroupSizeListener listener: listeners)
//		{
//			listener.onRelativeGroupSize(relativeSizes);
//		}
	}

}
