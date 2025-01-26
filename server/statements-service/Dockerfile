FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/statements-service.jar app.jar
CMD ["java", "-jar", "app.jar"]