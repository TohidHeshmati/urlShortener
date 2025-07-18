FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY build/libs/url_shortener-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]