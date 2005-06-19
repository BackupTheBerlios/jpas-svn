package org.jpas.model;

import java.util.Date;

import org.apache.log4j.Logger;
import org.jpas.da.*;
import org.jpas.model.Reminder.AmountMethod;
import org.jpas.model.Reminder.RepeatMethod;
import org.jpas.util.*;

public class ModelFactory implements JpasObserver
{
    private static final Logger defaultLogger = Logger.getLogger(ModelFactory.class);

	public static ModelFactory instance = new ModelFactory();

	public static ModelFactory getInstance()
	{
		return instance;
	}
    
	private ModelFactory(){} 

    
    class DoubleIntegerKey 
	{
		final Integer a;
		final Integer b;
		DoubleIntegerKey(final Integer a, final Integer b)
		{
			this.a = a;
			this.b = b;
		}
		
		public boolean equals(final Object o)
		{
			return o instanceof DoubleIntegerKey 
				&& ((DoubleIntegerKey)o).a.equals(a)
				&& ((DoubleIntegerKey)o).b.equals(b);
		}
		
		public int hashCode()
		{
			return a.intValue() * b.intValue();
		}
	}
    
    private WeakValueMap<Integer, AccountImpl> accountCache = new WeakValueMap<Integer, AccountImpl>();    
    private WeakValueMap<Integer, Transaction> transactionCache = new WeakValueMap<Integer, Transaction>();
    private WeakValueMap<DoubleIntegerKey, TransactionTransfer> transTransferCache = new WeakValueMap<DoubleIntegerKey, TransactionTransfer>();
    private WeakValueMap<Integer, Reminder> reminderCache = new WeakValueMap<Integer, Reminder>();
    private WeakValueMap<DoubleIntegerKey, ReminderTransfer> remTransferCache = new WeakValueMap<DoubleIntegerKey, ReminderTransfer>();

    private final JpasObservable accountObservable = new JpasObservableImpl();
    private final JpasObservable transactionObservable = new JpasObservableImpl();
    private final JpasObservable transactionTransferObservable = new JpasObservableImpl();
    private final JpasObservable reminderObservable = new JpasObservableImpl();
    private final JpasObservable reminderTransferObservable = new JpasObservableImpl();

	public void update(JpasObservable observable, JpasDataChange change) 
	{
		final Object value = change.getValue();
		if(value instanceof AccountImpl)
		{
			if(change instanceof JpasDataChange.Delete)
			{
				accountCache.remove(((AccountImpl)value).id);
			}
			accountObservable.notifyObservers(change);
		}
		else if(value instanceof Transaction)
		{
			if(change instanceof JpasDataChange.Delete)
			{
				transactionCache.remove(((Transaction)value).id);
			}
			transactionObservable.notifyObservers(change);
		}
		else if(value instanceof TransactionTransfer)
		{
			if(change instanceof JpasDataChange.Delete)
			{
				final TransactionTransfer transfer = (TransactionTransfer)value;
				transTransferCache.remove(new DoubleIntegerKey(transfer.transactionID, transfer.accountID));
			}
			transactionTransferObservable.notifyObservers(change);
		}
		else if(value instanceof Reminder)
		{
			if(change instanceof JpasDataChange.Delete)
			{
				reminderCache.remove(((Reminder)value).id);
			}
			reminderObservable.notifyObservers(change);	
		}
		else if(value instanceof ReminderTransfer)
		{
			if(change instanceof JpasDataChange.Delete)
			{
				final ReminderTransfer transfer = (ReminderTransfer)value;
				remTransferCache.remove(new DoubleIntegerKey(transfer.reminderID, transfer.accountID));
			}
			reminderTransferObservable.notifyObservers(change);
		}
	}
    
    public JpasObservable getAccountObservable()
    {
        return accountObservable;
    }

    public JpasObservable getTransactionObservable()
    {
        return transactionObservable;
    }

    public JpasObservable getTransactionTransferObservable()
    {
        return transactionTransferObservable;
    }

    public JpasObservable getReminderObservable()
    {
        return transactionObservable;
    }

    public JpasObservable getReminderTransferObservable()
    {
        return transactionTransferObservable;
    }
    
    public TransactionTransfer[] getTransfersForTransaction(final Transaction trans)
    {
        final Integer[] accountIDs = TransAccountMappingDA.getInstance()
                        .getAllTranfersForTransaction(trans.id);
        final TransactionTransfer[] ttArray = new TransactionTransfer[accountIDs.length];
        for (int i = 0; i < accountIDs.length; i++)
        {
            ttArray[i] = getTransactionTransferforIDs(trans.id, accountIDs[i]);
        }
        return ttArray;
    }

    public  TransactionTransfer getTransfer(final Transaction trans,  final Category cat)
    {
        if(TransAccountMappingDA.getInstance().doesTransAccountTransferExist(trans.id, ((AccountImpl)cat).id))
        {
            return getTransactionTransferforIDs(trans.id, ((AccountImpl)cat).id);
        }
        return null;
    }

    public TransactionTransfer createTransfer(final Transaction trans, final Category category, final long amount)
    {
        TransAccountMappingDA.getInstance().createTransAccountMapping(trans.id, ((AccountImpl)category).id, amount);

        final TransactionTransfer transfer =  getTransactionTransferforIDs(trans.id, ((AccountImpl)category).id);
        
        final JpasDataChange change = new JpasDataChange.AmountModify(transfer);
        transactionTransferObservable.notifyObservers(change);
        return transfer;
    }
    
    TransactionTransfer getTransactionTransferforIDs(final Integer transactionID, final Integer categoryID)
    {
        assert(TransAccountMappingDA.getInstance().doesTransAccountTransferExist(transactionID, categoryID));
        
		final DoubleIntegerKey key = new DoubleIntegerKey(transactionID, categoryID);
		TransactionTransfer transfer = transTransferCache.get(key);

		if(transfer == null)
	    {
			transfer = new TransactionTransfer(transactionID, categoryID);
			transfer.addObserver(this);
			transTransferCache.put(key, transfer);
	    }
		return transfer;
    }


    
    public Transaction[] getAllTransactionsAffecting(final Account account)
    {
        final Integer[] ids = TransactionDA.getInstance().getAllAffectingTransactionIDs(((AccountImpl)account).id);
        final Transaction[] trans = new Transaction[ids.length];
        long balance = 0;
        for (int i = 0; i < ids.length; i++)
        {
            trans[i] = getTransactionForID(ids[i]);
            if (trans[i].getAccount().equals(account))
            {
                balance -= trans[i].getAmount();
            }
            else
            {
                final AccountImpl cat = (AccountImpl)ModelFactory.getInstance().getCategoryForAccount(account);
                balance += getTransactionTransferforIDs(trans[i].id, cat.id ).getAmount();
            }
            trans[i].setBalance(balance);
        }
        return trans;
    }

    Transaction getTransactionForID(final Integer id)
    {
        Transaction trans = transactionCache.get(id);
        if (trans == null)
        {
            trans = new Transaction(id);
            trans.addObserver(this);
            transactionCache.put(id, trans);
        }
        return trans;
    }

    public Transaction createTransaction(final Account account,
                                                final String payee,
                                                final String memo,
                                                final String num,
                                                final Date date)
    {
        final Transaction trans = getTransactionForID(TransactionDA.getInstance()
                        .createTransaction(((AccountImpl)account).id, payee, memo, num,
                                        new java.sql.Date(date.getTime())));
        
        final JpasDataChange change = new JpasDataChange.AmountModify(trans);
        transactionObservable.notifyObservers(change);
        return trans;
    }
	
	
	
	
	
	
	
	
    public Category[] getAllCategories()
    {
        final Integer[] ids = AccountDA.getInstance().getAllAccountIDs();
        final Category[] categories = new AccountImpl[ids.length];
        for (int i = 0; i < ids.length; i++)
        {
            categories[i] = getAccountImplForID(ids[i]);
        }
        return categories;
    }

    public Category getDeletedBankCategory()
    {
        return getAccountImplForID(AccountDA.getInstance().getDeletedBankAccountID());
    }
    
    public Category getUnknownCategory()
    {
        return getAccountImplForID(AccountDA.getInstance().getUnknownCategoryID());
    }
    
    public Category getCategoryForAccount(final Account account)
    {
    	return (AccountImpl)account;
    }
    
    public Category createIncomeCategory(final String name)
    {
        return getAccountImplForID(AccountDA.getInstance().createAccount(name,
                AccountDA.AccountType.INCOME_CATEGORY));
    }

    public Category createExpenseCategory(final String name)
    {
        return getAccountImplForID(AccountDA.getInstance().createAccount(name,
                AccountDA.AccountType.EXPENSE_CATEGORY));
    }

    
    AccountImpl getAccountImplForID(final Integer id)
    {
        assert(id != null);
        AccountImpl account = accountCache.get(id);
        if (account == null)
        {
            account = new AccountImpl(id);
            account.addObserver(this);
            accountCache.put(id, account);
        }
        return account;
    }

    public Account getAccountForCategory(final Category account)
    {
    	return (AccountImpl)account;
    }
    
    public Account createAccount(final String name)
    {
        final Account account = getAccountImplForID(AccountDA.getInstance()
                .createAccount(name, AccountDA.AccountType.BANK));
        accountObservable.notifyObservers(new JpasDataChange.Add(account));
        return account;
    }
    
    public Account[] getAllAccounts()
    {
        final Integer[] ids = AccountDA.getInstance().getAllAccountIDs(
                AccountDA.AccountType.BANK);
        final Account[] accounts = new Account[ids.length];
        for (int i = 0; i < ids.length; i++)
        {
            accounts[i] = getAccountImplForID(ids[i]);
        }
        return accounts;
    }

    
    public Account getDeletedBankAccount()
    {
        return getAccountImplForID(AccountDA.getInstance().getDeletedBankAccountID());
    }
    
    public Reminder[] getAllReminders()
    {
        final Integer[] ids = ReminderDA.getInstance().getAllReminderIDs();
        final Reminder[] reminders = new Reminder[ids.length];
        for (int i = 0; i < ids.length; i++)
        {
            reminders[i] = getReminderForID(ids[i]);
        }
        return reminders;
    }

    Reminder getReminderForID(final Integer id)
    {
        Reminder rem = reminderCache.get(id);
        if (rem == null)
        {
            rem = new Reminder(id);
            rem.addObserver(this);
            reminderCache.put(id, rem);
        }
        return rem;
    }

    public Reminder createReminder(final Account account,
            final String payee, final String memo, final Date date,
            final AmountMethod amountMethod, final RepeatMethod repeatMethod,
            final int repeatValue)
    {
        return getReminderForID(ReminderDA.getInstance().createReminder(
                ((AccountImpl)account).id, payee, memo, new java.sql.Date(date.getTime()), amountMethod.daAmountMethod,
                repeatMethod.daRepeatMethod, repeatValue));
    }
    
    //TODO createReminderTransfer

    public ReminderTransfer[] getTransfersForReminder(final Reminder rem)
    {
        final Integer[] accountIDs = ReminderAccountMappingDA.getInstance()
                .getAllReminderAccountTranfers(rem.id);
        final ReminderTransfer[] ttArray = new ReminderTransfer[accountIDs.length];
        for (int i = 0; i < accountIDs.length; i++)
        {
            ttArray[i] = ModelFactory.getInstance().getReminderTransferforIDs(rem.id, accountIDs[i]);
        }
        return ttArray;
    }
    
    ReminderTransfer getReminderTransferforIDs(final Integer reminderID,
            final Integer accountID)
    {
        ReminderTransfer rt = remTransferCache.get(new DoubleIntegerKey(reminderID, accountID));
        if (rt == null)
        {
            rt = new ReminderTransfer(reminderID, accountID);
            rt.addObserver(this);
            remTransferCache.put(new DoubleIntegerKey(reminderID, accountID), rt);
        }
        return rt;
    }
}
