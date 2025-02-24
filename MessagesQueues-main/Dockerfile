FROM openjdk:17-jdk-slim
VOLUME /tmp
ARG JAR_FILE=target/message-topic-app-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]