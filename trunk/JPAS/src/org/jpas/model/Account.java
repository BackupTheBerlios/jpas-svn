package org.jpas.model;

import java.util.Map;
import java.util.WeakHashMap;
import org.apache.log4j.Logger;
import org.jpas.da.AccountDA;

/**
 * Title: JPAS Description: Java based Personal Accounting System Copyright:
 * Copyright (c) 2004 License: Distributed under the terms of the GPL v2
 * 
 * @author Justin Smith
 * @version 1.0
 */
public class Account
{
    private static Map accountCache = new WeakHashMap();
    private static final Logger defaultLogger = Logger.getLogger(Account.class);

    private static String validateName(final String name)
    {
        return name;
    }
    
    public static Account createAccount(final String name)
    {
        return getAccountForID(AccountDA.getInstance()
                .createAccount(name, true));
    }

    static Account getAccountForID(final Integer id)
    {
        Account account = (Account) accountCache.get(id);
        if (account == null)
        {
            account = new Account(id);
            accountCache.put(id, account);
        }
        return account;
    }

    public static Account[] getAllAccounts()
    {
        final Integer[] ids = AccountDA.getInstance().getAllAccountIDs(true);
        final Account[] accounts = new Account[ids.length];
        for (int i = 0; i < ids.length; i++)
        {
            accounts[i] = getAccountForID(ids[i]);
        }
        return accounts;
    }

    public static void main(final String[] args)
    {
        final Account[] accounts = getAllAccounts();
        for (int i = 0; i < accounts.length; i++)
        {
            System.out.println("name: " + accounts[i].getName());
            accounts[i].setName("account " + i);
        }
    }

    private final Integer id;
    private boolean isDeleted = false;
    private boolean isLoaded = false;
    private String name;

    private Account(final Integer id)
    {
        this.id = id;
    }

    public void delete()
    {
        AccountDA.getInstance().deleteAccount(id);
        accountCache.remove(id);
        isDeleted = true;
    }

    public String getName()
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        return name;
    }

    private void loadData()
    {
        AccountDA.getInstance().loadAccount(id, new AccountDA.AccountHandler()
        {
            public void setData(final String name, final boolean isBankAccount)
            {
                Account.this.name = name;
                isLoaded = true;
            }
        });
    }

    public void setName(final String name)
    {
        assert (!isDeleted);
        AccountDA.getInstance().updateAccountName(id, name);
        if (isLoaded)
        {
            loadData();
        }
    }
}