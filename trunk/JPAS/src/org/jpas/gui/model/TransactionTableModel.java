/**
 * Created on Oct 11, 2004 - 7:01:31 PM
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
package org.jpas.gui.model;

import javax.swing.SwingUtilities;
import javax.swing.event.*;
import javax.swing.table.*;
import org.jpas.util.*;
import org.jpas.model.*;
import java.util.*;

/**
 * @author Justin W Smith
 *
 */
public class TransactionTableModel extends AbstractTableModel
{
    private final Account account;
    
    
    private List<Transaction> transactionList = new ArrayList<Transaction>();
    /**
     * 
     */
    public TransactionTableModel(final Account account)
    {
        this.account = account;
        
        Transaction.getObservable().addObserver(new JpasObserver<Transaction>()
                {
            		public void update(final JpasObservable<Transaction> observable, final JpasDataChange<Transaction> change)
            		{
            		    if(change.getValue().affects(account))
            		    {
            		        loadData();
            		    }
            		}
                });
        loadData();
    }

    private void loadData()
    {
        final Transaction[] transArray = Transaction.getAllTransactionsAffecting(account);
        Arrays.sort(transArray, Transaction.getDateComparator());
        SwingUtilities.invokeLater(new Runnable()
                {
            		public void run()
            		{
            		    transactionList.addAll(Arrays.asList(transArray));
          		        fireTableStructureChanged();
            		}
                });
    }
    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount()
    {
        return Math.max(transactionList.size(), 10);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount()
    {
        // TODO Auto-generated method stub
        return 1;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    public String getColumnName(int columnIndex)
    {
        // TODO Auto-generated method stub
        return "Transaction";
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnClass(int)
     */
    public Class< ? > getColumnClass(int columnIndex)
    {
        return Transaction.class;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        return rowIndex >= transactionList.size() ? null : transactionList.get(rowIndex);
    }

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        // TODO Auto-generated method stub
    }

    public static void main(String[] args)
    {
    }
}
