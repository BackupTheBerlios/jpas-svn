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

import org.apache.log4j.Logger;
import org.jpas.da.AccountDA;
import org.jpas.da.DAFactory;
import org.jpas.util.*;

class AccountImpl extends JpasObservableImpl implements Category, Account
{
	public static Logger defaultLogger = Logger.getLogger(AccountImpl.class);
	
    final Integer id;
    private boolean isModified = false;
    private boolean isDeleted = false;
    private boolean isLoaded = false;
    
    private String name;
    private AccountDA.AccountType type;

    private boolean balanceLoaded = false;
    private long balance;

    static
    {
    	ModelFactory.getInstance().getTransactionObservable().addObserver(new JpasObserver()
		{
			public void update(JpasObservable observable, JpasDataChange change) 
			{
				if(change instanceof JpasDataChange.Delete || change instanceof JpasDataChange.AmountModify)
				{
					final Transaction trans = (Transaction)change.getValue();
					ModelFactory.getInstance().getAccountImplForID(((AccountImpl)trans.getAccount()).id).amountChanged();
				}
			}
		});
        ModelFactory.getInstance().getTransactionTransferObservable().addObserver(new JpasObserver()
        {
            public void update(JpasObservable observable, JpasDataChange change) 
            {
                if(change instanceof JpasDataChange.Delete || change instanceof JpasDataChange.AmountModify)
                {
                    final TransactionTransfer transfer = (TransactionTransfer)change.getValue();
                    ModelFactory.getInstance().getAccountImplForID(((AccountImpl)transfer.getCategory()).id).amountChanged();
                }
            }
        });
    }
    
    AccountImpl(final Integer id)
    {
        defaultLogger.debug("Constructing AccountImpl: " + id);
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

    public void reload()
    {
    	isLoaded = false;
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
                
                final Category cat = ModelFactory.getInstance().getUnknownCategory();
                final Integer[] transferIDs = DAFactory.getTransAccountMappingDA().getAllTranfersForAccount(id);
                for(int i = 0; i < transferIDs.length; i++)
                {
                    final TransactionTransfer transfer = ModelFactory.getInstance().getTransactionTransferforIDs(transferIDs[i], id);
                    ModelFactory.getInstance().createTransfer(ModelFactory.getInstance().getTransactionForID(transferIDs[i]), cat, transfer.getAmount());
                    transfer.delete();
                    transfer.commit();
                }

                final Account account = ModelFactory.getInstance().getDeletedBankAccount();
                final Integer[] transactionsIDs = DAFactory.getTransactionDA().getAllTransactionIDs(id);
                for(int i = 0; i < transactionsIDs.length; i++)
                {
                    final Transaction trans = ModelFactory.getInstance().getTransactionForID(transactionsIDs[i]);
                    trans.delete();
                    trans.commit();
                }
                
                DAFactory.getAccountDA().deleteAccount(id);
                isModified = false;

                announceDelete();
            }
            else
            {
                DAFactory.getAccountDA().updateAccount(id, name, type);
            	isModified = false;
            	
            	announceModify();
            }
        }
    }

    private void announceDelete()
    {
        final JpasDataChange change = new JpasDataChange.Delete(
                this);
        notifyObservers(change);
        deleteObservers();
    }

    private void announceModify()
    {
        final JpasDataChange change = new JpasDataChange.Modify(
                this);
        notifyObservers(change);
    }
    
    private void announceAmountModify()
    {
        final JpasDataChange change = new JpasDataChange.AmountModify(
                this);
        notifyObservers(change);
    }
    
    private void amountChanged()
    {
    	balanceLoaded = false;
    	announceAmountModify();
    }

    public String getCategoryName()
    {
        return (type == AccountDA.AccountType.BANK || type == AccountDA.AccountType.DELETED_BANK) ? "TXFR["
                + getName() + "]"
                : getName();
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
        DAFactory.getAccountDA().loadAccount(id, new AccountDA.AccountHandler()
        {
            public void setData(final String name,
                    final AccountDA.AccountType type)
            {
                AccountImpl.this.name = name;
                AccountImpl.this.type = type;
                isLoaded = true;
            }
        });
    }

    public void setName(final String name)
    {
        assert (!isDeleted);
        DAFactory.getAccountDA().updateAccountName(id, name);
        if (isLoaded)
        {
            loadData();
        }
    }

    public long getTotal()
    {
    	return getBalance();
    }

    public long getBalance()
    {
        if(!balanceLoaded)
        {
            balance = DAFactory.getTransAccountMappingDA().getAccountBalance(id);
            balanceLoaded = true;
        }
        return balance;
    }
    
    public String[] getAllPayees()
    {
    	return DAFactory.getAccountDA().getAllPayeesForAccount(id);
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

/*    
    private static void unitTest_Delete()
    {
        final AccountImpl[] cats = getAllCategories();
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
        final AccountImpl[] cats = getAllCategories();
        for (int i = 0; i < cats.length; i++)
        {
            System.out.println(cats[i].getCategoryName());
        }
    }

    public static void main(String[] args)
    {
        BasicConfigurator.configure();
        //unitTest_List();
        unitTest_Delete();
    }
*/
}
