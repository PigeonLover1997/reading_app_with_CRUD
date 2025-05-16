# buildステージ（Gradle版）
FROM gradle:8.4-jdk17 AS build
WORKDIR /app

# 依存だけ先にキャッシュ
COPY reading_app/build.gradle reading_app/settings.gradle ./  
RUN gradle clean assemble --no-daemon -x test

# ソース一式をコピー
COPY reading_app/src ./src
RUN gradle bootJar --no-daemon

# 実行ステージ
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
