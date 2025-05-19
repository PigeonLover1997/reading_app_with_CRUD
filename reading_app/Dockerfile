# === ビルドステージ ===
FROM eclipse-temurin:21-alpine AS build
WORKDIR /app

# Gradle 用ラッパーと設定コピー
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# ラッパー権限付与＆依存ダウンロード
RUN chmod +x gradlew \
    && ./gradlew --no-daemon dependencies

# ソースコードをコピーしてビルド
COPY src src
RUN ./gradlew --no-daemon clean build -x test

# === 実行ステージ ===
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# ビルド成果物をコピー
COPY --from=build /app/build/libs/*.jar app.jar

# 外部からアクセスするポート
EXPOSE 8080

# アプリ起動
ENTRYPOINT ["java", "-jar", "app.jar"]
