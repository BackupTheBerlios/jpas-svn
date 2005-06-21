package org.jpas.da;

import org.apache.log4j.BasicConfigurator;
import org.jpas.da.hsqldb.*;

public abstract class InitDB
{
    private static final InitDB instance = new InitDBImpl();
    
    public static InitDB getInstance()
    {
        return instance;
    }

    public abstract void createUser(final String userName, final String passwd,
                                    final boolean admin);

    public abstract void deleteUser(final String userName);

    public abstract void changePassword(final String userName,
                                        final String passwd);

    public abstract void deleteAccountTable();

    public abstract void createAccountTable();

    public abstract void deleteTransactionTable();

    public abstract void createTransactionTable();

    public abstract void deleteTransAccountMapTable();

    public abstract void createTransAccountMapTable();

    public abstract void deleteReminderTable();

    public abstract void createReminderTable();

    public abstract void deleteRemAccountMapTable();

    public abstract void createRemAccountMapTable();

    public static void unitTest_dropAll()
    {
        instance.deleteTransAccountMapTable();
        instance.deleteRemAccountMapTable();
        instance.deleteReminderTable();
        instance.deleteTransactionTable();
        instance.deleteAccountTable();
    }
    
    public static void unitTest_createAll()
    {
        instance.createAccountTable();
        instance.createTransactionTable();
        instance.createReminderTable();
        instance.createTransAccountMapTable();
        instance.createRemAccountMapTable();
    }
    
    public static void unitTest_populateAll()
    {
        AccountDAImpl.unitTest_Create();
        TransactionDAImpl.unitTest_Create();
        TransAccountMappingDAImpl.unitTest_Create();
        ReminderDAImpl.unitTest_Create();
        ReminderAccountMappingDAImpl.unitTest_Create();
    }
    
    public static void main(final String[] args)
    {
        BasicConfigurator.configure();

        //unitTest_dropAll();
        
        unitTest_createAll();
        
        unitTest_populateAll();
    }
}
