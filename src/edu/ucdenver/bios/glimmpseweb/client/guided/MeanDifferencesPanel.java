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
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.DataTable;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.glimmpseweb.client.TextValidation;
import edu.ucdenver.bios.glimmpseweb.client.XMLUtilities;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardContext;
import edu.ucdenver.bios.glimmpseweb.client.wizard.WizardStepPanel;

/**
 * Entry screen for means by outcome and study subgroup
 */
public class MeanDifferencesPanel extends WizardStepPanel
implements ChangeHandler
{
	protected FlexTable meansTable = new FlexTable();

	protected boolean hasCovariate = false;
	List<String> outcomes = null;
	HashMap<String, ArrayList<String>> predictorMap;
	protected DataTable groups = null;

	protected HTML errorHTML = new HTML();
	
	protected ArrayList<String> uploadedValues = new ArrayList<String>();
	
	public MeanDifferencesPanel(WizardContext context)
	{
		super(context, "Mean Differences");
		complete = true;
		VerticalPanel panel = new VerticalPanel();

		HTML header = new HTML(GlimmpseWeb.constants.meanDifferenceTitle());
		HTML description = new HTML(GlimmpseWeb.constants.meanDifferenceDescription());

		panel.add(header);
		panel.add(description);
		panel.add(meansTable);
		panel.add(errorHTML);

		// set style
		panel.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_PANEL);
		header.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_HEADER);
		description.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_DESCRIPTION);
        meansTable.setStyleName(GlimmpseConstants.STYLE_WIZARD_STEP_TABLE);
        errorHTML.setStyleName(GlimmpseConstants.STYLE_MESSAGE);
		initWidget(panel);
	}

	@Override
	public void reset()
	{
		meansTable.removeAllRows();
		uploadedValues.clear();
		complete = true;
	}

//	@Override
//	public void loadFromNode(Node node)
//	{
//		if (GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX.equalsIgnoreCase(node.getNodeName()))
//		{
//			NodeList children = node.getChildNodes();
//			for(int i = 0; i < children.getLength(); i++)
//			{
//				Node child = children.item(i);
//				String childName = child.getNodeName();
//				if (GlimmpseConstants.TAG_MATRIX.equals(childName))
//				{
//					NamedNodeMap childattrs = child.getAttributes();
//					Node nameNode = childattrs.getNamedItem(GlimmpseConstants.ATTR_NAME);
//					if (nameNode != null)
//					{
//						if (GlimmpseConstants.MATRIX_FIXED.equals(nameNode.getNodeValue()))
//						{
//							NamedNodeMap attrs = child.getAttributes();
//							Node rowNode = attrs.getNamedItem("rows");
//							Node colNode = attrs.getNamedItem("columns");
//							if (rowNode != null && colNode != null)
//							{           
//								NodeList rowNodeList = child.getChildNodes();
//								for(int r = 0; r < rowNodeList.getLength(); r++)
//								{
//									NodeList colNodeList = rowNodeList.item(r).getChildNodes();
//									for(int c = 0; c < colNodeList.getLength(); c++)
//									{
//										Node colItem = colNodeList.item(c).getFirstChild();
//										if (colItem != null) 
//										{
//											uploadedValues.add(colItem.getNodeValue());
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//	}

	public void onEnter()
	{
//		if (changed)
//		{
//			changed = false;
//			reset();
//	    	if (predictorMap.size() > 0 && outcomes.size() > 0)
//	    	{
//	    		// create the table header row
//	    		meansTable.getRowFormatter().setStyleName(0, 
//	    				GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_HEADER);
//	    		int col = 0;
//	    		for(;col < groups.getNumberOfColumns(); col++)
//	    		{
//	    			meansTable.setWidget(0, col, new HTML(groups.getColumnLabel(col)));
//	    		}
//				for(String outcome: outcomes)
//				{
//					meansTable.setWidget(0, col, new HTML(outcome));
//					col++;
//				}
//				
//				// now fill in the group values, and add "0" text boxes for entering the means
//				int rowOrderCount = 0;
//				boolean uploadComplete = false;
//	    		for(int row = 0; row < groups.getNumberOfRows(); row++)
//	    		{
//	    			meansTable.getRowFormatter().setStyleName(row+1, GlimmpseConstants.STYLE_WIZARD_STEP_TABLE_ROW);
//	    			for(col = 0; col < groups.getNumberOfColumns(); col++)
//	    			{
//	    				meansTable.setWidget(row+1, col, new HTML(groups.getValueString(row, col)));
//	    			}
//					for(String outcome: outcomes)
//					{
//						TextBox tb = new TextBox();
//						if (rowOrderCount < uploadedValues.size())
//						{
//							tb.setText(uploadedValues.get(rowOrderCount));
//							uploadComplete = true;
//						}
//						else
//						{
//							tb.setText("0");
//						}
//						tb.addChangeHandler(this);
//						meansTable.setWidget(row+1, col, tb);
//						col++;
//						rowOrderCount++;
//					}
//	    		}
//	    		
//	    		if (uploadComplete) uploadedValues.clear();
//	    	}
//		}
	}

	public String toXML()
	{
		StringBuffer buffer = new StringBuffer();
		if (!skip && complete)
		{
			XMLUtilities.fixedRandomMatrixOpenTag(buffer, GlimmpseConstants.MATRIX_BETA, false);

			int columns = meansTable.getCellCount(0);
			int rows = meansTable.getRowCount();
			
			// main effects hypothesis
			XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_FIXED, 
					groups.getNumberOfRows(), outcomes.size());
			for(int row = 1; row < rows; row++)
			{
				buffer.append("<r>");
				for(int col = groups.getNumberOfColumns(); col < columns; col++)
				{
					buffer.append("<c>");
					buffer.append(((TextBox) meansTable.getWidget(row, col)).getText());
					buffer.append("</c>");
				}
				buffer.append("</r>");
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);

			if (hasCovariate)
			{
				XMLUtilities.matrixOpenTag(buffer, GlimmpseConstants.MATRIX_RANDOM, 1, outcomes.size());
				XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_ROW);
				for(int col = 0; col < columns; col++)
				{
					XMLUtilities.openTag(buffer, GlimmpseConstants.TAG_COLUMN);
					buffer.append(1);
					XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_COLUMN);
				}
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_ROW);
				XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_MATRIX);
			}
			XMLUtilities.closeTag(buffer, GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX);
		}
		return buffer.toString();
	}


	
	@Override
	public void onChange(ChangeEvent event)
	{
		TextBox tb = (TextBox) event.getSource();
		try
		{
			Double.parseDouble(tb.getText());
			TextValidation.displayOkay(errorHTML, "");
		}
		catch (NumberFormatException nfe)
		{
			TextValidation.displayError(errorHTML, GlimmpseWeb.constants.errorInvalidNumber());
			tb.setText("0");
		}
	}
}
