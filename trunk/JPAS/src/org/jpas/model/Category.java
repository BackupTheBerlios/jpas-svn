/**
 * Created on Sep 7, 2004
 * 
 * Title: JPAS
 * Description: Java based Personal Accounting System
 * Copyright: Copyright (c) 2004
 * License: Distributed under the terms of the GPL v2
 * @author Justin Smith
 * @version 1.0
 * 
 */
package org.jpas.model;

import org.apache.log4j.*;
import org.jpas.da.*;

import java.util.*;

public class Category 
{
	private static final Logger defaultLogger = Logger.getLogger(Category.class);
	
	private static Map<Integer, Category> categoryCache = new WeakHashMap<Integer, Category>();

    public static Category[] getAllCategories()
    {
        final Integer[] ids = AccountDA.getInstance().getAllAccountIDs();
        final Category[] categories = new Category[ids.length];
        for (int i = 0; i < ids.length; i++)
        {
            categories[i] = getCategoryForID(ids[i]);
        }

        return categories;
    }

    public static Category createCategory(final String name)
    {
        return getCategoryForID(
                AccountDA.getInstance().createAccount(name, true));
    }

    static Category getCategoryForID(final Integer id)
    {
        Category account = categoryCache.get(id);
        if (account == null)
        {
            account = new Category(id);
            categoryCache.put(id, account);
        }
        return account;
    }
    
   
    final Integer id;
    private boolean isDeleted = false;
    private boolean isLoaded = false;

    private String name;
    private boolean isBankAccount;
    
    private Category(final Integer id)
    {
        defaultLogger.debug("Constructing Category: " + id);
        this.id = id;
    }
    
    public void delete()
    {
        AccountDA.getInstance().deleteAccount(id);
        categoryCache.remove(id);
        isDeleted = true;
    }

    public String getName()
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        return isBankAccount ? "TRANSFER to [" + name + "]" : name;
    }

    private void loadData()
    {
        AccountDA.getInstance().loadAccount(id, new AccountDA.AccountHandler()
        {
            public void setData(final String name, final boolean isBankAccount)
            {
                Category.this.name = name;
                Category.this.isBankAccount = isBankAccount;
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
    
    public boolean isTranfer()
    {
        return isBankAccount;
    }
    
    private static void unitTest_List()
    {
        final Category[] cats = Category.getAllCategories();
        for(int i = 0; i < cats.length; i++)
        {
            System.out.println(cats[i].getName());
        }
    }
    
    public static void main(String[] args) 
	{
		BasicConfigurator.configure();
        unitTest_List();
	}
}
