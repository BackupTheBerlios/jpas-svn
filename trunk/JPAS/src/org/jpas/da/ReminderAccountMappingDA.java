package org.jpas.da;

import org.jpas.da.hsqldb.ReminderAccountMappingDAImpl;


public abstract class ReminderAccountMappingDA
{
    private static ReminderAccountMappingDA instance = new ReminderAccountMappingDAImpl();
    
    public static ReminderAccountMappingDA getInstance()
    {
        return instance;
    }

    public static interface ReminderAccountTranferHandler
    {
        public void setData(final long amount);
    }
    
    public abstract void createReminderAccountMapping(final Integer reminderID,
                                                      final Integer accountID,
                                                      final long amount);

    public abstract Integer[] getAllReminderAccountTranfers(
                                                            final Integer reminderID);

    public abstract void loadReminderAccountMapping(
                                                    final Integer reminderID,
                                                    final Integer accountID,
                                                    final ReminderAccountTranferHandler handler);

    public abstract void deleteReminderAccountMapping(final Integer reminderID,
                                                      final Integer accountID);

    public abstract void updateReminderAccountMapping(final Integer reminderID,
                                                      final Integer accountID,
                                                      final long amount);

    public abstract long getReminderAmount(final Integer reminderID);

}
