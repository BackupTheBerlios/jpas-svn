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

import javax.swing.JTable;

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
    /**
     * 
     */
    public TransactionTable(final Account account)
    {
    	this.setModel(new TransactionTableModel(account));
        this.setDefaultRenderer(Transaction.class, new TransactionTableCellRenderer());
        this.setDefaultEditor(Transaction.class, new TransactionTableCellEditor(account));
        this.setRowHeight(36);
        this.setSurrendersFocusOnKeystroke(true);
    }

    public static void main(String[] args)
    {
    }
}
