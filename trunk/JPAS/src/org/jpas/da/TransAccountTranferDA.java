/*
 * Created on Sep 11, 2004 - 9:59:59 AM
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
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.*;

/**
 * @author Owner
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TransAccountTranferDA
{
    private static Logger defaultLogger = Logger.getLogger(TransAccountTranferDA.class);
    
    private static TransAccountTranferDA instance = new TransAccountTranferDA();
    
    public static TransAccountTranferDA getInstance()
    {
        return instance;
    }
    
	public static interface TransAccountTranferHandler
	{
		public void setData(final long amount);
	}
   
    private TransAccountTranferDA()
    {
    }

    
    public void loadTransAccount(final Integer transactionID, final Integer accountID, final TransAccountTranferHandler handler)
    {
		final String sqlStr = "SELECT * FROM " + DBNames.TN_TRANSACTION_ACCOUNT_MAP
                + " WHERE " + DBNames.CN_TAM_TRANSACTION_ID + " IS " + transactionID
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
                defaultLogger.error("Transaction/Account ids not found: \"" + sqlStr
                        + "\"");
                throw new RuntimeException("Transaction/Account ids not found: \""
                        + sqlStr + "\"");
            }
        } catch (final SQLException sqle)
        {
            defaultLogger.error(sqlStr, sqle);
            throw new RuntimeException(sqlStr, sqle);
        }
    }

    public void deleteTransAccountMapping(final Integer transactionID, final Integer accountID)
    {
		final String sqlStr = "DELETE FROM " + DBNames.TN_TRANSACTION_ACCOUNT_MAP
                + " WHERE " + DBNames.CN_TAM_TRANSACTION_ID + " IS " + transactionID
                + " AND " + DBNames.CN_TAM_ACCOUNT_ID + " IS " + accountID;
        try
        {
            if(ConnectionManager.getInstance().update(sqlStr) < 1)
            {
                defaultLogger.error("TransAccountMap not found: \"" + sqlStr + "\"");
                throw new RuntimeException("TransAccountMap not found: \"" + sqlStr + "\"");
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
		    if(rs.next())
		    {
		        return ((Long)rs.getObject(1)).longValue();
		    }

		    return 0;
		}
		catch(final SQLException sqle)
		{
            defaultLogger.error("SQLException while loading Trans-Account Mapping for total!",
                    sqle);
            throw new RuntimeException("Unable to load account id's!", sqle);

		}
    }
    
    public long getAccountBalance(final Integer accountID)
    {
		final String outflowsSql = "SELECT SUM(" 
		    	+ DBNames.TN_TRANSACTION_ACCOUNT_MAP + "." + DBNames.CN_TAM_AMOUNT 
		    	+ ") FROM " + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " JOIN " + DBNames.TN_TRANSACTION 
		    	+ " ON " + DBNames.TN_TRANSACTION + "." + DBNames.CN_TRANSACTION_ID
		    	+ " WHERE " 
		    	+ DBNames.TN_TRANSACTION + "." + DBNames.CN_TRANSACTION_ACCOUNT
		    	+ " IS" + accountID;
		
		final String inflowsSql = "SELECT SUM(" 
		    	+ DBNames.TN_TRANSACTION_ACCOUNT_MAP + "." + DBNames.CN_TAM_AMOUNT
		    	+ ") FROM " + DBNames.TN_TRANSACTION_ACCOUNT_MAP
		    	+ " WHERE " + DBNames.CN_TAM_ACCOUNT_ID + " IS " + accountID;
		
		final long outflowsTotal;
		final long inflowsTotal;
        try
        {
            final ResultSet rs = ConnectionManager.getInstance().query(outflowsSql);
            if (rs.next())
            {
                outflowsTotal = ((Long) rs.getObject(1)).longValue();
            }
            else
            {
                outflowsTotal = 0;
            }
        }
        catch (final SQLException sqle)
        {
            defaultLogger.error(
                            "SQLException while loading Trans-Account Mapping for total!",
                            sqle);
            throw new RuntimeException("Unable to load account id's!", sqle);
        }

        try
        {
            final ResultSet rs = ConnectionManager.getInstance().query(inflowsSql);
            if (rs.next())
            {
                inflowsTotal = ((Long) rs.getObject(1)).longValue();
            }
            else
            {
                inflowsTotal = 0;
            }
        }
        catch (final SQLException sqle)
        {
            defaultLogger.error(
                            "SQLException while loading Trans-Account Mapping for total!",
                            sqle);
            throw new RuntimeException("Unable to load account id's!", sqle);
        }

        return outflowsTotal + inflowsTotal;
    }
    
    public Integer[] getAllTransAccountTranfers(final Integer transactionID)
    {
		final String sqlStr = "SELECT " + DBNames.CN_TAM_ACCOUNT_ID + " FROM "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP + " WHERE "
                + DBNames.CN_TAM_TRANSACTION_ID + " IS '" 
                + transactionID + "'";
        
		try
        {
            final ResultSet rs = ConnectionManager.getInstance().query(sqlStr);
            final List<Integer> idList = new ArrayList<Integer>();
            while (rs.next())
            {
                idList.add((Integer)rs.getObject(DBNames.CN_TAM_ACCOUNT_ID));
            }
            return idList.toArray(new Integer[idList.size()]);
        } 
		catch (final SQLException sqle)
        {
            defaultLogger.error("SQLException while loading Trans-Account Mapping!",
                    sqle);
            throw new RuntimeException("Unable to load Trans-Account Mapping!", sqle);
        }    
    }
    
    private static void unitTest_Create()
    {
        
    }
    
    public static void main(String[] args)
    {
		BasicConfigurator.configure();
		
		unitTest_Create();
    }
}
