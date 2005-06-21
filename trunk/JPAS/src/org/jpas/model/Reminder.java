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

import org.jpas.da.hsqldb.ReminderAccountMappingDAImpl;
import org.jpas.da.hsqldb.ReminderDAImpl;
import org.jpas.util.*;

/**
 * @author jsmith
 *  
 */
public class Reminder extends JpasObservableImpl implements JpasObserver
{
    /** TODO covert this to an enum. */
    public static class AmountMethod
    {
        private static final Map<ReminderDAImpl.AmountMethod, AmountMethod> valueMap = new HashMap<ReminderDAImpl.AmountMethod, AmountMethod>();
        final ReminderDAImpl.AmountMethod daAmountMethod;

        private AmountMethod(final ReminderDAImpl.AmountMethod amountMethod)
        {
            this.daAmountMethod = amountMethod;
            valueMap.put(amountMethod, this);
        }

        public static AmountMethod getAmountMethodFor(
                final ReminderDAImpl.AmountMethod am)
        {
            return valueMap.get(am);
        }

        public static final AmountMethod FIXED = new AmountMethod(
                ReminderDAImpl.AmountMethod.FIXED);
        public static final AmountMethod AVERAGE_TWO = new AmountMethod(
                ReminderDAImpl.AmountMethod.AVERAGE_TWO);
        public static final AmountMethod AVERAGE_THREE = new AmountMethod(
                ReminderDAImpl.AmountMethod.AVERAGE_THREE);
        public static final AmountMethod LAST = new AmountMethod(
                ReminderDAImpl.AmountMethod.LAST);
    }

    /** TODO covert this to an enum. */
    public static class RepeatMethod
    {
        private static final Map<ReminderDAImpl.RepeatMethod, RepeatMethod> valueMap = new HashMap<ReminderDAImpl.RepeatMethod, RepeatMethod>();
        final ReminderDAImpl.RepeatMethod daRepeatMethod;

        private RepeatMethod(final ReminderDAImpl.RepeatMethod repeatMethod)
        {
            this.daRepeatMethod = repeatMethod;
            valueMap.put(repeatMethod, this);
        }

        public static RepeatMethod getRepeatMethodFor(
                final ReminderDAImpl.RepeatMethod rm)
        {
            return valueMap.get(rm);
        }

        public static final RepeatMethod DAILY = new RepeatMethod(
                ReminderDAImpl.RepeatMethod.DAILY);
        public static final RepeatMethod WEEKLY = new RepeatMethod(
                ReminderDAImpl.RepeatMethod.WEEKLY);
        public static final RepeatMethod MONTHLY = new RepeatMethod(
                ReminderDAImpl.RepeatMethod.MONTHLY);
        public static final RepeatMethod YEARLY = new RepeatMethod(
                ReminderDAImpl.RepeatMethod.YEARLY);
    }

    static
    {
    	ModelFactory.getInstance().getReminderTransferObservable().addObserver(new JpasObserver()
		{
			public void update(JpasObservable observable, JpasDataChange change) 
			{
				if(change instanceof JpasDataChange.Delete || change instanceof JpasDataChange.AmountModify)
				{
					final ReminderTransfer transfer = (ReminderTransfer)change.getValue();
					ModelFactory.getInstance().getReminderForID(transfer.reminderID).amountChange();
				}
			}
		});
    }
    
    private boolean isModified = false;
    private boolean isDeleted = false;
    private boolean isLoaded = false;
    
    final Integer id;

    private Integer accountId;
    private String payee;
    private String memo;
    private Date date;
    private AmountMethod amountMethod;
    private RepeatMethod repeatMethod;
    private int repeatValue;

    private boolean amountLoaded = false;
    private long amount;
    
    Reminder(final Integer id)
    {
        this.id = id;
    }

    private void loadData()
    {
        ReminderDAImpl.getInstance().loadReminder(id,
                new ReminderDAImpl.ReminderHandler()
                {
                    public void setData(final Integer accountId,
                            final String payee, final String memo,
                            final Date date,
                            final ReminderDAImpl.AmountMethod amountMethod,
                            final ReminderDAImpl.RepeatMethod repeatMethod,
                            final int repeatValue)
                    {
                        Reminder.this.accountId = accountId;
                        Reminder.this.payee = payee;
                        Reminder.this.memo = memo;
                        Reminder.this.date = date;
                        Reminder.this.amountMethod = AmountMethod
                                .getAmountMethodFor(amountMethod);
                        Reminder.this.repeatMethod = RepeatMethod
                                .getRepeatMethodFor(repeatMethod);
                        Reminder.this.repeatValue = repeatValue;
                        isLoaded = true;
                    }
                });
    }

    public void delete()
    {
        if(!isDeleted)
        {
        	isDeleted = true;
        	isModified = true;
        }
    }

    public void commit()
    {
    	if(isModified)
    	{
    		final JpasDataChange change;
    		if(isDeleted)
    		{
		    	final Integer[] accountIDs = ReminderAccountMappingDAImpl.getInstance()
		        	.getAllReminderAccountTranfers(id);
				for (int i = 0; i < accountIDs.length; i++)
				{
					final ReminderTransfer transfer = ModelFactory.getInstance().getReminderTransferforIDs(id, accountIDs[i]);
					transfer.deleteObserver(this);
				    transfer.delete();
					transfer.commit();
				}
		        change = new JpasDataChange.Delete(this);
		        ReminderDAImpl.getInstance().deleteReminder(id);
		        deleteObservers();
		        isModified = false;
    		}
    		else
    		{
				change = new JpasDataChange.Modify(this);
				ReminderDAImpl.getInstance().updateReminder(id, accountId, payee, memo, new java.sql.Date(date.getTime()), amountMethod.daAmountMethod, repeatMethod.daRepeatMethod, repeatValue);
				isModified = false;
    		}
	        notifyObservers(change);
    	}
    }

    
    private void amountChange()
    {
        final JpasDataChange myChange = new JpasDataChange.AmountModify(this);
        amountLoaded = false;
        notifyObservers(myChange);
    }

    public Account getAccount()
    {
        if (!isLoaded)
        {
            loadData();
        }
        return ModelFactory.getInstance().getAccountImplForID(accountId);
    }

    public String getPayee()
    {
        if (!isLoaded)
        {
            loadData();
        }
        return payee;
    }

    public String getMemo()
    {
        if (!isLoaded)
        {
            loadData();
        }
        return memo;
    }

    public Date getDate()
    {
        if (!isLoaded)
        {
            loadData();
        }
        return date;
    }

    public AmountMethod getAmountMethod()
    {
        if (!isLoaded)
        {
            loadData();
        }
        return amountMethod;
    }

    public RepeatMethod getRepeatMethod()
    {
        if (!isLoaded)
        {
            loadData();
        }
        return repeatMethod;
    }

    public int getRepeatValue()
    {
        if (!isLoaded)
        {
            loadData();
        }
        return repeatValue;
    }

    public void setPayee(final String payee)
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        if(!this.payee.equals(payee))
        {
        	this.payee = payee;
        	isModified = true;
        }
    }

    public void setMemo(final String memo)
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        if(!this.memo.equals(memo))
        {
        	this.memo = memo;
        	isModified = true;
        }
    }

    public void setDate(final Date date)
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        if(!this.date.equals(date))
        {
        	this.date = date;
        	isModified = true;
        }
    }

    public void setAccount(final Integer accountID)
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        if(!this.accountId.equals(accountID))
        {
        	this.accountId = accountID;
        	isModified = true;
        }
    }

    public void setAmountMethod(final AmountMethod amountMethod)
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        if(!this.amountMethod.equals(amountMethod))
        {
        	this.amountMethod = amountMethod;
        	isModified = true;
        }
    }

    public void setRepeatMethod(final RepeatMethod repeatMethod)
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        if(!this.repeatMethod.equals(repeatMethod))
        {
        	this.repeatMethod = repeatMethod;
        	isModified = true;
        }
    }

    public void setRepeatValue(final int repeatValue)
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        if(this.repeatValue != repeatValue)
        {
        	this.repeatValue = repeatValue;
        	isModified = true;
        }
    }

    public long getAmount()
    {
        if(!amountLoaded)
        {
            amount = ReminderAccountMappingDAImpl.getInstance().getReminderAmount(id);
            amountLoaded = true;
        }
        return amount;
    }

    public static void main(String[] args)
    {
    }

	public void update(JpasObservable ob, JpasDataChange change) 
	{
    	assert(!isDeleted);
        if(change instanceof JpasDataChange.Delete)
        {
            ob.deleteObserver(this);
        }
        final JpasDataChange myChange = new JpasDataChange.AmountModify(this);
        amountLoaded = false;
        notifyObservers(myChange);
	}
}
