# ExploreMate - Main Dockerfile
# This Dockerfile is for building the entire application
# For local development, use docker-compose instead

FROM eclipse-temurin:17-jdk-alpine AS build

# Install Maven
RUN apk add --no-cache maven

# Set working directory
WORKDIR /app

# Copy all service pom.xml files
COPY auth-service/auth-service/pom.xml ./auth-service/
COPY api-gateway/api-gateway/pom.xml ./api-gateway/
COPY trip-service/trip-service/pom.xml ./trip-service/
COPY email-service/email-service/pom.xml ./email-service/
COPY ai-service/ai-service/pom.xml ./ai-service/
COPY service-discovery/service-discovery/pom.xml ./service-discovery/

# Download dependencies (parallel for faster download)
RUN cd auth-service && mvn dependency:go-offline -B && cd .. \
    && cd api-gateway && mvn dependency:go-offline -B && cd .. \
    && cd trip-service && mvn dependency:go-offline -B && cd .. \
    && cd email-service && mvn dependency:go-offline -B && cd .. \
    && cd ai-service && mvn dependency:go-offline -B && cd .. \
    && cd service-discovery && mvn dependency:go-offline -B

# Build all services
WORKDIR /app/auth-service
RUN mvn clean package -DskipTests -B

WORKDIR /app/api-gateway
RUN mvn clean package -DskipTests -B

WORKDIR /app/trip-service
RUN mvn clean package -DskipTests -B

WORKDIR /app/email-service
RUN mvn clean package -DskipTests -B

WORKDIR /app/ai-service
RUN mvn clean package -DskipTests -B

WORKDIR /app/service-discovery
RUN mvn clean package -DskipTests -B

# Final stage - create a small runtime image
FROM eclipse-temurin:17-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

WORKDIR /app

# Create directories for each service
RUN mkdir -p /app/services

# Copy built jars from build stage
COPY --from=build /app/auth-service/target/*.jar /app/services/auth-service.jar
COPY --from=build /app/api-gateway/target/*.jar /app/services/api-gateway.jar
COPY --from=build /app/trip-service/target/*.jar /app/services/trip-service.jar
COPY --from=build /app/email-service/target/*.jar /app/services/email-service.jar
COPY --from=build /app/ai-service/target/*.jar /app/services/ai-service.jar
COPY --from=build /app/service-discovery/target/*.jar /app/services/service-discovery.jar

# Default command - shows available services
CMD ["echo", "Services built: auth-service.jar, api-gateway.jar, trip-service.jar, email-service.jar, ai-service.jar, service-discovery.jar"]
