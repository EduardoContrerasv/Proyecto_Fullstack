CREATE TABLE combats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    enemy VARCHAR(255),
    max_participants INT,
    user_id BIGINT,
    result VARCHAR(50),
    experience_gained INT,
    coins_gained INT,
    combat_date DATETIME,
    base_experience INT,
    base_coins INT,
    currency_type VARCHAR(30)
);

CREATE TABLE combat_participants (
    combat_id BIGINT NOT NULL,
    character_id BIGINT,
    CONSTRAINT fk_combat_participants FOREIGN KEY (combat_id) REFERENCES combats(id)
);

CREATE TABLE combat_character_names (
    combat_id BIGINT NOT NULL,
    character_name VARCHAR(255),
    CONSTRAINT fk_combat_character_names FOREIGN KEY (combat_id) REFERENCES combats(id)
);
