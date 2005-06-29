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

import java.awt.*;

import javax.swing.JTable;

import org.jpas.gui.editors.TransactionTableCellEditor;
import org.jpas.gui.models.TransactionTableModel;
import org.jpas.gui.renderers.TransactionTableCellRenderer;
import org.jpas.gui.util.Appearance;
import org.jpas.model.Account;
import org.jpas.model.Transaction;

/**
 * @author Justin W Smith
 *
 */
public class TransactionTable extends JTable
{
	private final Dimension dim;
	private final TransactionTableModel model = new TransactionTableModel();
	private final TransactionTableCellRenderer transactionRenderer;
	private final TransactionTableCellEditor transactionEditor;
	//private final Color topColor = new Color(231, 255, 231);
	//private final Color bottomColor = new Color(229, 227, 208);
	
	private final int[] columnWidths;
	private final int[] rowHeights;

    /**
     * 
     */
    public TransactionTable()
    {
        this.columnWidths = createColumnWidths();
        this.rowHeights = createRowHeights();
    	transactionRenderer = new TransactionTableCellRenderer(columnWidths, rowHeights);
    	transactionEditor = new TransactionTableCellEditor(columnWidths, rowHeights);
    	getTableHeader().setReorderingAllowed(false);
    	this.setModel(model);
        this.setDefaultRenderer(Transaction.class, transactionRenderer);
        this.setDefaultEditor(Transaction.class, transactionEditor);
        this.setRowHeight(total(rowHeights) + 1);
        this.setSurrendersFocusOnKeystroke(true);
        this.setBackground(Appearance.getInstance().getTransactionRowColor2());
        dim = transactionRenderer.getPreferredSize();
    }

    public int[] createColumnWidths()
    {
        return new int[]{105, 85, 125, 85, 85, 95};
    }
    
    public int[] createRowHeights()
    {
        return new int[]{18, 18};
    }
    
    private int total(final int[] values)
    {
        int total = 0;
        for(int i = 0; i < values.length; i++)
        {
            total += values[i];
        }
        return total;
    }
    
    public void setAccount(final Account account)
    {
        assert(account != null);
        model.setAccount(account);
        transactionRenderer.setAccount(account);
        transactionEditor.setAccount(account);
    }
    
    public Dimension getPreferredSize()
    {
    	return new Dimension(dim.width, getRowHeight() * getModel().getRowCount());
    }
    
    public Dimension getMinimumSize()
    {
    	return getPreferredSize();
    }

    public void paintComponent(final Graphics g)
    {
        if(isOpaque() && (g != null))
        {
            final Graphics sg = g.create();
            try
            {
		    	final Rectangle rect = getBounds();
		    	sg.setColor(getBackground());
		    	sg.fillRect(0, 0, rect.width, rect.height);
		    	
		    	final int rowHeight = getRowHeight();
		    	final int halfHeight = rowHeight/2;
	
	    	    for(int y = 0; y < rect.height; y += rowHeight)
		    	{
	    	        sg.setColor(Appearance.getInstance().getTransactionRowColor1());
		    	    sg.fillRect(0, y, rect.width, halfHeight);
		    	    sg.setColor(Color.gray);
		    	    sg.drawLine(0, y, rect.width, y);
		    	    final int halfY = y + halfHeight;
		    	    sg.drawLine(0, halfY, rect.width, halfY);
		    	}
	    	    int fromLeft = 0;
	    	    for(int i = 0; i < 2; i++)
	    	    {
    	            fromLeft += columnWidths[i];
		    	    sg.drawLine(fromLeft, 0, fromLeft, rect.height);
		    	    sg.drawLine(fromLeft-1, 0, fromLeft-1, rect.height);
	    	    }
	    	    
	    	    
	    	    int fromRight = rect.width < dim.width 
	    	    				? rect.width - dim.width
	    	    				: 0;
	    	    for(int i = columnWidths.length -1; i >= 3; i--)
	    	    {
    	            fromRight += columnWidths[i];
    	            final int x = rect.width - fromRight;
		    	    sg.drawLine(x, 0, x, rect.height);
		    	    sg.drawLine(x-1, 0, x-1, rect.height);
	    	    }
	    	    
            }
            finally
            {
                sg.dispose();
            }
        }

    	if (ui != null) 
    	{
            final Graphics sg = (g == null) ? null : g.create();
            try 
            {
                ui.paint(sg, this);
            }
            finally {
                sg.dispose();
            }
        }

    }

    public static void main(String[] args)
    {
    }
}
