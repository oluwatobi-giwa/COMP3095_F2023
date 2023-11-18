# ----------------
# Dockerfile for product-service
# ----------------

# -----
# Build Stage
# ----

FROM gradle:8.3-jdk17 AS builder

# Copy the application files from the host machine to the image filesystem
COPY --chown=gradle:gradle . /home/gradle/src

# Set the directory within the image for executing future commands
WORKDIR /home/gradle/src

# Run gradle build within the container - NOT the host machine
RUN gradle build -x test

#----------------
# Package stage
#----------------
FROM openjdk:17

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/discovery-service.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "/app/discovery-service.jar"]