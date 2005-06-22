package org.jpas.da;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;

public abstract class AccountDA
{
	public static class AccountType
	{
	    private static final Map<Integer, AccountType> valueMap = new HashMap<Integer, AccountType>();
	    
	    public final int dbValue;
	    private AccountType(final int value)
	    {
	        this.dbValue = value;
	        valueMap.put(new Integer(value), this);
	    }
	    
	    public static AccountType getAccountTypeFor(final int value)
	    {
	        return valueMap.get(new Integer(value));
	    }
	    
	    public static final AccountType BANK = new AccountType(0);
	    public static final AccountType EXPENSE_CATEGORY = new AccountType(1);
	    public static final AccountType INCOME_CATEGORY = new AccountType(2);
	    public static final AccountType DELETED_BANK = new AccountType(3);
	    public static final AccountType UNKNOWN_CATEGORY = new AccountType(4); 
	}

	public static interface AccountHandler
	{
		public void setData(String name, AccountType bankAccount);
	}

    
    public abstract void loadAccount(final Integer id,
                                     final AccountHandler handler);

    public abstract void updateAccount(final Integer id, final String name,
                                       final AccountType type);

    public abstract void updateAccountName(final Integer id, final String name);

    public abstract void updateAccountType(final Integer id,
                                           final AccountType type);

    public abstract Integer getDeletedBankAccountID();

    public abstract Integer getUnknownCategoryID();

    public abstract Integer createAccount(final String name,
                                          final AccountType type);

    public abstract void deleteAccount(final Integer id);

    public abstract boolean doesAccountExist(final Integer id);

    public abstract boolean doesAccountExist(final AccountType type);

    public abstract Integer[] getAllAccountIDsExcept(final AccountType type);

    public abstract Integer[] getAllAccountIDs(final AccountType type);

    public abstract String[] getAllPayeesForAccount(final Integer accountId);

    public abstract Integer[] getAllAccountIDs();

    public static void unitTest_Create()
    {
        DAFactory.getAccountDA().createAccount("DELETED", AccountType.DELETED_BANK);
        DAFactory.getAccountDA().createAccount("UNKNOWN", AccountType.UNKNOWN_CATEGORY);
        DAFactory.getAccountDA().createAccount("Checking", AccountType.BANK);
        DAFactory.getAccountDA().createAccount("Savings", AccountType.BANK);
        DAFactory.getAccountDA().createAccount("Utility", AccountType.EXPENSE_CATEGORY);
        DAFactory.getAccountDA().createAccount("Entertainment", AccountType.EXPENSE_CATEGORY);
    }
    
    public static void main(final String[] args)
    {
        BasicConfigurator.configure();
        
        unitTest_Create();
    }
}
