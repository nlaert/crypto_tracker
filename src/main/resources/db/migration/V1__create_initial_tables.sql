CREATE TABLE wallet
(
    id    SERIAL PRIMARY KEY,
    email VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE wallet_assets
(
    wallet_id      bigint,
    symbol         VARCHAR(10),
    quantity       DECIMAL(20, 10) NOT NULL,
    original_value DECIMAL(20,2) NOT NULL,
    PRIMARY KEY (wallet_id, symbol),
    FOREIGN KEY (wallet_id) REFERENCES wallet (id)
);

CREATE TABLE assets
(
    symbol VARCHAR(10)  PRIMARY KEY,
    name   VARCHAR(50)  NOT NULL,
    price  DECIMAL(20,2)        NOT NULL
);
