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
import edu.ucdenver.bios.webservice.common.domain.RepeatedMeasuresNode;

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

            if (factorList.size() == 1) {
                /* special case - either one sample or single factor with multiple levels */
                BetweenParticipantFactor factor = factorList.get(0);
                List<Category> categoryList = factor.getCategoryList();
                if (categoryList != null && categoryList.size() > 0) {
                    columnLabels.add(factor.getPredictorName());
                    totalPermutations = categoryList.size();
                    
                    ArrayList<String> permutationList = new ArrayList<String>();
                    for(Category category: categoryList) {
                        permutationList.add(category.getCategory());
                    }
                    columns.add(permutationList);
                }

            } else {
                // add column names for factors which are complete, that is,
                // have at least 2 categories.  Also calculate total rows
                for(BetweenParticipantFactor factor: factorList)
                {
                    List<Category> categoryList = factor.getCategoryList();
                    if (categoryList != null && categoryList.size() > 1) {
                        columnLabels.add(factor.getPredictorName());
                        totalPermutations *= categoryList.size();
                    }
                }     
                // now that we have the total permutations, fill in the column values
                // for each factor
                int numRepetitions = totalPermutations;
                for(BetweenParticipantFactor factor: factorList)
                {
                    List<Category> categoryList = factor.getCategoryList();
                    int numCategories = categoryList.size();
                    if (categoryList != null && numCategories > 1) {
                        // number of times each category repeats
                        numRepetitions /= numCategories;
                        ArrayList<String> permutationValues = new ArrayList<String>();
                        int currentCategory = 0;
                        int currentRepetition = 0;
                        String category = categoryList.get(currentCategory).getCategory();
                        for(int i = 0; i < totalPermutations; i++) {
                            if (currentRepetition == numRepetitions) {
                                currentCategory++;
                                if (currentCategory >= numCategories) {
                                    currentCategory = 0;
                                }
                                category = categoryList.get(currentCategory).getCategory();
                                currentRepetition = 0;
                            }
                            permutationValues.add(category);
                            currentRepetition++;
                        }
                        columns.add(permutationValues);
                    }
                } 
            }
        }
    }

    public void loadRepeatedMeasures(List<RepeatedMeasuresNode> factorList) {
        clear();
        //        if (factorList != null && factorList.size() > 0) {
        //            int totalPermutations = 1;
        //            // add column names for factors which are complete, that is,
        //            // have at least 2 categories.  Also calculate total rows
        //            for(RepeatedMeasuresNode factor: factorList)
        //            {
        //                List<Category> categoryList = factor.;
        //                if (categoryList != null && categoryList.size() > 1) {
        //                    columnLabels.add(factor.getPredictorName());
        //                    totalPermutations *= categoryList.size();
        //                }
        //            }
        //            
        //            // now that we have the total permutations, fill in the column values
        //            // for each factor
        //            int numRepetitions = totalPermutations;
        //            for(BetweenParticipantFactor factor: factorList)
        //            {
        //                List<Category> categoryList = factor.getCategoryList();
        //                int numCategories = categoryList.size();
        //                if (categoryList != null && numCategories > 1) {
        //                    // number of times each category repeats
        //                    numRepetitions /= numCategories;
        //                    ArrayList<String> permutationValues = new ArrayList<String>();
        //                    int currentCategory = 0;
        //                    int currentRepetition = 0;
        //                    String category = categoryList.get(currentCategory).getCategory();
        //                    for(int i = 0; i < totalPermutations; i++) {
        //                        if (currentRepetition == numRepetitions) {
        //                            currentCategory++;
        //                            if (currentCategory >= numCategories) {
        //                                currentCategory = 0;
        //                            }
        //                            category = categoryList.get(currentCategory).getCategory();
        //                            currentRepetition = 0;
        //                        }
        //                        permutationValues.add(category);
        //                        currentRepetition++;
        //                    }
        //                    columns.add(permutationValues);
        //                }
        //            } 
        //        }
    }

    public List<String> getColumnLabels() {
        return columnLabels;
    }

    public int getNumberOfColumns() {
        return columns.size();
    }

    public int getNumberOfRows() {
        int rows = 0;
        if (columns.size() > 0) {
            rows = columns.get(0).size();
        }
        return rows;
    }
    public List<String> getColumn(int index) {
        if (index < 0 || index > columns.size()-1) {
            return null;
        }
        return columns.get(index);
    }

    public void clear() {
        columnLabels.clear();
        columns.clear(); // TODO: clear individuals columns?
    }
}
