package org.jpas.da;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import org.jpas.da.hsqldb.ReminderDAImpl;

public abstract class ReminderDA
{

    private static final ReminderDA instance = new ReminderDAImpl();
    
    public static ReminderDA getInstance()
    {
        return instance;
    }

    
    /** TODO covert this to an enum. */
    public static class AmountMethod 
    {
        protected static final Map<Integer, AmountMethod> valueMap = new HashMap<Integer, AmountMethod>();
        public final int dbValue;
        
        protected AmountMethod(final int dbValue)
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
        protected static final Map<Integer, RepeatMethod> valueMap = new HashMap<Integer, RepeatMethod>();
        public final int dbValue;
        
        protected RepeatMethod(final int dbValue)
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
    }

    
    public static interface ReminderHandler
    {
        public void setData(final Integer accountId, final String payee, final String memo, final Date date, final AmountMethod amountMethod, final RepeatMethod repeat, final int repeatValue);
    }

    
    public abstract Integer createReminder(final Integer accountId,
                                           final String payee,
                                           final String memo, final Date date,
                                           final AmountMethod amountMethod,
                                           final RepeatMethod repeatMethod,
                                           final int repeatValue);

    public abstract void deleteReminder(final Integer id);

    public abstract Integer[] getAllReminderIDs();

    public abstract void loadReminder(final Integer id,
                                      final ReminderHandler handler);

    public abstract void updateReminder(final Integer id,
                                        final Integer accountID,
                                        final String payee, final String memo,
                                        final Date date,
                                        final AmountMethod amountMethod,
                                        final RepeatMethod repeatMethod,
                                        final int repeatValue);

    public abstract void updateReminderAccount(final Integer id,
                                               final Integer accountID);

    public abstract void updateReminderPayee(final Integer id,
                                             final String payee);

    public abstract void updateReminderMemo(final Integer id, final String memo);

    public abstract void updateReminderDate(final Integer id, final Date date);

    public abstract void updateReminderAmountMethod(
                                                    final Integer id,
                                                    final AmountMethod amountMethod);

    public abstract void updateReminderRepeatMethod(
                                                    final Integer id,
                                                    final RepeatMethod repeatMethod);

    public abstract void updateReminderRepeatValue(final Integer id,
                                                   final int repeatValue);

    public abstract boolean doesReminderExist(final Integer id);

}
