package org.jpas.da;

import java.sql.Date;

import org.apache.log4j.BasicConfigurator;

public abstract class TransactionDA
{
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

    public static void unitTest_Create()
    {
        DAFactory.getTransactionDA().createTransaction(new Integer(2), "Joe`s bar and grill", "memo", "23", new Date(System.currentTimeMillis()));
        DAFactory.getTransactionDA().createTransaction(new Integer(2), "Kat`s Home Cooking", "memos", "21", new Date(System.currentTimeMillis()));
    }
    
    
    public static void main(final String[] args)
    {
        BasicConfigurator.configure();
        unitTest_Create();
    }
}
