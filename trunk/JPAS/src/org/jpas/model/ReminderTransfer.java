/**
 * Created on Oct 2, 2004 - 12:43:37 PM
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
public class ReminderTransfer extends JpasObservableImpl
{
    private boolean isLoaded = false;
    private boolean isDeleted = false;
    private boolean isModified = false;
    
    final Integer reminderID;
    final Integer accountID;
    private long amount;

    
    ReminderTransfer(final Integer reminderID,
            final Integer accountID)
    {
        this.reminderID = reminderID;
        this.accountID = accountID;
    }

    public Category getCategory()
    {
        return ModelFactory.getInstance().getAccountImplForID(accountID);
    }

    public Reminder getReminder()
    {
        return ModelFactory.getInstance().getReminderForID(reminderID);
    }

    private void loadData()
    {
        assert (!isDeleted);
        DAFactory.getTransAccountMappingDA().loadTransAccountMapping(
                reminderID, accountID,
                new TransAccountMappingDA.TransAccountTranferHandler()
                {
                    public void setData(final long amount)
                    {
                        ReminderTransfer.this.amount = amount;
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

    private void announceChange(final JpasDataChange change)
    {
    	notifyObservers(change);
    }
    
    public void commit()
    {
    	if(isModified)
    	{
    		if(isDeleted)
    		{
	    		final JpasDataChange change = new JpasDataChange.Delete(this);
                DAFactory.getTransAccountMappingDA().deleteTransAccountMapping(reminderID, accountID);
	    		announceChange(change);
	    		deleteObservers();
	    		isModified = false;
	    	}
	    	else
	    	{
	    		final JpasDataChange change = new JpasDataChange.AmountModify(this);
                DAFactory.getTransAccountMappingDA().updateTAMAmount(reminderID, accountID, amount);
	    		announceChange(change);
	    		isModified = false;
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
