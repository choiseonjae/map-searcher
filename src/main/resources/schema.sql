CREATE TABLE keyword_history (
     id BIGINT PRIMARY KEY AUTO_INCREMENT,
     keyword VARCHAR(255) NOT NULL,
     search_count BIGINT DEFAULT 0
);
