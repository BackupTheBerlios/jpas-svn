/**
 * Created on Sep 18, 2004 - 4:20:49 PM
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jpas.da.DAFactory;
import org.jpas.da.DBNames;
import org.jpas.da.ReminderAccountMappingDA;

public class ReminderAccountMappingDAImpl extends ReminderAccountMappingDA
{
    //TODO This is just a copy of the HSQLDB implementation    
    
    private static Logger defaultLogger = Logger.getLogger(ReminderAccountMappingDAImpl.class);
    
   
    public ReminderAccountMappingDAImpl()
    {
    }

    /* (non-Javadoc)
     * @see org.jpas.da.ReminderAccountMappingDA#createReminderAccountMapping(java.lang.Integer, java.lang.Integer, long)
     */
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
            final int result = DAFactory.getConnectionManager().update(sqlStr);
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
    
    /* (non-Javadoc)
     * @see org.jpas.da.ReminderAccountMappingDA#getAllReminderAccountTranfers(java.lang.Integer)
     */
    public Integer[] getAllReminderAccountTranfers(final Integer reminderID)
    {
        final String sqlStr = "SELECT " + DBNames.CN_RAM_ACCOUNT_ID + " FROM "
                + DBNames.TN_REMINDER_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_RAM_REMINDER_ID + " = '" + reminderID + "'";

        try
        {
            final ResultSet rs = DAFactory.getConnectionManager().query(sqlStr);
            final List<Integer> idList = new ArrayList<Integer>();
            while (rs.next())
            {
                idList.add((Integer) rs.getObject(DBNames.CN_RAM_ACCOUNT_ID));
            }
            return idList.toArray(new Integer[idList.size()]);
        }
        catch (final SQLException sqle)
        {
            defaultLogger.error(
                    "SQLException while loading Reminder-Account Mapping!", sqle);
            throw new RuntimeException("Unable to load Reminder-Account Mapping!",
                    sqle);
        }
    }

    
    /* (non-Javadoc)
     * @see org.jpas.da.ReminderAccountMappingDA#loadReminderAccountMapping(java.lang.Integer, java.lang.Integer, org.jpas.da.ReminderAccountMappingDAImpl.ReminderAccountTranferHandler)
     */
    public void loadReminderAccountMapping(final Integer reminderID, final Integer accountID, final ReminderAccountTranferHandler handler)
    {
		final String sqlStr = "SELECT * FROM " + DBNames.TN_REMINDER_ACCOUNT_MAP
                + " WHERE " + DBNames.CN_RAM_REMINDER_ID + " = " + reminderID
                + " AND " + DBNames.CN_RAM_ACCOUNT_ID + " = " + accountID;
        try
        {
            final ResultSet rs = DAFactory.getConnectionManager().query(sqlStr);
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

    /* (non-Javadoc)
     * @see org.jpas.da.ReminderAccountMappingDA#deleteReminderAccountMapping(java.lang.Integer, java.lang.Integer)
     */
    public void deleteReminderAccountMapping(final Integer reminderID, final Integer accountID)
    {
		final String sqlStr = "DELETE FROM " + DBNames.TN_REMINDER_ACCOUNT_MAP
                + " WHERE " + DBNames.CN_RAM_REMINDER_ID + " = " + reminderID
                + " AND " + DBNames.CN_RAM_ACCOUNT_ID + " = " + accountID;
        try
        {
            if(DAFactory.getConnectionManager().update(sqlStr) < 1)
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
    
    /* (non-Javadoc)
     * @see org.jpas.da.ReminderAccountMappingDA#updateReminderAccountMapping(java.lang.Integer, java.lang.Integer, long)
     */
    public void updateReminderAccountMapping(final Integer reminderID,
            final Integer accountID, final long amount)
    {
        final String sqlStr = "UPDATE " + DBNames.TN_REMINDER_ACCOUNT_MAP
                + " SET " + DBNames.CN_RAM_AMOUNT + " = " + amount + " WHERE "
                + DBNames.CN_RAM_REMINDER_ID + " = " + reminderID
                + " AND " + DBNames.CN_RAM_ACCOUNT_ID + " = " + accountID;

        try
        {
            final int result = DAFactory.getConnectionManager().update(sqlStr);
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

    
    /* (non-Javadoc)
     * @see org.jpas.da.ReminderAccountMappingDA#getReminderAmount(java.lang.Integer)
     */
    public long getReminderAmount(final Integer reminderID)
    {
		final String sqlStr = "SELECT SUM(" + DBNames.CN_RAM_AMOUNT + ") FROM "
                + DBNames.TN_REMINDER_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_RAM_REMINDER_ID + " = '" + reminderID + "'";

		try
		{
		    final ResultSet rs = DAFactory.getConnectionManager().query(sqlStr);
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
