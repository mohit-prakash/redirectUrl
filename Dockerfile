# Use a base image that has JDK 21 installed
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the target directory to the working directory
COPY target/redirectToSpecificUrl-0.0.1-SNAPSHOT.jar redirectToSpecificUrl.jar

# Expose the port your application runs on
EXPOSE 7887

# Command to run the application
ENTRYPOINT ["java", "-jar", "redirectToSpecificUrl.jar"]
