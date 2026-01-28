# Build stage
FROM bellsoft/liberica-openjdk-alpine:17 AS build
WORKDIR /app

# Copy gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Grant execute permission for gradlew
RUN chmod +x gradlew

# Download dependencies (caching layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build the application
RUN ./gradlew bootJar -x test --no-daemon

# Runtime stage
FROM bellsoft/liberica-openjre-alpine:17
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/app.jar app.jar

# JVM options for low-spec ARM64 server
ENTRYPOINT ["java", "-XX:+UseSerialGC", "-Xms256m", "-Xmx384m", "-Xss512k", "-jar", "app.jar"]
