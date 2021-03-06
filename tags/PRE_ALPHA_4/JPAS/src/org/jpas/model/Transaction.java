/**
 * Created on Sep 25, 2004
 * 
 * Title: JPAS Description: Java based Personal Accounting System Copyright: Copyright (c) 2004 Justin W Smith
 * 
 * @author Justin W Smith
 * @version 1.0
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.jpas.model;

import java.util.Date;
import java.util.Comparator;

import org.apache.log4j.Logger;
import org.jpas.da.TransAccountMappingDA;
import org.jpas.da.TransactionDA;
import org.jpas.util.JpasDataChange;
import org.jpas.util.JpasObservable;
import org.jpas.util.WeakValueMap;

public class Transaction extends JpasObservable<Transaction>
{
    private static Logger defaulLogger = Logger.getLogger(Transaction.class);

    private static WeakValueMap<Integer, Transaction> transactionCache = new WeakValueMap<Integer, Transaction>();

    private static JpasObservable<Transaction> observable = new JpasObservable<Transaction>();

    public static JpasObservable<Transaction> getObservable()
    {
        return observable;
    }

    private boolean isDeleted = false;
    private boolean isLoaded = false;

    private long balance;

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
                final int dateComp = a.getDate().compareTo(b.getDate());
                return dateComp != 0 ? dateComp : a.id.intValue()
                                - b.id.intValue();
            }
        };
    }

    public static Transaction[] getAllTransactionsAffecting(final Account account)
    {
        final Integer[] ids = TransactionDA.getInstance()
                        .getAllAffectingTransactionIDs(account.id);
        final Transaction[] trans = new Transaction[ids.length];
        long balance = 0;
        for (int i = 0; i < ids.length; i++)
        {
            trans[i] = getTransactionForID(ids[i]);
            if (trans[i].getAccount().equals(account))
            {
                balance -= trans[i].getAmount();
            }
            else
            {
                final Category cat = Category.getCategoryForAccount(account);
                balance += trans[i].getTransfer(cat).getAmount();
            }
            trans[i].setBalance(balance);
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
                                                final String payee,
                                                final String memo,
                                                final String num,
                                                final Date date)
    {
        return getTransactionForID(TransactionDA.getInstance()
                        .createTransaction(account.id, payee, memo, num,
                                        new java.sql.Date(date.getTime())));
    }

    private Transaction(final Integer id)
    {
        this.id = id;
    }

    void setBalance(final long balance)
    {
        this.balance = balance;
    }

    public long getBalance()
    {
        return balance;
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

        final Integer[] accountIDs = TransAccountMappingDA.getInstance()
                        .getAllTranfersForTransaction(id);
        for (int i = 0; i < accountIDs.length; i++)
        {
            TransactionTransfer.getTransactionTransferforIDs(id, accountIDs[i])
                            .delete(true);
        }

        isDeleted = true;

        if (!internalCall)
        {
            Account.getAccountForID(accountID).amountChanged();
        }
    }

    public void announceDelete()
    {
        final JpasDataChange<Transaction> change = new JpasDataChange.Delete<Transaction>(
                        this);
        observable.notifyObservers(change);
        notifyObservers(change);
    }

    public void announceModify()
    {
        final JpasDataChange<Transaction> change = new JpasDataChange.Modify<Transaction>(
                        this);
        observable.notifyObservers(change);
        notifyObservers(change);
    }

    public void amountChanged()
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
                                                final String payee,
                                                final String memo,
                                                final String num,
                                                final java.sql.Date date)
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

    public TransactionTransfer[] getAllTransfers()
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

    public TransactionTransfer getTransfer(final Category cat)
    {
        if (affects(cat))
        {
            return TransactionTransfer.getTransactionTransferforIDs(id, cat.id);
        }
        return null;
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
        TransactionDA.getInstance().updateTransactionDate(id,
                        new java.sql.Date(date.getTime()));
        if (isLoaded)
        {
            loadData();
        }
    }

    public void set(final String payee, final String memo, final String num,
                    final Date date)
    {
        assert (!isDeleted);
        TransactionDA.getInstance().updateTransaction(id, payee, memo, num,
                        new java.sql.Date(date.getTime()));
        if (isLoaded)
        {
            loadData();
        }
    }

    public long getAmount()
    {
        if (!amountLoaded)
        {
            defaulLogger.debug("Loading amount");
            amount = TransAccountMappingDA.getInstance().getTransactionAmount(
                            id);
            amountLoaded = true;
        }
        defaulLogger.debug("Amount: " + amount);
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

    public boolean affects(final Category category)
    {
        return TransactionDA.getInstance().doesTransactionAffectAccount(id,
                        category.id);
    }

    public boolean affects(final Account account)
    {
        return TransactionDA.getInstance().doesTransactionAffectAccount(id,
                        account.id);
    }

}
