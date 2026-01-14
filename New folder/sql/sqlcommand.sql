use bharatcrypto;
SELECT * FROM user;
SELECT * FROM coin;
SELECT * FROM coins;
SELECT * FROM users;
SET FOREIGN_KEY_CHECKS=0;
DELETE FROM users where email = "jit189111@gmail.com";
SET FOREIGN_KEY_CHECKS=1;

SELECT MAX(coin_rank) AS max_value FROM coins;
SELECT COUNT(*) AS total_rows FROM coins;

