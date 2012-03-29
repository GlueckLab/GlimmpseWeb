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
package edu.ucdenver.bios.glimmpseweb.client.shared;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

import edu.ucdenver.bios.glimmpseweb.client.GlimmpseConstants;
import edu.ucdenver.bios.glimmpseweb.client.GlimmpseWeb;
import edu.ucdenver.bios.webservice.common.domain.StudyDesign;


/**
 * Panel for displaying matrices used in power calculations.
 * This is a pop-up on the results screen.
 *
 */
public class MatrixDisplayPanel extends Composite
{
	protected FlexTable tableOfMatrices = new FlexTable();
	
	public MatrixDisplayPanel()
	{
		VerticalPanel panel = new VerticalPanel();

		panel.add(tableOfMatrices);
		
		// style 
		tableOfMatrices.setBorderWidth(1);
		
		// initialize
		initWidget(panel);
	}
	
	public void loadFromStudyDesign(StudyDesign design)
	{
//		try
//		{
//			Document doc = XMLParser.parse(matrixXML);
//
//			int row = 0;
//
//			Node paramsNode = doc.getFirstChild();
//			if (paramsNode != null)
//			{
//				NodeList children = paramsNode.getChildNodes();
//				for(int i = 0; i < children.getLength(); i++)
//				{
//					Node child = children.item(i);
//					if (GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX.equals(child.getNodeName()))
//					{
//						// TODO: matrix names
//						NamedNodeMap attrs = child.getAttributes();
//						Node nameNode = attrs.getNamedItem(GlimmpseConstants.ATTR_NAME);
//						tableOfMatrices.setWidget(row, 0, new HTML(prettyMatrixName(nameNode.getNodeValue())));
//						tableOfMatrices.setWidget(row++, 1, buildFixedRandomMatrixGrid(child, "1"));
//					}
//					else if (GlimmpseConstants.TAG_MATRIX.equals(child.getNodeName()))
//					{
//						NamedNodeMap attrs = child.getAttributes();
//						Node nameNode = attrs.getNamedItem(GlimmpseConstants.ATTR_NAME);
//						if (nameNode != null)
//						{
//							tableOfMatrices.setWidget(row, 0, new HTML(prettyMatrixName(nameNode.getNodeValue())));
//						}
//
//						FlexTable table = new FlexTable();
//						table.setBorderWidth(1);
//						addMatrixData(child, table, 0, 0);
//						tableOfMatrices.setWidget(row++, 1, table);
//					}
//				}
//			}
//		}
//		catch (DOMParseException e)
//		{
//			Window.alert(e.getMessage());
//		}
	}
	
	private String prettyMatrixName(String name)
	{
		if (GlimmpseConstants.MATRIX_DESIGN.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.matrixCategoricalEffectsLabel();
		else if (GlimmpseConstants.MATRIX_WITHIN_CONTRAST.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.withinSubjectContrastMatrixName();
		else if (GlimmpseConstants.MATRIX_SIGMA_ERROR.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.sigmaErrorMatrixName();
		else if (GlimmpseConstants.MATRIX_SIGMA_OUTCOME.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.sigmaOutcomeMatrixName();
		else if (GlimmpseConstants.MATRIX_SIGMA_OUTCOME_COVARIATE.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.sigmaOutcomeCovariateMatrixName();
		else if (GlimmpseConstants.MATRIX_SIGMA_COVARIATE.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.sigmaCovariateMatrixName();
		else if (GlimmpseConstants.MATRIX_THETA.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.thetaNullMatrixName();
		else if (GlimmpseConstants.MATRIX_BETA.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.betaFixedMatrixName();
		else if (GlimmpseConstants.MATRIX_BETWEEN_CONTRAST.equalsIgnoreCase(name))
			return GlimmpseWeb.constants.betweenSubjectContrastMatrixName();
		else
			return "";
	}
	
	public void reset()
	{
		tableOfMatrices.removeAllRows();
	}
	
    public FlexTable buildFixedRandomMatrixGrid(Node matrixNode, String randomValue)
    {
    	FlexTable table = new FlexTable();
    	table.setBorderWidth(1);
    	if (matrixNode != null)
    	{
    		NamedNodeMap frAttrs = matrixNode.getAttributes();
    		boolean combineHorizontal = true;
    		Node combineHorizontalNode = frAttrs.getNamedItem(GlimmpseConstants.ATTR_COMBINE_HORIZONTAL);
    		if (combineHorizontalNode != null) combineHorizontal = Boolean.parseBoolean(combineHorizontalNode.getNodeValue());   		
    		
    		Node fixedNode = null;
    		Node randomNode = null;
    		NodeList children = matrixNode.getChildNodes();
    		for(int i = 0; i < children.getLength() && (fixedNode == null || randomNode == null); i++)
    		{
    			Node child = children.item(i);
    			if (GlimmpseConstants.TAG_MATRIX.equals(child.getNodeName()))
    			{
    				NamedNodeMap attrs = child.getAttributes();
    				Node nameNode = attrs.getNamedItem(GlimmpseConstants.ATTR_NAME);
    				if (GlimmpseConstants.MATRIX_FIXED.equals(nameNode.getNodeValue()))
    					fixedNode = child;
    				else if (GlimmpseConstants.MATRIX_FIXED.equals(nameNode.getNodeValue()))
    					randomNode = child;
    			}
    		}
    		
    		int startRow = 0;
    		int startCol = 0;
    		if (fixedNode != null)
    		{
    			addMatrixData(fixedNode, table, startRow, startCol);
    		}
    		if (randomNode != null)
    		{
    			if (combineHorizontal)
    			{
    				startCol = table.getCellCount(0);
    			}
    			else
    			{
    				startRow = table.getRowCount();
    			}
    			addMatrixData(fixedNode, table, startRow, startCol);

    		}
    	}
    	
    	
    	return table;
    }

	
	
    public void addMatrixData(Node matrixNode, FlexTable table, int startRow, int startCol)
    {    	
        NamedNodeMap attrs = matrixNode.getAttributes();
        Node rowNode = attrs.getNamedItem("rows");
        Node colNode = attrs.getNamedItem("columns");
        if (rowNode != null && colNode != null)
        {                   
            NodeList rowNodeList = matrixNode.getChildNodes();
            for(int r = 0; r < rowNodeList.getLength(); r++)
            {
                NodeList colNodeList = rowNodeList.item(r).getChildNodes();
                for(int c = 0; c < colNodeList.getLength(); c++)
                {
                    Node colItem = colNodeList.item(c).getFirstChild();
                    if (colItem != null) 
                    {
                    	table.setWidget(startRow + r, startCol + c, new Label(colItem.getNodeValue()));
                    }
                }
            }
        }
    }
	
}
