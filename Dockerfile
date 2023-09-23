FROM eclipse-temurin:17

LABEL mentainer="william.feliciano2@gmail.com"

WORKDIR /app

COPY target/AuthServer-DockerMsql-0.0.5-SNAPSHOT.jar /app/AuthServer-DockerMsql.jar

ENTRYPOINT ["java","-jar", "AuthServer-DockerMsql.jar"]

#FROM openjdk:17-jdk-alpine
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java", "-jar", "/app.jar"]