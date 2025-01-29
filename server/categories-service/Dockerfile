FROM openjdk:17-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY build/libs/categories-service.jar app.jar
CMD ["java", "-jar", "app.jar"]