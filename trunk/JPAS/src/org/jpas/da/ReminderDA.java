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

package org.jpas.da;

import java.sql.Date;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * @author jsmith
 *
 */
public class ReminderDA
{
    private static final Logger defaultLogger = Logger.getLogger(TransactionDA.class);

    private static final ReminderDA instance = new ReminderDA();
    
    public static ReminderDA getInstance()
    {
        return instance;
    }

    private ReminderDA()
    { 
    }

    
    /** TODO covert this to an enum. */
    public static class AmountMethod 
    {
        private static final Map<Integer, AmountMethod> valueMap = new HashMap<Integer, AmountMethod>();
        private final int dbValue;
        
        private AmountMethod(final int dbValue)
        {
            this.dbValue = dbValue;
            valueMap.put(new Integer(dbValue), this);
        }
        
        public static AmountMethod getAmountMethodFor(final int dbValue)
        {
            return valueMap.get(new Integer(dbValue));
        }
        
        public static final AmountMethod FIXED = new AmountMethod(0); 
        public static final AmountMethod AVERAGE_TWO = new AmountMethod(1);
        public static final AmountMethod AVERAGE_THREE = new AmountMethod(2);
        public static final AmountMethod LAST = new AmountMethod(3);
    }

    /** TODO covert this to an enum. */
    public static class RepeatMethod 
    {
        private static final Map<Integer, RepeatMethod> valueMap = new HashMap<Integer, RepeatMethod>();
        private final int dbValue;
        
        private RepeatMethod(final int dbValue)
        {
            this.dbValue = dbValue;
            valueMap.put(new Integer(dbValue), this);
        }
        
        public static RepeatMethod getRepeatMethodFor(final int dbValue)
        {
            return valueMap.get(new Integer(dbValue));
        }
        
        public static final RepeatMethod DAILY = new RepeatMethod(0);
        public static final RepeatMethod WEEKLY = new RepeatMethod(1); 
        public static final RepeatMethod MONTHLY = new RepeatMethod(2);
        public static final RepeatMethod YEARLY = new RepeatMethod(3);
        public static final RepeatMethod TWO_WEEKS = new RepeatMethod(4);
        public static final RepeatMethod BIMONTHLY = new RepeatMethod(5);
        public static final RepeatMethod BIYEARLY = new RepeatMethod(6);
        public static final RepeatMethod TWO_MONTHS = new RepeatMethod(7);
        public static final RepeatMethod THREE_MONTHS = new RepeatMethod(8);
        public static final RepeatMethod FOUR_MONTHS = new RepeatMethod(9);
    }

    
	public static interface ReminderHandler
	{
		public void setData(final Integer accountId, final String payee, final String memo, final Date date, final AmountMethod amountMethod, final RepeatMethod repeat);
	}
    
    
    public static void main(String[] args)
    {
        final AmountMethod am = AmountMethod.FIXED;
    }
}
