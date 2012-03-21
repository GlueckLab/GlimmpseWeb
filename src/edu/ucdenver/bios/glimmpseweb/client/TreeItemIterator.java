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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * Depth first traversal for GWT tree widgets.  
 * 
 * @author Sarah Kreidler
 *
 */
public class TreeItemIterator
{
    /**
     * Visit each node in the tree using depth first search
     * @param tree tree widget
     * @param action action to perform at each node
     */
    public static void traverseDepthFirst(Tree tree, TreeItemAction action)
    {
        if (tree != null && action != null)
        {
            TreeItem root = tree.getItem(0);
            traverseDepthFirst(root, action, 1, 0);
        }
    }
    
    /**
     * Recursive depth first search.
     * 
     * @param item starting TreeItem for the traversal
     * @param visitCount indicates this node is the ith visit in the traversal
     * @param parentVisitCount indicates the node's parent is the jth visit in the traversal
     */
    private static void traverseDepthFirst(TreeItem item, TreeItemAction action,
            int visitCount, int parentVisitCount)
    {
        if (item != null) {
            action.execute(item, visitCount, parentVisitCount);
            visitCount++;
            parentVisitCount++;
            List<TreeItem> children = fetchChildren(item);
            if (children != null) {
                for(TreeItem child: children) {
                    traverseDepthFirst(child, action, visitCount++,  parentVisitCount);
                }
            }
        }
    }
    
    /**
     * Get the children of this tree item
     * @param item tree item
     * @return list of children
     */
    private static List<TreeItem> fetchChildren(TreeItem item)
    {
        List<TreeItem> children = null;
        if (item != null) {
            children = new ArrayList<TreeItem>();
            for (int i = 0; i < item.getChildCount(); ++i)
                children.add(item.getChild(i));
        }
        return children;
    }

}
