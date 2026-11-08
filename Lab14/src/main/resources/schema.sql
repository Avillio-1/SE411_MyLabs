CREATE TABLE courses (
    id BIGINT PRIMARY KEY CHECK (id >= 0),
    code VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    description VARCHAR,
    credits INTEGER NOT NULL CHECK (credits BETWEEN 1 AND 10)
);
