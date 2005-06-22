package org.jpas.da.hsqldb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jpas.da.AccountDA;
import org.jpas.da.DAFactory;
import org.jpas.da.DBNames;
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

public class AccountDAImpl extends AccountDA
{
	private static final Logger defaultLogger = Logger.getLogger(AccountDAImpl.class);

	public AccountDAImpl()
	{
	    if(!doesAccountExist(AccountType.DELETED_BANK))
	    {
	        createAccount("DELETED", AccountType.DELETED_BANK);
	    }
	    if(!doesAccountExist(AccountType.UNKNOWN_CATEGORY))
	    {
	        createAccount("UNKNOWN", AccountType.UNKNOWN_CATEGORY);
	    }
	}

	/* (non-Javadoc)
     * @see org.jpas.da.hsqldb.AccountDA#loadAccount(java.lang.Integer, org.jpas.da.hsqldb.AccountDAImpl.AccountHandler)
     */
	public void loadAccount(final Integer id, final AccountHandler handler)
	{
		final String sqlStr = "SELECT * FROM " + DBNames.TN_ACCOUNT
										 + " WHERE " + DBNames.CN_ACCOUNT_ID
										 + " = " + id;
		try
		{
			final ResultSet rs =  DAFactory.getConnectionManager().query(sqlStr);

			if(rs.next())
			{
				handler.setData(rs.getString(DBNames.CN_ACCOUNT_NAME),
				        		AccountType.getAccountTypeFor(rs.getInt(DBNames.CN_ACCOUNT_TYPE)));
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

	/* (non-Javadoc)
     * @see org.jpas.da.hsqldb.AccountDA#updateAccount(java.lang.Integer, java.lang.String, org.jpas.da.hsqldb.AccountDAImpl.AccountType)
     */
	public void updateAccount(final Integer id, final String name, final AccountType type)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_ACCOUNT
										 + " SET " + DBNames.CN_ACCOUNT_NAME
										 + " = '" + name + "' , "
										 + DBNames.CN_ACCOUNT_TYPE
										 + " = '" + type.dbValue
										 + "' WHERE " + DBNames.CN_ACCOUNT_ID
											 + " = " + id;

		try
		{
			final int result = DAFactory.getConnectionManager().update(sqlStr);
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

	/* (non-Javadoc)
     * @see org.jpas.da.hsqldb.AccountDA#updateAccountName(java.lang.Integer, java.lang.String)
     */
	public void updateAccountName(final Integer id, final String name)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_ACCOUNT
										 + " SET " + DBNames.CN_ACCOUNT_NAME
										 + " = '" + name
										 + "' WHERE " + DBNames.CN_ACCOUNT_ID
											 + " = " + id;

		try
		{
			final int result = DAFactory.getConnectionManager().update(sqlStr);
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

	/* (non-Javadoc)
     * @see org.jpas.da.hsqldb.AccountDA#updateAccountType(java.lang.Integer, org.jpas.da.hsqldb.AccountDAImpl.AccountType)
     */
	public void updateAccountType(final Integer id, final AccountType type)
	{
		final String sqlStr = "UPDATE " + DBNames.TN_ACCOUNT
										 + " SET " + DBNames.CN_ACCOUNT_TYPE
										 + " = '" + type.dbValue
										 + "' WHERE " + DBNames.CN_ACCOUNT_ID
											 + " = " + id;

		try
		{
			final int result = DAFactory.getConnectionManager().update(sqlStr);
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

	/* (non-Javadoc)
     * @see org.jpas.da.hsqldb.AccountDA#getDeletedBankAccountID()
     */
	public Integer getDeletedBankAccountID()
	{
		final String sqlStr = "SELECT " + DBNames.CN_ACCOUNT_ID + " FROM "
                + DBNames.TN_ACCOUNT + " WHERE " + DBNames.CN_ACCOUNT_TYPE
                + " = " + AccountType.DELETED_BANK.dbValue;
        try
        {
        	final ResultSet rs = DAFactory.getConnectionManager().query(sqlStr);
        	rs.next();
            return (Integer)rs.getObject(DBNames.CN_ACCOUNT_ID);
        }
        catch (final SQLException sqle)
        {
            defaultLogger.error("SQLException while loading account name!",
                    sqle);
            throw new RuntimeException("Unable to load account id's!", sqle);
        }	
	}

	/* (non-Javadoc)
     * @see org.jpas.da.hsqldb.AccountDA#getUnknownCategoryID()
     */
	public Integer getUnknownCategoryID()
	{
		final String sqlStr = "SELECT " + DBNames.CN_ACCOUNT_ID + " FROM "
                + DBNames.TN_ACCOUNT + " WHERE " + DBNames.CN_ACCOUNT_TYPE
                + " = " + AccountType.UNKNOWN_CATEGORY.dbValue;
        try
        {
            final ResultSet rs = DAFactory.getConnectionManager().query(sqlStr);
            rs.next();
            return (Integer)rs.getObject(DBNames.CN_ACCOUNT_ID);
        }
        catch (final SQLException sqle)
        {
            defaultLogger.error("SQLException while loading \"unknown category\"!",
                    sqle);
            throw new RuntimeException("Unable to load account id's!", sqle);
        }	
	}

	
	/* (non-Javadoc)
     * @see org.jpas.da.hsqldb.AccountDA#createAccount(java.lang.String, org.jpas.da.hsqldb.AccountDAImpl.AccountType)
     */
	public Integer createAccount(final String name, final AccountType type)
	{
		final String sqlSequenceStr = "CALL NEXT VALUE FOR " + DBNames.SEQ_ACCOUNT_ID;

		final Integer id;
		try
		{
			final ResultSet rs = DAFactory.getConnectionManager().query(sqlSequenceStr);
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
										 + " , " + DBNames.CN_ACCOUNT_TYPE
										 + " ) VALUES ( '"
										 + id + "' , '" + name + "' , '"
										 + type.dbValue + "')";


		try
		{
			final int result = DAFactory.getConnectionManager().update(sqlStr);
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

	/* (non-Javadoc)
     * @see org.jpas.da.hsqldb.AccountDA#deleteAccount(java.lang.Integer)
     */
	public void deleteAccount(final Integer id)
	{
		try
		{
			final int result = DAFactory.getConnectionManager().update(
											  "DELETE FROM " + DBNames.TN_ACCOUNT
											 + " WHERE " + DBNames.CN_ACCOUNT_ID
											 + " = " + id);
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

	/* (non-Javadoc)
     * @see org.jpas.da.hsqldb.AccountDA#doesAccountExist(java.lang.Integer)
     */
	public boolean doesAccountExist(final Integer id)
	{
		final String sqlStr = "SELECT " + DBNames.CN_ACCOUNT_ID
								+ " FROM " + DBNames.TN_ACCOUNT
								+ " WHERE " + DBNames.CN_ACCOUNT_ID
								+ " = " + id;
		try
		{
			return DAFactory.getConnectionManager().query(sqlStr).next();
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error("SQLException while loading account name!", sqle);
			throw new RuntimeException("Unable to load account id's!", sqle);
		}	
	}

	/* (non-Javadoc)
     * @see org.jpas.da.hsqldb.AccountDA#doesAccountExist(org.jpas.da.hsqldb.AccountDAImpl.AccountType)
     */
	public boolean doesAccountExist(final AccountType type)
	{
		final String sqlStr = "SELECT " + DBNames.CN_ACCOUNT_ID
								+ " FROM " + DBNames.TN_ACCOUNT
								+ " WHERE " + DBNames.CN_ACCOUNT_TYPE
								+ " = " + type.dbValue;
		try
		{
			return DAFactory.getConnectionManager().query(sqlStr).next();
		}
		catch(final SQLException sqle)
		{
			defaultLogger.error("SQLException while loading account name!", sqle);
			throw new RuntimeException("Unable to load account id's!", sqle);
		}	
	}

	
	/* (non-Javadoc)
     * @see org.jpas.da.hsqldb.AccountDA#getAllAccountIDsExcept(org.jpas.da.hsqldb.AccountDAImpl.AccountType)
     */
	public Integer[] getAllAccountIDsExcept(final AccountType type)
	{
		final String sqlStr = "SELECT " + DBNames.CN_ACCOUNT_ID
							+ " FROM " + DBNames.TN_ACCOUNT
							+ " WHERE " + DBNames.CN_ACCOUNT_TYPE
							+ " != " +  type.dbValue
							+ " ORDER BY " + DBNames.CN_ACCOUNT_NAME;
		try
		{
			final ResultSet rs =  DAFactory.getConnectionManager().query(sqlStr);
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

	
	/* (non-Javadoc)
     * @see org.jpas.da.hsqldb.AccountDA#getAllAccountIDs(org.jpas.da.hsqldb.AccountDAImpl.AccountType)
     */
	public Integer[] getAllAccountIDs(final AccountType type)
	{
		final String sqlStr = "SELECT " + DBNames.CN_ACCOUNT_ID
							+ " FROM " + DBNames.TN_ACCOUNT
							+ " WHERE " + DBNames.CN_ACCOUNT_TYPE
							+ " = " +  type.dbValue
							+ " ORDER BY " + DBNames.CN_ACCOUNT_NAME;
		try
		{
			final ResultSet rs =  DAFactory.getConnectionManager().query(sqlStr);
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
	
	/* (non-Javadoc)
     * @see org.jpas.da.hsqldb.AccountDA#getAllPayeesForAccount(java.lang.Integer)
     */
	public String[] getAllPayeesForAccount(final Integer accountId)
	{
		final String sqlStr = "SELECT " + DBNames.CN_TRANSACTION_PAYEE + " FROM "
				+ DBNames.TN_TRANSACTION + " WHERE "
				+ DBNames.CN_TRANSACTION_ACCOUNT + " = " + accountId
				+ " ORDER BY " + DBNames.CN_TRANSACTION_DATE;
		try 
		{
			final ResultSet rs = DAFactory.getConnectionManager().query(sqlStr);
			final List<String> idList = new ArrayList<String>();
			
			while (rs.next()) 
			{
				idList.add(rs.getString(DBNames.CN_TRANSACTION_PAYEE));
			}
			
			return idList.toArray(new String[idList.size()]);
		} 
		catch (final SQLException sqle) 
		{
			defaultLogger.error("SQLException while loading account name!",
					sqle);
			throw new RuntimeException("Unable to load transaction id's!", sqle);
		}
	}

	
	/* (non-Javadoc)
     * @see org.jpas.da.hsqldb.AccountDA#getAllAccountIDs()
     */
	public Integer[] getAllAccountIDs()
	{
		final String sqlStr = "SELECT " + DBNames.CN_ACCOUNT_ID
							+ " FROM " + DBNames.TN_ACCOUNT
							+ " ORDER BY " + DBNames.CN_ACCOUNT_NAME;
		try
		{
			final ResultSet rs =  DAFactory.getConnectionManager().query(sqlStr);
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
}
