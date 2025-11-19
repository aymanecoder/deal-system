-- Manual SQL script to create all required tables
-- Run this script if Liquibase doesn't create the tables automatically
-- Usage: psql -U deals_user -d deals_data -f create-tables-manually.sql

-- Create transaction_log table
CREATE TABLE IF NOT EXISTS transaction_log (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    file_name VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    valid_count BIGINT,
    invalid_count BIGINT,
    processing_duration_ms BIGINT,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    error_message VARCHAR(1000)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_file_name ON transaction_log(file_name);

-- Create valid_deal table
CREATE TABLE IF NOT EXISTS valid_deal (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    file_name VARCHAR(255) NOT NULL,
    deal_id VARCHAR(100) NOT NULL UNIQUE,
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    date_time TIMESTAMP NOT NULL,
    amount NUMERIC(19,2) NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_deal_id ON valid_deal(deal_id);
CREATE INDEX IF NOT EXISTS idx_from_currency ON valid_deal(from_currency);
CREATE INDEX IF NOT EXISTS idx_file_name_valid ON valid_deal(file_name);

-- Create invalid_deal table
CREATE TABLE IF NOT EXISTS invalid_deal (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    file_name VARCHAR(255) NOT NULL,
    deal_id VARCHAR(100),
    from_currency VARCHAR(3),
    to_currency VARCHAR(3),
    date_time VARCHAR(50),
    amount VARCHAR(50),
    error_message VARCHAR(500),
    row_data TEXT
);

CREATE INDEX IF NOT EXISTS idx_invalid_file_name ON invalid_deal(file_name);

-- Create accumulative_deal_count table
CREATE TABLE IF NOT EXISTS accumulative_deal_count (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP,
    currency_code VARCHAR(3) NOT NULL UNIQUE,
    count_of_deals BIGINT NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_currency_code ON accumulative_deal_count(currency_code);

