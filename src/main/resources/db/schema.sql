CREATE TABLE app (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE org (
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(255),
    url VARCHAR(255)
);