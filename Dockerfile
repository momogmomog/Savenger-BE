FROM eclipse-temurin:21-jdk-alpine

WORKDIR /application

# Copy just the Gradle wrapper files first (more efficient caching)
COPY gradlew ./
COPY gradle ./gradle

RUN chmod +x gradlew

COPY . ./

RUN ./gradlew clean bootJar

CMD java -jar build/libs/*.jar
