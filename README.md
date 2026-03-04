# This is GROUP-02 MINI PROJECT

## this is the project structure

- **src/**
    - **Config/**: This is for class for configuring the project.
    - **Model/**: this is for db entities ( tables ).
    - **Service/**: This is for business logic and db call class .
    - **Utils/**: This is for the utility class.
    - **Controller/**: This for handling application logic.
    - **View/**: This is for console UI class.
    - `Main.java`

## How to connect to database

### Please run the sql I have provided called `init.sql`

1. first run the create database
2. change the schema to the database we created since you might create the tables inside the default `postgres` table
3. run the create table

```postgresql
CREATE DATABASE kshrd_java_mini_proj;

CREATE TABLE IF NOT EXISTS products
(
    id            SERIAL PRIMARY KEY,
    name          VARCHAR(100) UNIQUE,
    unit_price    DECIMAL(10, 2),
    quantity      INT  DEFAULT 0,
    imported_date DATE DEFAULT CURRENT_DATE
);
```

### Please create an Environment variable

- `.env` file with these keys and your credentials, or you can rename the `.env.example` I have provided to `.env`

- We need this to connect to database

```dotenv

DB_USER=YOUR_DB_USER_NAME
DB_PASSWORD=YOUR_DB_USER_PASSWORD

DB_URL=jdbc:postgresql://localhost:YOUR_DB_PORT/YOUR_DB_NAME

```