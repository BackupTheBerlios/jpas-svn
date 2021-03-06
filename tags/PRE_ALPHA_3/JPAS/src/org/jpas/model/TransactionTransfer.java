/**
 * Created on Sep 26, 2004
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
package org.jpas.model;

import org.jpas.da.TransAccountMappingDA;
import org.jpas.util.JpasDataChange;
import org.jpas.util.JpasObservable;
import org.jpas.util.WeakValueMap;

/**
 * @author Justin W Smith
 *  
 */
public class TransactionTransfer extends JpasObservable<TransactionTransfer>
{
    private static WeakValueMap<Integer, WeakValueMap<Integer, TransactionTransfer>> transTransferCache = new WeakValueMap<Integer, WeakValueMap<Integer, TransactionTransfer>>();
    private static JpasObservable<TransactionTransfer> observable = new JpasObservable<TransactionTransfer>();

    public static JpasObservable<TransactionTransfer> getObservable()
    {
        return observable;
    }

    private boolean isDeleted = false;
    private boolean isLoaded = false;
    final Integer transactionID;
    Integer accountID;
    private long amount;

    static TransactionTransfer getTransactionTransferforIDs(
            final Integer transactionID, final Integer accountID)
    {
        WeakValueMap<Integer, TransactionTransfer> map = transTransferCache
                .get(transactionID);
        if (map == null)
        {
            map = new WeakValueMap<Integer, TransactionTransfer>();
            transTransferCache.put(transactionID, map);
        }
        TransactionTransfer tt = map.get(accountID);
        if (tt == null)
        {
            tt = new TransactionTransfer(transactionID, accountID);
            map.put(accountID, tt);
        }
        return tt;
    }

    private TransactionTransfer(final Integer transactionID,
            final Integer accountID)
    {
        this.transactionID = transactionID;
        this.accountID = accountID;
    }

    public Category getCategory()
    {
        return Category.getCategoryForID(accountID);
    }

    public Transaction getTransaction()
    {
        return Transaction.getTransactionForID(transactionID);
    }

    private void loadData()
    {
        assert (!isDeleted);
        TransAccountMappingDA.getInstance().loadTransAccountMapping(
                transactionID, accountID,
                new TransAccountMappingDA.TransAccountTranferHandler()
                {
                    public void setData(final long amount)
                    {
                        TransactionTransfer.this.amount = amount;
                        isLoaded = true;
                    }
                });
    }

    public long getAmount()
    {
        if (!isLoaded)
        {
            loadData();
        }
        return amount;
    }

    private void announceDelete()
    {
        final JpasDataChange<TransactionTransfer> change = new JpasDataChange.Delete<TransactionTransfer>(
                this);
        observable.notifyObservers(change);
        notifyObservers(change);
    }

    private void announceModify()
    {
        final JpasDataChange<TransactionTransfer> change = new JpasDataChange.Modify<TransactionTransfer>(
                this);
        observable.notifyObservers(change);
        notifyObservers(change);
    }

    public void setAmount(final long amount)
    {
        assert (!isDeleted);
        TransAccountMappingDA.getInstance().updateTAMAmount(
                transactionID, accountID, amount);
        if (isLoaded)
        {
            loadData();
        }
        final Transaction trans = Transaction
                .getTransactionForID(transactionID);
        announceModify();
        trans.amountChanged();
        Category.getCategoryForID(accountID).amountChanged();
        Account.getAccountForID(accountID).amountChanged();
    }

    void setCategory(final Category category)
    {
        assert (!isDeleted);
        TransAccountMappingDA.getInstance().updateTAMAccount(
                transactionID, accountID, category.id);
        this.accountID = category.id;
        
        if (isLoaded)
        {
            loadData();
        }

        announceModify();
        
        Transaction.getTransactionForID(transactionID).amountChanged();
        Category.getCategoryForID(accountID).amountChanged();
        Account.getAccountForID(accountID).amountChanged();
    }

    
    public boolean isDeleted()
    {
        return isDeleted;
    }

    public boolean isLoaded()
    {
        return isLoaded;
    }

    public void delete()
    {
        delete(false);
    }
    
    void delete(final boolean internalCall)
    {
        if (!internalCall)
        {
            TransAccountMappingDA.getInstance().deleteTransAccountMapping(
                    transactionID, accountID);
        }
        
        final WeakValueMap<Integer, TransactionTransfer> map = transTransferCache
                .get(transactionID);
        map.remove(accountID);
        if (map.size() == 0)
        {
            transTransferCache.remove(transactionID);
        }
        
        isDeleted = true;
        announceDelete();
        
        if (!internalCall)
        {
            Transaction.getTransactionForID(transactionID).amountChanged();
        }
        
        Category.getCategoryForID(accountID).amountChanged();
        Account.getAccountForID(accountID).amountChanged();
    }

    public static void main(String[] args)
    {
    }
}
