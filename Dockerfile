FROM eclipse-temurin:17

LABEL mentainer="william.feliciano2@gmail.com"

WORKDIR /app

COPY target/AuthServer-DockerMsql-0.0.1-SNAPSHOT.jar /app/AuthServer-DockerMsql.jar

ENTRYPOINT ["java","-jar", "AuthServer-DockerMsql.jar"]