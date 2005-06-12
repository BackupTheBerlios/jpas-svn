/**
 * Created on Sep 8, 2004 - 7:08:04 PM
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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

public class TransactionDA
{
    private static final Logger defaultLogger = Logger.getLogger(TransactionDA.class);

    private static final TransactionDA instance = new TransactionDA();
    
    public static TransactionDA getInstance()
    {
        return instance;
    }

    private TransactionDA()
    {
    }
    
	public static interface TransactionHandler
	{
		public void setData(final Integer accountId, final String payee, final String memo, final String num, final Date date);
	}
    
    
	public void loadTransaction(final Integer id, final TransactionHandler handler)
	{
		final String sqlStr = "SELECT * FROM " + DBNames.TN_TRANSACTION
										 + " WHERE " + DBNames.CN_TRANSACTION_ID
										 + " = " + id;
		try
		{
			final ResultSet rs =  ConnectionManager.getInstance().query(sqlStr);

			if(rs.next())
			{
				handler.setData(new Integer(rs.getInt(DBNames.CN_TRANSACTION_ACCOUNT)),
								rs.getString(DBNames.CN_TRANSACTION_PAYEE),
								rs.getString(DBNames.CN_TRANSACTION_MEMO),
								rs.getString(DBNames.CN_TRANSACTION_NUM),
								rs.getDate(DBNames.CN_TRANSACTION_DATE));
			}
			else
			{
				defaultLogger.error("Transaction id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Transaction id not found: \""+ sqlStr +"\"");
			}
				
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateTransaction(final Integer id, final String payee, final String memo, final String num, final Date date)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_TRANSACTION
										 + " SET " + DBNames.CN_TRANSACTION_PAYEE
										 + " = '" + payee 
										 + "' , " + DBNames.CN_TRANSACTION_MEMO
										 + " = '" + memo
										 + "' , " + DBNames.CN_TRANSACTION_NUM
										 + " = '" + num
										 + "' , " + DBNames.CN_TRANSACTION_DATE
										 + " = '" + date
										 + "' WHERE " + DBNames.CN_TRANSACTION_ID
											 + " = " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Transaction id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Transaction id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateTransactionPayee(final Integer id, final String payee)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_TRANSACTION
										 + " SET " + DBNames.CN_TRANSACTION_PAYEE
										 + " = '" + payee 
										 + "' WHERE " + DBNames.CN_TRANSACTION_ID
											 + " = " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Transaction id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Transaction id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateTransactionMemo(final Integer id, final String memo)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_TRANSACTION
										 + " SET " + DBNames.CN_TRANSACTION_MEMO
										 + " = '" + memo
										 + "' WHERE " + DBNames.CN_TRANSACTION_ID
											 + " = " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Transaction id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Transaction id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateTransactionNum(final Integer id, final String num)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_TRANSACTION
										 + " SET " + DBNames.CN_TRANSACTION_NUM
										 + " = '" + num
										 + "' WHERE " + DBNames.CN_TRANSACTION_ID
											 + " = " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Transaction id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Transaction id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateTransactionDate(final Integer id, final Date date)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_TRANSACTION
										 + " SET " + DBNames.CN_TRANSACTION_DATE
										 + " = '" + date
										 + "' WHERE " + DBNames.CN_TRANSACTION_ID
											 + " = " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Transaction id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Transaction id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public Integer createTransaction(final Integer accountId, final String payee, final String memo, final String num, final Date date)
	{
		final String sqlSequenceStr = "CALL NEXT VALUE FOR " + DBNames.SEQ_TRANSACTION_ID;

		final Integer id;
		try
		{
			final ResultSet rs = ConnectionManager.getInstance().query(sqlSequenceStr);
			if(!rs.next())
			{
				defaultLogger.error("Unable to get next transaction ID: \"" + sqlSequenceStr + "\"");
				throw new RuntimeException("Unable to get next transaction ID: \"" + sqlSequenceStr + "\"");
			}
			id = (Integer)rs.getObject(1);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlSequenceStr, sqle);
			throw new RuntimeException(sqlSequenceStr, sqle);
		}

		final String sqlStr = "INSERT INTO " + DBNames.TN_TRANSACTION
										 + " ( " + DBNames.CN_TRANSACTION_ID
										 + " , " + DBNames.CN_TRANSACTION_ACCOUNT
										 + " , " + DBNames.CN_TRANSACTION_PAYEE
										 + " , " + DBNames.CN_TRANSACTION_MEMO
										 + " , " + DBNames.CN_TRANSACTION_NUM
										 + " , " + DBNames.CN_TRANSACTION_DATE
										 + " ) VALUES ( "
										 + id + " , " 
										 + accountId + " , '"
										 + payee + "' , '" 
										 + memo + "' , '"
										 + num + "' , '" 
										 + date + "')";


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

	
	public void deleteTransaction(final Integer id)
	{
		try
		{
			final int result = ConnectionManager.getInstance().update(
											  "DELETE FROM " + DBNames.TN_TRANSACTION
											 + " WHERE " + DBNames.CN_TRANSACTION_ID
											 + " = " + id);
			if(result < 1)
			{
				defaultLogger.error("Transaction id not found: "+ id +"!");
				throw new RuntimeException("Unable to update Transaction for id: " + id);
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error("SQLException while updating Transaction for id: " + id + "!", sqle);
			throw new RuntimeException("Unable to update Transaction for id: " + id, sqle);
		}
	}

	public boolean doesTransactionExist(final Integer id)
	{
		final String sqlStr = "SELECT " + DBNames.CN_TRANSACTION_ID
								+ " FROM " + DBNames.TN_TRANSACTION
								+ " WHERE " + DBNames.CN_TRANSACTION_ID
								+ " = " + id;
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

	public boolean doesTransactionAffectAccount(final Integer transId, final Integer accountId)
	{
		final String sqlStr = "SELECT " 
	    	+ DBNames.TN_TRANSACTION + "." + DBNames.CN_TRANSACTION_ID 
	    	+ " FROM "
            + DBNames.TN_TRANSACTION + " , " 
            + DBNames.TN_ACCOUNT + " , "
            + DBNames.TN_TRANSACTION_ACCOUNT_MAP
            + " WHERE "
            + DBNames.TN_TRANSACTION_ACCOUNT_MAP  + "." + DBNames.CN_TAM_TRANSACTION_ID
            + " = " + DBNames.TN_TRANSACTION  + "." + DBNames.CN_TRANSACTION_ID
            + " AND "
            + DBNames.TN_TRANSACTION_ACCOUNT_MAP  + "." + DBNames.CN_TAM_ACCOUNT_ID
            + " = "  + DBNames.TN_ACCOUNT  + "." + DBNames.CN_ACCOUNT_ID
            + " AND "
            + DBNames.TN_ACCOUNT  + "." + DBNames.CN_ACCOUNT_ID 
            + " = " + accountId
            + " AND "
            + DBNames.TN_TRANSACTION  + "." + DBNames.CN_TRANSACTION_ID
            + " = " + transId
            + " UNION "
            + " SELECT " + DBNames.CN_TRANSACTION_ID
			+ " FROM " + DBNames.TN_TRANSACTION
			+ " WHERE " + DBNames.CN_TRANSACTION_ACCOUNT
			+ " = " +  accountId
            + " AND "
            + DBNames.CN_TRANSACTION_ID
            + " = " + transId;

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

	
	public Integer[] getAllTransactionIDs(final Integer accountId)
	{
		final String sqlStr = "SELECT " + DBNames.CN_TRANSACTION_ID
							+ " FROM " + DBNames.TN_TRANSACTION
							+ " WHERE " + DBNames.CN_TRANSACTION_ACCOUNT
							+ " = " +  accountId
							+ " ORDER BY " + DBNames.CN_TRANSACTION_DATE;
		try
		{
			final ResultSet rs =  ConnectionManager.getInstance().query(sqlStr);
			final List<Integer> idList = new ArrayList<Integer>();
			while(rs.next())
			{
				idList.add((Integer)rs.getObject(DBNames.CN_TRANSACTION_ID));
			}
			return idList.toArray(new Integer[idList.size()]);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error("SQLException while loading account name!", sqle);
			throw new RuntimeException("Unable to load transaction id's!", sqle);
		}
	}

	public Integer[] getAllAffectingTransactionIDs(final Integer accountId)
	{
		final String sqlStr = "SELECT " 
		    	+ DBNames.TN_TRANSACTION + "." + DBNames.CN_TRANSACTION_ID 
		    	+ " FROM "
                + DBNames.TN_TRANSACTION + " , " 
                + DBNames.TN_ACCOUNT + " , "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP
                + " WHERE "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP  + "." + DBNames.CN_TAM_TRANSACTION_ID
                + " = " + DBNames.TN_TRANSACTION  + "." + DBNames.CN_TRANSACTION_ID
                + " AND "
                + DBNames.TN_TRANSACTION_ACCOUNT_MAP  + "." + DBNames.CN_TAM_ACCOUNT_ID
                + " = "  + DBNames.TN_ACCOUNT  + "." + DBNames.CN_ACCOUNT_ID
                + " AND "
                + DBNames.TN_ACCOUNT  + "." + DBNames.CN_ACCOUNT_ID 
                + " = " + accountId
                + " UNION "
                + " SELECT " + DBNames.CN_TRANSACTION_ID
				+ " FROM " + DBNames.TN_TRANSACTION
				+ " WHERE " + DBNames.CN_TRANSACTION_ACCOUNT
				+ " = " +  accountId;

        try
        {
            final ResultSet rs = ConnectionManager.getInstance().query(sqlStr);
            final List<Integer> idList = new ArrayList<Integer>();
            while (rs.next())
            {
                idList.add((Integer) rs.getObject(DBNames.CN_TRANSACTION_ID));
            }
            return idList.toArray(new Integer[idList.size()]);
        }
        catch (final SQLException sqle)
        {
            defaultLogger.error("SQLException while loading account name!",
                    sqle);
            throw new RuntimeException("Unable to load transaction id's!", sqle);
        }
	}
	
    public static void unitTest_Create()
    {
        getInstance().createTransaction(new Integer(2), "Joe`s bar and grill", "memo", "23", new Date(System.currentTimeMillis()));
    }
    
    
    public static void main(final String[] args)
    {
		BasicConfigurator.configure();
		unitTest_Create();
    }
}
