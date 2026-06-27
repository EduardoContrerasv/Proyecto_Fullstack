CREATE TABLE quests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    quest_type VARCHAR(50) NOT NULL,
    objective INT NOT NULL,
    exp_reward INT NOT NULL,
    coin_reward INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE user_quest (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quest_id BIGINT NOT NULL,
    objectives_remaining INT,
    is_completed BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_user_quest_quest FOREIGN KEY (quest_id) REFERENCES quests(id)
);
