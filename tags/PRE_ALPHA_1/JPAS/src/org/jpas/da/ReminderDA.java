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

package org.jpas.da;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

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
    }

    
	public static interface ReminderHandler
	{
		public void setData(final Integer accountId, final String payee, final String memo, final Date date, final AmountMethod amountMethod, final RepeatMethod repeat, final int repeatValue);
	}
    
	public Integer createReminder(final Integer accountId, final String payee, final String memo, final Date date, final AmountMethod amountMethod, final RepeatMethod repeatMethod, final int repeatValue)
	{
		final String sqlSequenceStr = "CALL NEXT VALUE FOR " + DBNames.SEQ_REMINDER_ID;

		final Integer id;
		try
		{
			final ResultSet rs = ConnectionManager.getInstance().query(sqlSequenceStr);
			if(!rs.next())
			{
				defaultLogger.error("Unable to get next reminder ID: \"" + sqlSequenceStr + "\"");
				throw new RuntimeException("Unable to get next reminder ID: \"" + sqlSequenceStr + "\"");
			}
			id = (Integer)rs.getObject(1);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlSequenceStr, sqle);
			throw new RuntimeException(sqlSequenceStr, sqle);
		}

		final String sqlStr = "INSERT INTO " + DBNames.TN_REMINDER
										 + " ( " + DBNames.CN_REMINDER_ID
										 + " , " + DBNames.CN_REMINDER_ACCOUNT
										 + " , " + DBNames.CN_REMINDER_PAYEE
										 + " , " + DBNames.CN_REMINDER_MEMO
										 + " , " + DBNames.CN_REMINDER_DATE
										 + " , " + DBNames.CN_REMINDER_AMOUNT_METHOD
										 + " , " + DBNames.CN_REMINDER_REPEAT_METHOD
										 + " , " + DBNames.CN_REMINDER_REPEAT_VALUE
										 + " ) VALUES ( "
										 + id + " , " 
										 + accountId + " , '"
										 + payee + "' , '" 
										 + memo + "' , '"
										 + date + "' , "
										 + amountMethod.dbValue + " , "
										 + repeatMethod.dbValue + " , "
										 + repeatValue + ")";


		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Unable to create transaction: \"" + sqlStr + "\"");
				throw new RuntimeException(sqlStr);
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}

		return id;
	}

	public void deleteReminder(final Integer id)
	{
		try
		{
			final int result = ConnectionManager.getInstance().update(
											  "DELETE FROM " + DBNames.TN_REMINDER
											 + " WHERE " + DBNames.CN_REMINDER_ID
											 + " IS '" + id + "'");
			if(result < 1)
			{
				defaultLogger.error("Reminder id not found: "+ id +"!");
				throw new RuntimeException("Unable to update Reminder for id: " + id);
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error("SQLException while deleting Reminder for id: " + id + "!", sqle);
			throw new RuntimeException("Unable to deleting Reminder for id: " + id, sqle);
		}
	}

	
	public void loadReminder(final Integer id, final ReminderHandler handler)
	{
		final String sqlStr = "SELECT * FROM " + DBNames.TN_REMINDER
										 + " WHERE " + DBNames.CN_REMINDER_ID
										 + " IS " + id;
		try
		{
			final ResultSet rs =  ConnectionManager.getInstance().query(sqlStr);

			if(rs.next())
			{
				handler.setData(new Integer(rs.getInt(DBNames.CN_REMINDER_ACCOUNT)),
								rs.getString(DBNames.CN_REMINDER_PAYEE),
								rs.getString(DBNames.CN_REMINDER_MEMO),
								rs.getDate(DBNames.CN_REMINDER_DATE),
								AmountMethod.getAmountMethodFor(rs.getInt(DBNames.CN_REMINDER_AMOUNT_METHOD)),
								RepeatMethod.getRepeatMethodFor(rs.getInt(DBNames.CN_REMINDER_REPEAT_METHOD)),
								rs.getInt(DBNames.CN_REMINDER_REPEAT_VALUE));
			}
			else
			{
				defaultLogger.error("Reminder id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Reminder id not found: \""+ sqlStr +"\"");
			}
				
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateReminder(final Integer id, final Integer accountID, final String payee, final String memo, final Date date, final AmountMethod amountMethod, final RepeatMethod repeatMethod, final int repeatValue)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_ACCOUNT
										 + " = " + accountID
										 + " , " + DBNames.CN_REMINDER_PAYEE
										 + " = '" + payee 
										 + "' , " + DBNames.CN_REMINDER_MEMO
										 + " = '" + memo
										 + "' , " + DBNames.CN_REMINDER_DATE
										 + " = " + date
										 + " , " + DBNames.CN_REMINDER_AMOUNT_METHOD
										 + " = " + amountMethod.dbValue
										 + " , " + DBNames.CN_REMINDER_REPEAT_METHOD
										 + " = " + repeatMethod.dbValue
										 + " , " + DBNames.CN_REMINDER_REPEAT_VALUE
										 + " = " + repeatValue
										 + " WHERE " + DBNames.CN_REMINDER_ID
											 + " IS " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Reminder id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Reminder id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateReminderAccount(final Integer id, final Integer accountID)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_ACCOUNT
										 + " = " + accountID 
										 + " WHERE " + DBNames.CN_REMINDER_ID
										 + " IS " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Reminder id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Reminder id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	
	public void updateReminderPayee(final Integer id, final String payee)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_PAYEE
										 + " = '" + payee 
										 + "' WHERE " + DBNames.CN_REMINDER_ID
											 + " IS " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Reminder id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Reminder id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateReminderMemo(final Integer id, final String memo)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_MEMO
										 + " = '" + memo 
										 + "' WHERE " + DBNames.CN_REMINDER_ID
											 + " IS " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Reminder id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Reminder id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateReminderDate(final Integer id, final Date date)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_DATE
										 + " = " + date 
										 + " WHERE " + DBNames.CN_REMINDER_ID
											 + " IS " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Reminder id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Reminder id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateReminderAmountMethod(final Integer id, final AmountMethod amountMethod)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_AMOUNT_METHOD
										 + " = " + amountMethod.dbValue
										 + " WHERE " + DBNames.CN_REMINDER_ID
											 + " IS " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Reminder id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Reminder id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateReminderRepeatMethod(final Integer id, final RepeatMethod repeatMethod)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_REPEAT_METHOD
										 + " = " + repeatMethod.dbValue 
										 + " WHERE " + DBNames.CN_REMINDER_ID
											 + " IS " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Reminder id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Reminder id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateReminderRepeatValue(final Integer id, final int repeatValue)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_REPEAT_VALUE
										 + " = " + repeatValue 
										 + " WHERE " + DBNames.CN_REMINDER_ID
											 + " IS " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Reminder id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Reminder id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}
	
	public boolean doesReminderExist(final Integer id)
	{
		final String sqlStr = "SELECT " + DBNames.CN_REMINDER_ID
								+ " FROM " + DBNames.TN_REMINDER
								+ " WHERE " + DBNames.CN_REMINDER_ID
								+ " IS " + id;
		try
		{
			return ConnectionManager.getInstance().query(sqlStr).next();
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error("SQLException while loading account name!", sqle);
			throw new RuntimeException("Unable to load transaction id's!", sqle);
		}	
	}

    public static void unitTest_Create()
    {
        getInstance().createReminder(new Integer(0), "My Utilities", "memo", new Date(System.currentTimeMillis()), AmountMethod.FIXED, RepeatMethod.WEEKLY, 1);
    }
    
    
    public static void main(final String[] args)
    {
		BasicConfigurator.configure();
		unitTest_Create();
    }
}
