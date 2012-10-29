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
package edu.ucdenver.bios.glimmpseweb.context;

import java.util.ArrayList;
import java.util.List;

import edu.ucdenver.bios.webservice.common.domain.BetweenParticipantFactor;
import edu.ucdenver.bios.webservice.common.domain.Category;

/**
 * Convenience class for tables of string labels.  Used for generating
 * all possible permutations of factors
 * @author Sarah Kreidler
 *
 */
public class FactorTable {
    protected ArrayList<String> columnLabels = new ArrayList<String>();
    protected ArrayList<ArrayList<String>> columns = new ArrayList<ArrayList<String>>();

    /**
     * Create an empty factor table
     */
    public FactorTable() {}

    /**
     * Fill the factor table from a list of between participant factors.
     * Generates all possible combinations of levels of each factor
     * 
     * @param factorList list of between participant factors
     */
    public void loadBetweenParticipantFactors(List<BetweenParticipantFactor> factorList) {
        clear();        
        if (factorList != null && factorList.size() > 0) {
            int totalPermutations = 1;

            // calculate total size and add column headers
            for(BetweenParticipantFactor factor: factorList) {
                List<Category> categoryList = factor.getCategoryList();
                if (categoryList != null && categoryList.size() >= 2) {
                    // add colum header
                    columnLabels.add(factor.getPredictorName());
                    // multiple in the number of categories
                    totalPermutations *= categoryList.size();
                }
            }
            
            // now build the columns
            int numRepetitions = 1;
            for(BetweenParticipantFactor factor: factorList) {
                ArrayList<String> column = new ArrayList<String>(totalPermutations);
                List<Category> categoryList = factor.getCategoryList();
                if (categoryList != null && categoryList.size() >= 2) {
                    for(int perm = 0; perm < totalPermutations; ) {
                        for(Category category: categoryList) {
                            String value = category.getCategory();
                            for(int rep = 0; rep < numRepetitions; rep++) {
                                column.add(value);
                                perm++;
                            }
                        }
                    }
                    numRepetitions *= categoryList.size();
                }
                columns.add(column);
            }
        }
    }

    /**
     * Add a factor to the table
     * @param factor
     */
    public void addFactor(BetweenParticipantFactor factor) {
        List<Category> categoryList = factor.getCategoryList();
        if (categoryList != null && categoryList.size() >= 2) {
            columnLabels.add(factor.getPredictorName());
        }
        
    }
    
    /** 
     * remove a factor from the table
     * @param factor
     */
    public void deleteFactor(BetweenParticipantFactor factor) {
        
    }
    
    /**
     * Add a category to the specified factor
     * @param factor
     * @param category
     */
    public void addFactorCategory(BetweenParticipantFactor factor,
            String category) {
        
    }
   
    /**
     * Remove the specified category from the factor
     * @param factor
     * @param category
     */
    public void deleteFactorCategory(BetweenParticipantFactor factor,
            String category) {
        
    }
    
    /**
     * Get the predictor column labels
     * @return
     */
    public List<String> getColumnLabels() {
        return columnLabels;
    }

    /**
     * Get the number of factors
     * @return
     */
    public int getNumberOfColumns() {
        return columns.size();
    }

    /**
     * Get the number of combinations of factors
     * @return
     */
    public int getNumberOfRows() {
        int rows = 0;
        if (columns.size() > 0) {
            rows = columns.get(0).size();
        }
        return rows;
    }
    
    /**
     * Get the list of values for a single factor
     * @param index
     * @return
     */
    public List<String> getColumn(int index) {
        if (index < 0 || index > columns.size()-1) {
            return null;
        }
        return columns.get(index);
    }

    /**
     * Clear the table
     */
    public void clear() {
        columnLabels.clear();
        columns.clear(); // TODO: clear individuals columns?
    }
}
