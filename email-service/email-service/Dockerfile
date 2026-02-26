# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom file first for better caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Runtime stage - Using Java 17 for smaller footprint
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the built artifact
COPY --from=build /app/target/*.jar app.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 9090

# Run the application with JVM memory limits optimized for 512MB container
# -Xmx192m: Max heap 192MB
# -Xms128m: Initial heap 128MB
# -XX:MaxMetaspaceSize=64m: Limit metaspace
# -XX:+UseG1GC: Use G1 garbage collector for smaller footprint
ENTRYPOINT ["java", "-Xmx384m", "-Xms256m", "-XX:MaxMetaspaceSize=128m", "-XX:+UseG1GC", "-jar", "app.jar"]
