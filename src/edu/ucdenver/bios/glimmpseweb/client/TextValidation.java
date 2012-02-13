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


import java.text.ParseException;
import java.text.StringCharacterIterator;

import org.apache.regexp.CharacterIterator;

import com.google.gwt.user.client.ui.HTML;

/**
 * Common text validation routines
 */
public class TextValidation
{
	private static final String validPunctuationChars = " ._-";
	public static String parseString(String str)
	throws ParseException
	{
		if (str == null || str.isEmpty()) throw new ParseException("empty string", 0);
		
		for (int pos = 0; pos < str.length(); pos++)
		{
		    char ch = str.charAt(pos);
			if (!Character.isDigit(ch) && !Character.isLetter(ch) &&  
					validPunctuationChars.indexOf(ch) != -1)
				throw new ParseException("invalid character at ", pos);
			pos++;
		}
		return str;

	}
	
	
	public static int parseInteger(String str, int lowerBound, int upperBound)
	throws NumberFormatException
	{
        if (str == null || str.isEmpty()) throw new NumberFormatException("empty string");

        int n = Integer.parseInt(str);
        if (n > upperBound || n < lowerBound) throw new NumberFormatException("out of bounds");

        return n;
	}
	
	public static int parseInteger(String str, double bound, boolean lower)
	throws NumberFormatException
	{
	    if (str == null || str.isEmpty()) throw new NumberFormatException();

	    int n = Integer.parseInt(str);
	    if ((lower && n <= bound) || (!lower && n >= bound))
	        throw new NumberFormatException();

	    return n;
	}
	
	public static double parseDouble(String str, double lowerBound, double upperBound, 
			boolean includeEndpoints)
	throws NumberFormatException
	{
		if (str == null || str.isEmpty()) throw new NumberFormatException();

		double n = Double.parseDouble(str);
		if (includeEndpoints)
		{
			if (n > upperBound || n < lowerBound) throw new NumberFormatException();
		}
		else
		{
			if (n >= upperBound || n <= lowerBound) throw new NumberFormatException();
		}

		return n;
	}
	
	public static double parseDouble(String str, double bound, boolean lower)
	throws NumberFormatException
	{
	    if (str == null || str.isEmpty()) throw new NumberFormatException();

	    double n = Double.parseDouble(str);
	    if ((lower && n < bound) || (!lower && n > bound))
	        throw new NumberFormatException();

	    return n;
	}
	
    public static void displayError(HTML widget, String msg)
    {
        widget.removeStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_OKAY);
        widget.removeStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_ERROR);

        widget.addStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_ERROR);
        widget.setHTML(msg);
    }
    
    public static void displayOkay(HTML widget, String msg)
    {
        widget.removeStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_ERROR);
        widget.removeStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_OKAY);

        widget.addStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_OKAY);
        widget.setHTML(msg);
    }
}
