# ExploreMate - Main Dockerfile
# This Dockerfile is for building the entire application
# For local development, use docker-compose instead

FROM eclipse-temurin:21-jdk-alpine AS build

# Install Maven
RUN apk add --no-cache maven

# Build Service Discovery
FROM eclipse-temurin:21-jdk-alpine AS service-discovery-build
WORKDIR /app
COPY service-discovery/service-discovery/pom.xml ./pom.xml
COPY service-discovery/service-discovery/src ./src
RUN mvn clean package -DskipTests -B

# Build Auth Service
FROM eclipse-temurin:21-jdk-alpine AS auth-service-build
WORKDIR /app
COPY auth-service/auth-service/pom.xml ./pom.xml
COPY auth-service/auth-service/src ./src
RUN mvn clean package -DskipTests -B

# Build API Gateway
FROM eclipse-temurin:21-jdk-alpine AS api-gateway-build
WORKDIR /app
COPY api-gateway/api-gateway/pom.xml ./pom.xml
COPY api-gateway/api-gateway/src ./src
RUN mvn clean package -DskipTests -B

# Build Trip Service
FROM eclipse-temurin:21-jdk-alpine AS trip-service-build
WORKDIR /app
COPY trip-service/trip-service/pom.xml ./pom.xml
COPY trip-service/trip-service/src ./src
RUN mvn clean package -DskipTests -B

# Build Email Service
FROM eclipse-temurin:21-jdk-alpine AS email-service-build
WORKDIR /app
COPY email-service/email-service/pom.xml ./pom.xml
COPY email-service/email-service/src ./src
RUN mvn clean package -DskipTests -B

# Build AI Service
FROM eclipse-temurin:21-jdk-alpine AS ai-service-build
WORKDIR /app
COPY ai-service/ai-service/pom.xml ./pom.xml
COPY ai-service/ai-service/src ./src
RUN mvn clean package -DskipTests -B

# Final stage - create a small runtime image
FROM eclipse-temurin:21-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

WORKDIR /app

# Create directories for each service
RUN mkdir -p /app/services

# Copy built jars from build stages
COPY --from=service-discovery-build /app/target/*.jar /app/services/service-discovery.jar
COPY --from=auth-service-build /app/target/*.jar /app/services/auth-service.jar
COPY --from=api-gateway-build /app/target/*.jar /app/services/api-gateway.jar
COPY --from=trip-service-build /app/target/*.jar /app/services/trip-service.jar
COPY --from=email-service-build /app/target/*.jar /app/services/email-service.jar
COPY --from=ai-service-build /app/target/*.jar /app/services/ai-service.jar

# Default command - shows available services
CMD ["echo", "Services built: auth-service.jar, api-gateway.jar, trip-service.jar, email-service.jar, ai-service.jar, service-discovery.jar"]
