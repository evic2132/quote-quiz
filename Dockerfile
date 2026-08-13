FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY api-contract ./api-contract
COPY server ./server

RUN chmod +x ./gradlew
RUN ./gradlew :server:bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/server/build/libs/*.jar app.jar

EXPOSE 10000

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-10000}"]
