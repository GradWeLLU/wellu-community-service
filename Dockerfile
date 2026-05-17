# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /build

# Copy dependency descriptor first for better layer caching.
COPY pom.xml .

RUN mvn -q -e -B dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests


# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
