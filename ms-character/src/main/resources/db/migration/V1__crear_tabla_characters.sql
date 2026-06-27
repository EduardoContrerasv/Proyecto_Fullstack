CREATE TABLE base_characters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    character_archetype VARCHAR(50) NOT NULL,
    default_cosmetic_item_id BIGINT NOT NULL,
    base_health INT NOT NULL,
    base_attack INT NOT NULL,
    base_defense INT NOT NULL
);

CREATE TABLE user_characters (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    base_character_id BIGINT NOT NULL,
    equipped_weapon_id BIGINT NULL,
    equipped_armor_id BIGINT NULL,
    equipped_cosmetic_id BIGINT NOT NULL,
    current_level INT NOT NULL DEFAULT 1,
    current_experience INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_user_char_base FOREIGN KEY (base_character_id) REFERENCES base_characters(id)
);
