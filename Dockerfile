# ExploreMate - Multi-service Spring Boot Application
# This Dockerfile builds all microservices and runs the API Gateway

# ============ BUILD STAGE ============
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Build Service Discovery
WORKDIR /build/service-discovery
COPY service-discovery/service-discovery/pom.xml .
COPY service-discovery/service-discovery/src ./src
RUN mvn clean package -DskipTests -B

# Build Auth Service
WORKDIR /build/auth-service
COPY auth-service/auth-service/pom.xml .
COPY auth-service/auth-service/src ./src
RUN mvn clean package -DskipTests -B

# Build API Gateway
WORKDIR /build/api-gateway
COPY api-gateway/api-gateway/pom.xml .
COPY api-gateway/api-gateway/src ./src
RUN mvn clean package -DskipTests -B

# Build Trip Service
WORKDIR /build/trip-service
COPY trip-service/trip-service/pom.xml .
COPY trip-service/trip-service/src ./src
RUN mvn clean package -DskipTests -B

# Build Email Service
WORKDIR /build/email-service
COPY email-service/email-service/pom.xml .
COPY email-service/email-service/src ./src
RUN mvn clean package -DskipTests -B

# Build AI Service
WORKDIR /build/ai-service
COPY ai-service/ai-service/pom.xml .
COPY ai-service/ai-service/src ./src
RUN mvn clean package -DskipTests -B

# ============ RUNTIME STAGE ============
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create directories for each service
RUN mkdir -p /app/services

# Copy built JARs from build stage
COPY --from=builder /build/service-discovery/target/*.jar /app/services/service-discovery.jar
COPY --from=builder /build/auth-service/target/*.jar /app/services/auth-service.jar
COPY --from=builder /build/api-gateway/target/*.jar /app/services/api-gateway.jar
COPY --from=builder /build/trip-service/target/*.jar /app/services/trip-service.jar
COPY --from=builder /build/email-service/target/*.jar /app/services/email-service.jar
COPY --from=builder /build/ai-service/target/*.jar /app/services/ai-service.jar

# Install curl for health checks
RUN apk add --no-cache curl bash

# Create startup script
RUN echo '#!/bin/bash' > /app/start.sh && \
    echo 'echo "Starting ExploreMate Services..."' >> /app/start.sh && \
    echo 'java -jar /app/services/service-discovery.jar &' >> /app/start.sh && \
    echo 'sleep 30' >> /app/start.sh && \
    echo 'java -jar /app/services/auth-service.jar &' >> /app/start.sh && \
    echo 'java -jar /app/services/trip-service.jar &' >> /app/start.sh && \
    echo 'java -jar /app/services/email-service.jar &' >> /app/start.sh && \
    echo 'java -jar /app/services/ai-service.jar &' >> /app/start.sh && \
    echo 'java -jar /app/services/api-gateway.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

# Expose ports (API Gateway is the main entry point)
EXPOSE 9080

# Run the API Gateway (main service)
CMD ["/app/start.sh"]
