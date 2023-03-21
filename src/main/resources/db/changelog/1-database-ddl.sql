--liquibase formatted sql

--changeset thorold:1
CREATE TABLE region
(
    name   VARCHAR(2) PRIMARY KEY,
    g2g_id uuid NOT NULL UNIQUE
);
CREATE TABLE realm
(
    id          INT PRIMARY KEY,
    name        VARCHAR(32) NOT NULL,
    region_name VARCHAR(2)  NOT NULL REFERENCES region (name),
    game_version VARCHAR(32) NOT NULL,
    faction     VARCHAR(32) NOT NULL
);
CREATE TABLE price
(
    id         BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    realm_id   INT            NOT NULL REFERENCES realm (id),
    value      DECIMAL(10, 9) NOT NULL,
    created_at TIMESTAMP      NOT NULL DEFAULT NOW()
);
CREATE TABLE auction_house
(
    id       INT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    realm_id INT NOT NULL REFERENCES realm (id)
);