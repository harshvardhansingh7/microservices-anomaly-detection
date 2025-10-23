FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests
EXPOSE 8082
CMD ["java", "-jar", "target/log-consumer-0.0.1-SNAPSHOT.jar"]
