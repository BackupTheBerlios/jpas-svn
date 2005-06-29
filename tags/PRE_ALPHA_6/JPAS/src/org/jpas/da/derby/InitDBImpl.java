package org.jpas.da.derby;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.jpas.da.DAFactory;
import org.jpas.da.DBNames;
import org.jpas.da.InitDB;

/**
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

public class InitDBImpl extends InitDB
{
    //TODO This is just a copy of the HSQLDB implementation
    
	private static final Logger defaultLogger = Logger.getLogger(InitDBImpl.class);
	public InitDBImpl()
	{
	}

	private void deleteTable(final String tableName)
	{
		final String sqlDeleteStr = "DROP TABLE " + tableName + " IF EXISTS";
		try
		{
            DAFactory.getConnectionManager().update(sqlDeleteStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlDeleteStr, sqle);
			throw new RuntimeException(sqlDeleteStr, sqle);
		}
	}

	private void deleteSequence(final String sequenceName)
	{
///*
		final String sqlDeleteStr = "DROP SEQUENCE " + sequenceName;

		try
		{
            DAFactory.getConnectionManager().update(sqlDeleteStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlDeleteStr, sqle);
			throw new RuntimeException(sqlDeleteStr, sqle);
		}
//*/
	}

	/* (non-Javadoc)
     * @see org.jpas.da.InitDB#createUser(java.lang.String, java.lang.String, boolean)
     */
	public void createUser(final String userName, final String passwd, final boolean admin)
	{
		final String sqlCreate = "CREATE USER " + userName + " PASSWORD " + passwd + (admin ? "ADMIN" : "");
		try
		{
            DAFactory.getConnectionManager().update(sqlCreate);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlCreate, sqle);
			throw new RuntimeException(sqlCreate, sqle);
		}
	}

	/* (non-Javadoc)
     * @see org.jpas.da.InitDB#deleteUser(java.lang.String)
     */
	public void deleteUser(final String userName)
	{
		final String sqlDrop = "DROP USER " + userName;
		try
		{
            DAFactory.getConnectionManager().update(sqlDrop);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlDrop, sqle);
			throw new RuntimeException(sqlDrop, sqle);
		}
	}

	/* (non-Javadoc)
     * @see org.jpas.da.InitDB#changePassword(java.lang.String, java.lang.String)
     */
	public void changePassword(final String userName, final String passwd)
	{
		final String sqlAlter = "ALTER USER " + userName + " SET PASSWORD " + passwd;
		try
		{
            DAFactory.getConnectionManager().update(sqlAlter);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlAlter, sqle);
			throw new RuntimeException(sqlAlter, sqle);
		}

	}

	/* (non-Javadoc)
     * @see org.jpas.da.InitDB#deleteAccountTable()
     */
	public void deleteAccountTable()
	{
		deleteTable(DBNames.TN_ACCOUNT);
		deleteSequence(DBNames.SEQ_ACCOUNT_ID);
	}

	/* (non-Javadoc)
     * @see org.jpas.da.InitDB#createAccountTable()
     */
	public void createAccountTable()
	{
		final String sqlCreateStr = "CREATE TABLE " + DBNames.TN_ACCOUNT
										  + " ( " + DBNames.CN_ACCOUNT_ID
										  + " INTEGER PRIMARY KEY, "
										  + DBNames.CN_ACCOUNT_NAME
										  + " VARCHAR(64), "
										  + DBNames.CN_ACCOUNT_TYPE
										  + " INTEGER )";

		final String sqlSequenceStr = "CREATE SEQUENCE "
										  + DBNames.SEQ_ACCOUNT_ID
										  +" AS INTEGER";

		try
		{
            DAFactory.getConnectionManager().update(sqlCreateStr);
		}
		catch(final SQLException sqle)
		{
		  defaultLogger.error(sqlCreateStr, sqle);
		  throw new RuntimeException(sqlCreateStr, sqle);
		}

		try
		{
            DAFactory.getConnectionManager().update(sqlSequenceStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlSequenceStr, sqle);
			throw new RuntimeException(sqlSequenceStr, sqle);
		}
	}

	/* (non-Javadoc)
     * @see org.jpas.da.InitDB#deleteTransactionTable()
     */
	public void deleteTransactionTable()
	{
		deleteTable(DBNames.TN_TRANSACTION);
		deleteSequence(DBNames.SEQ_TRANSACTION_ID);
	}

	/* (non-Javadoc)
     * @see org.jpas.da.InitDB#createTransactionTable()
     */
	public void createTransactionTable()
	{
		final String sqlCreateStr = "CREATE TABLE "
			+ DBNames.TN_TRANSACTION + " ("
			+ DBNames.CN_TRANSACTION_ID
			  + " INTEGER PRIMARY KEY, "
			+ DBNames.CN_TRANSACTION_PAYEE + " VARCHAR(64), "
			+ DBNames.CN_TRANSACTION_ACCOUNT + " INTEGER NOT NULL, "
			+ DBNames.CN_TRANSACTION_DATE + " DATE, "
			+ DBNames.CN_TRANSACTION_NUM + " VARCHAR(8), "
			+ DBNames.CN_TRANSACTION_MEMO + " VARCHAR(64), "
			+ " FOREIGN KEY (" + DBNames.CN_TRANSACTION_ACCOUNT
			  + ") REFERENCES " + DBNames.TN_ACCOUNT + "(" + DBNames.CN_ACCOUNT_ID + ") "
			+ " ON DELETE CASCADE "
			+ ")";

		final String sqlIndexAccount = "CREATE INDEX " + DBNames.IDX_TRANSACTION_ACCOUNT + " ON " + DBNames.TN_TRANSACTION
				+ "(" + DBNames.CN_TRANSACTION_ACCOUNT + " , "
				+ DBNames.CN_TRANSACTION_DATE + " , " + DBNames.CN_TRANSACTION_ID + ")";

		final String sqlSequenceStr = "CREATE SEQUENCE "
									 + DBNames.SEQ_TRANSACTION_ID
									 +" AS INTEGER";

		try
		{
            DAFactory.getConnectionManager().update(sqlCreateStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlCreateStr, sqle);
			throw new RuntimeException(sqlCreateStr, sqle);
		}

		try
		{
            DAFactory.getConnectionManager().update(sqlIndexAccount);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlIndexAccount, sqle);
			throw new RuntimeException(sqlIndexAccount, sqle);
		}

		try
		{
            DAFactory.getConnectionManager().update(sqlSequenceStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlSequenceStr, sqle);
			throw new RuntimeException(sqlSequenceStr, sqle);
		}
	}

	/* (non-Javadoc)
     * @see org.jpas.da.InitDB#deleteTransAccountMapTable()
     */
	public void deleteTransAccountMapTable()
	{
		deleteTable(DBNames.TN_TRANSACTION_ACCOUNT_MAP);
	}
	
	/* (non-Javadoc)
     * @see org.jpas.da.InitDB#createTransAccountMapTable()
     */
	public void createTransAccountMapTable()
	{
		final String sqlCreateStr = "CREATE TABLE "
			+ DBNames.TN_TRANSACTION_ACCOUNT_MAP + " ("
			+ DBNames.CN_TAM_ACCOUNT_ID
			  + " INTEGER, "
			+ DBNames.CN_TAM_TRANSACTION_ID + " INTEGER, "
			+ DBNames.CN_TAM_AMOUNT + " BIGINT, "
			+ "PRIMARY KEY (" + DBNames.CN_TAM_ACCOUNT_ID
			+ ", " + DBNames.CN_TAM_TRANSACTION_ID + "), "
			+ " FOREIGN KEY (" + DBNames.CN_TAM_ACCOUNT_ID
			+ ") REFERENCES " + DBNames.TN_ACCOUNT + "("
			+ DBNames.CN_ACCOUNT_ID + ") ON DELETE CASCADE , "
			+ " FOREIGN KEY (" + DBNames.CN_TAM_TRANSACTION_ID
			+ ") REFERENCES " + DBNames.TN_TRANSACTION + "("
			+ DBNames.CN_TRANSACTION_ID + ") ON DELETE CASCADE )";


		final String sqlIndexAccount = "CREATE INDEX " 
				+ DBNames.IDX_TAM_ACCOUNT + " ON " 
				+ DBNames.TN_TRANSACTION_ACCOUNT_MAP
				+ "(" + DBNames.CN_TAM_ACCOUNT_ID + ")";

		final String sqlIndexTransaction = "CREATE INDEX " 
			+ DBNames.IDX_TAM_TRANSACTION + " ON " 
			+ DBNames.TN_TRANSACTION_ACCOUNT_MAP
			+ "(" + DBNames.CN_TAM_TRANSACTION_ID + ")";

		try
		{
            DAFactory.getConnectionManager().update(sqlCreateStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlCreateStr, sqle);
			throw new RuntimeException(sqlCreateStr, sqle);
		}

		try
		{
            DAFactory.getConnectionManager().update(sqlIndexAccount);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlIndexAccount, sqle);
			throw new RuntimeException(sqlIndexAccount, sqle);
		}

		try
		{
            DAFactory.getConnectionManager().update(sqlIndexTransaction);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlIndexTransaction, sqle);
			throw new RuntimeException(sqlIndexTransaction, sqle);
		}
	}

	
	
	/* (non-Javadoc)
     * @see org.jpas.da.InitDB#deleteReminderTable()
     */
	public void deleteReminderTable()
	{
		deleteTable(DBNames.TN_REMINDER);
		deleteSequence(DBNames.SEQ_REMINDER_ID);
	}

	/* (non-Javadoc)
     * @see org.jpas.da.InitDB#createReminderTable()
     */
	public void createReminderTable()
	{
		final String sqlCreateStr = "CREATE TABLE " + DBNames.TN_REMINDER
			+ "  ( "
			+ DBNames.CN_REMINDER_ID + " INTEGER PRIMARY KEY, "
			+ DBNames.CN_REMINDER_PAYEE + " VARCHAR(64), "
			+ DBNames.CN_REMINDER_ACCOUNT + " INTEGER NOT NULL, "
			+ DBNames.CN_REMINDER_DATE + " DATE, "
			+ DBNames.CN_REMINDER_AMOUNT_METHOD + " INTEGER, "
			+ DBNames.CN_REMINDER_REPEAT_METHOD + " INTEGER, "
			+ DBNames.CN_REMINDER_REPEAT_VALUE + " INTEGER, "
			+ DBNames.CN_REMINDER_MEMO + " VARCHAR(64), "
			+ " FOREIGN KEY (" + DBNames.CN_REMINDER_ACCOUNT + ") REFERENCES "
			+ DBNames.TN_ACCOUNT + " ("+ DBNames.CN_ACCOUNT_ID +") ON DELETE CASCADE "
			+ ")";

		final String sqlSequenceStr = "CREATE SEQUENCE "
										  + DBNames.SEQ_REMINDER_ID
										  +" AS INTEGER";

		try
		{
            DAFactory.getConnectionManager().update(sqlCreateStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlCreateStr, sqle);
			throw new RuntimeException(sqlCreateStr, sqle);
		}

		try
		{
            DAFactory.getConnectionManager().update(sqlSequenceStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlSequenceStr, sqle);
			throw new RuntimeException(sqlSequenceStr, sqle);
		}
	}

	/* (non-Javadoc)
     * @see org.jpas.da.InitDB#deleteRemAccountMapTable()
     */
	public void deleteRemAccountMapTable()
	{
		deleteTable(DBNames.TN_REMINDER_ACCOUNT_MAP);
	}
	
	/* (non-Javadoc)
     * @see org.jpas.da.InitDB#createRemAccountMapTable()
     */
	public void createRemAccountMapTable()
	{
		final String sqlCreateStr = "CREATE TABLE "
			+ DBNames.TN_REMINDER_ACCOUNT_MAP + " ("
			+ DBNames.CN_RAM_ACCOUNT_ID + " INTEGER, "
			+ DBNames.CN_RAM_REMINDER_ID + " INTEGER, "
			+ DBNames.CN_RAM_AMOUNT + " BIGINT, "
			+ "PRIMARY KEY (" + DBNames.CN_RAM_ACCOUNT_ID
			+ ", " + DBNames.CN_RAM_REMINDER_ID + "), "
			+ " FOREIGN KEY (" + DBNames.CN_RAM_ACCOUNT_ID
			+ ") REFERENCES " + DBNames.TN_ACCOUNT + "("
			+ DBNames.CN_ACCOUNT_ID + ") ON DELETE CASCADE , "
			+ " FOREIGN KEY (" + DBNames.CN_RAM_REMINDER_ID
			+ ") REFERENCES " + DBNames.TN_REMINDER + "("
			+ DBNames.CN_REMINDER_ID + ") ON DELETE CASCADE )";


		final String sqlIndexAccount = "CREATE INDEX " 
				+ DBNames.IDX_RAM_ACCOUNT + " ON " 
				+ DBNames.TN_REMINDER_ACCOUNT_MAP
				+ "(" + DBNames.CN_RAM_ACCOUNT_ID + ")";

		final String sqlIndexTransaction = "CREATE INDEX " 
			+ DBNames.IDX_RAM_REMINDER + " ON " 
			+ DBNames.TN_REMINDER_ACCOUNT_MAP
			+ "(" + DBNames.CN_RAM_REMINDER_ID + ")";

		try
		{
            DAFactory.getConnectionManager().update(sqlCreateStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlCreateStr, sqle);
			throw new RuntimeException(sqlCreateStr, sqle);
		}

		try
		{
            DAFactory.getConnectionManager().update(sqlIndexAccount);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlIndexAccount, sqle);
			throw new RuntimeException(sqlIndexAccount, sqle);
		}

		try
		{
            DAFactory.getConnectionManager().update(sqlIndexTransaction);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlIndexTransaction, sqle);
			throw new RuntimeException(sqlIndexTransaction, sqle);
		}
	}
}
