--Flywayの初期マイグレーション用SQL
--V1の後に実行される
--ユーザーの希望する難易度・語数・問題数・トピックを保存するカラムの追加を行う
ALTER TABLE users
  ADD COLUMN difficulty VARCHAR(16),
  ADD COLUMN word_count INTEGER,
  ADD COLUMN question_count INTEGER,
  ADD COLUMN topic VARCHAR(1000);