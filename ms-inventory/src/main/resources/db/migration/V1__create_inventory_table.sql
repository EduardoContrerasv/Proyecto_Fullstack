CREATE TABLE inventory (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    item_type VARCHAR(50) NOT NULL,
    CONSTRAINT uq_inventory_user_item UNIQUE (user_id, item_id)
);
