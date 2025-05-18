# Build stage
FROM gradle:8.5-jdk17-alpine AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

# Runtime stage
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
