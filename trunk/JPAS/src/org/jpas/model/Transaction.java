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
import org.jpas.da.*;
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
    
    Integer accountID;
    String payee;
    String memo;
    String num;
    Date date;

    static Transaction getTransactionForID(final Integer id)
    {
        synchronized(transactionCache)
        {
	    	Transaction trans = transactionCache.get(id);
	    	if(trans == null)
	    	{
	    		trans = new Transaction(id);
	    		transactionCache.put(id, trans);
	    	}
	    	return trans;
        }
    }
    
    public static Transaction createTransaction(final Account account, final String payee, final String memo, final String num, final Date date)
    {
    	return getTransactionForID(
    			TransactionDA.getInstance().createTransaction( account.id, payee, memo, num, date));
    }

    private Transaction(final Integer id)
    {
    	this.id = id;
    }
    
    public void delete()
	{
	    delete(true);
	}
	
	void delete(final boolean callDA)
    {
        synchronized(this)
        {
            synchronized(transactionCache)
            {
	    	    if(callDA)
	    	    {
	    	        TransactionDA.getInstance().deleteTransaction(id);
	    	    }
            	transactionCache.remove(id);
            }
        	isDeleted = true;
        }
        announceDelete();
    }
    
    void announceDelete()
    {
        final JpasDataChange<Transaction> change = new JpasDataChange.Delete<Transaction>(this);
        observable.notifyObservers(change);
        notifyObservers(change);
    }
    
    void announceModify()
    {
        final JpasDataChange<Transaction> change = new JpasDataChange.Modify<Transaction>(this);
        observable.notifyObservers(change);
        notifyObservers(change);
    }
    
    void amountChanged()
    {
        announceModify();
        Category.getCategoryForID(accountID).amountChanged();
        Account.getAccountForID(accountID).amountChanged();
    }
    
    private void loadData()
    {
    	assert(!isDeleted);
    	TransactionDA.getInstance().loadTransaction(id, new TransactionDA.TransactionHandler()
    		{
				public void setData(final Integer accountId, final String payee, final String memo, final String num, final Date date)
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
    
    public synchronized Account getAccount()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
        return Account.getAccountForID(accountID);
    }
    
    public synchronized String getPayee()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
    	return payee;
    }
    
    public synchronized String getMemo()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
    	return memo;
    }

    public synchronized String getNum()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
    	return num;
    }

    public synchronized Date getDate()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
    	return date;
    }
    
    public synchronized TransactionTransfer[] getTransfers()
    {
        final Integer[] accountIDs = TransAccountMappingDA.getInstance().getAllTransAccountTranfers(id);
        final TransactionTransfer[] ttArray = new TransactionTransfer[accountIDs.length];
        for(int i = 0; i < accountIDs.length; i++)
        {
            ttArray[i] = TransactionTransfer.getTransactionTransferforIDs(id, accountIDs[i]);
        }
        
        return ttArray;
    }
    
    public synchronized TransactionTransfer addTransfer(final Category category, final long amount)
    {
        synchronized(this)
        {
	        assert(!isDeleted);
	        if(TransAccountMappingDA.getInstance().doesTransAccountTransferExist(id, category.id))
	        {
	            TransAccountMappingDA.getInstance().updateTransAccountMapping(id, category.id, amount);
	        }
	        else
	        {
	            TransAccountMappingDA.getInstance().createTransAccountMapping(id, category.id, amount);
	        }
	        return TransactionTransfer.getTransactionTransferforIDs(id, category.id);
        }
    }
    
    public void setPayee(final String payee)
    {
        synchronized(this)
        {
	        assert(!isDeleted);
	        TransactionDA.getInstance().updateTransactionPayee(id, payee);
	        if (isLoaded)
	        {
	            loadData();
	        }
        }
    }

    public void setMemo(final String memo)
    {
        synchronized(this)
        {
	        assert (!isDeleted);
	        TransactionDA.getInstance().updateTransactionMemo(id, memo);
	        if (isLoaded)
	        {
	            loadData();
	        }
        }
    }

    public void setNum(final String num)
    {
        synchronized(this)
        {
	        assert (!isDeleted);
	        TransactionDA.getInstance().updateTransactionMemo(id, num);
	        if (isLoaded)
	        {
	            loadData();
	        }
        }
    }
    
    public void setDate(final Date date)
    {
        synchronized(this)
        {
	        assert (!isDeleted);
	        TransactionDA.getInstance().updateTransactionDate(id, date);
	        if (isLoaded)
	        {
	            loadData();
	        }
        }
    }
    
    public synchronized long getAmount()
    {
        return TransAccountMappingDA.getInstance().getTransactionAmount(id);
    }
    
    public synchronized boolean isDeleted()
    {
        return isDeleted;
    }
    
    public synchronized boolean isLoaded()
    {
        return isLoaded;
    }
}
