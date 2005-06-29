package org.jpas.da;

import org.apache.log4j.BasicConfigurator;

public abstract class TransAccountMappingDA
{

    public static interface TransAccountTranferHandler
    {
        public void setData(final long amount);
    }

    
    public abstract void createTransAccountMapping(final Integer transactionID,
                                                   final Integer accountID,
                                                   final long amount);

    public abstract void loadTransAccountMapping(
                                                 final Integer transactionID,
                                                 final Integer accountID,
                                                 final TransAccountTranferHandler handler);

    public abstract void updateTAMAccount(final Integer transactionID,
                                          final Integer accountID,
                                          final Integer newAccountID);

    public abstract void updateTAMAmount(final Integer transactionID,
                                         final Integer accountID,
                                         final long amount);

    public abstract void deleteTransAccountMapping(final Integer transactionID,
                                                   final Integer accountID);

    public abstract long getTransactionAmount(final Integer transactionID);

    public abstract long getAccountBalance(final Integer accountID);

    public abstract Integer[] getAllTranfersForAccount(final Integer accountID);

    public abstract Integer[] getAllTranfersForTransaction(
                                                           final Integer transactionID);

    public abstract boolean doesTransAccountTransferExist(
                                                          final Integer transId,
                                                          final Integer accountId);
    public static void unitTest_Create()
    {
        DAFactory.getTransAccountMappingDA().createTransAccountMapping(new Integer(0), new Integer(3),
                435);
        DAFactory.getTransAccountMappingDA().createTransAccountMapping(new Integer(0), new Integer(4),
                755);
        DAFactory.getTransAccountMappingDA().createTransAccountMapping(new Integer(1), new Integer(5),78945);
    }

    public static void unitTest_GetAmount()
    {
        System.out.println("Amount: "
                + DAFactory.getTransAccountMappingDA().getTransactionAmount(new Integer(0)));
    }

    public static void unitTest_GetBalance()
    {
        System.out.println("Balance: "
                + DAFactory.getTransAccountMappingDA().getAccountBalance(new Integer(0)));
        System.out.println("Balance: "
                + DAFactory.getTransAccountMappingDA().getAccountBalance(new Integer(1)));
        System.out.println("Balance: "
                + DAFactory.getTransAccountMappingDA().getAccountBalance(new Integer(2)));
        System.out.println("Balance: "
                + DAFactory.getTransAccountMappingDA().getAccountBalance(new Integer(3)));
    }

    public static void main(String[] args)
    {
        BasicConfigurator.configure();

        //unitTest_Create();
        //unitTest_GetBalance();
        unitTest_GetAmount();
    }

}
