FROM eclipse-temurin:19-jdk-alpine

COPY /target/server-0.0.1-SNAPSHOT.jar /usr/src/switch-server.jar

WORKDIR /usr/src/

EXPOSE 5000
ENTRYPOINT ["java", "-jar", "switch-server.jar"]