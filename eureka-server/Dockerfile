# Use lightweight OpenJDK base image
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Maven/Gradle project build output (if using Maven, it will produce target/*.jar)
COPY target/eureka-server-0.0.1-SNAPSHOT.jar app.jar

# Expose Eureka port
EXPOSE 8761

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
