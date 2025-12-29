FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/billing-service-*.jar app.jar
EXPOSE 3003
ENTRYPOINT ["java", "-jar", "app.jar"]