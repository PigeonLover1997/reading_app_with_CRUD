# ====== ビルドステージ ======
FROM gradle:8.4-jdk21 as build

WORKDIR /app

# Gradle関連ファイルを先にコピー
COPY reading_app/build.gradle reading_app/settings.gradle ./
COPY reading_app/gradle ./gradle

# 依存だけ先に解決（キャッシュ活用）
COPY reading_app/gradlew ./gradlew
COPY reading_app/gradlew.bat ./gradlew.bat
RUN chmod +x gradlew

RUN ./gradlew --no-daemon dependencies

# ソースを全部コピー（この後でchmodしない！）
COPY reading_app/src ./src

# ビルド実行
RUN ./gradlew --no-daemon clean build -x test

# ====== 実行ステージ ======
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
