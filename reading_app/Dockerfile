# syntax=docker/dockerfile:1

# --- Build Stage ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Gradle wrapper and build scripts first for better caching
COPY --link build.gradle settings.gradle gradlew ./
COPY --link gradle gradle/

# Download dependencies (leverage Docker cache)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# Copy source code
COPY --link src ./src
COPY --link . ./  # To include resources, but .dockerignore should exclude unwanted files

# Build the application (skip tests for faster build)
RUN ./gradlew build --no-daemon -x test

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create non-root user and group
RUN groupadd -r appuser && useradd --no-log-init -r -g appuser appuser

# Copy built jar from build stage
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Set permissions
RUN chown appuser:appuser /app/app.jar
USER appuser

# Expose port (Spring Boot default)
EXPOSE 8080

# JVM options: container-aware memory settings
ENV JAVA_OPTS="-XX:MaxRAMPercentage=80.0 -XX:+UseContainerSupport"

# Use exec form for proper signal handling
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
