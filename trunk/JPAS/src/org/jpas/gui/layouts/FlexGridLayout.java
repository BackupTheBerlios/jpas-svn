/**
 * Created on Oct 25, 2004 - 9:05:05 PM
 * 
 * Title: JPAS
 * Description: Java based Personal Accounting System
 * Copyright: Copyright (c) 2004 Justin W Smith
 * @author Justin W Smith
 * @version 1.0
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.jpas.gui.layouts;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

/**
 * @author Justin W Smith
 *
 */
public class FlexGridLayout implements LayoutManager
{
    final int[] rowHeights;
    final int[] columnWidths;
    
    final boolean[] flexColumns;
    final boolean[] flexRows;
    
    int flexColumnCount = 0;
    int flexRowCount = 0;
    
    int currentRow  = 0;
    int currentColumn = 0;
    
    final Dimension dim;
    
    /**
     * 
     */
    public FlexGridLayout(final int[] rowHeights, final int[] columnWidths)
    {
        this.rowHeights = (int[])rowHeights.clone();
        this.columnWidths = (int[])columnWidths.clone();
        
        dim = new Dimension( total(columnWidths), total(rowHeights));
        flexColumns = new boolean[columnWidths.length];
        flexRows = new boolean[rowHeights.length];
    }

    public void setFlexColumn(final int column, final boolean flex)
    {
        flexColumns[column] = flex;
        flexColumnCount = count(flexColumns);
    }

    public void setFlexRow(final int row, final boolean flex)
    {
        flexRows[row] = flex;
        flexRowCount = count(flexRows);
    }
    
    private static int count(final boolean[] array)
    {
        int total = 0;
        for(int i = 0; i < array.length; i++)
        {
            if(array[i])
            {
                total++;
            }
        }
        return total;
    }
    
    private static int total(final int[] array)
    {
        int total = 0;
        for(int i = 0; i < array.length; i++)
        {
            total += array[i];
        }
        return total;
    }
    
    /* (non-Javadoc)
     * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
     */
    public void addLayoutComponent(final String name, final Component comp)
    {
        throw new UnsupportedOperationException("Cannot remove a component from this layout!");
    }

    /* (non-Javadoc)
     * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
     */
    public void removeLayoutComponent(final Component comp)
    {
        throw new UnsupportedOperationException("Cannot remove a component from this layout!");
    }

    /* (non-Javadoc)
     * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
     */
    public Dimension preferredLayoutSize(final Container parent)
    {
        return dim;
    }

    /* (non-Javadoc)
     * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
     */
    public Dimension minimumLayoutSize(final Container parent)
    {
        return dim;
    }

    /* (non-Javadoc)
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    public void layoutContainer(final Container parent)
    {
        final Component[] components = parent.getComponents();
        final Dimension parentDim = parent.getSize();
        
        int flexWidth = parentDim.width - dim.width;
        int flexHeight = parentDim.height - dim.height;
        int flexColumnsLeft = flexColumnCount;
        int flexRowsLeft = flexRowCount;

        final int origFlexWidth = flexWidth;
        
        int x = 0;
        int y = 0;
        int k = 0;
        for(int i = 0; i < rowHeights.length; i++)
        {
            int height = rowHeights[i];
            if(flexRows[i] && flexHeight > 0)
            {
                int addedHeight = flexHeight / flexRowsLeft;
                flexHeight -= addedHeight;
                flexRowsLeft--;
                height += addedHeight;
            }

            for(int j = 0; j < columnWidths.length; j++)
            {
                int width = columnWidths[j];
                if(flexColumns[j] && flexWidth > 0)
                {
                    int addedWidth = flexWidth / flexColumnsLeft;
                    flexWidth -= addedWidth;
                    flexColumnsLeft--;
                    width += addedWidth;
                }
                
                final Component comp = k >= components.length ? null : components[k++];
                if(comp != null)
                {
                    comp.setBounds(x, y, width, height);
                }
                x += width;
            }
            x = 0;
            y += height; 
            flexWidth = origFlexWidth;
            flexColumnsLeft = flexColumnCount;
        }
    }

    public static void main(String[] args)
    {
    }
}
