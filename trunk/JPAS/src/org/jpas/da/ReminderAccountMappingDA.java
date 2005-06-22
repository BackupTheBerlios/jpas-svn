package org.jpas.da;

import org.apache.log4j.BasicConfigurator;


public abstract class ReminderAccountMappingDA
{
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

   public static void unitTest_GetAmount()
    {
        System.out.println("Amount: "
                + DAFactory.getReminderAccountMappingDA().getReminderAmount(new Integer(0)));
    }

    
    public static void unitTest_Create()
    {
        DAFactory.getReminderAccountMappingDA().createReminderAccountMapping(new Integer(0), new Integer(2),
                7735);
        DAFactory.getReminderAccountMappingDA().createReminderAccountMapping(new Integer(0), new Integer(3),
                5855);
    }

    public static void main(String[] args)
    {
        BasicConfigurator.configure();

        unitTest_GetAmount();
    }


}
