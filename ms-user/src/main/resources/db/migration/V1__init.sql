CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    register_date DATETIME NOT NULL,
    account_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    account_level INT NOT NULL DEFAULT 1,
    CONSTRAINT chk_account_status CHECK (account_status IN ('ACTIVE','BANNED','DELETED'))
);
