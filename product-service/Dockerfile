# Build Stage
FROM gradle:8.3.0-jdk17-alpine AS builder
COPY --chown=gradle:gradle . /home/gradle/src/
WORKDIR /home/gradle/src/
RUN gradle build -x test

# Package Stage
FROM openjdk:17-alpine
RUN mkdir /app
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/product-service.jar
EXPOSE 8084
ENV MONGO_DB_USERNAME=rootadmin MONGO_DB_PASSWORD=password
ENTRYPOINT ["java", "-jar", "/app/product-service.jar"]