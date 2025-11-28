FROM eclipse-temurin:21-jdk-alpine

WORKDIR /application

# Copy just the Gradle wrapper files first (more efficient caching)
COPY gradlew ./
COPY gradle ./gradle

COPY . ./

RUN chmod +x gradlew
RUN ./gradlew clean bootJar

CMD java -jar build/libs/*.jar
