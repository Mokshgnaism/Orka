# ==========================================
# STAGE 1: Build Environment
# ==========================================
# Uses Maven with Java 24 to compile your application
FROM maven:3.9.9-eclipse-temurin-24 AS builder

# Set the working directory inside the build container
WORKDIR /build

# Copy everything from the root context (crucial for finding common-grammar)
COPY . .

# Build only this specific microservice.
# -pl specifies the module name (change 'main-service' to match your folder name)
# -am (Also Make) tells Maven to find and build 'common-grammar' automatically
RUN mvn clean package -pl ScriptExecutor -am -DskipTests

# ==========================================
# STAGE 2: Lightweight Runtime Environment
# ==========================================
# Uses a slim, optimized Java 24 runtime image (no Maven overhead)
FROM eclipse-temurin:24-jre-alpine

# Create a secure system group and user to avoid running as root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Set the runtime working directory
WORKDIR /app

# Copy the generated JAR file from the builder stage
# (Change 'main-service' in the path below when adapting for other services)
COPY --from=builder /build/ScriptExecutor/target/*.jar app.jar

# Document the port your microservice listens on
EXPOSE 8080

# Execute the application with optimized container flags
ENTRYPOINT ["java", "-jar", "app.jar"]