FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY build.gradle.kts .
COPY gradle ./gradle
COPY gradlew .
COPY src ./src

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
