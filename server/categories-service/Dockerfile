FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/categories-service.jar app.jar
CMD ["java", "-jar", "app.jar"]