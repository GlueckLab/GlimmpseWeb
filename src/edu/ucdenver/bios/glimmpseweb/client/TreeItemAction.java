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

import com.google.gwt.user.client.ui.TreeItem;

/**
 * Interface for performing actions while traversing a
 * GWT Tree widget.  For use with a TreeItemIterator.
 * 
 * @see TreeItemIterator
 * @author Sarah Kreidler
 */
public interface TreeItemAction
{
    /**
     * Action to perform when visiting the specified tree node
     * @param item 
     * @param visitCount counter indicating that this is the ith node visited 
     * during the traversal 
     * @param parentVisitCount counter indicating that the node's parent is
     * the jth node visited during the traversal
     */
    public void execute(TreeItem item, int visitCount, int parentVisitCount);
}
