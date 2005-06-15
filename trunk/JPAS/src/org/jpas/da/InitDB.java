package org.jpas.da;

import java.sql.SQLException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

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

public class InitDB
{
	private static final Logger defaultLogger = Logger.getLogger(InitDB.class);
	public static final InitDB instance = new InitDB();

	private InitDB()
	{
	}

	private void deleteTable(final String tableName)
	{
		final String sqlDeleteStr = "DROP TABLE " + tableName + " IF EXISTS";
		try
		{
			ConnectionManager.getInstance().update(sqlDeleteStr);
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
			ConnectionManager.getInstance().update(sqlDeleteStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlDeleteStr, sqle);
			throw new RuntimeException(sqlDeleteStr, sqle);
		}
//*/
	}

	public void createUser(final String userName, final String passwd, final boolean admin)
	{
		final String sqlCreate = "CREATE USER " + userName + " PASSWORD " + passwd + (admin ? "ADMIN" : "");
		try
		{
			ConnectionManager.getInstance().update(sqlCreate);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlCreate, sqle);
			throw new RuntimeException(sqlCreate, sqle);
		}
	}

	public void deleteUser(final String userName)
	{
		final String sqlDrop = "DROP USER " + userName;
		try
		{
			ConnectionManager.getInstance().update(sqlDrop);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlDrop, sqle);
			throw new RuntimeException(sqlDrop, sqle);
		}
	}

	public void changePassword(final String userName, final String passwd)
	{
		final String sqlAlter = "ALTER USER " + userName + " SET PASSWORD " + passwd;
		try
		{
			ConnectionManager.getInstance().update(sqlAlter);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlAlter, sqle);
			throw new RuntimeException(sqlAlter, sqle);
		}

	}

	public void deleteAccountTable()
	{
		deleteTable(DBNames.TN_ACCOUNT);
		deleteSequence(DBNames.SEQ_ACCOUNT_ID);
	}

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
		  ConnectionManager.getInstance().update(sqlCreateStr);
		}
		catch(final SQLException sqle)
		{
		  defaultLogger.error(sqlCreateStr, sqle);
		  throw new RuntimeException(sqlCreateStr, sqle);
		}

		try
		{
			ConnectionManager.getInstance().update(sqlSequenceStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlSequenceStr, sqle);
			throw new RuntimeException(sqlSequenceStr, sqle);
		}
	}

	public void deleteTransactionTable()
	{
		deleteTable(DBNames.TN_TRANSACTION);
		deleteSequence(DBNames.SEQ_TRANSACTION_ID);
	}

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
			ConnectionManager.getInstance().update(sqlCreateStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlCreateStr, sqle);
			throw new RuntimeException(sqlCreateStr, sqle);
		}

		try
		{
			ConnectionManager.getInstance().update(sqlIndexAccount);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlIndexAccount, sqle);
			throw new RuntimeException(sqlIndexAccount, sqle);
		}

		try
		{
			ConnectionManager.getInstance().update(sqlSequenceStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlSequenceStr, sqle);
			throw new RuntimeException(sqlSequenceStr, sqle);
		}
	}

	public void deleteTransAccountMapTable()
	{
		deleteTable(DBNames.TN_TRANSACTION_ACCOUNT_MAP);
	}
	
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
			ConnectionManager.getInstance().update(sqlCreateStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlCreateStr, sqle);
			throw new RuntimeException(sqlCreateStr, sqle);
		}

		try
		{
			ConnectionManager.getInstance().update(sqlIndexAccount);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlIndexAccount, sqle);
			throw new RuntimeException(sqlIndexAccount, sqle);
		}

		try
		{
			ConnectionManager.getInstance().update(sqlIndexTransaction);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlIndexTransaction, sqle);
			throw new RuntimeException(sqlIndexTransaction, sqle);
		}
	}

	
	
	public void deleteReminderTable()
	{
		deleteTable(DBNames.TN_REMINDER);
		deleteSequence(DBNames.SEQ_REMINDER_ID);
	}

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
			ConnectionManager.getInstance().update(sqlCreateStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlCreateStr, sqle);
			throw new RuntimeException(sqlCreateStr, sqle);
		}

		try
		{
			ConnectionManager.getInstance().update(sqlSequenceStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlSequenceStr, sqle);
			throw new RuntimeException(sqlSequenceStr, sqle);
		}
	}

	public void deleteRemAccountMapTable()
	{
		deleteTable(DBNames.TN_REMINDER_ACCOUNT_MAP);
	}
	
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
			ConnectionManager.getInstance().update(sqlCreateStr);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlCreateStr, sqle);
			throw new RuntimeException(sqlCreateStr, sqle);
		}

		try
		{
			ConnectionManager.getInstance().update(sqlIndexAccount);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlIndexAccount, sqle);
			throw new RuntimeException(sqlIndexAccount, sqle);
		}

		try
		{
			ConnectionManager.getInstance().update(sqlIndexTransaction);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlIndexTransaction, sqle);
			throw new RuntimeException(sqlIndexTransaction, sqle);
		}
	}

	public static void unitTest_dropAll()
	{
		instance.deleteTransAccountMapTable();
		instance.deleteRemAccountMapTable();
		instance.deleteReminderTable();
		instance.deleteTransactionTable();
		instance.deleteAccountTable();
	}
	
	public static void unitTest_createAll()
	{
		instance.createAccountTable();
		instance.createTransactionTable();
		instance.createReminderTable();
		instance.createTransAccountMapTable();
		instance.createRemAccountMapTable();
	}
	
	public static void unitTest_populateAll()
	{
	    AccountDA.unitTest_Create();
	    TransactionDA.unitTest_Create();
	    TransAccountMappingDA.unitTest_Create();
	    ReminderDA.unitTest_Create();
	    ReminderAccountMappingDA.unitTest_Create();
	}
	
	public static void main(final String[] args)
	{
		BasicConfigurator.configure();

		unitTest_dropAll();
		
		unitTest_createAll();
		
		unitTest_populateAll();
	}
}
