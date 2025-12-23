-- Create the sequence table if it doesn't exist
CREATE TABLE IF NOT EXISTS user_seq (
    next_val BIGINT
);
-- Insert the initial value if the table is empty
INSERT INTO user_seq (next_val) SELECT 1 WHERE NOT EXISTS (SELECT * FROM user_seq);