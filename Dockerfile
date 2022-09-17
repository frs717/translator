FROM openjdk:8-jdk-alpine

COPY target/translator-0.0.1-SNAPSHOT.jar translator.jar

ENTRYPOINT ["java","-jar","/translator.jar"]