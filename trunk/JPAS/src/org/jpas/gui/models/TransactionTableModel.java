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
package org.jpas.gui.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.jpas.gui.data.TransactionData;
import org.jpas.model.*;
import org.jpas.util.JpasDataChange;
import org.jpas.util.JpasObservable;
import org.jpas.util.JpasObserver;

/**
 * @author Justin W Smith
 *
 */
public class TransactionTableModel extends AbstractTableModel
{
	private static final Logger defaultLogger = Logger.getLogger(TransactionTableModel.class);
	
    private Account account = null;
    
    
    private List<Transaction> transactionList = new ArrayList<Transaction>();
    /**
     * 
     */
    public TransactionTableModel()
    {
        Transaction.getObservable().addObserver(new JpasObserver<Transaction>()
                {
            		public void update(final JpasObservable<Transaction> observable, final JpasDataChange<Transaction> change)
            		{
            		    if(account != null && change.getValue().affects(account))
            		    {
            		        loadData();
            		    }
            		}
                });
    }

    private void loadData()
    {
        transactionList.clear();
        final Transaction[] transArray = Transaction.getAllTransactionsAffecting(account);
        Arrays.sort(transArray, Transaction.getDateComparator());
	    transactionList.addAll(Arrays.asList(transArray));
        fireTableStructureChanged();
    }
    
    public void setAccount(final Account account)
    {
        this.account = account;
        loadData();
    }
    
    public Account getAccount()
    {
        return account;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount()
    {
        return transactionList.size() +1;
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
        return rowIndex <= transactionList.size();
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
		final TransactionData transData = (TransactionData)aValue;
		if(rowIndex >= transactionList.size())
		{
			if(transData.getWithdraw() > 0 || transData.getDeposit() > 0)
			{
				final Transaction trans = Transaction.createTransaction(getAccount(), transData.getPayee(), transData.getMemo(), transData.getNum(), transData.getDate());
				trans.addTransfer( transData.getCategories()[0], transData.getWithdraw() - transData.getDeposit());
			}
			return;
		}
		final Transaction trans = transactionList.get(rowIndex);
		trans.set(transData.getPayee(), transData.getMemo(), transData.getNum(), transData.getDate());
		
		if(trans.getAccount().equals(account))
		{
			final Category[] categories = transData.getCategories();
			
			// Ignore the amount on a split transaction
			if(categories.length == 1)
			{
				defaultLogger.debug("Clearing all transfers");
				final TransactionTransfer[] transfers = trans.getAllTransfers();
				for(int i = 0; i < transfers.length; i++)
				{
					transfers[i].delete();
				}
				defaultLogger.debug("Adding new transfer");
				trans.addTransfer( transData.getCategories()[0], transData.getWithdraw() - transData.getDeposit());
				return;
			}
			defaultLogger.debug("Ignoring the amount b/c it is a split transaction?");
		}
		else
		{
			// Should only affect the tranfer to this account
			final TransactionTransfer[] transfers = trans.getAllTransfers();
			for(int i = 0; i < transfers.length; i++)
			{
				//System.out.println("To account:" + transfers[i].getCategory().getName());
				if(transfers[i].getCategory().equals(Category.getCategoryForAccount(account)))
				{
					defaultLogger.debug("Setting amount:");
					transfers[i].setAmount(transData.getDeposit() - transData.getWithdraw());
					return;
				}
			}
			// TODO: Should this even happen?
			defaultLogger.debug("Should this even be possible?");
			trans.addTransfer( transData.getCategories()[0], transData.getWithdraw() - transData.getDeposit());
		}
    }

    public static void main(String[] args)
    {
    }
}
