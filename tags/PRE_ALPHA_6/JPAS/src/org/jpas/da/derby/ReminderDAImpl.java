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

package org.jpas.da.derby;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jpas.da.DAFactory;
import org.jpas.da.DBNames;
import org.jpas.da.ReminderDA;

/**
 * @author jsmith
 *
 */
public class ReminderDAImpl extends ReminderDA
{
    //TODO This is just a copy of the HSQLDB implementation
    
    private static final Logger defaultLogger = Logger.getLogger(ReminderDAImpl.class);

    public ReminderDAImpl()
    { 
    }

    
	/* (non-Javadoc)
     * @see org.jpas.da.ReminderDA#createReminder(java.lang.Integer, java.lang.String, java.lang.String, java.sql.Date, org.jpas.da.ReminderDAImpl.AmountMethod, org.jpas.da.ReminderDAImpl.RepeatMethod, int)
     */
	public Integer createReminder(final Integer accountId, final String payee, final String memo, final Date date, final AmountMethod amountMethod, final RepeatMethod repeatMethod, final int repeatValue)
	{
		final String sqlSequenceStr = "CALL NEXT VALUE FOR " + DBNames.SEQ_REMINDER_ID;

		final Integer id;
		try
		{
			final ResultSet rs = DAFactory.getConnectionManager().query(sqlSequenceStr);
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
			final int result = DAFactory.getConnectionManager().update(sqlStr);
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

	/* (non-Javadoc)
     * @see org.jpas.da.ReminderDA#deleteReminder(java.lang.Integer)
     */
	public void deleteReminder(final Integer id)
	{
		try
		{
			final int result = DAFactory.getConnectionManager().update(
											  "DELETE FROM " + DBNames.TN_REMINDER
											 + " WHERE " + DBNames.CN_REMINDER_ID
											 + " = '" + id + "'");
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

	/* (non-Javadoc)
     * @see org.jpas.da.ReminderDA#getAllReminderIDs()
     */
	public Integer[] getAllReminderIDs()
	{
		final String sqlStr = "SELECT " + DBNames.CN_REMINDER_ID
							+ " FROM " + DBNames.TN_REMINDER
							+ " ORDER BY " + DBNames.CN_REMINDER_DATE;
		try
		{
			final ResultSet rs =  DAFactory.getConnectionManager().query(sqlStr);
			final List<Integer> idList = new ArrayList<Integer>();
			while(rs.next())
			{
				idList.add((Integer)rs.getObject(DBNames.CN_REMINDER_ID));
			}
			return idList.toArray(new Integer[idList.size()]);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error("SQLException while loading account name!", sqle);
			throw new RuntimeException("Unable to load transaction id's!", sqle);
		}
	}

	
	/* (non-Javadoc)
     * @see org.jpas.da.ReminderDA#loadReminder(java.lang.Integer, org.jpas.da.ReminderDAImpl.ReminderHandler)
     */
	public void loadReminder(final Integer id, final ReminderHandler handler)
	{
		final String sqlStr = "SELECT * FROM " + DBNames.TN_REMINDER
										 + " WHERE " + DBNames.CN_REMINDER_ID
										 + " = " + id;
		try
		{
			final ResultSet rs =  DAFactory.getConnectionManager().query(sqlStr);

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

	/* (non-Javadoc)
     * @see org.jpas.da.ReminderDA#updateReminder(java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.sql.Date, org.jpas.da.ReminderDAImpl.AmountMethod, org.jpas.da.ReminderDAImpl.RepeatMethod, int)
     */
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
											 + " = " + id;

		try
		{
			final int result = DAFactory.getConnectionManager().update(sqlStr);
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

	/* (non-Javadoc)
     * @see org.jpas.da.ReminderDA#updateReminderAccount(java.lang.Integer, java.lang.Integer)
     */
	public void updateReminderAccount(final Integer id, final Integer accountID)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_ACCOUNT
										 + " = " + accountID 
										 + " WHERE " + DBNames.CN_REMINDER_ID
										 + " = " + id;

		try
		{
			final int result = DAFactory.getConnectionManager().update(sqlStr);
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

	
	/* (non-Javadoc)
     * @see org.jpas.da.ReminderDA#updateReminderPayee(java.lang.Integer, java.lang.String)
     */
	public void updateReminderPayee(final Integer id, final String payee)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_PAYEE
										 + " = '" + payee 
										 + "' WHERE " + DBNames.CN_REMINDER_ID
											 + " = " + id;

		try
		{
			final int result = DAFactory.getConnectionManager().update(sqlStr);
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

	/* (non-Javadoc)
     * @see org.jpas.da.ReminderDA#updateReminderMemo(java.lang.Integer, java.lang.String)
     */
	public void updateReminderMemo(final Integer id, final String memo)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_MEMO
										 + " = '" + memo 
										 + "' WHERE " + DBNames.CN_REMINDER_ID
											 + " = " + id;

		try
		{
			final int result = DAFactory.getConnectionManager().update(sqlStr);
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

	/* (non-Javadoc)
     * @see org.jpas.da.ReminderDA#updateReminderDate(java.lang.Integer, java.sql.Date)
     */
	public void updateReminderDate(final Integer id, final Date date)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_DATE
										 + " = " + date 
										 + " WHERE " + DBNames.CN_REMINDER_ID
											 + " = " + id;

		try
		{
			final int result = DAFactory.getConnectionManager().update(sqlStr);
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

	/* (non-Javadoc)
     * @see org.jpas.da.ReminderDA#updateReminderAmountMethod(java.lang.Integer, org.jpas.da.ReminderDAImpl.AmountMethod)
     */
	public void updateReminderAmountMethod(final Integer id, final AmountMethod amountMethod)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_AMOUNT_METHOD
										 + " = " + amountMethod.dbValue
										 + " WHERE " + DBNames.CN_REMINDER_ID
											 + " = " + id;

		try
		{
			final int result = DAFactory.getConnectionManager().update(sqlStr);
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

	/* (non-Javadoc)
     * @see org.jpas.da.ReminderDA#updateReminderRepeatMethod(java.lang.Integer, org.jpas.da.ReminderDAImpl.RepeatMethod)
     */
	public void updateReminderRepeatMethod(final Integer id, final RepeatMethod repeatMethod)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_REPEAT_METHOD
										 + " = " + repeatMethod.dbValue 
										 + " WHERE " + DBNames.CN_REMINDER_ID
											 + " = " + id;

		try
		{
			final int result = DAFactory.getConnectionManager().update(sqlStr);
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

	/* (non-Javadoc)
     * @see org.jpas.da.ReminderDA#updateReminderRepeatValue(java.lang.Integer, int)
     */
	public void updateReminderRepeatValue(final Integer id, final int repeatValue)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_REMINDER
										 + " SET " + DBNames.CN_REMINDER_REPEAT_VALUE
										 + " = " + repeatValue 
										 + " WHERE " + DBNames.CN_REMINDER_ID
											 + " = " + id;

		try
		{
			final int result = DAFactory.getConnectionManager().update(sqlStr);
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
	
	/* (non-Javadoc)
     * @see org.jpas.da.ReminderDA#doesReminderExist(java.lang.Integer)
     */
	public boolean doesReminderExist(final Integer id)
	{
		final String sqlStr = "SELECT " + DBNames.CN_REMINDER_ID
								+ " FROM " + DBNames.TN_REMINDER
								+ " WHERE " + DBNames.CN_REMINDER_ID
								+ " = " + id;
		try
		{
			return DAFactory.getConnectionManager().query(sqlStr).next();
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error("SQLException while loading account name!", sqle);
			throw new RuntimeException("Unable to load transaction id's!", sqle);
		}	
	}

}
