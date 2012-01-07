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
package edu.ucdenver.bios.glimmpseweb.client;

/**
 * Utilities for writing XML representations 
 */
public class XMLUtilities
{
	/**
	 * Write an XML open tag for a matrix.
	 * 
	 * @param buffer StringBuffer to which XML is appended
	 * @param name name of the matrix
	 * @param rows row dimension
	 * @param columns column dimension
	 */
	public static void matrixOpenTag(StringBuffer buffer, String name, int rows, int columns)
	{
		buffer.append("<");
		buffer.append(GlimmpseConstants.TAG_MATRIX);
		buffer.append(" ");
		buffer.append(GlimmpseConstants.ATTR_NAME);
		buffer.append("='");
		buffer.append(name);
		buffer.append("' ");
		buffer.append(GlimmpseConstants.ATTR_ROWS);
		buffer.append("='");
		buffer.append(rows);
		buffer.append("' ");
		buffer.append(GlimmpseConstants.ATTR_COLUMNS);
		buffer.append("='");
		buffer.append(columns);
		buffer.append("' ");
		buffer.append(">");
	}
	
	/**
	 * Create an XML open tag for a FixedRandomMatrix object
	 * @param buffer StringBuffer to which XML is appended
	 * @param name name of the matrix
	 * @param combineHorizontal indicates if the fixed/random submatrices are appended 
	 * horizontally to create the full matrix
	 */
	public static void fixedRandomMatrixOpenTag(StringBuffer buffer, String name, boolean combineHorizontal)
	{
		buffer.append("<");
		buffer.append(GlimmpseConstants.TAG_FIXED_RANDOM_MATRIX);
		buffer.append(" ");
		buffer.append(GlimmpseConstants.ATTR_NAME);
		buffer.append("='");
		buffer.append(name);
		buffer.append("' ");
		buffer.append(GlimmpseConstants.ATTR_COMBINE_HORIZONTAL);
		buffer.append("='");
		buffer.append(combineHorizontal);
		buffer.append("' ");
		buffer.append(">");
	}
	
	/**
	 * Write an XML close tag
	 * @param buffer StringBuffer to which the XML is appended
	 * @param tagName name of the XML tag
	 */
	public static void closeTag(StringBuffer buffer, String tagName)
	{
		buffer.append("</");
		buffer.append(tagName);
		buffer.append(">");
	}
	
	/**
	 * Write an XML open tag
	 * @param buffer StringBuffer to which the XML is appended
	 * @param tagName name of the XML tag
	 */
	public static void openTag(StringBuffer buffer, String tagName)
	{
		buffer.append("<");
		buffer.append(tagName);
		buffer.append(">");
	}
	
	/**
	 * Write an XML open tag with attributes
	 * @param buffer StringBuffer to which the XML is appended
	 * @param tagName name of the XML tag
	 * @param attrs String containing the list of attributes (already XML formatted)
	 */
	public static void openTag(StringBuffer buffer, String tagName, String attrs)
	{
		buffer.append("<");
		buffer.append(tagName);
		buffer.append(" ");
		buffer.append(attrs);
		buffer.append(">");
	}
}
