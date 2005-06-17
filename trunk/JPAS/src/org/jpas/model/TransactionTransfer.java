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
import org.jpas.util.*;

import java.util.*;

/**
 * @author Justin W Smith
 *  
 */
public class TransactionTransfer extends JpasObservable<TransactionTransfer>
{
	static class TransTransferKey 
	{
		final Integer transactionID;
		final Integer categoryID;
		TransTransferKey(final Integer transactionID, final Integer categoryID)
		{
			this.transactionID = transactionID;
			this.categoryID = categoryID;
		}
		
		public boolean equals(final Object o)
		{
			return o instanceof TransTransferKey 
				&& ((TransTransferKey)o).transactionID.equals(transactionID)
				&& ((TransTransferKey)o).categoryID.equals(categoryID);
		}
		
		public int hashCode()
		{
			return transactionID.intValue() * categoryID.intValue();
		}
	}
	
    private static WeakValueMap<TransTransferKey, TransactionTransfer> transTransferCache = new WeakValueMap<TransTransferKey, TransactionTransfer>();
    private static JpasObservable<TransactionTransfer> observable = new JpasObservable<TransactionTransfer>();

    public static JpasObservable<TransactionTransfer> getObservable()
    {
        return observable;
    }

    private boolean isLoaded = false;
    private boolean isDeleted = false;
    private boolean isAmountModified = false;
    private boolean isCategoryModified = false;
    
    final Integer transactionID;
    Integer accountID;
    private long amount;

    static TransactionTransfer getTransactionTransferforIDs(final Integer transactionID, final Integer categoryID)
    {
    	if(TransAccountMappingDA.getInstance().doesTransAccountTransferExist(transactionID, categoryID))
    	{
    		final TransTransferKey key = new TransTransferKey(transactionID, categoryID);
    		TransactionTransfer transfer = transTransferCache.get(key);

    		if(transfer == null)
		    {
    			transfer = new TransactionTransfer(transactionID, categoryID);
    			transTransferCache.put(key, transfer);
		    }
    		return transfer;
    	}
    	return null;
    }
    
    private TransactionTransfer(final Integer transactionID,
            final Integer accountID)
    {
        this.transactionID = transactionID;
        this.accountID = accountID;
        addObserver(Transaction.getTransactionForID(transactionID));
        addObserver(Account.getAccountForID(accountID));
        addObserver(Category.getCategoryForID(accountID));
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

    private void announceChange(final JpasDataChange<TransactionTransfer> change)
    {
    	if(true)
    	{
    		observable.notifyObservers(change);
    	}
    	notifyObservers(change);
    }
    
    private void commit()
    {
    	if(isDeleted)
    	{
    		final JpasDataChange<TransactionTransfer> change = new JpasDataChange.Delete<TransactionTransfer>(this);
    		TransAccountMappingDA.getInstance().deleteTransAccountMapping(transactionID, accountID);
    		announceChange(change);
    	}
    	else
    	{
	    	if(isAmountModified)
	    	{
	    		final JpasDataChange<TransactionTransfer> change = new JpasDataChange.AmountModify<TransactionTransfer>(this);
	    		TransAccountMappingDA.getInstance().updateTAMAmount(transactionID, accountID, amount);
	    		announceChange(change);
	    	}
	    	if(isCategoryModified)
	    	{
	    		final JpasDataChange<TransactionTransfer> change = new JpasDataChange.Modify<TransactionTransfer>(this);
	    		TransAccountMappingDA.getInstance().updateTAMAmount(transactionID, accountID, amount);
	    		announceChange(change);
	    	}
    	}
    }

    public void setAmount(final long amount)
    {
        assert (!isDeleted);
        if (isLoaded)
        {
            loadData();
        }
        if(this.amount != amount)
        {
        	isAmountModified = true;
        	this.amount = amount;
        }
    }

    void setCategory(final Category category)
    {
        assert (!isDeleted);
        if (isLoaded)
        {
            loadData();
        }
        if(this.accountID != category.id)
        {
        	isCategoryModified = true;
        	this.accountID = category.id;
        }
    }

    public void delete()
    {
        if(!isDeleted)
        {
        	isDeleted = true;
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
    	return isCategoryModified || isAmountModified;
    }
    
    public boolean isCategoryModified()
    {
    	return isCategoryModified;
    }
    
    public boolean isAmountModified()
    {
    	return isAmountModified;
    }
    
    public static void main(String[] args)
    {
    }
}
