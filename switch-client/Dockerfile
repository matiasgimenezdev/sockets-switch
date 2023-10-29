FROM eclipse-temurin:19-jdk-alpine

COPY /target/client-0.0.1-SNAPSHOT.jar /usr/src/switch-client.jar

WORKDIR /usr/src/

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "switch-client.jar"]