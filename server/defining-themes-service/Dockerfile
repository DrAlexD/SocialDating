FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY build/libs/defining-themes-service.jar app.jar
CMD ["java", "-jar", "app.jar"]