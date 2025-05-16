# syntax=docker/dockerfile:1

# --- Build Stage ---
FROM gradle:8.4-jdk17 AS build
WORKDIR /app

# Copy Gradle wrapper and build scripts first for better caching
COPY --link build.gradle settings.gradle gradlew ./
COPY --link gradle ./gradle

# Download dependencies (leverage Docker cache)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# Copy source code
COPY --link src ./src

# Build the application (skip tests for faster build)
RUN ./gradlew build --no-daemon -x test

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Create non-root user and group
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# Copy built jar from build stage
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Set permissions
RUN chown -R appuser:appgroup /app
USER appuser

# JVM options: container-aware memory, GC tuning
ENV JAVA_OPTS="-XX:MaxRAMPercentage=80.0 -XX:+UseContainerSupport"

# Expose default Spring Boot port
EXPOSE 8080

# Use exec form for proper signal handling
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
