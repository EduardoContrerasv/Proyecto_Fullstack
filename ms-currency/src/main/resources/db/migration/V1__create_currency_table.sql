CREATE TABLE user_currency (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            user_id BIGINT NOT NULL,
            currency_type VARCHAR(50) NOT NULL,
            amount INT NOT NULL DEFAULT 0
);