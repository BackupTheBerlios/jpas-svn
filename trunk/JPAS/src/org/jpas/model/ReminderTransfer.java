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

import org.jpas.da.ReminderAccountMappingDA;
import org.jpas.util.JpasDataChange;
import org.jpas.util.JpasObservable;
import org.jpas.util.WeakValueMap;

/**
 * @author Justin W Smith
 * 
 */
public class ReminderTransfer extends JpasObservable<ReminderTransfer>
{
    private static WeakValueMap<Integer, WeakValueMap<Integer, ReminderTransfer>> remTransferCache = new WeakValueMap<Integer, WeakValueMap<Integer, ReminderTransfer>>();
    
    private static JpasObservable<ReminderTransfer> observable = new  JpasObservable<ReminderTransfer>();

    public static JpasObservable<ReminderTransfer> getObservable()
	{
    	return observable;
	}
    
    private boolean isDeleted = false;
    private boolean isLoaded = false;

    final Integer reminderID;
    Integer accountID;

    private long amount;
    
    static ReminderTransfer getReminderTransferforIDs(final Integer reminderID, final Integer accountID)
    {
    	synchronized(remTransferCache)
		{
	    	WeakValueMap<Integer, ReminderTransfer> map = remTransferCache.get(reminderID);
	        if(map == null)
	        {
	            map = new WeakValueMap<Integer, ReminderTransfer>();
	            remTransferCache.put(reminderID, map);
	        }
	        
	        ReminderTransfer rt = map.get(accountID);
	        if(rt == null)
	        {
	            rt = new ReminderTransfer(reminderID, accountID);
	            map.put(accountID, rt);
	        }
	        
	        return rt;
		}
    }
    
    private ReminderTransfer(final Integer reminderID, final Integer accountID)
    {
        this.reminderID = reminderID;
        this.accountID = accountID;
    }
    
    public synchronized Category getCategory()
    {
        return Category.getCategoryForID(accountID);
    }
    
    public synchronized Reminder getTransaction()
    {
        return Reminder.getReminderForID(reminderID);
    }
    
    private void loadData()
    {
        assert(!isDeleted);
        ReminderAccountMappingDA.getInstance().loadReminderAccountMapping(reminderID, accountID, 
            new ReminderAccountMappingDA.ReminderAccountTranferHandler()
            {
        		public void setData(final long amount)
        		{
        		    ReminderTransfer.this.amount = amount;
        		    isLoaded = true;
        		}
            });
    }
    
    
    public synchronized long getAmount()
    {
        if(!isLoaded)
        {
            loadData();
        }
        return amount;
    }
    
    public void setAmount(final long amount)
    {
     	synchronized(this)
		{
	        assert(!isDeleted);
	        ReminderAccountMappingDA.getInstance().updateReminderAccountMapping(reminderID, accountID, amount);
	        if(isLoaded)
	        {
	            loadData();
	        }
		}
    	announceModify();
        Reminder.getReminderForID(reminderID).announceModify();
    }

    void announceDelete()
    {
    	final JpasDataChange<ReminderTransfer> change = new JpasDataChange.Delete<ReminderTransfer>(this);
    	observable.notifyObservers(change);
		notifyObservers(change);
		deleteObservers();
    }
    
    void announceModify()
    {
    	final JpasDataChange<ReminderTransfer> change = new JpasDataChange.Modify<ReminderTransfer>(this);
    	observable.notifyObservers(change);
		notifyObservers(change);
		deleteObservers();
    }
    
    public synchronized boolean isDeleted()
    {
        return isDeleted;
    }
    
    public synchronized boolean isLoaded()
    {
        return isLoaded;
    }
    
    public void delete()
	{
	    delete(true);
	}
	
	void delete(final boolean callDA)
    {
    	synchronized(this)
		{
        	synchronized(remTransferCache)
			{
	    	    if(callDA)
	    	    {
	    	        ReminderAccountMappingDA.getInstance().deleteReminderAccountMapping(reminderID, accountID);
	    	    }
		    	final WeakValueMap<Integer, ReminderTransfer> map = remTransferCache.get(reminderID);
		    	map.remove(accountID);
		    	if(map.size() == 0)
		    	{
		    	    remTransferCache.remove(reminderID);
		    	}
			}
	    	isDeleted = true;
		}
    	announceDelete();
        Reminder.getReminderForID(reminderID).amountChanged();
    }
    
    public static void main(String[] args)
    {
    }
}
