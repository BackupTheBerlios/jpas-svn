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

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import org.jpas.da.*;
import org.jpas.util.JpasDataChange;
import org.jpas.util.JpasObservable;
import org.jpas.util.JpasObserver;
import org.jpas.util.WeakValueMap;
import org.jpas.util.JpasDataChange.Delete;

/**
 * @author jsmith
 *
 */
public class Reminder extends JpasObservable<Reminder> implements JpasObserver<ReminderTransfer>
{
    /** TODO covert this to an enum. */
    public static class AmountMethod 
    {
        private static final Map<ReminderDA.AmountMethod, AmountMethod> valueMap = new HashMap<ReminderDA.AmountMethod, AmountMethod>();
        private final ReminderDA.AmountMethod daAmountMethod;
        
        private AmountMethod(final ReminderDA.AmountMethod amountMethod)
        {
            this.daAmountMethod = amountMethod;
            valueMap.put(amountMethod, this);
        }
        
        public static AmountMethod getAmountMethodFor(final ReminderDA.AmountMethod am)
        {
            return valueMap.get(am);
        }
        
        public static final AmountMethod FIXED = new AmountMethod(ReminderDA.AmountMethod.FIXED); 
        public static final AmountMethod AVERAGE_TWO = new AmountMethod(ReminderDA.AmountMethod.AVERAGE_TWO);
        public static final AmountMethod AVERAGE_THREE = new AmountMethod(ReminderDA.AmountMethod.AVERAGE_THREE);
        public static final AmountMethod LAST = new AmountMethod(ReminderDA.AmountMethod.LAST);
    }

    /** TODO covert this to an enum. */
    public static class RepeatMethod 
    {
        private static final Map<ReminderDA.RepeatMethod, RepeatMethod> valueMap = new HashMap<ReminderDA.RepeatMethod, RepeatMethod>();
        private final ReminderDA.RepeatMethod daRepeatMethod;
        
        private RepeatMethod(final ReminderDA.RepeatMethod repeatMethod)
        {
            this.daRepeatMethod = repeatMethod;
            valueMap.put(repeatMethod, this);
        }
        
        public static RepeatMethod getRepeatMethodFor(final ReminderDA.RepeatMethod rm)
        {
            return valueMap.get(rm);
        }
        
        public static final RepeatMethod DAILY = new RepeatMethod(ReminderDA.RepeatMethod.DAILY);
        public static final RepeatMethod WEEKLY = new RepeatMethod(ReminderDA.RepeatMethod.WEEKLY); 
        public static final RepeatMethod MONTHLY = new RepeatMethod(ReminderDA.RepeatMethod.MONTHLY);
        public static final RepeatMethod YEARLY = new RepeatMethod(ReminderDA.RepeatMethod.YEARLY);
    }

	private static WeakValueMap<Integer, Reminder> reminderCache = new WeakValueMap<Integer, Reminder>();
	
	private static JpasObservable<Reminder> observable = new JpasObservable<Reminder>();
	
	public static  JpasObservable<Reminder> getObservable()
	{
		return observable;
	}
	
    static Reminder getReminderForID(final Integer id)
    {
    	synchronized(reminderCache)
		{
	        Reminder rem = reminderCache.get(id);
	    	if(rem == null)
	    	{
	    		rem = new Reminder(id);
	    		reminderCache.put(id, rem);
	    	}
	    	return rem;
		}
    }
    
    public static Reminder createReminder(final Account account, final String payee, final String memo, final Date date, final AmountMethod amountMethod, final RepeatMethod repeatMethod, final int repeatValue)
    {
    	return getReminderForID(
    	        ReminderDA.getInstance().createReminder( account.id, payee, memo, date, amountMethod.daAmountMethod , repeatMethod.daRepeatMethod, repeatValue));
    }

    private boolean isDeleted = false;
    private boolean isLoaded = false;

    final Integer id;
    
    Integer accountId;
    String payee;
    String memo;
    Date date;
    AmountMethod amountMethod; 
    RepeatMethod repeatMethod;
    int repeatValue;
    
    private Reminder(final Integer id)
    {
        this.id = id;
    }
    
    private void loadData()
    {
    	ReminderDA.getInstance().loadReminder( id, new ReminderDA.ReminderHandler()
    	{
    		public void setData(final Integer accountId, final String payee, final String memo, final Date date, final ReminderDA.AmountMethod amountMethod, final ReminderDA.RepeatMethod repeatMethod, final int repeatValue)
    		{
    		    Reminder.this.accountId = accountId;
    		    Reminder.this.payee = payee;
    		    Reminder.this.memo = memo;
    		    Reminder.this.date = date;
    		    Reminder.this.amountMethod = AmountMethod.getAmountMethodFor(amountMethod);
    		    Reminder.this.repeatMethod = RepeatMethod.getRepeatMethodFor(repeatMethod);
    		    Reminder.this.repeatValue = repeatValue;
    		    isLoaded = true;
    		}
    	});

    }

    public void delete()
    {
    	synchronized(this)
		{
    		synchronized(reminderCache)
			{
		        ReminderDA.getInstance().deleteReminder(id);
		        reminderCache.remove(id);
		        isDeleted = true;
			}
		}
    }
    
    public synchronized Account getAccount()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
        return Account.getAccountForID(accountId);
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

    public synchronized Date getDate()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
    	return date;
    }
    
    public synchronized AmountMethod getAmountMethod()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
    	return amountMethod;
    }

    public synchronized RepeatMethod getRepeatMethod()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
    	return repeatMethod;
    }

    public synchronized int getRepeatValue()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
    	return repeatValue;
    }

    public ReminderTransfer[] getTransfers()
    {
        final Integer[] accountIDs = ReminderAccountMappingDA.getInstance().getAllReminderAccountTranfers(id);
        final ReminderTransfer[] ttArray = new ReminderTransfer[accountIDs.length];
        for(int i = 0; i < accountIDs.length; i++)
        {
            ttArray[i] = ReminderTransfer.getReminderTransferforIDs(id, accountIDs[i]);
        }
        
        return ttArray;
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
    
    public void setAccount(final Integer accountID)
    {
    	synchronized(this)
		{
	        assert (!isDeleted);
	        ReminderDA.getInstance().updateReminderAccount(id, accountID);
	        if (isLoaded)
	        {
	            loadData();
	        }
		}
    }

    public void setAmountMethod(final AmountMethod amountMethod)
    {
    	synchronized(this)
		{
	        assert (!isDeleted);
	        ReminderDA.getInstance().updateReminderAmountMethod(id, amountMethod.daAmountMethod);
	        if (isLoaded)
	        {
	            loadData();
	        }
		}
    }

    public void setRepeatMethod(final RepeatMethod repeatMethod)
    {
    	synchronized(this)
		{
	        assert (!isDeleted);
	        ReminderDA.getInstance().updateReminderRepeatMethod(id, repeatMethod.daRepeatMethod);
	        if (isLoaded)
	        {
	            loadData();
	        }
		}
    }

    public void setRepeatValue(final int repeatValue)
    {
    	synchronized(this)
		{
	        assert (!isDeleted);
	        ReminderDA.getInstance().updateReminderRepeatValue(id, repeatValue);
	        if (isLoaded)
	        {
	            loadData();
	        }
		}
    	final JpasDataChange<Reminder> change = new JpasDataChange.Delete<Reminder>(this);
    	observable.notifyObservers(change);
		notifyObservers(change);
		deleteObservers();

    }
    
    public long getAmount()
    {
    	return ReminderAccountMappingDA.getInstance().getReminderAmount(id);
    }
    
    public static void main(String[] args)
    {
    }

	/** (non-Javadoc)
	 * @see org.jpas.util.JpasObserver#update(org.jpas.util.JpasObservable, org.jpas.util.JpasDataChange)
	 */
	public void update(JpasObservable<ReminderTransfer> observable, JpasDataChange<ReminderTransfer> change) 
	{
		
	}
}
