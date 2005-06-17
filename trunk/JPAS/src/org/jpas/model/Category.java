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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.jpas.da.AccountDA;
import org.jpas.da.TransAccountMappingDA;
import org.jpas.da.TransactionDA;
import org.jpas.util.*;

public class Category extends JpasObservable<Category> implements JpasObserver<TransactionTransfer>
{
    private static final Logger defaultLogger = Logger
            .getLogger(Category.class);
    private static WeakValueMap<Integer, Category> categoryCache = new WeakValueMap<Integer, Category>();
    private static JpasObservable<Category> observable = new JpasObservable<Category>();

    public static JpasObservable<Category> getObservable()
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

    public static Category getDeletedBankCategory()
    {
        return getCategoryForID(AccountDA.getInstance().getDeletedBankAccountID());
    }
    
    public static Category getUnknownCategory()
    {
        return getCategoryForID(AccountDA.getInstance().getUnknownCategoryID());
    }
    
    public static Category getCategoryForAccount(final Account account)
    {
    	return getCategoryForID(account.id);
    }
    
    public static Category createIncomeCategory(final String name)
    {
        return getCategoryForID(AccountDA.getInstance().createAccount(name,
                AccountDA.AccountType.INCOME_CATEGORY));
    }

    public static Category createExpenseCategory(final String name)
    {
        return getCategoryForID(AccountDA.getInstance().createAccount(name,
                AccountDA.AccountType.EXPENSE_CATEGORY));
    }

    
    static Category getCategoryForID(final Integer id)
    {
        assert(id != null);
        
        Category category = categoryCache.get(id);
        if (category == null)
        {
            category = new Category(id);
            categoryCache.put(id, category);
        }
        return category;
    }

    final Integer id;
    private boolean isModified = false;
    private boolean isDeleted = false;
    private boolean isLoaded = false;
    
    private String name;
    private AccountDA.AccountType type;

    private boolean totalLoaded = false;
    private long total;

    
    private Category(final Integer id)
    {
        defaultLogger.debug("Constructing Category: " + id);
        this.id = id;
    }

    public void delete()
    {
        if(!isDeleted)
        {
            isDeleted = true;
            isModified = true;
        }
    }

    public void commit()
    {
        if(isModified)
        {
            if(isDeleted)
            {
                if(!canBeDeleted())
                {
                    throw new RuntimeException("This category cannot be deleted!");
                }
                
                final Category cat = getUnknownCategory();
                final Integer[] transferIDs = TransAccountMappingDA.getInstance().getAllTranfersForAccount(id);
                for(int i = 0; i < transferIDs.length; i++)
                {
                    final TransactionTransfer transfer = TransactionTransfer.getTransactionTransferforIDs(transferIDs[i], id);
                    transfer.deleteObserver(this);
                    TransactionTransfer.createTransfer(Transaction.getTransactionForID(transferIDs[i]), cat, transfer.getAmount());
                    transfer.delete();
                    transfer.commit(true);
                }

                final Account twin = Account.getAccountForID(id);
                final Account account = Account.getDeletedBankAccount();
                final Integer[] transactionsIDs = TransactionDA.getInstance().getAllTransactionIDs(id);
                for(int i = 0; i < transactionsIDs.length; i++)
                {
                    final Transaction trans = Transaction.getTransactionForID(transactionsIDs[i]);
                    trans.deleteObserver(twin);
                    trans.delete();
                    trans.commit(true);
                }
                
                
                
                AccountDA.getInstance().deleteAccount(id);
            }
        }
        categoryCache.remove(id);
        isDeleted = true;
        announceDelete();
    }

    private void announceDelete()
    {
        final JpasDataChange<Category> change = new JpasDataChange.Delete<Category>(
                this);
        observable.notifyObservers(change);
        notifyObservers(change);
        deleteObservers();
    }

    private void announceModify()
    {
        final JpasDataChange<Category> change = new JpasDataChange.Modify<Category>(
                this);
        observable.notifyObservers(change);
        notifyObservers(change);
    }

    void amountChanged()
    {
        announceModify();
    }

    public String getName()
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        return (type == AccountDA.AccountType.BANK || type == AccountDA.AccountType.DELETED_BANK) ? "TXFR["
                + name + "]"
                : name;
    }

    public boolean isTranfer()
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        return type == AccountDA.AccountType.BANK
                || type == AccountDA.AccountType.DELETED_BANK;
    }

    public boolean isIncome()
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        return type == AccountDA.AccountType.INCOME_CATEGORY;
    }

    public boolean isExpense()
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        return type == AccountDA.AccountType.EXPENSE_CATEGORY;
    }
    
    public boolean isUnknown()
    {
        assert (!isDeleted);
        if (!isLoaded)
        {
            loadData();
        }
        return type == AccountDA.AccountType.UNKNOWN_CATEGORY;
    }
    
    private void loadData()
    {
        AccountDA.getInstance().loadAccount(id, new AccountDA.AccountHandler()
        {
            public void setData(final String name,
                    final AccountDA.AccountType type)
            {
                Category.this.name = name;
                Category.this.type = type;
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
        announceModify();
    }

    public long getTotal()
    {
        if(!totalLoaded)
        {
            total = TransAccountMappingDA.getInstance().getAccountBalance(id);
            totalLoaded = true;
        }
        return total;
    }

    public boolean canBeDeleted()
    {
        if(!isLoaded)
        {
            loadData();
        }
        return type != AccountDA.AccountType.DELETED_BANK && type != AccountDA.AccountType.UNKNOWN_CATEGORY && type != AccountDA.AccountType.BANK;
    }
    
    public boolean isDeleted()
    {
        return isDeleted;
    }

    public boolean isLoaded()
    {
        return isLoaded;
    }

    private static void unitTest_Delete()
    {
        final Category[] cats = Category.getAllCategories();
        for (int i = 0; i < cats.length; i++)
        {
            if(cats[i].canBeDeleted())
            {
                cats[i].delete();
            }
        }
    }
    
    public static void unitTest_List()
    {
        final Category[] cats = Category.getAllCategories();
        for (int i = 0; i < cats.length; i++)
        {
            System.out.println(cats[i].getName());
        }
    }

    public static void main(String[] args)
    {
        BasicConfigurator.configure();
        //unitTest_List();
        unitTest_Delete();
    }

    public void update(JpasObservable<TransactionTransfer> observable, JpasDataChange<TransactionTransfer> change)
    {
        // TODO Auto-generated method stub
        
    }
}
