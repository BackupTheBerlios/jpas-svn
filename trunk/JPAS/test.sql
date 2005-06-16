select * from account_tbl;

select * from transaction_tbl;

select * from trans_account_map_tbl;

SELECT SUM(trans_account_map_tbl.amount) FROM trans_account_map_tbl , transaction_tbl WHERE transaction_tbl.id = trans_account_map_tbl.transaction_id AND transaction_tbl.account = 2;

SELECT transaction_tbl.id, transaction_tbl.date FROM transaction_tbl , account_tbl , trans_account_map_tbl WHERE trans_account_map_tbl.transaction_id = transaction_tbl.id AND trans_account_map_tbl.account_id = account_tbl.id AND account_tbl.id = 2 UNION  SELECT id, date FROM transaction_tbl WHERE account = 2 ORDER BY transaction_tbl.date, transaction_tbl.id;