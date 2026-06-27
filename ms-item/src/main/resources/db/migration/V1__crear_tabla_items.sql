CREATE TABLE items (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       description VARCHAR(255),
                       price INT NOT NULL DEFAULT 0,
                       required_level INT NOT NULL DEFAULT 0,
                       item_type VARCHAR(50),
                       rarity VARCHAR(50)
);