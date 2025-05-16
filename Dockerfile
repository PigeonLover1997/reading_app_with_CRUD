#################################
# １）ビルド用ステージ
#################################
FROM gradle:8.4-jdk21 AS build

# 作業ディレクトリを /app にする
WORKDIR /app

# リポジトリ直下の reading_app フォルダを丸ごとコピー
COPY reading_app/ .  

# デバッグ用にファイル一覧を出力（あとでログで見られます）
RUN ls -R /app

# 実際にビルド
RUN gradle clean build --no-daemon -x test

#################################
# ２）ランタイム用ステージ
#################################
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# ビルドステージから生成された jar をコピー
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
