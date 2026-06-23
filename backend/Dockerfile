FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY src src
RUN chmod +x gradlew && ./gradlew bootJar

FROM eclipse-temurin:21-jre
RUN addgroup --system appgroup && adduser --system appuser --ingroup appgroup
USER appuser
WORKDIR /app
COPY --from=builder /app/build/libs/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
