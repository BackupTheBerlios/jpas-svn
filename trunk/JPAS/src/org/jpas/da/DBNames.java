package org.jpas.da;

/**
 * <p>Title: JPAS</p>
 * <p>Description: Java based Personal Accounting System</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>License: Distributed under the terms of the GPL v2</p>
 * @author Justin Smith
 * @version 1.0
 */

public class DBNames
{
	public static String TN_ACCOUNT = "tbl_account";
	public static String CN_ACCOUNT_ID = "id";
	public static String CN_ACCOUNT_NAME = "name";
	public static String SEQ_ACCOUNT_ID = "seq_account_id";


	public static String TN_TRANSACTION = "tbl_transaction";
	public static String CN_TRANSACTION_ID = "id";
	public static String CN_TRANSACTION_PAYEE = "payee";
	public static String CN_TRANSACTION_ACCOUNT = "account";
	public static String CN_TRANSACTION_AMOUNT = "amount";
	public static String CN_TRANSACTION_DATE = "date";
	public static String CN_TRANSACTION_NUM = "num";
	public static String CN_TRANSACTION_MEMO = "memo";
	public static String CN_TRANSACTION_OPPOSITE = "opposite";
	public static String IDX_TRANSACTION_ACCOUNT = "idx_transaction_account";
	public static String IDX_TRANSACTION_PAYEE = "idx_transaction_payee";
	public static String SEQ_TRANSACTION_ID = "seq_transaction_id";


	public static String TN_CATEGORY = "tbl_category";
	public static String CN_CATEGORY_ID = "id";
	public static String CN_CATEGORY_NAME = "name";
	public static String CN_CATEGORY_PARENT = "parent";
	public static String CN_CATEGORY_TAX_DEDUCTABLE = "tax_deductable";
	public static String IDX_CATEGORY_NAME = "idx_category_name";
	public static String SEQ_CATEGORY_ID = "seq_category_id";

	public static String TN_REMINDER = "tbl_reminder";
	public static String CN_REMINDER_ID = "id";
	public static String CN_REMINDER_PAYEE = "payee";
	public static String CN_REMINDER_ACCOUNT = "account";
	public static String CN_REMINDER_AMOUNT_METHOD = "amount_method";
	public static String CN_REMINDER_AMOUNT_VALUE = "amount_value";
	public static String CN_REMINDER_REPEAT_METHOD = "repeat_method";
	public static String CN_REMINDER_REPEAT_VALUE = "repeat_value";
	public static String CN_REMINDER_CATEGORY = "category";
	public static String CN_REMINDER_MEMO = "memo";
	public static String SEQ_REMINDER_ID = "seq_reminder_id";
}
