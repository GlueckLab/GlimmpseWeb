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

import com.google.gwt.user.client.ui.HTML;

/**
 * Common text validation routines
 */
public class TextValidation
{
    // special characters allowed in a string input
    private static final String validPunctuationChars = " ._-";
    
    /**
     * Parse a string and throw an exception if disallowed characters appear
     * @param str the string to be validated
     * @return the string
     * @throws ParseException
     */
    public static String parseString(String str)
    throws ParseException
    {
        if (str == null || str.isEmpty()) throw new ParseException("empty string", 0);

        for (int pos = 0; pos < str.length(); pos++)
        {
            char ch = str.charAt(pos);
            if (!Character.isDigit(ch) && !Character.isLetter(ch) &&  
                    validPunctuationChars.indexOf(ch) < 0)
                throw new ParseException("invalid character at ", pos);
            pos++;
        }
        return str;

    }
    
    /**
     * Parse a string as an email address and throw an exception
     * if not in the form email@domain.com.
     * @param str email string
     */
    public static String parseEmail(String str) 
    throws ParseException {
        if (str == null || str.isEmpty()) throw new ParseException("empty string", 0);
        
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$";
        
        boolean valid = str.matches(emailPattern);
        if (!valid) {
            throw new ParseException("not a correctly formatted email", 0);
        }
        return str;
    }

    /**
     * Parse an integer between the specified bounds
     * @param str
     * @param lowerBound
     * @param upperBound
     * @return
     * @throws NumberFormatException
     */
    public static int parseInteger(String str, int lowerBound, int upperBound)
    throws NumberFormatException
    {
        if (str == null || str.isEmpty()) throw new NumberFormatException("empty string");

        int n = Integer.parseInt(str);
        if (n > upperBound || n < lowerBound) throw new NumberFormatException("out of bounds");

        return n;
    }

    /**
     * Parse an integer with a single upper or lower bound
     * @param str
     * @param bound
     * @param lower
     * @return
     * @throws NumberFormatException
     */
    public static int parseInteger(String str, double bound, boolean lower)
    throws NumberFormatException
    {
        if (str == null || str.isEmpty()) throw new NumberFormatException();

        int n = Integer.parseInt(str);
        if ((lower && n <= bound) || (!lower && n >= bound))
            throw new NumberFormatException();

        return n;
    }

    /**
     * Parse a double between the specified bounds
     * @param str
     * @param lowerBound
     * @param upperBound
     * @param includeEndpoints
     * @return
     * @throws NumberFormatException
     */
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

    /**
     * Parse a double with a single lower or upper bound
     * @param str
     * @param bound
     * @param lower
     * @return
     * @throws NumberFormatException
     */
    public static double parseDouble(String str, double bound, boolean lower)
    throws NumberFormatException
    {
        if (str == null || str.isEmpty()) throw new NumberFormatException();

        double n = Double.parseDouble(str);
        if ((lower && n < bound) || (!lower && n > bound))
            throw new NumberFormatException();

        return n;
    }

    /**
     * Parse a double with a single upper or lower bound including the boundary
     * @param str
     * @param bound
     * @param lower
     * @param includeBoundary
     * @return
     * @throws NumberFormatException
     */
    public static double parseDouble(String str, double bound, boolean lower,
            boolean includeEndpoint)
    throws NumberFormatException
    {
        if (str == null || str.isEmpty()) throw new NumberFormatException();

        double n = Double.parseDouble(str);
        if (includeEndpoint) {
            if ((lower && n <= bound) || (!lower && n >= bound))
                throw new NumberFormatException();
        } else {
            if ((lower && n < bound) || (!lower && n > bound))
                throw new NumberFormatException();
        }

        return n;
    }

    /**
     * Parse any double
     * @param str
     * @return
     * @throws NumberFormatException
     */
    public static double parseDouble(String str) throws NumberFormatException
    {
        double n = Double.parseDouble(str);
        return n;
    }

    /**
     * Set styles and display an error message
     * @param widget
     * @param msg
     */
    public static void displayError(HTML widget, String msg)
    {
        widget.removeStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_OKAY);
        widget.removeStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_ERROR);

        widget.addStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_ERROR);
        widget.setHTML(msg);
    }

    /**
     * Set styles and display an "okay" message
     * @param widget
     * @param msg
     */
    public static void displayOkay(HTML widget, String msg)
    {
        widget.removeStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_ERROR);
        widget.removeStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_OKAY);

        widget.addStyleDependentName(GlimmpseConstants.STYLE_MESSAGE_OKAY);
        widget.setHTML(msg);
    }
}
