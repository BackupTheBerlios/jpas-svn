package org.jpas.model;

import org.apache.log4j.*;
import org.jpas.da.*;
import java.util.*;
/**
 * <p>Title: JPAS</p>
 * <p>Description: Java based Personal Accounting System</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>License: Distributed under the terms of the GPL v2</p>
 * @author Justin Smith
 * @version 1.0
 */

public class Account
{
	private static final Logger defaultLogger = Logger.getLogger(Account.class);
	private static Map accountCache = new WeakHashMap();

	public static Account[] getAllAccounts()
	{
		final Integer[] ids = AccountDA.getInstance().getAllAccountIDs();
		final Account[] accounts = new Account[ids.length];
		for(int i = 0; i < ids.length; i++)
		{
			accounts[i] = new Account(ids[i]);
		}

		return accounts;
	}

	public static Account createAccount(final String name, final boolean isBankAccount)
	{
		return new Account(AccountDA.getInstance().createAccount(name, isBankAccount));
	}


	private final Integer id;
	private boolean isLoaded = false;
	private boolean isDeleted = false;

	private String name;
	private boolean bankAccount;

	private Account(final Integer id)
	{
		this.id = id;
	}

	private void loadData()
	{
		AccountDA.getInstance().loadAccount(
			 id,
			 new AccountDA.AccountHandler()
				{
					public void setData(final String name, final boolean isBankAccount)
					{
						Account.this.name = name;
						Account.this.bankAccount = isBankAccount;
						isLoaded = true;
					}
				});
	}

	public String getName()
	{
		//assert(!isDeleted);

		if(!isLoaded)
		{
			loadData();
		}
		return name;
	}

	public boolean isBankAccount()
	{
		if(!isLoaded)
		{
			loadData();
		}
		return bankAccount;
	}
	
	public void setName(final String name)
	{
		//assert(!isDeleted);

		AccountDA.getInstance().updateAccountName(id, name);
		if(isLoaded)
		{
			loadData();
		}
	}

	public void delete()
	{
		AccountDA.getInstance().deleteAccount(id);
		isDeleted = true;
	}

	public static void main(final String[] args)
	{
		final Account[] accounts = getAllAccounts();
		for(int i = 0; i < accounts.length; i++)
		{
			System.out.println("name: " + accounts[i].getName());
			accounts[i].setName("account " + i);
		}
	}
}
