/**
 * Created on Sep 25, 2004
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

import java.sql.Date;
import java.util.Comparator;

import org.jpas.da.TransAccountMappingDA;
import org.jpas.da.TransactionDA;
import org.jpas.util.JpasDataChange;
import org.jpas.util.JpasObservable;
import org.jpas.util.WeakValueMap;

public class Transaction extends JpasObservable<Transaction>
{
    private static WeakValueMap<Integer, Transaction> transactionCache = new WeakValueMap<Integer, Transaction>();
    private static JpasObservable<Transaction> observable = new JpasObservable<Transaction>();

    public static JpasObservable<Transaction> getObservable()
    {
        return observable;
    }

    private boolean isDeleted = false;
    private boolean isLoaded = false;
    
    final Integer id;
    
    private Integer accountID;
    private String payee;
    private String memo;
    private String num;
    private Date date;

    private boolean amountLoaded = false;
    private long amount;
    
    public static Comparator<Transaction> getDateComparator()
    {
        return new Comparator<Transaction>()
        {
            public int compare(final Transaction a, final Transaction b)
            {
                final int dateComp = a.date.compareTo(b.date);
                return dateComp != 0 ? dateComp : a.id.intValue()
                        - b.id.intValue();
            }
        };
    }

    public static Transaction[] getAllTransactionsAffecting(
            final Account account)
    {
        final Integer[] ids = TransactionDA.getInstance()
                .getAllAffectingTransactionIDs(account.id);
        final Transaction[] trans = new Transaction[ids.length];
        for (int i = 0; i < ids.length; i++)
        {
            trans[i] = getTransactionForID(ids[i]);
        }
        return trans;
    }

    static Transaction getTransactionForID(final Integer id)
    {
        Transaction trans = transactionCache.get(id);
        if (trans == null)
        {
            trans = new Transaction(id);
            transactionCache.put(id, trans);
        }
        return trans;
    }

    public static Transaction createTransaction(final Account account,
            final String payee, final String memo, final String num,
            final Date date)
    {
        return getTransactionForID(TransactionDA.getInstance()
                .createTransaction(account.id, payee, memo, num, date));
    }

    private Transaction(final Integer id)
    {
        this.id = id;
    }

    public void delete()
    {
        delete(false);
    }

    void delete(final boolean internalCall)
    {
        if (!internalCall)
        {
            TransactionDA.getInstance().deleteTransaction(id);
        }
        transactionCache.remove(id);
        
        final Integer[] accountIDs = TransAccountMappingDA.getInstance().getAllTranfersForTransaction(id);
		for (int i = 0; i < accountIDs.length; i++)
		{
		    TransactionTransfer.getTransactionTransferforIDs(id, accountIDs[i]).delete(true);
		}

        isDeleted = true;
        
        announceDelete();
        if(!internalCall)
        {
	        Account.getAccountForID(accountID).amountChanged();
        }
    }

    private void announceDelete()
    {
        final JpasDataChange<Transaction> change = new JpasDataChange.Delete<Transaction>(
                this);
        observable.notifyObservers(change);
        notifyObservers(change);
    }

    private void announceModify()
    {
        final JpasDataChange<Transaction> change = new JpasDataChange.Modify<Transaction>(
                this);
        observable.notifyObservers(change);
        notifyObservers(change);
    }

    void amountChanged()
    {
        amountLoaded = false;
        announceModify();
        Category.getCategoryForID(accountID).amountChanged();
        Account.getAccountForID(accountID).amountChanged();
    }

    private void loadData()
    {
        assert (!isDeleted);
        TransactionDA.getInstance().loadTransaction(id,
                new TransactionDA.TransactionHandler()
                {
                    public void setData(final Integer accountId,
                            final String payee, final String memo,
                            final String num, final Date date)
                    {
                        Transaction.this.accountID = accountId;
                        Transaction.this.payee = payee;
                        Transaction.this.memo = memo;
                        Transaction.this.num = num;
                        Transaction.this.date = date;
                        isLoaded = true;
                    }
                });
    }

    public Account getAccount()
    {
        if (!isLoaded)
        {
            loadData();
        }
        return Account.getAccountForID(accountID);
    }

    public String getPayee()
    {
        if (!isLoaded)
        {
            loadData();
        }
        return payee;
    }

    public String getMemo()
    {
        if (!isLoaded)
        {
            loadData();
        }
        return memo;
    }

    public String getNum()
    {
        if (!isLoaded)
        {
            loadData();
        }
        return num;
    }

    public Date getDate()
    {
        if (!isLoaded)
        {
            loadData();
        }
        return date;
    }

    public TransactionTransfer[] getTransfers()
    {
        final Integer[] accountIDs = TransAccountMappingDA.getInstance()
                .getAllTranfersForTransaction(id);
        final TransactionTransfer[] ttArray = new TransactionTransfer[accountIDs.length];
        for (int i = 0; i < accountIDs.length; i++)
        {
            ttArray[i] = TransactionTransfer.getTransactionTransferforIDs(id,
                    accountIDs[i]);
        }
        return ttArray;
    }

    public TransactionTransfer addTransfer(final Category category,
            final long amount)
    {
        assert (!isDeleted);
        if (TransAccountMappingDA.getInstance().doesTransAccountTransferExist(
                id, category.id))
        {
            TransAccountMappingDA.getInstance().updateTAMAmount(id,
                    category.id, amount);
        }
        else
        {
            TransAccountMappingDA.getInstance().createTransAccountMapping(id,
                    category.id, amount);
        }
        return TransactionTransfer
                .getTransactionTransferforIDs(id, category.id);
    }

    public void setPayee(final String payee)
    {
        assert (!isDeleted);
        TransactionDA.getInstance().updateTransactionPayee(id, payee);
        if (isLoaded)
        {
            loadData();
        }
    }

    public void setMemo(final String memo)
    {
        assert (!isDeleted);
        TransactionDA.getInstance().updateTransactionMemo(id, memo);
        if (isLoaded)
        {
            loadData();
        }
    }

    public void setNum(final String num)
    {
        assert (!isDeleted);
        TransactionDA.getInstance().updateTransactionMemo(id, num);
        if (isLoaded)
        {
            loadData();
        }
    }

    public void setDate(final Date date)
    {
        assert (!isDeleted);
        TransactionDA.getInstance().updateTransactionDate(id, date);
        if (isLoaded)
        {
            loadData();
        }
    }

    public long getAmount()
    {
        if(!amountLoaded)
        {
            amount = TransAccountMappingDA.getInstance().getTransactionAmount(id);
            amountLoaded = true;
        }
        return amount;
    }

    public boolean isDeleted()
    {
        return isDeleted;
    }

    public boolean isLoaded()
    {
        return isLoaded;
    }

    public boolean affects(final Account account)
    {
        return TransactionDA.getInstance().doesTransactionAffectAccount(id, account.id);
    }
}
