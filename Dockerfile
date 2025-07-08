# Use Maven to build the application
FROM maven:3.9.10-eclipse-temurin-21-alpine AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Use OpenJDK to run the application
FROM openjdk:21-alpine

WORKDIR /app
COPY --from=build /app/target/your-app.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
