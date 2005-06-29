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

import java.util.Comparator;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jpas.da.DAFactory;
import org.jpas.da.TransactionDA;
import org.jpas.util.*;

public class Transaction extends JpasObservableImpl
{
    private static Logger defaulLogger = Logger.getLogger(Transaction.class);

    private static Comparator<Transaction> dateComparator = new Comparator<Transaction>()
    {
        public int compare(final Transaction a, final Transaction b)
        {
            final int dateComp = a.getDate().compareTo(b.getDate());
            return dateComp != 0 ? dateComp : a.id.intValue() - b.id.intValue();
        }
    };
	
    public static Comparator<Transaction> getDateComparator()
    {
        return dateComparator;
    }

    static
    {
    	ModelFactory.getInstance().getTransactionTransferObservable().addObserver(new JpasObserver()
		{
			public void update(JpasObservable observable, JpasDataChange change) 
			{
				if(change instanceof JpasDataChange.Delete || change instanceof JpasDataChange.AmountModify)
				{
					final TransactionTransfer transfer = (TransactionTransfer)change.getValue();
					ModelFactory.getInstance().getTransactionForID(transfer.transactionID).amountChanged();
				}
			}
		});
    }

    
    
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


    Transaction(final Integer id)
    {
        this.id = id;
    }

    private void loadData()
    {
        assert (!isDeleted);
        DAFactory.getTransactionDA().loadTransaction(id,
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
            amount = DAFactory.getTransAccountMappingDA().getTransactionAmount(id);
            amountLoaded = true;
        }
        return amount;
    }

    private void amountChanged()
    {
        if(!isDeleted)
        {
            final JpasDataChange myChange = new JpasDataChange.AmountModify(this);
            amountLoaded = false;
            notifyObservers(myChange);
        }
    }
	
    public void commit()
    {
    	if(isModified)
    	{
    		final JpasDataChange change;
			if(isDeleted)
			{
                final TransactionTransfer[] transfers = ModelFactory.getInstance().getTransfersForTransaction(this);
                for(int i = 0; i < transfers.length; i++)
                {
                    transfers[i].delete();
                    transfers[i].commit();
                }
				change = new JpasDataChange.Delete(this);
                DAFactory.getTransactionDA().deleteTransaction(id);
                isModified = false;
                notifyObservers(change);
		        deleteObservers();
			}
			else
			{
				change = new JpasDataChange.Modify(this);
                DAFactory.getTransactionDA().updateTransaction(id, payee, memo, num,
                        new java.sql.Date(date.getTime()));
				isModified = false;
                notifyObservers(change);
			}
    	}
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
        return ModelFactory.getInstance().getAccountImplForID(accountID);
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
        return DAFactory.getTransactionDA().doesTransactionAffectAccount(id, ((AccountImpl)category).id);
    }

    public boolean affects(final Account account)
    {
        return DAFactory.getTransactionDA().doesTransactionAffectAccount(id, ((AccountImpl)account).id);
    }
}
