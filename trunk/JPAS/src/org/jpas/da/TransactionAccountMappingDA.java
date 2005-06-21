package org.jpas.da;

import org.jpas.da.hsqldb.TransAccountMappingDAImpl;


public abstract class TransactionAccountMappingDA
{
    private static TransactionAccountMappingDA instance = new TransAccountMappingDAImpl();

    public static TransactionAccountMappingDA getInstance()
    {
        return instance;
    }

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

}
