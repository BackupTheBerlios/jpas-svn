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

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.*;
import org.jpas.model.*;

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
    
    
    public long getTotalAmount(final Integer transactionID)
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
		    else
		    {
		        return 0;
		    }
		}
		catch(final SQLException sqle)
		{
            defaultLogger.error("SQLException while loading Trans-Account Mapping for total!",
                    sqle);
            throw new RuntimeException("Unable to load account id's!", sqle);

		}
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
            final List idList = new ArrayList();
            while (rs.next())
            {
                idList.add(rs.getObject(DBNames.CN_TAM_ACCOUNT_ID));
            }
            return (Integer[]) idList.toArray(new Integer[idList.size()]);
        } 
		catch (final SQLException sqle)
        {
            defaultLogger.error("SQLException while loading Trans-Account Mapping!",
                    sqle);
            throw new RuntimeException("Unable to load Trans-Account Mapping!", sqle);
        }    
    }
    
    
    
    public static void main(String[] args)
    {
    }
}
