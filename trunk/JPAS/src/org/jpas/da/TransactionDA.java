package org.jpas.da;

import java.sql.Date;

import org.jpas.da.hsqldb.TransactionDAImpl;

public abstract class TransactionDA
{
    private static final TransactionDA instance = new TransactionDAImpl();
    
    public static TransactionDA getInstance()
    {
        return instance;
    }
    
    public static interface TransactionHandler
    {
        public void setData(final Integer accountId, final String payee, final String memo, final String num, final Date date);
    }

    public abstract void loadTransaction(final Integer id,
                                         final TransactionHandler handler);

    public abstract void updateTransaction(final Integer id,
                                           final String payee,
                                           final String memo, final String num,
                                           final Date date);

    public abstract void updateTransactionPayee(final Integer id,
                                                final String payee);

    public abstract void updateTransactionMemo(final Integer id,
                                               final String memo);

    public abstract void updateTransactionNum(final Integer id, final String num);

    public abstract void updateTransactionDate(final Integer id, final Date date);

    public abstract Integer createTransaction(final Integer accountId,
                                              final String payee,
                                              final String memo,
                                              final String num, final Date date);

    public abstract void deleteTransaction(final Integer id);

    public abstract boolean doesTransactionExist(final Integer id);

    public abstract boolean doesTransactionAffectAccount(final Integer transId,
                                                         final Integer accountId);

    public abstract Integer[] getAllTransactionIDs(final Integer accountId);

    public abstract Integer[] getAllAffectingTransactionIDs(
                                                            final Integer accountId);

}
