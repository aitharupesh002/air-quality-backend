# Stage 1: build the jar with Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B clean package -DskipTests

# Stage 2: small runtime image with just the JRE and the jar
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/backend.jar app.jar

# Render sets PORT automatically; application.properties reads it.
ENV PORT=8080
EXPOSE 8080

# MaxRAMPercentage lets the JVM use most of a small (512 MB) free-tier container.
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70", "-jar", "app.jar"]
