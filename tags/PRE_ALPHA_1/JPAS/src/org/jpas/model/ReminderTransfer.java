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

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.jpas.da.ReminderAccountMappingDA;

/**
 * @author Justin W Smith
 * 
 */
public class ReminderTransfer
{
    private static Map<Integer, Map<Integer, ReminderTransfer>> remTransferCache = new HashMap<Integer, Map<Integer, ReminderTransfer>>();
    
    private boolean isDeleted = false;
    private boolean isLoaded = false;

    final Integer reminderID;
    final Integer accountID;

    private long amount;
    
    static ReminderTransfer getReminderTransferforIDs(final Integer reminderID, final Integer accountID)
    {
        Map<Integer, ReminderTransfer> map = remTransferCache.get(reminderID);
        if(map == null)
        {
            map = new WeakHashMap<Integer, ReminderTransfer>();
            remTransferCache.put(reminderID, map);
        }
        
        ReminderTransfer tt = map.get(accountID);
        if(tt == null)
        {
            tt = new ReminderTransfer(reminderID, accountID);
            map.put(accountID, tt);
        }
        
        return tt;
    }
    
    private ReminderTransfer(final Integer reminderID, final Integer accountID)
    {
        this.reminderID = reminderID;
        this.accountID = accountID;
    }
    
    public Account getAccount()
    {
        return Account.getAccountForID(accountID);
    }
    
    public Reminder getTransaction()
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
        ReminderAccountMappingDA.getInstance().updateReminderAccountMapping(reminderID, accountID, amount);
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
    	ReminderAccountMappingDA.getInstance().deleteReminderAccountMapping(reminderID, accountID);
    	final Map<Integer,  ReminderTransfer> map = remTransferCache.get(reminderID);
    	map.remove(accountID);
    	if(map.size() == 0)
    	{
    	    remTransferCache.remove(reminderID);
    	}
    	isDeleted = true;
    }
    
    public static void main(String[] args)
    {
    }
}
