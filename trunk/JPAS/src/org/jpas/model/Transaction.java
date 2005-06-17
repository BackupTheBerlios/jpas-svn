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
import org.jpas.util.JpasObserver;
import org.jpas.util.WeakValueMap;

public class Transaction extends JpasObservable<Transaction> implements JpasObserver<TransactionTransfer>
{
    private static Logger defaulLogger = Logger.getLogger(Transaction.class);

    private static WeakValueMap<Integer, Transaction> transactionCache = new WeakValueMap<Integer, Transaction>();
    private static JpasObservable<Transaction> observable = new JpasObservable<Transaction>();
    private static Comparator<Transaction> dateComparator = new Comparator<Transaction>()
    {
        public int compare(final Transaction a, final Transaction b)
        {
            final int dateComp = a.getDate().compareTo(b.getDate());
            return dateComp != 0 ? dateComp : a.id.intValue() - b.id.intValue();
        }
    };

    private boolean isLoaded = false;
    private boolean isDeleted = false;
    private boolean isModified = false;

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
        return dateComparator;
    }

    public static JpasObservable<Transaction> getObservable()
    {
        return observable;
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
                //TODO balance += trans[i].getTransfer(cat).getAmount();
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

    public long getAmount()
    {
    	assert(!isDeleted);
        if (!amountLoaded)
        {
            amount = TransAccountMappingDA.getInstance().getTransactionAmount(id);
            amountLoaded = true;
        }
        return amount;
    }

    private void announceAmountChange()
    {
        final JpasDataChange<Transaction> myChange = new JpasDataChange.AmountModify<Transaction>(this);
        amountLoaded = false;
        if(true)
        {
            observable.notifyObservers(myChange);
        }
        notifyObservers(myChange);
    }
    
	public void update(JpasObservable<TransactionTransfer> ob, JpasDataChange<TransactionTransfer> change) 
	{
    	assert(!isDeleted);
        if(change instanceof JpasDataChange.Delete)
        {
            ob.deleteObserver(this);
        }
        announceAmountChange();
	}
    
    public void commit(final boolean broadcastForAll)
    {
    	if(isModified)
    	{
    		final JpasDataChange<Transaction> change;
			if(isDeleted)
			{
                final TransactionTransfer[] transfers = TransactionTransfer.getTransfersForTransaction(this);
                for(int i = 0; i < transfers.length; i++)
                {
                    transfers[i].deleteObserver(this);
                    transfers[i].delete();
                    transfers[i].commit(false);
                }
                
				change = new JpasDataChange.Delete<Transaction>(this);
		        TransactionDA.getInstance().deleteTransaction(id);
			}
			else
			{
				change = new JpasDataChange.Modify<Transaction>(this);
				TransactionDA.getInstance().updateTransaction(id, payee, memo, num,
                        new java.sql.Date(date.getTime()));

			}
			if(broadcastForAll)
			{
				observable.notifyObservers(change);
			}
	        notifyObservers(change);
    	}
    }
    
    private void setBalance(final long balance)
    {
        this.balance = balance;
    }

    public long getBalance()
    {
        return balance;
    }

    public void delete()
    {
        if(!isDeleted)
        {
        	isDeleted = true;
        	isModified = true;
        }
    }

    public Account getAccount()
    {
    	assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        return Account.getAccountForID(accountID);
    }

    public String getPayee()
    {
    	assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        return payee;
    }

    public String getMemo()
    {
    	assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        return memo;
    }

    public String getNum()
    {
    	assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        return num;
    }

    public Date getDate()
    {
    	assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        return date;
    }
    
    public void setPayee(final String payee)
    {
        assert (!isDeleted);
        if(!isLoaded)
        {
        	loadData();
        }
        if(!this.payee.equals(payee))
        {
        	isModified = true;
        	this.payee = payee;
        }
    }

    public void setMemo(final String memo)
    {
        assert (!isDeleted);
        if(!isLoaded)
        {
        	loadData();
        }
        if(!this.memo.equals(memo))
        {
        	isModified = true;
        	this.memo = memo;
        }
    }

    public void setNum(final String num)
    {
        assert (!isDeleted);
        if(!isLoaded)
        {
        	loadData();
        }
        if(!this.num.equals(num))
        {
        	isModified = true;
        	this.num = num;
        }
    }

    public void setDate(final Date date)
    {
        assert (!isDeleted);
        if(!isLoaded)
        {
        	loadData();
        }
        if(!this.date.equals(date))
        {
        	isModified = true;
        	this.date = date;
        }
    }

    
    
    public boolean isDeleted()
    {
        return isDeleted;
    }

    public boolean isLoaded()
    {
        return isLoaded;
    }

    public boolean isModified()
    {
    	return isModified;
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
