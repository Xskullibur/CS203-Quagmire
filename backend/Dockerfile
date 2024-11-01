# Use Maven image to build the application
FROM maven:3.8.4-openjdk-17 AS build
WORKDIR /workspace/app

# Copy pom.xml and source code
COPY pom.xml .
COPY src src

# Package the application
RUN mvn package -DskipTests

# Use OpenJDK for the runtime image
FROM openjdk:17-jdk-slim

VOLUME /tmp
COPY --from=build /workspace/app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
