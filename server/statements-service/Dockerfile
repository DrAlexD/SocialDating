FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl
WORKDIR /app
COPY build/libs/statements-service.jar app.jar
CMD ["java", "-jar", "app.jar"]