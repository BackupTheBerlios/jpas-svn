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

import org.jpas.util.JpasObservable;

public interface Account extends JpasObservable
{
    public String getName();

    public void setName(final String name);

    public long getBalance();

    public boolean isIncome();
    
    public boolean isExpense();
    
    public boolean isDeleted();

    public boolean isLoaded();

    public String[] getAllPayees();
    
    public void commit();
    
    public void reload();
    
/*
    public static void unitTest_delete()
    {
        final Account[] accounts = getAllAccounts();
        for (int i = 0; i < accounts.length; i++)
        {
            System.out.println("name: " + accounts[i].getName());
            //if(accounts[i].id.equals(new Integer(3)))
            	accounts[i].delete();
        }
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
        for (int i = 0; i < accs.length; i++)
        {
            System.out.println(accs[i].getName());
        }
    }

    public static void main(final String[] args)
    {
        BasicConfigurator.configure();
        //unitTest_List();
        unitTest_delete();
    }
*/
}