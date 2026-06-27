CREATE TABLE shop_catalog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    price INT NOT NULL,
    currency_type VARCHAR(30) NOT NULL
);
