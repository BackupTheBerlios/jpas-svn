package org.jpas.da;

import java.sql.*;
import org.apache.log4j.*;
import java.util.*;
/**
 * <p>Title: JPAS</p>
 * <p>Description: Java based Personal Accounting System</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>License: Distributed under the terms of the GPL v2</p>
 * @author Justin Smith
 * @version 1.0
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
		public void setData(final String name);
	}

	public void loadAccount(final Integer id, final AccountHandler handler)
	{
		final String sqlStr = "SELECT " + DBNames.CN_ACCOUNT_NAME
										 + " FROM " + DBNames.TN_ACCOUNT
										 + " WHERE " + DBNames.CN_ACCOUNT_ID
										 + " IS " + id;
		try
		{
			final ResultSet rs =  ConnectionManager.getInstance().query(sqlStr);

			final String name = rs.next() ? rs.getString(DBNames.CN_ACCOUNT_NAME) : null;
			if(name == null)
			{
				defaultLogger.error("Account id not found: \""+ sqlStr +"\"");
				throw new RuntimeException("Account id not found: \""+ sqlStr +"\"");
			}
			handler.setData(name);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error(sqlStr, sqle);
			throw new RuntimeException(sqlStr, sqle);
		}
	}

	public void updateAccount(final Integer id, final String name)
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

	public Integer createAccount(final String name)
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
										 + " ) VALUES ( '"
										 + id + "' , '" + name + "' )";


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

	public Integer[] getAllAccountIDs()
	{
		final String sqlStr = "SELECT " + DBNames.CN_ACCOUNT_ID
							+ " FROM " + DBNames.TN_ACCOUNT
							+ " ORDER BY " + DBNames.CN_ACCOUNT_NAME;
		try
		{
			final ResultSet rs =  ConnectionManager.getInstance().query(sqlStr);
			final List idList = new ArrayList();
			while(rs.next())
			{
				idList.add(rs.getObject(DBNames.CN_ACCOUNT_ID));
			}
			return (Integer[])idList.toArray(new Integer[idList.size()]);
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error("SQLException while loading account name!", sqle);
			throw new RuntimeException("Unable to load account id's!", sqle);
		}
	}

	public static void main(final String[] args)
	{
		BasicConfigurator.configure();
		final Integer id = instance.createAccount("Checking");
		instance.updateAccount(id, "Share");
//		instance.deleteAccount(id);
	}
}
