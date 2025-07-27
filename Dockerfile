#FROM maven:3.8.6-eclipse-temurin-11-alpine
FROM gradle:8.9.0-jdk21-alpine

WORKDIR /application

COPY . ./


RUN gradle clean bootJar

WORKDIR /
CMD java -jar /application/build/libs/*.jar
