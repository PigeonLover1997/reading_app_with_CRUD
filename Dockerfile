# ====== ビルドステージ ======
FROM gradle:8.4-jdk21 as build

WORKDIR /app

# Gradleファイル一式
COPY reading_app/build.gradle reading_app/settings.gradle ./reading_app/
COPY reading_app/gradle ./reading_app/gradle
COPY reading_app/gradlew ./reading_app/gradlew
COPY reading_app/gradlew.bat ./reading_app/gradlew.bat

# ソース全体をコピー（application.propertiesを含めるため！）
COPY reading_app/src ./reading_app/src

WORKDIR /app/reading_app
RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon clean build -x test

# ====== 実行ステージ ======
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/reading_app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
