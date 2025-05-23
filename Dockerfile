# ====== ビルドステージ ======
FROM gradle:8.4-jdk21 as build

# 作業ディレクトリを /app に
WORKDIR /app

# Gradle関連ファイルをコピー
COPY reading_app/build.gradle reading_app/settings.gradle ./

# ラッパー＆依存DL
COPY reading_app/gradlew ./gradlew
COPY reading_app/gradlew.bat ./gradlew.bat
COPY reading_app/gradle ./gradle
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies

# ソースをコピー
COPY reading_app/src ./src
COPY reading_app/. .

# ビルド実行
RUN ./gradlew --no-daemon clean build -x test

# ====== 実行ステージ ======
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
