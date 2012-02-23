/*
 * Web service utility functions for managing hibernate, json, etc.
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

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;

/**
 * Class to compute the Lear correlation for a single
 * dimension of repeated measures
 * 
 * @author Vijay Akula
 *
 */
public class LearCorrelation 
{
	List <Integer> spacingList = new ArrayList<Integer>();
	int minDistance, maxDistance;
	int spacingListSize;
	
	/**
	 * Create a Lear Correlation object to calculate correlation between
	 * repeated measurements.  Assumes that the spacing values in
	 * the RepeatedMeasuresNode objects are sorted in ascending order
	 * 
	 * @param node repeated measures node object
	 * @throws IllegalArgumentException
	 */
	public LearCorrelation(RepeatedMeasuresNode node)
	throws IllegalArgumentException
	{
	    if (node == null)
	        throw new IllegalArgumentException("Failed to create Lear Correlation: null repeated measures object");
	    
	    spacingList = node.getSpacingList();
	    if (spacingList == null || spacingList.size() < 2)
	        throw new IllegalArgumentException("Failed to create Lear Correlation: invalid spacing");

		minDistance = Math.abs(spacingList.get(1) - spacingList.get(0));
		maxDistance = Math.abs(spacingList.get(spacingList.size()-1) - spacingList.get(0));
	}
//
//	//Method to calculate the mainimum distance between values in spacing list
//	public int calculateMinDistance()
//	{		
//		for(int j = 0; j < spacingListSize; j++ )
//		{
//			for(int i = 0; i < spacingListSize; i++)
//			{
//				int difference = Math.abs(spacingList.get(j) - spacingList.get(i));
//				if(difference < minDistance && difference > 0)
//				{
//					minDistance = difference;
//				}
//			}
//		}
//		return minDistance;
//	}
//
//	//Method to calculate the maximum distance between values in spacing list
//	public int calculateMaxDistance()
//	{
//		for(int j = 0; j < spacingListSize; j++ )
//		{
//			for(int i = 0; i < spacingListSize; i++)
//			{
//				int difference = Math.abs(spacingList.get(j) - spacingList.get(i));
//				if(difference > minDistance && difference > 0)
//				{
//					maxDistance = difference;
//				}
//			}
//		}
//		return maxDistance;
//	}

	/**
	 * Calculate the Lear correlation between the ith and jth 
	 * repeated measurement.  From the publication:
	 * 
	 * Simpson SL, Edwards LJ, Muller KE, Sen PK, Styner MA. 
	 * A linear exponent AR(1) family of correlation structures. 
	 * Stat Med. 2010;29(17):1825-1838.
	 * 
	 * @param i index of one of the measurements
	 * @param j index of the comparison measurement
	 * @param baseCorrelation correlation between elements that are 1 unit apart
	 * @param rateOfDecay rate at which the correlation decays.
	 */
	public double getRho(int i, int j, double baseCorrelation, double rateOfDecay)
	throws IllegalArgumentException
	{
	    if (i < 0 || j < 0 || i > spacingList.size()-1 || j > spacingList.size()-1)
	        throw new IllegalArgumentException("Invalid measurement indices");
	    if (baseCorrelation < -1 || baseCorrelation > 1)
	        throw new IllegalArgumentException("Base correlation must be between -1 and 1");
	    if (rateOfDecay < 0)
	        throw new IllegalArgumentException("Rate of decay must be positive");
	    
		int measurementDistance = Math.abs(spacingList.get(i) - spacingList.get(j));
		double powerValue;
		powerValue = minDistance+(rateOfDecay*(measurementDistance-minDistance)/(maxDistance-minDistance));
		return Math.pow(baseCorrelation, powerValue);
	}
}