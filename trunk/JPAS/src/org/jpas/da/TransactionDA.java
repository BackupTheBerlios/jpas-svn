/*
 * Created on Sep 8, 2004 - 7:08:04 PM
 * 
 * Title: JPAS
 * Description: Java based Personal Accounting System
 * Copyright: Copyright (c) 2004
 * License: Distributed under the terms of the GPL v2
 * @author Justin Smith
 * @version 1.0
 */
package org.jpas.da;


import java.sql.*;
import org.apache.log4j.*;
import org.jpas.da.AccountDA.AccountHandler;
import java.util.*;
import java.sql.Date;
/*
 * 
 * Title: JPAS
 * Description: Java based Personal Accounting System
 * Copyright: Copyright (c) 2004
 * License: Distributed under the terms of the GPL v2
 * @author Justin Smith
 * @version 1.0
 */
public class TransactionDA
{
    private static final Logger defaultLogger = Logger.getLogger(TransactionDA.class);

    
    
    
	public static interface TransactionHandler
	{
		public void setData(final Integer accountId, final String payee, final String memo, final String num, final Date date);
	}
    
    
	public void loadAccount(final Integer id, final TransactionHandler handler)
	{
		final String sqlStr = "SELECT * FROM " + DBNames.TN_TRANSACTION
										 + " WHERE " + DBNames.CN_TRANSACTION_ID
										 + " IS " + id;
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
											 + " IS " + id;

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
											 + " IS " + id;

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
											 + " IS " + id;

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
											 + " IS " + id;

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
											 + " IS " + id;

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

	public Integer createAccount(final Integer accountId, final String payee, final String memo, final String num, final Date date)
	{
		final String sqlSequenceStr = "CALL NEXT VALUE FOR " + DBNames.SEQ_TRANSACTION_ID;

		final Integer id;
		try
		{
			final ResultSet rs = ConnectionManager.getInstance().query(sqlSequenceStr);
			if(!rs.next())
			{
				defaultLogger.error("Unable to get next Account ID: \"" + sqlSequenceStr + "\"");
				throw new RuntimeException("Unable to get next Account ID: \"" + sqlSequenceStr + "\"");
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
										 + " ) VALUES ( '"
										 + id + "' , '" 
										 + accountId + "' , '"
										 + payee + "' , '" 
										 + memo + "' , '"
										 + num + "' , '" 
										 + date + "')";


		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Unable to create account: \"" + sqlStr + "\"");
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
											 + " IS '" + id + "'");
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

    
    
    
    
    
    public static void main(final String[] args)
    {
    }
}
