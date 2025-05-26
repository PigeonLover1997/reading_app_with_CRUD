# ====== ビルドステージ ======
FROM gradle:8.4-jdk21 as build

WORKDIR /app/reading_app

# Gradle関連ファイルを先にコピー
COPY reading_app/reading_app/build.gradle .
COPY reading_app/reading_app/settings.gradle .
COPY reading_app/reading_app/gradle ./gradle
COPY reading_app/reading_app/gradlew ./gradlew
COPY reading_app/reading_app/gradlew.bat ./gradlew.bat

# ソース全体をコピー
COPY reading_app/reading_app/src ./src

# 権限付与＆ビルド
RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon clean build -x test

# ====== 実行ステージ ======
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/reading_app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
