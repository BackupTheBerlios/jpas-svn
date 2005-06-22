package org.jpas.da;

import org.apache.log4j.BasicConfigurator;

public abstract class InitDB
{
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
        DAFactory.getInitDB().deleteTransAccountMapTable();
        DAFactory.getInitDB().deleteRemAccountMapTable();
        DAFactory.getInitDB().deleteReminderTable();
        DAFactory.getInitDB().deleteTransactionTable();
        DAFactory.getInitDB().deleteAccountTable();
    }
    
    public static void unitTest_createAll()
    {
        DAFactory.getInitDB().createAccountTable();
        DAFactory.getInitDB().createTransactionTable();
        DAFactory.getInitDB().createReminderTable();
        DAFactory.getInitDB().createTransAccountMapTable();
        DAFactory.getInitDB().createRemAccountMapTable();
    }
    
    public static void unitTest_populateAll()
    {
        AccountDA.unitTest_Create();
        TransactionDA.unitTest_Create();
        TransAccountMappingDA.unitTest_Create();
        ReminderDA.unitTest_Create();
        ReminderAccountMappingDA.unitTest_Create();
    }
    
    public static void main(final String[] args)
    {
        BasicConfigurator.configure();

        unitTest_dropAll();
        
        unitTest_createAll();
        
        unitTest_populateAll();
    }
}
