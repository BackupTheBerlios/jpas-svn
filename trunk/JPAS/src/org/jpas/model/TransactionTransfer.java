/*
 * Created on Sep 26, 2004
 *
 * Title: JPAS
 * Description: Java based Personal Accounting System
 * Copyright: Copyright (c) 2004
 * License: Distributed under the terms of the GPL v2
 * @author Justin Smith
 * @version 1.0
 */

package org.jpas.model;

import java.util.*;

import org.jpas.da.TransAccountMappingDA;
/**
 * @author jsmith
 *  
 */
public class TransactionTransfer
{
    private static Map<Integer, Map<Integer, TransactionTransfer>> transTransferCache = new HashMap<Integer, Map<Integer, TransactionTransfer>>();
    
    private boolean isDeleted = false;
    private boolean isLoaded = false;

    final Integer transactionID;
    final Integer accountID;

    private long amount;
    
    static TransactionTransfer getTransactionTransferforIDs(final Integer transactionID, final Integer accountID)
    {
        Map<Integer, TransactionTransfer> map = transTransferCache.get(transactionID);
        if(map == null)
        {
            map = new WeakHashMap<Integer, TransactionTransfer>();
            transTransferCache.put(transactionID, map);
        }
        
        TransactionTransfer tt = map.get(accountID);
        if(tt == null)
        {
            tt = new TransactionTransfer(transactionID, accountID);
            map.put(accountID, tt);
        }
        
        return tt;
    }
    
    private TransactionTransfer(final Integer transactionID, final Integer accountID)
    {
        this.transactionID = transactionID;
        this.accountID = accountID;
    }
    
    public Account getAccount()
    {
        return Account.getAccountForID(accountID);
    }
    
    public Transaction getTransaction()
    {
        return Transaction.getTransactionForID(transactionID);
    }
    
    private void loadData()
    {
        assert(!isDeleted);
        TransAccountMappingDA.getInstance().loadTransAccountMapping(transactionID, accountID, 
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
        if(!isLoaded)
        {
            loadData();
        }
        return amount;
    }
    
    public void setAmount(final long amount)
    {
        assert(!isDeleted);
        TransAccountMappingDA.getInstance().updateTransAccountMapping(transactionID, accountID, amount);
        if(isLoaded)
        {
            loadData();
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
    
    public void delete()
    {
    	TransAccountMappingDA.getInstance().deleteTransAccountMapping(transactionID, accountID);
    	final Map<Integer,  TransactionTransfer> map = transTransferCache.get(transactionID);
    	map.remove(accountID);
    	if(map.size() == 0)
    	{
    	    transTransferCache.remove(transactionID);
    	}
    	isDeleted = true;
    }
    
    public static void main(String[] args)
    {
    }
}
