package org.jpas.da;

import java.sql.*;
import org.apache.log4j.*;
import java.util.*;
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

public class AccountDA
{
	private static final Logger defaultLogger = Logger.getLogger(AccountDA.class);

	private static AccountDA instance = new AccountDA();

	public static AccountDA getInstance()
	{
		return instance;
	}

	private AccountDA(){}

	public static interface AccountHandler
	{
		public void setData(String name, boolean bankAccount);
	}

	public void loadAccount(final Integer id, final AccountHandler handler)
	{
		final String sqlStr = "SELECT * FROM " + DBNames.TN_ACCOUNT
										 + " WHERE " + DBNames.CN_ACCOUNT_ID
										 + " IS " + id;
		try
		{
			final ResultSet rs =  ConnectionManager.getInstance().query(sqlStr);

			if(rs.next())
			{
				handler.setData(rs.getString(DBNames.CN_ACCOUNT_NAME),
								rs.getBoolean(DBNames.CN_ACCOUNT_IS_BANK));
			}
			else
			{
				defaultLogger.error("Account id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Account id not found: \""+ sqlStr +"\"");
			}
				
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateAccount(final Integer id, final String name, final boolean isBankAccount)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_ACCOUNT
										 + " SET " + DBNames.CN_ACCOUNT_NAME
										 + " = '" + name + "' , "
										 + DBNames.CN_ACCOUNT_IS_BANK
										 + " = '" + isBankAccount
										 + "' WHERE " + DBNames.CN_ACCOUNT_ID
											 + " IS " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Account id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Account id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateAccountName(final Integer id, final String name)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_ACCOUNT
										 + " SET " + DBNames.CN_ACCOUNT_NAME
										 + " = '" + name
										 + "' WHERE " + DBNames.CN_ACCOUNT_ID
											 + " IS " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Account id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Account id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateAccountIsBank(final Integer id, final boolean isBank)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_ACCOUNT
										 + " SET " + DBNames.CN_ACCOUNT_IS_BANK
										 + " = '" + isBank
										 + "' WHERE " + DBNames.CN_ACCOUNT_ID
											 + " IS " + id;

		try
		{
			final int result = ConnectionManager.getInstance().update(sqlStr);
			if(result < 1)
			{
				defaultLogger.error("Account id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Account id not found: \""+ sqlStr +"\"");
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public Integer createAccount(final String name, boolean isBankAccount)
	{
		final String sqlSequenceStr = "CALL NEXT VALUE FOR " + DBNames.SEQ_ACCOUNT_ID;

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

		final String sqlStr = "INSERT INTO " + DBNames.TN_ACCOUNT
										 + " ( " + DBNames.CN_ACCOUNT_ID
										 + " , " + DBNames.CN_ACCOUNT_NAME
										 + " , " + DBNames.CN_ACCOUNT_IS_BANK
										 + " ) VALUES ( '"
										 + id + "' , '" + name + "' , '"
										 + isBankAccount + "')";


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

	public void deleteAccount(final Integer id)
	{
		try
		{
			final int result = ConnectionManager.getInstance().update(
											  "DELETE FROM " + DBNames.TN_ACCOUNT
											 + " WHERE " + DBNames.CN_ACCOUNT_ID
											 + " IS '" + id + "'");
			if(result < 1)
			{
				defaultLogger.error("Account id not found: "+ id +"!");
				throw new RuntimeException("Unable to update account for id: " + id);
			}
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error("SQLException while updating account for id: " + id + "!", sqle);
			throw new RuntimeException("Unable to update account for id: " + id, sqle);
		}
	}

	public boolean doesAccountExist(final Integer id)
	{
		final String sqlStr = "SELECT " + DBNames.CN_ACCOUNT_ID
								+ " FROM " + DBNames.TN_ACCOUNT
								+ " WHERE " + DBNames.CN_ACCOUNT_ID
								+ " IS '" + id + "'";
		try
		{
			return ConnectionManager.getInstance().query(sqlStr).next();
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error("SQLException while loading account name!", sqle);
			throw new RuntimeException("Unable to load account id's!", sqle);
		}	
	}
	
	public Integer[] getAllAccountIDs(final boolean isBankAccount)
	{
		final String sqlStr = "SELECT " + DBNames.CN_ACCOUNT_ID
							+ " FROM " + DBNames.TN_ACCOUNT
							+ " WHERE " + DBNames.CN_ACCOUNT_IS_BANK
							+ " IS '" +  isBankAccount
							+ "' ORDER BY " + DBNames.CN_ACCOUNT_NAME;
		try
		{
			final ResultSet rs =  ConnectionManager.getInstance().query(sqlStr);
			final List<Integer> idList = new ArrayList<Integer>();
			while(rs.next())
			{
				idList.add((Integer)rs.getObject(DBNames.CN_ACCOUNT_ID));
			}
			return idList.toArray(new Integer[idList.size()]);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error("SQLException while loading account name!", sqle);
			throw new RuntimeException("Unable to load account id's!", sqle);
		}
	}
	
	public Integer[] getAllAccountIDs()
	{
		final String sqlStr = "SELECT " + DBNames.CN_ACCOUNT_ID
							+ " FROM " + DBNames.TN_ACCOUNT
							+ " ORDER BY " + DBNames.CN_ACCOUNT_NAME;
		try
		{
			final ResultSet rs =  ConnectionManager.getInstance().query(sqlStr);
			final List<Integer> idList = new ArrayList<Integer>();
			while(rs.next())
			{
				idList.add((Integer)rs.getObject(DBNames.CN_ACCOUNT_ID));
			}
			return idList.toArray(new Integer[idList.size()]);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error("SQLException while loading account name!", sqle);
			throw new RuntimeException("Unable to load account id's!", sqle);
		}
	}

	public static void unitTest_Create()
	{
		instance.createAccount("Checking", true);
		instance.createAccount("Savings", true);
		instance.createAccount("Utility", false);
		instance.createAccount("Entertainment", false);
	}
	
	public static void main(final String[] args)
	{
		BasicConfigurator.configure();
		
		unitTest_Create();
	}
}
