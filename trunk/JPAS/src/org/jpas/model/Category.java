/**
 * Created on Sep 7, 2004
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
package org.jpas.model;

import org.apache.log4j.*;
import org.jpas.da.*;
import org.jpas.util.JpasDataChange;
import org.jpas.util.JpasObservable;
import org.jpas.util.WeakValueMap;

public class Category extends JpasObservable<Category>
{
	private static final Logger defaultLogger = Logger.getLogger(Category.class);
	
	private static WeakValueMap<Integer, Category> categoryCache = new WeakValueMap<Integer, Category>();
	
	private static JpasObservable<Category> observable = new JpasObservable<Category>();
	
	public static  JpasObservable<Category> getObservable()
	{
		return observable;
	}

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
    	synchronized(categoryCache)
		{
	        Category category = categoryCache.get(id);
	        if (category == null)
	        {
	            category = new Category(id);
	            categoryCache.put(id, category);
	        }
	        return category;
		}
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
    	synchronized(this)
		{
    		synchronized(categoryCache)
			{
		        AccountDA.getInstance().deleteAccount(id);
		        categoryCache.remove(id);
		        isDeleted = true;
			}
		}
    	announceDelete();
    }

    private void announceDelete()
    {
    	final JpasDataChange<Category> change = new JpasDataChange.Delete<Category>(this);
    	observable.notifyObservers(change);
		notifyObservers(change);
		deleteObservers();
    }

    private void announceModify()
    {
    	final JpasDataChange<Category> change = new JpasDataChange.Modify<Category>(this);
    	observable.notifyObservers(change);
		notifyObservers(change);
    }

    
    public synchronized String getName()
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        return isBankAccount ? "TRANSFER to [" + name + "]" : name;
    }

    public synchronized boolean isTranfer()
    {
    	assert(!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        return isBankAccount;
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
    	synchronized(this)
		{
	        assert (!isDeleted);
	        AccountDA.getInstance().updateAccountName(id, name);
	        if (isLoaded)
	        {
	            loadData();
	        }
		}
    	announceModify();
	}
    
    public long getTotal()
    {
    	return TransAccountMappingDA.getInstance().getAccountBalance(id);
    }
    
    public boolean isDeleted()
    {
        return isDeleted;
    }
    
    public boolean isLoaded()
    {
        return isLoaded;
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
