package org.jpas.da;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.*;

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
public class TransAccountMappingDA
{
    private static Logger defaultLogger = Logger
            .getLogger(TransAccountMappingDA.class);

    private static TransAccountMappingDA instance = new TransAccountMappingDA();

    public static TransAccountMappingDA getInstance()
    {
        return instance;
    }

    public static interface TransAccountTranferHandler
    {
        public void setData(final long amount);
    }

    private TransAccountMappingDA()
    {
    }

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
            final int result = ConnectionManager.getInstance().update(sqlStr);
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

    public void loadTransAccountMapping(final Integer transactionID,
            final Integer accountID, final TransAccountTranferHandler handler)
    {
        final String sqlStr = "SELECT * FROM "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_TAM_TRANSACTION_ID + " IS " + transactionID
                + " AND " + DBNames.CN_TAM_ACCOUNT_ID + " IS " + accountID;
        try
        {
            final ResultSet rs = ConnectionManager.getInstance().query(sqlStr);
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

    public void updateTAMAccount(final Integer transactionID,
            final Integer accountID, final Integer newAccountID)
    {
        final String sqlStr = "UPDATE " + DBNames.TN_TRANSACTION_ACCOUNT_MAP
                + " SET " + DBNames.CN_TAM_ACCOUNT_ID + " = " + newAccountID + " WHERE "
                + DBNames.CN_TAM_TRANSACTION_ID + " IS " + transactionID
                + " AND " + DBNames.CN_TAM_ACCOUNT_ID + " IS " + accountID;

        try
        {
            final int result = ConnectionManager.getInstance().update(sqlStr);
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
    
    public void updateTAMAmount(final Integer transactionID,
            final Integer accountID, final long amount)
    {
        final String sqlStr = "UPDATE " + DBNames.TN_TRANSACTION_ACCOUNT_MAP
                + " SET " + DBNames.CN_TAM_AMOUNT + " = " + amount + " WHERE "
                + DBNames.CN_TAM_TRANSACTION_ID + " IS " + transactionID
                + " AND " + DBNames.CN_TAM_ACCOUNT_ID + " IS " + accountID;

        try
        {
            final int result = ConnectionManager.getInstance().update(sqlStr);
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

    public void deleteTransAccountMapping(final Integer transactionID,
            final Integer accountID)
    {
        final String sqlStr = "DELETE FROM "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_TAM_TRANSACTION_ID + " IS " + transactionID
                + " AND " + DBNames.CN_TAM_ACCOUNT_ID + " IS " + accountID;
        try
        {
            if (ConnectionManager.getInstance().update(sqlStr) < 1)
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

    public long getTransactionAmount(final Integer transactionID)
    {
        final String sqlStr = "SELECT SUM(" + DBNames.CN_TAM_AMOUNT + ") FROM "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_TAM_TRANSACTION_ID + " IS '" + transactionID + "'";

        try
        {
            final ResultSet rs = ConnectionManager.getInstance().query(sqlStr);
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
                + " IS " + accountID;

        final String inflowsSql = "SELECT SUM("
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + "."
                + DBNames.CN_TAM_AMOUNT + ") FROM "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_TAM_ACCOUNT_ID + " IS " + accountID;

        final long outflowsTotal;
        final long inflowsTotal;
        try
        {
            final ResultSet rs = ConnectionManager.getInstance().query(
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
            final ResultSet rs = ConnectionManager.getInstance().query(
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

    public Integer[] getAllTranfersForAccount(final Integer accountID)
    {
        final String sqlStr = "SELECT " + DBNames.CN_TAM_TRANSACTION_ID + " FROM "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_TAM_ACCOUNT_ID + " = " + accountID;

        try
        {
            final ResultSet rs = ConnectionManager.getInstance().query(sqlStr);
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

    
    public Integer[] getAllTranfersForTransaction(final Integer transactionID)
    {
        final String sqlStr = "SELECT " + DBNames.CN_TAM_ACCOUNT_ID + " FROM "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_TAM_TRANSACTION_ID + " IS '" + transactionID + "'";

        try
        {
            final ResultSet rs = ConnectionManager.getInstance().query(sqlStr);
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

    public boolean doesTransAccountTransferExist(final Integer transId, final Integer accountId)
    {
		final String sqlStr = "SELECT " + DBNames.CN_TAM_TRANSACTION_ID
								+ " FROM " + DBNames.TN_TRANSACTION_ACCOUNT_MAP
								+ " WHERE " + DBNames.CN_TAM_TRANSACTION_ID
								+ " IS " + transId 
								+ " AND " + DBNames.CN_TAM_ACCOUNT_ID
								+ " IS " + accountId;
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
        getInstance().createTransAccountMapping(new Integer(0), new Integer(3),
                435);
        getInstance().createTransAccountMapping(new Integer(0), new Integer(4),
                755);
    }

    public static void unitTest_GetAmount()
    {
        System.out.println("Amount: "
                + getInstance().getTransactionAmount(new Integer(0)));
    }

    public static void unitTest_GetBalance()
    {
        System.out.println("Balance: "
                + getInstance().getAccountBalance(new Integer(0)));
        System.out.println("Balance: "
                + getInstance().getAccountBalance(new Integer(1)));
        System.out.println("Balance: "
                + getInstance().getAccountBalance(new Integer(2)));
        System.out.println("Balance: "
                + getInstance().getAccountBalance(new Integer(3)));
    }

    public static void main(String[] args)
    {
        BasicConfigurator.configure();

        //unitTest_Create();
        //unitTest_GetBalance();
        unitTest_GetAmount();
    }
}
