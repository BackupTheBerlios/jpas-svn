package org.jpas.da;

/**
 * Title: JPAS
 * Description: Java based Personal Accounting System
 * Copyright: Copyright (c) 2004 Justin W Smith
 * @author Justin W Smith
 * @version 1.0
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
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
	public static String CN_ACCOUNT_TYPE = "type";
	public static String SEQ_ACCOUNT_ID = "account_seq";

	public static String TN_REMINDER_ACCOUNT_MAP = "rem_account_map_tbl";
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
