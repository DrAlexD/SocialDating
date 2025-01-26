FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/users-service.jar app.jar
CMD ["java", "-jar", "app.jar"]