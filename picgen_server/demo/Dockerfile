#Build app
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper and pom files
COPY mvnw .
COPY .mvn/ .mvn/
COPY pom.xml .

# Copy source
COPY src ./src

# Build the JAR
RUN ./mvnw clean package -DskipTests

#runtime image
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# Copy JAR from build
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Run
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
