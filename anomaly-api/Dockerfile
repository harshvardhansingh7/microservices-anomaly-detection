FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests
EXPOSE 8081
# Use the actual jar name built by Maven
CMD ["java", "-jar", "target/anomaly-api-0.0.1-SNAPSHOT.jar"]
