# Use an official Maven image with Java 17 (adjust version if needed)
FROM maven:3.9.3-eclipse-temurin-21

# Set working directory inside the container
WORKDIR /app

# Copy all files from your local project into the container
COPY . .

# Make the Maven wrapper executable (optional if you want to use it)
RUN chmod +x ./mvnw

# Build the Spring Boot app (creates the jar file)
RUN ./mvnw clean install

# Run the Spring Boot jar (change 'your-app.jar' to your actual jar file name)
CMD ["java", "-jar", "target/your-app.jar"]