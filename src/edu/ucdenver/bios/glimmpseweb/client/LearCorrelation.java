package edu.ucdenver.bios.glimmpseweb.client;

import java.util.ArrayList;
import java.util.List;
import java.lang.Math;

import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasures;

public class LearCorrelation 
{
	List <Integer> spacingList = new ArrayList<Integer>();
	int minDistance, maxDistance;
	int spacingListSize;
	public LearCorrelation(RepeatedMeasures obj)
	{
		spacingList = obj.getSpacingList();
		minDistance = Math.abs(spacingList.get(0) - spacingList.get(1));
		maxDistance = Math.abs(spacingList.get(0) - spacingList.get(1));
		spacingListSize = spacingList.size();
		calculateMaxDistance();
		calculateMinDistance();
	}

	//Method to calculate the mainimum distance between values in spacing list
	public int calculateMinDistance()
	{		
		for(int j = 0; j < spacingListSize; j++ )
		{
			for(int i = 0; i < spacingListSize; i++)
			{
				int difference = Math.abs(spacingList.get(j) - spacingList.get(i));
				if(difference < minDistance && difference > 0)
				{
					minDistance = difference;
				}
			}
		}
		return minDistance;
	}

	//Method to calculate the maximum distance between values in spacing list
	public int calculateMaxDistance()
	{
		for(int j = 0; j < spacingListSize; j++ )
		{
			for(int i = 0; i < spacingListSize; i++)
			{
				int difference = Math.abs(spacingList.get(j) - spacingList.get(i));
				if(difference > minDistance && difference > 0)
				{
					maxDistance = difference;
				}
			}
		}
		return maxDistance;
	}

	//Method to calculate the roh value depending upon the indices given
	public double getRho(int i, int j, double strongestCorrelation, int rateOfDecay)
	{
		int distanceBetweenIndices = Math.abs(spacingList.get(i-1) - spacingList.get(j-1));
		double rho;
		double powerValue;
		powerValue = minDistance+(strongestCorrelation*(distanceBetweenIndices-minDistance)/(maxDistance-minDistance));
		rho = Math.pow(strongestCorrelation, powerValue);
		return rho;
	}
}