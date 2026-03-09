# Build stage — Maven pre-installed, no wrapper needed
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage — lightweight JRE only
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/email-service-*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-Xmx128m", "-jar", "app.jar"]
