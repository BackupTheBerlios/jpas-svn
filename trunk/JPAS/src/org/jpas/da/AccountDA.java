package org.jpas.da;

import org.apache.log4j.BasicConfigurator;
import org.jpas.da.hsqldb.AccountDAImpl;
import org.jpas.da.hsqldb.AccountDAImpl.AccountHandler;
import org.jpas.da.hsqldb.AccountDAImpl.AccountType;

public abstract class AccountDA
{
    private static AccountDA instance = new AccountDAImpl();

    public static AccountDA getInstance()
    {
        return instance;
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
        instance.createAccount("Checking", AccountType.BANK);
        instance.createAccount("Savings", AccountType.BANK);
        instance.createAccount("Utility", AccountType.EXPENSE_CATEGORY);
        instance.createAccount("Entertainment", AccountType.EXPENSE_CATEGORY);
    }
    
    public static void main(final String[] args)
    {
        BasicConfigurator.configure();
        
        unitTest_Create();
    }
}
