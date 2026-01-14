use bharatcrypto;
SELECT * FROM user;
SELECT * FROM coin;
SELECT * FROM coins;
SELECT * FROM users;
SELECT * FROM watchlist;
SELECT * FROM watchlist_coins;
SELECT * FROM payment_details;
SELECT * FROM payment_order;
SELECT * FROM treading_history;
SET FOREIGN_KEY_CHECKS=0;
DELETE FROM users where email = "jit189111@gmail.com";
SET FOREIGN_KEY_CHECKS=1;

SELECT MAX(coin_rank) AS max_value FROM coins;
SELECT COUNT(*) AS total_rows FROM coins;

