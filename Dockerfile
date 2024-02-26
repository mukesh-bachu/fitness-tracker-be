# Start with a base image containing Java runtime (Java 17)
FROM openjdk:17-slim as build

# Copy the shaded jar file
COPY target/FitBitDashBoard-0.0.1-SNAPSHOT.jar app.jar

# Run the jar file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
