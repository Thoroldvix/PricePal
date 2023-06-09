--liquibase formatted sql

--changeset thoroldvix:1
CREATE TABLE server
(
    id          INT PRIMARY KEY,
    name        VARCHAR(32) NOT NULL,
    region      INT         NOT NULL,
    faction     INT         NOT NULL,
    type        INT         NOT NULL,
    unique_name VARCHAR(64),
    locale      VARCHAR(16) NOT NULL
);

CREATE TABLE population
(
    id         BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    value INT       NOT NULL,
    server_id  INT       NOT NULL REFERENCES server (id),
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE gold_price
(
    id         BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    server_id  INT           NOT NULL REFERENCES server (id),
    value      DECIMAL(7, 6) NOT NULL,
    updated_at TIMESTAMP     NOT NULL
);

CREATE TABLE item
(
    id          INT PRIMARY KEY,
    quality     INT                                                   NOT NULL,
    type        INT                                                   NOT NULL,
    name        VARCHAR(255)                                          NOT NULL,
    slot        INT                                                   NOT NULL,
     vendor_price  INT,
    unique_name VARCHAR(64) GENERATED ALWAYS AS (REPLACE(REPLACE(REPLACE(LOWER(name), ' ', '-'), '''', ''), ':',
                                                          '')) STORED NOT NULL);

CREATE TABLE item_price
(
    id               BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    min_buyout       BIGINT                              DEFAULT 0,
    historical_value BIGINT                              DEFAULT 0,
    market_value     BIGINT                              DEFAULT 0,
    quantity         INT                                 DEFAULT 0,
    num_auctions     INT                                 DEFAULT 0,
    item_id          INT REFERENCES item (id),
    server_id        INT REFERENCES server (id) NOT NULL,
    updated_at       TIMESTAMP                  NOT NULL DEFAULT NOW()
);






