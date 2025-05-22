--Flywayの初期マイグレーション用SQL
--Flywayはプロジェクト起動時に「まだDBにないバージョンの.sqlファイル」を自動実行

-- V1__create_user_table.sql
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(100) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'USER',
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  is_active BOOLEAN NOT NULL DEFAULT TRUE
);