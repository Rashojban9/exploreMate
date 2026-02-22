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

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the built artifact
COPY --from=build /app/target/*.jar app.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 9080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
