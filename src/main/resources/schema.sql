CREATE TABLE IF NOT EXISTS request(
    id IDENTITY,
    source_text VARCHAR NOT NULL,
    source_language VARCHAR(2) NOT NULL,
    target_language VARCHAR(2) NOT NULL,
    translated_text VARCHAR NOT NULL,
    ip VARCHAR(32) NOT NULL,
    request_time TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS word(
    id IDENTITY,
    request BIGINT NOT NULL,
    source_word VARCHAR NOT NULL,
    translated_word VARCHAR NOT NULL
);

ALTER TABLE word
    ADD FOREIGN KEY (request) REFERENCES request(id);
