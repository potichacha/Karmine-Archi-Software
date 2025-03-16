FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y curl && apt-get install -y default-mysql-client
VOLUME /tmp
ARG JAR_FILE=target/message-topic-app-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]