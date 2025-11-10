# Build stage
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app
COPY gradlew gradlew
COPY gradle gradle
RUN chmod +x gradlew
COPY build.gradle settings.gradle .
RUN ./gradlew --no-daemon dependencies
COPY src src
RUN ./gradlew --no-daemon bootJar

# Runtime stage
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
EXPOSE 9081
ENV SPRING_PROFILES_ACTIVE=docker
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
