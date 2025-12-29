FROM eclipse-temurin:17-jre
ENV SPRING_PROFILES_ACTIVE=prod
WORKDIR /opt/app
ARG JAR_FILE=app.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/opt/app/app.jar"]
