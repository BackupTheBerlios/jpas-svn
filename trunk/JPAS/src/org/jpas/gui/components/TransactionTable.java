/**
 * Created on Oct 11, 2004 - 6:51:29 PM
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
package org.jpas.gui.components;

import javax.swing.*;

import java.awt.*;

import javax.swing.table.*;

import org.jpas.gui.editors.TransactionTableCellEditor;
import org.jpas.gui.model.TransactionTableModel;
import org.jpas.gui.renderers.TransactionTableCellRenderer;
import org.jpas.model.Account;
import org.jpas.model.Transaction;

/**
 * @author Justin W Smith
 *
 */
public class TransactionTable extends JTable
{
	private final Dimension dim;
	private final TransactionTableCellRenderer transactionRenderer;
	private final Color topColor = new Color(213, 255, 213);
	
    /**
     * 
     */
    public TransactionTable(final Account account)
    {
    	transactionRenderer = new TransactionTableCellRenderer(account);
    	this.setModel(new TransactionTableModel(account));
        this.setDefaultRenderer(Transaction.class, transactionRenderer);
        this.setDefaultEditor(Transaction.class, new TransactionTableCellEditor(account));
        this.setRowHeight(36);
        this.setSurrendersFocusOnKeystroke(true);
        
        dim = transactionRenderer.getPreferredSize();
    }

    public Dimension getPreferredSize()
    {
    	return new Dimension(dim.width, dim.height * getModel().getRowCount());
    }
    
    public Dimension getMinimumSize()
    {
    	return getPreferredSize();
    }

    public void paintComponent(final Graphics g)
    {
        if(isOpaque())
        {
	    	final Graphics sg = g.create();
	    	
	    	final Rectangle rect = getBounds();
	    	sg.setColor(getBackground());
	    	sg.fillRect(0, 0, rect.width, rect.height);
	    	
	    	final int rowHeight = getRowHeight();
	    	final int halfHeight = rowHeight/2;
	    	//sg.setColor(topColor);
	    	
	    	//final JPanel panel = (JPanel)transactionRenderer.getTableCellRendererComponent(this, null, false, false, 0, 0);
    	    //panel.setDebugGraphicsOptions(DebugGraphics.LOG_OPTION);
    	    
    	    //paintCells(g);

    	    for(int y = 0; y < rect.height; y += rowHeight)
	    	{
    	        final Graphics cg = sg.create(0, y, rect.width, rowHeight);
	    	    cg.setClip(0, 0, rect.width, rowHeight);
	    	    cg.setColor(topColor);
	    	    //panel.setBounds(0, y, rect.width, rowHeight);
	    	    //panel.getUI().update(g, panel);
	    	    cg.fillRect(0, 0, rect.width, halfHeight);
	    	}
        }

    	if (ui != null) {
            Graphics scratchGraphics = (g == null) ? null : g.create();
            try {
                ui.paint(scratchGraphics, this);
            }
            finally {
                scratchGraphics.dispose();
            }
        }

    }

    private void paintCells(final Graphics g)
    {
        //final Rectangle bounds = getBounds();
        Rectangle cellRect = getCellRect(0, 0, false);
        CellRendererPane rendererPane = (CellRendererPane)getComponent(0);
        for(int row = 0; row < 15/*cellRect.y <= bounds.y*/; row++) 
	    {
	        cellRect = getCellRect(row, 0, false);
	        final Graphics sg = g.create(cellRect.x, cellRect.y, cellRect.width, cellRect.height);
	        
            Component component = prepareRenderer(transactionRenderer, row, 0);
            
            //rendererPane.paintComponent( sg, component, this, 0, 0, cellRect.width, cellRect.height, true);
            
            //component.paint(sg);
	    }
    }

    
    public static void main(String[] args)
    {
    }
}
