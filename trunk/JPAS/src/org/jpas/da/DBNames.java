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
	public static final String TN_TRANSACTION = "transaction_tbl";
	public static final String CN_TRANSACTION_ID = "id";
	public static final String CN_TRANSACTION_PAYEE = "payee";
	public static final String CN_TRANSACTION_ACCOUNT = "account";
	public static final String CN_TRANSACTION_DATE = "date";
	public static final String CN_TRANSACTION_NUM = "num";
	public static final String CN_TRANSACTION_MEMO = "memo";
	public static final String IDX_TRANSACTION_ACCOUNT = "trans_account_idx";
	public static final String SEQ_TRANSACTION_ID = "transaction_seq";

	public static final String TN_TRANSACTION_ACCOUNT_MAP = "trans_account_map_tbl";
	public static final String CN_TAM_ACCOUNT_ID = "account_id";
	public static final String CN_TAM_TRANSACTION_ID = "transaction_id";
	public static final String CN_TAM_AMOUNT = "amount";
	public static final String IDX_TAM_ACCOUNT = "tam_account_idx";
	public static final String IDX_TAM_TRANSACTION = "tam_transaction_idx";
		
	public static String TN_ACCOUNT = "account_tbl";
	public static String CN_ACCOUNT_ID = "id";
	public static String CN_ACCOUNT_NAME = "name";
	public static String CN_ACCOUNT_PRIMARY = "bank";
	public static String SEQ_ACCOUNT_ID = "account_seq";

	public static String TN_REMINDER_ACCOUNT_MAP = "ram_account_map_tbl";
	public static String CN_RAM_ACCOUNT_ID = "account_id";
	public static String CN_RAM_REMINDER_ID = "reminder_id";
	public static String CN_RAM_AMOUNT = "amount";
	public static final String IDX_RAM_ACCOUNT = "ram_account_idx";
	public static final String IDX_RAM_REMINDER = "ram_reminder_idx";

	
	public static String TN_REMINDER = "reminder_tbl";
	public static String CN_REMINDER_ID = "id";
	public static String CN_REMINDER_PAYEE = "payee";
	public static String CN_REMINDER_DATE = "date";
	public static String CN_REMINDER_ACCOUNT = "account";
	public static String CN_REMINDER_AMOUNT_METHOD = "amount_method";
	public static String CN_REMINDER_REPEAT_METHOD = "repeat_method";
	public static String CN_REMINDER_REPEAT_VALUE = "repeat_value";
	public static String CN_REMINDER_MEMO = "memo";
	public static String SEQ_REMINDER_ID = "reminder_seq";
}
