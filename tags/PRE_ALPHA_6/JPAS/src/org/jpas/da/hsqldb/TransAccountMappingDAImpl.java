package org.jpas.da.hsqldb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jpas.da.DAFactory;
import org.jpas.da.DBNames;
import org.jpas.da.TransAccountMappingDA;

/**
 * Created on Sep 11, 2004 - 9:59:59 AM
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
public class TransAccountMappingDAImpl extends TransAccountMappingDA
{
    private static Logger defaultLogger = Logger
            .getLogger(TransAccountMappingDAImpl.class);

    public TransAccountMappingDAImpl()
    {
    }

    /* (non-Javadoc)
     * @see org.jpas.da.TransAccountMappingDA#createTransAccountMapping(java.lang.Integer, java.lang.Integer, long)
     */
    public void createTransAccountMapping(final Integer transactionID,
            final Integer accountID, final long amount)
    {
        final String sqlStr = "INSERT INTO "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " ( "
                + DBNames.CN_TAM_TRANSACTION_ID + " , "
                + DBNames.CN_TAM_ACCOUNT_ID + " , " + DBNames.CN_TAM_AMOUNT
                + " ) VALUES ( " + transactionID + " , " + accountID + " , "
                + amount + ")";
        try
        {
            final int result = DAFactory.getConnectionManager().update(sqlStr);
            if (result < 1)
            {
                defaultLogger
                        .error("Unable to create transaction/account mapping: \""
                                + sqlStr + "\"");
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
     * @see org.jpas.da.TransAccountMappingDA#loadTransAccountMapping(java.lang.Integer, java.lang.Integer, org.jpas.da.TransAccountMappingDAImpl.TransAccountTranferHandler)
     */
    public void loadTransAccountMapping(final Integer transactionID,
            final Integer accountID, final TransAccountTranferHandler handler)
    {
        final String sqlStr = "SELECT * FROM "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_TAM_TRANSACTION_ID + " = " + transactionID
                + " AND " + DBNames.CN_TAM_ACCOUNT_ID + " = " + accountID;
        try
        {
            final ResultSet rs = DAFactory.getConnectionManager().query(sqlStr);
            if (rs.next())
            {
                handler.setData(rs.getLong(DBNames.CN_TAM_AMOUNT));
            }
            else
            {
                defaultLogger.error("Transaction/Account ids not found: \""
                        + sqlStr + "\"");
                throw new RuntimeException(
                        "Transaction/Account ids not found: \"" + sqlStr + "\"");
            }
        }
        catch (final SQLException sqle)
        {
            defaultLogger.error(sqlStr, sqle);
            throw new RuntimeException(sqlStr, sqle);
        }
    }

    /* (non-Javadoc)
     * @see org.jpas.da.TransAccountMappingDA#updateTAMAccount(java.lang.Integer, java.lang.Integer, java.lang.Integer)
     */
    public void updateTAMAccount(final Integer transactionID,
            final Integer accountID, final Integer newAccountID)
    {
        final String sqlStr = "UPDATE " + DBNames.TN_TRANSACTION_ACCOUNT_MAP
                + " SET " + DBNames.CN_TAM_ACCOUNT_ID + " = " + newAccountID + " WHERE "
                + DBNames.CN_TAM_TRANSACTION_ID + " = " + transactionID
                + " AND " + DBNames.CN_TAM_ACCOUNT_ID + " = " + accountID;

        try
        {
            final int result = DAFactory.getConnectionManager().update(sqlStr);
            if (result < 1)
            {
                defaultLogger.error("Transaction/Account id's not found: \""
                        + sqlStr + "\"");
                throw new RuntimeException(
                        "Transaction/Account id's not found: \"" + sqlStr
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
     * @see org.jpas.da.TransAccountMappingDA#updateTAMAmount(java.lang.Integer, java.lang.Integer, long)
     */
    public void updateTAMAmount(final Integer transactionID,
            final Integer accountID, final long amount)
    {
        final String sqlStr = "UPDATE " + DBNames.TN_TRANSACTION_ACCOUNT_MAP
                + " SET " + DBNames.CN_TAM_AMOUNT + " = " + amount + " WHERE "
                + DBNames.CN_TAM_TRANSACTION_ID + " = " + transactionID
                + " AND " + DBNames.CN_TAM_ACCOUNT_ID + " = " + accountID;

        try
        {
            final int result = DAFactory.getConnectionManager().update(sqlStr);
            if (result < 1)
            {
                defaultLogger.error("Transaction/Account id's not found: \""
                        + sqlStr + "\"");
                throw new RuntimeException(
                        "Transaction/Account id's not found: \"" + sqlStr
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
     * @see org.jpas.da.TransAccountMappingDA#deleteTransAccountMapping(java.lang.Integer, java.lang.Integer)
     */
    public void deleteTransAccountMapping(final Integer transactionID,
            final Integer accountID)
    {
        final String sqlStr = "DELETE FROM "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_TAM_TRANSACTION_ID + " = " + transactionID
                + " AND " + DBNames.CN_TAM_ACCOUNT_ID + " = " + accountID;
        try
        {
            if (DAFactory.getConnectionManager().update(sqlStr) < 1)
            {
                defaultLogger.error("TransAccountMap not found: \"" + sqlStr
                        + "\"");
                throw new RuntimeException("TransAccountMap not found: \""
                        + sqlStr + "\"");
            }
        }
        catch (final SQLException sqle)
        {
            defaultLogger.error(sqlStr, sqle);
            throw new RuntimeException(sqlStr, sqle);
        }
    }

    /* (non-Javadoc)
     * @see org.jpas.da.TransAccountMappingDA#getTransactionAmount(java.lang.Integer)
     */
    public long getTransactionAmount(final Integer transactionID)
    {
        final String sqlStr = "SELECT SUM(" + DBNames.CN_TAM_AMOUNT + ") FROM "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_TAM_TRANSACTION_ID + " = '" + transactionID + "'";

        try
        {
            final ResultSet rs = DAFactory.getConnectionManager().query(sqlStr);
            if (rs.next())
            {
                return rs.getLong(1);
            }

            return 0;
        }
        catch (final SQLException sqle)
        {
            defaultLogger
                    .error(
                            "SQLException while loading Trans-Account Mapping for total!",
                            sqle);
            throw new RuntimeException("Unable to load account id's!", sqle);

        }
    }

    /* (non-Javadoc)
     * @see org.jpas.da.TransAccountMappingDA#getAccountBalance(java.lang.Integer)
     */
    public long getAccountBalance(final Integer accountID)
    {
        final String outflowsSql = "SELECT SUM("
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + "."
                + DBNames.CN_TAM_AMOUNT + ") FROM "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " , "
                + DBNames.TN_TRANSACTION + " WHERE " + DBNames.TN_TRANSACTION
                + "." + DBNames.CN_TRANSACTION_ID + " = "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + "."
                + DBNames.CN_TAM_TRANSACTION_ID + " AND "
                + DBNames.TN_TRANSACTION + "." + DBNames.CN_TRANSACTION_ACCOUNT
                + " = " + accountID;

        final String inflowsSql = "SELECT SUM("
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + "."
                + DBNames.CN_TAM_AMOUNT + ") FROM "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_TAM_ACCOUNT_ID + " = " + accountID;

        final long outflowsTotal;
        final long inflowsTotal;
        try
        {
            final ResultSet rs = DAFactory.getConnectionManager().query(
                    outflowsSql);
            if (rs.next())
            {
                //final Long val = (Long) rs.getObject(1);
                outflowsTotal = rs.getLong(1);
            }
            else
            {
                outflowsTotal = 0;
            }
            defaultLogger.debug("Outflows: " + outflowsTotal);
        }
        catch (final SQLException sqle)
        {
            defaultLogger
                    .error(
                            "SQLException while loading Trans-Account Mapping for total!",
                            sqle);
            throw new RuntimeException("Unable to load account id's!", sqle);
        }

        try
        {
            final ResultSet rs = DAFactory.getConnectionManager().query(
                    inflowsSql);
            if (rs.next())
            {
                inflowsTotal = rs.getLong(1);
            }
            else
            {
                inflowsTotal = 0;
            }
            defaultLogger.debug("Inflows: " + inflowsTotal);

        }
        catch (final SQLException sqle)
        {
            defaultLogger
                    .error(
                            "SQLException while loading Trans-Account Mapping for total!",
                            sqle);
            throw new RuntimeException("Unable to load account id's!", sqle);
        }

        return inflowsTotal - outflowsTotal;
    }

    /* (non-Javadoc)
     * @see org.jpas.da.TransAccountMappingDA#getAllTranfersForAccount(java.lang.Integer)
     */
    public Integer[] getAllTranfersForAccount(final Integer accountID)
    {
        final String sqlStr = "SELECT " + DBNames.CN_TAM_TRANSACTION_ID + " FROM "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_TAM_ACCOUNT_ID + " = " + accountID;

        try
        {
            final ResultSet rs = DAFactory.getConnectionManager().query(sqlStr);
            final List<Integer> idList = new ArrayList<Integer>();
            while (rs.next())
            {
                idList.add((Integer) rs.getObject(DBNames.CN_TAM_TRANSACTION_ID));
            }
            return idList.toArray(new Integer[idList.size()]);
        }
        catch (final SQLException sqle)
        {
            defaultLogger.error(
                    "SQLException while loading Trans-Account Mapping!", sqle);
            throw new RuntimeException("Unable to load Trans-Account Mapping!",
                    sqle);
        }
    }

    
    /* (non-Javadoc)
     * @see org.jpas.da.TransAccountMappingDA#getAllTranfersForTransaction(java.lang.Integer)
     */
    public Integer[] getAllTranfersForTransaction(final Integer transactionID)
    {
        final String sqlStr = "SELECT " + DBNames.CN_TAM_ACCOUNT_ID + " FROM "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_TAM_TRANSACTION_ID + " = '" + transactionID + "'";

        try
        {
            final ResultSet rs = DAFactory.getConnectionManager().query(sqlStr);
            final List<Integer> idList = new ArrayList<Integer>();
            while (rs.next())
            {
                idList.add((Integer) rs.getObject(DBNames.CN_TAM_ACCOUNT_ID));
            }
            return idList.toArray(new Integer[idList.size()]);
        }
        catch (final SQLException sqle)
        {
            defaultLogger.error(
                    "SQLException while loading Trans-Account Mapping!", sqle);
            throw new RuntimeException("Unable to load Trans-Account Mapping!",
                    sqle);
        }
    }

    /* (non-Javadoc)
     * @see org.jpas.da.TransAccountMappingDA#doesTransAccountTransferExist(java.lang.Integer, java.lang.Integer)
     */
    public boolean doesTransAccountTransferExist(final Integer transId, final Integer accountId)
    {
		final String sqlStr = "SELECT " + DBNames.CN_TAM_TRANSACTION_ID
								+ " FROM " + DBNames.TN_TRANSACTION_ACCOUNT_MAP
								+ " WHERE " + DBNames.CN_TAM_TRANSACTION_ID
								+ " = " + transId 
								+ " AND " + DBNames.CN_TAM_ACCOUNT_ID
								+ " = " + accountId;
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
