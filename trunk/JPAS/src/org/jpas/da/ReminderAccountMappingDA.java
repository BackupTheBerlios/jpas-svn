/*
 * Created on Sep 18, 2004 - 4:20:49 PM
 * 
 * Title: JPAS
 * Description: Java based Personal Accounting System
 * Copyright: Copyright (c) 2004
 * License: Distributed under the terms of the GPL v2
 * @author Justin Smith
 * @version 1.0
 */
package org.jpas.da;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class ReminderAccountMappingDA
{
    private static Logger defaultLogger = Logger.getLogger(ReminderAccountMappingDA.class);
    
    private static ReminderAccountMappingDA instance = new ReminderAccountMappingDA();
    
    public static ReminderAccountMappingDA getInstance()
    {
        return instance;
    }
    
	public static interface ReminderAccountTranferHandler
	{
		public void setData(final long amount);
	}
   
    private ReminderAccountMappingDA()
    {
    }

    public void createReminderAccountMapping(final Integer reminderID, final Integer accountID, final long amount)
    {
		final String sqlStr = "INSERT INTO " + DBNames.TN_REMINDER_ACCOUNT_MAP + " ( "
                + DBNames.CN_RAM_REMINDER_ID + " , "
                + DBNames.CN_RAM_ACCOUNT_ID + " , "
                + DBNames.CN_RAM_AMOUNT 
                + " ) VALUES ( " + reminderID + " , "
                + accountID + " , " + amount + ")";
        try
        {
            final int result = ConnectionManager.getInstance().update(sqlStr);
            if (result < 1)
            {
                defaultLogger.error("Unable to create reminder/account mapping: \"" + sqlStr
                        + "\"");
                throw new RuntimeException(sqlStr);
            }
        }
        catch (final SQLException sqle)
        {
            defaultLogger.error(sqlStr, sqle);
            throw new RuntimeException(sqlStr, sqle);
        }
    }
    
    public void loadReminderAccountMapping(final Integer reminderID, final Integer accountID, final ReminderAccountTranferHandler handler)
    {
		final String sqlStr = "SELECT * FROM " + DBNames.TN_REMINDER_ACCOUNT_MAP
                + " WHERE " + DBNames.CN_RAM_REMINDER_ID + " IS " + reminderID
                + " AND " + DBNames.CN_RAM_ACCOUNT_ID + " IS " + accountID;
        try
        {
            final ResultSet rs = ConnectionManager.getInstance().query(sqlStr);
            if (rs.next())
            {
                handler.setData(rs.getLong(DBNames.CN_RAM_AMOUNT));
            } 
            else
            {
                defaultLogger.error("Reminder/Account ids not found: \"" + sqlStr
                        + "\"");
                throw new RuntimeException("Reminder/Account ids not found: \""
                        + sqlStr + "\"");
            }
        } catch (final SQLException sqle)
        {
            defaultLogger.error(sqlStr, sqle);
            throw new RuntimeException(sqlStr, sqle);
        }
    }

    public void deleteReminderAccountMapping(final Integer reminderID, final Integer accountID)
    {
		final String sqlStr = "DELETE FROM " + DBNames.TN_REMINDER_ACCOUNT_MAP
                + " WHERE " + DBNames.CN_RAM_REMINDER_ID + " IS " + reminderID
                + " AND " + DBNames.CN_RAM_ACCOUNT_ID + " IS " + accountID;
        try
        {
            if(ConnectionManager.getInstance().update(sqlStr) < 1)
            {
                defaultLogger.error("ReminderAccountMap not found: \"" + sqlStr + "\"");
                throw new RuntimeException("ReminderAccountMap not found: \"" + sqlStr + "\"");
            }
        }
        catch (final SQLException sqle)
        {
            defaultLogger.error(sqlStr, sqle);
            throw new RuntimeException(sqlStr, sqle);
        }
    }
    
    public void updateReminderAccountMapping(final Integer reminderID,
            final Integer accountID, final long amount)
    {
        final String sqlStr = "UPDATE " + DBNames.TN_REMINDER_ACCOUNT_MAP
                + " SET " + DBNames.CN_RAM_AMOUNT + " = " + amount + " WHERE "
                + DBNames.CN_RAM_REMINDER_ID + " IS " + reminderID
                + " AND " + DBNames.CN_RAM_ACCOUNT_ID + " IS " + accountID;

        try
        {
            final int result = ConnectionManager.getInstance().update(sqlStr);
            if (result < 1)
            {
                defaultLogger.error("Reminder/Account id's not found: \""
                        + sqlStr + "\"");
                throw new RuntimeException(
                        "Reminder/Account id's not found: \"" + sqlStr
                                + "\"");
            }
        }
        catch (final SQLException sqle)
        {
            defaultLogger.error(sqlStr, sqle);
            throw new RuntimeException(sqlStr, sqle);
        }
    }

    
    public long getReminderAmount(final Integer reminderID)
    {
		final String sqlStr = "SELECT SUM(" + DBNames.CN_RAM_AMOUNT + ") FROM "
                + DBNames.TN_REMINDER_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_RAM_REMINDER_ID + " IS '" + reminderID + "'";

		try
		{
		    final ResultSet rs = ConnectionManager.getInstance().query(sqlStr);
		    if(rs.next())
		    {
		        return rs.getLong(1);
		    }

		    return 0;
		}
		catch(final SQLException sqle)
		{
            defaultLogger.error("SQLException while loading Reminder-Account Mapping for total!",
                    sqle);
            throw new RuntimeException("Unable to load account id's!", sqle);

		}
    }
}
