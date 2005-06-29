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

import org.jpas.da.DAFactory;
import org.jpas.da.TransAccountMappingDA;
import org.jpas.util.JpasDataChange;
import org.jpas.util.JpasObservableImpl;

/**
 * @author Justin W Smith
 *  
 */
public class TransactionTransfer extends JpasObservableImpl
{
    private boolean isLoaded = false;
    private boolean isDeleted = false;
    private boolean isModified = false;
    
    final Integer transactionID;
    final Integer accountID;
    private long amount;

    
    TransactionTransfer(final Integer transactionID,
            final Integer accountID)
    {
        this.transactionID = transactionID;
        this.accountID = accountID;
    }

    public Category getCategory()
    {
        return ModelFactory.getInstance().getAccountImplForID(accountID);
    }

    public Transaction getTransaction()
    {
        return ModelFactory.getInstance().getTransactionForID(transactionID);
    }

    private void loadData()
    {
        assert (!isDeleted);
        DAFactory.getTransAccountMappingDA().loadTransAccountMapping(
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

    public void commit()
    {
    	if(isModified)
    	{
    		if(isDeleted)
    		{
	    		final JpasDataChange change = new JpasDataChange.Delete(this);
                DAFactory.getTransAccountMappingDA().deleteTransAccountMapping(transactionID, accountID);
                isModified = false;
                notifyObservers(change);
	    		deleteObservers();
	    	}
	    	else
	    	{
	    		final JpasDataChange change = new JpasDataChange.AmountModify(this);
                DAFactory.getTransAccountMappingDA().updateTAMAmount(transactionID, accountID, amount);
                isModified = false;
                notifyObservers(change);
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
        	isModified = true;
        	this.amount = amount;
        }
    }

    public void delete()
    {
        if(!isDeleted)
        {
        	isDeleted = true;
        	isModified = true;
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
    
    public static void main(String[] args)
    {
    }
}
