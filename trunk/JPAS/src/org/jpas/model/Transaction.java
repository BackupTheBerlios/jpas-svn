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
import java.util.Map;
import java.util.WeakHashMap;

import org.jpas.da.*;

public class Transaction 
{
	private static Map<Integer, Transaction> transactionCache = new WeakHashMap<Integer, Transaction>();
	
    //private final Integer id;
    private boolean isDeleted = false;
    private boolean isLoaded = false;

    private final Integer id;
    
    private Account account;
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
    
    private Transaction(final Integer id)
    {
    	this.id = id;
    }
    
    public static Transaction createTransaction(final Account account, final String payee, final String memo, final String num, final Date date)
    {
    	return getTransactionForID(
    			TransactionDA.getInstance().createTransaction( account.id, payee, memo, num, date));
    }
    
    public void delete()
    {
    	TransactionDA.getInstance().deleteTransaction(id);
    	transactionCache.remove(id);
    	isDeleted = true;
    }
    
    private void loadData()
    {
    	TransactionDA.getInstance().loadTransaction(id, new TransactionDA.TransactionHandler()
    		{
				public void setData(final Integer accountId, final String payee, final String memo, final String num, final Date date)
				{
					Transaction.this.account = Account.getAccountForID(accountId);
					Transaction.this.payee = payee;
					Transaction.this.memo = memo;
					Transaction.this.num = num;
					Transaction.this.date = date;
					isLoaded = true;
				}
    		});
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
}
