/*
 * Created on Sep 25, 2004
 *
 * Title: JPAS
 * Description: Java based Personal Accounting System
 * Copyright: Copyright (c) 2004
 * License: Distributed under the terms of the GPL v2
 * @author Justin Smith
 * @version 1.0
 * 
 */
package org.jpas.model;

import java.sql.Date;
import java.util.*;

import org.jpas.da.*;

public class Transaction 
{
	private static Map<Integer, Transaction> transactionCache = new WeakHashMap<Integer, Transaction>();
	
    private boolean isDeleted = false;
    private boolean isLoaded = false;

    final Integer id;
    
    private Integer accountID;
    private String payee;
    private String memo;
    private String num;
    private Date date;

    static Transaction getTransactionForID(final Integer id)
    {
    	Transaction trans = transactionCache.get(id);
    	if(trans == null)
    	{
    		trans = new Transaction(id);
    		transactionCache.put(id, trans);
    	}
    	return trans;
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
    	TransactionDA.getInstance().deleteTransaction(id);
    	transactionCache.remove(id);
    	isDeleted = true;
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
    
    public Account getAccount()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
        return Account.getAccountForID(accountID);
    }
    
    public String getPayee()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
    	return payee;
    }
    
    public String getMemo()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
    	return memo;
    }

    public String getNum()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
    	return num;
    }

    public Date getDate()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
    	return date;
    }
    
    public TransactionTransfer[] getAllTransfers()
    {
        final Integer[] accountIDs = TransAccountMappingDA.getInstance().getAllTransAccountTranfers(id);
        final TransactionTransfer[] ttArray = new TransactionTransfer[accountIDs.length];
        for(int i = 0; i < accountIDs.length; i++)
        {
            ttArray[i] = TransactionTransfer.getTransactionTransferforIDs(id, accountIDs[i]);
        }
        
        return ttArray;
    }
    
    public TransactionTransfer addTransfer(final Category category, final long amount)
    {
        assert(!isDeleted);
        TransAccountMappingDA.getInstance().createTransAccountMapping(id, category.id, amount);
        return TransactionTransfer.getTransactionTransferforIDs(id, category.id);
    }
    
    public void setPayee(final String payee)
    {
        assert(!isDeleted);
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
    	return TransAccountMappingDA.getInstance().getTransactionAmount(id);
    }
    
    public boolean isDeleted()
    {
        return isDeleted;
    }
    
    public boolean isLoaded()
    {
        return isLoaded;
    }
}
