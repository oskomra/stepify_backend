# First stage: build the application using Maven
FROM maven:3.9.10-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy and download dependencies first (caches better)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Package the application (skip tests if needed)
RUN mvn clean package -DskipTests

# Second stage: run the built app with JDK
FROM openjdk:21-alpine

WORKDIR /app

# ✅ Use the alias "build" from the previous stage
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
