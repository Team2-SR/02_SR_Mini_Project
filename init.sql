CREATE DATABASE kshrd_java_mini_proj;

CREATE TABLE IF NOT EXISTS products
(
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(100) UNIQUE,
    unit_price    DECIMAL(10, 2),
    quantity      INT  DEFAULT 0,
    imported_date DATE DEFAULT CURRENT_DATE
);
