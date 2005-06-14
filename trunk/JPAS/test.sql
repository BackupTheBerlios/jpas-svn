select * from account_tbl;

select * from transaction_tbl;

select * from trans_account_map_tbl;

SELECT SUM(trans_account_map_tbl.amount) FROM trans_account_map_tbl , transaction_tbl WHERE transaction_tbl.id = trans_account_map_tbl.transaction_id AND transaction_tbl.account = 2
