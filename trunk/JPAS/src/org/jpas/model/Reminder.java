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

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import org.jpas.da.*;
import org.jpas.da.ReminderDA.AmountMethod;
import org.jpas.da.ReminderDA.RepeatMethod;

/**
 * @author jsmith
 *
 */
public class Reminder
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

	private static Map<Integer, Reminder> reminderCache = new WeakHashMap<Integer, Reminder>();
	
    static Reminder getReminderForID(final Integer id)
    {
        Reminder rem = reminderCache.get(id);
    	if(rem == null)
    	{
    		rem = new Reminder(id);
    		reminderCache.put(id, rem);
    	}
    	return rem;
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
        ReminderDA.getInstance().deleteReminder(id);
        reminderCache.remove(id);
        isDeleted = true;
    }
    
    public Account getAccount()
    {
    	if(!isLoaded)
    	{
    		loadData();
    	}
        return Account.getAccountForID(accountId);
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

    
    public static void main(String[] args)
    {
    }
}
