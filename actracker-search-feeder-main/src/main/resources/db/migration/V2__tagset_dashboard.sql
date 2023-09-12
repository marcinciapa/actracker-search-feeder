CREATE TABLE IF NOT EXISTS tag_set (
    id VARCHAR(36) PRIMARY KEY,
    version BIGINT NOT NULL,
    deleted BOOLEAN NOT NULL,
    entity JSONB NOT NULL
);

CREATE TABLE IF NOT EXISTS dashboard (
    id VARCHAR(36) PRIMARY KEY,
    version BIGINT NOT NULL,
    deleted BOOLEAN NOT NULL,
    entity JSONB NOT NULL
);
