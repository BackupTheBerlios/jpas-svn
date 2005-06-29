package org.jpas.da;

import org.jpas.util.PropertyManager;

public class DAFactory
{
    private static final ConnectionManager connMan;
    private static final InitDB initDB;
    private static final AccountDA accountDA;
    private static final TransactionDA transDA;
    private static final TransAccountMappingDA tamDA;
    private static final ReminderDA reminderDA;
    private static final ReminderAccountMappingDA ramDA;
    
    static
    {
        try
        {
            final String daImpl = PropertyManager.getInstance().getProperty(PropertyManager.DB_IMPL, "hsqldb");
            connMan = (ConnectionManager) Class.forName("org.jpas.da." + daImpl + ".ConnectionManagerImpl").newInstance();
            initDB = (InitDB) Class.forName("org.jpas.da." + daImpl + ".InitDBImpl").newInstance();
            accountDA = (AccountDA) Class.forName("org.jpas.da." + daImpl + ".AccountDAImpl").newInstance();
            transDA = (TransactionDA) Class.forName("org.jpas.da." + daImpl + ".TransactionDAImpl").newInstance();
            tamDA = (TransAccountMappingDA) Class.forName("org.jpas.da." + daImpl + ".TransAccountMappingDAImpl").newInstance();
            reminderDA = (ReminderDA) Class.forName("org.jpas.da." + daImpl + ".ReminderDAImpl").newInstance();
            ramDA = (ReminderAccountMappingDA) Class.forName("org.jpas.da." + daImpl + ".ReminderAccountMappingDAImpl").newInstance();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static ConnectionManager getConnectionManager()
    {
        return connMan;
    }
    
    public static InitDB getInitDB()
    {
        return initDB;
    }
    public static AccountDA getAccountDA()
    {
        return accountDA;
    }
    public static TransactionDA getTransactionDA()
    {
        return transDA;
    }
    public static TransAccountMappingDA getTransAccountMappingDA()
    {
        return tamDA;
    }
    public static ReminderDA getReminderDA()
    {
        return reminderDA;
    }
    public static ReminderAccountMappingDA getReminderAccountMappingDA()
    {
        return ramDA;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
