# Build Stage
FROM gradle:8.3-jdk17 AS builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test

# Package Stage
FROM openjdk:17
RUN mkdir /app
COPY --from=builder /home/gradle/src/build/libs/*.jar /app/inventory-service.jar

EXPOSE 8085

ENV SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/inventory-service \
SPRING_DATASOURCE_USERNAME=admin \
SPRING_DATASOURCE_PASSWORD=password

ENTRYPOINT ["java", "-jar", "/app/inventory-service.jar"]