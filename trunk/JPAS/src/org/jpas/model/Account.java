/**
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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.jpas.da.AccountDA;
import org.jpas.da.TransAccountMappingDA;
import org.jpas.util.JpasDataChange;
import org.jpas.util.JpasObservable;
import org.jpas.util.WeakValueMap;

public class Account extends JpasObservable<Account>
{
    private static WeakValueMap<Integer, Account> accountCache = new WeakValueMap<Integer, Account>();
    private static final Logger defaultLogger = Logger.getLogger(Account.class);

    private static final JpasObservable<Account> observable = new JpasObservable<Account>();
    
    public static JpasObservable<Account> getObservable()
    {
    	return observable;
    }
    
    public static Account createAccount(final String name)
    {
        final Account account = getAccountForID(AccountDA.getInstance()
                .createAccount(name, true));
        
        observable.notifyObservers(new JpasDataChange.Add<Account>(account));
        
        return account;
    }

    static Account getAccountForID(final Integer id)
    {
        synchronized(accountCache)
		{
	    	Account account = accountCache.get(id);
	        if (account == null)
	        {
	            account = new Account(id);
	            accountCache.put(id, account);
	        }
	        return account;
		}
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

    final Integer id;
    private boolean isDeleted = false;
    private boolean isLoaded = false;
    private String name;

    private Account(final Integer id)
    {
        defaultLogger.debug("Constructing Account: " + id);
        this.id = id;
    }

    public void delete()
    {
    	synchronized(this)
		{
	    	synchronized(accountCache)
			{
		        AccountDA.getInstance().deleteAccount(id);
		        accountCache.remove(id);
		        isDeleted = true;
			}
		}
    	
    	final JpasDataChange<Account> dataChange = new JpasDataChange.Delete<Account>(this);
    	observable.notifyObservers(dataChange);
    	notifyObservers(dataChange);    	
    }

    public String getName()
    {
    	synchronized(this)
		{
	        assert (!isDeleted);
	        if (!isLoaded)
	        {
	            loadData();
	        }
		}
        return name;
    }

    private void loadData()
    {
    	assert(!isDeleted);
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
    	synchronized(this)
		{
	        assert (!isDeleted);
	        AccountDA.getInstance().updateAccountName(id, name);
	        if (isLoaded)
	        {
	            loadData();
	        }
		}
    	final JpasDataChange<Account> dataChange = new JpasDataChange.Modify<Account>(this);
    	observable.notifyObservers(dataChange);
    	notifyObservers(dataChange);    	
    }

    public long getBalance()
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
    
    public static void unitTest_rename()
    {
        final Account[] accounts = getAllAccounts();
        for (int i = 0; i < accounts.length; i++)
        {
            System.out.println("name: " + accounts[i].getName());
            accounts[i].setName("account " + i);
        }
    }

    public static void unitTest_List()
    {
        final Account[] accs = Account.getAllAccounts();
        for(int i = 0; i < accs.length; i++)
        {
            System.out.println(accs[i].getName());
        }
    }

    
    public static void main(final String[] args)
    {
		BasicConfigurator.configure();
        unitTest_List();
    }

}