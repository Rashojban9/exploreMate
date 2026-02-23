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

# Install curl and bash
RUN apk add --no-cache curl bash

# Create directories for each service
RUN mkdir -p /app/services

# Copy built JARs from build stage
COPY --from=builder /build/service-discovery/target/*.jar /app/services/service-discovery.jar
COPY --from=builder /build/auth-service/target/*.jar /app/services/auth-service.jar
COPY --from=builder /build/api-gateway/target/*.jar /app/services/api-gateway.jar
COPY --from=builder /build/trip-service/target/*.jar /app/services/trip-service.jar
COPY --from=builder /build/email-service/target/*.jar /app/services/email-service.jar
COPY --from=builder /build/ai-service/target/*.jar /app/services/ai-service.jar

# Create startup script that runs API Gateway in foreground
RUN echo '#!/bin/bash' > /app/start.sh && \
    echo 'echo "Starting ExploreMate Services..."' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Start Service Discovery (Eureka) in background' >> /app/start.sh && \
    echo 'echo "Starting Service Discovery..."' >> /app/start.sh && \
    echo 'java -jar /app/services/service-discovery.jar > /tmp/service-discovery.log 2>&1 &' >> /app/start.sh && \
    echo 'SERVICE_PID=$!' >> /app/start.sh && \
    echo 'echo "Service Discovery PID: $SERVICE_PID"' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Wait for Service Discovery to start' >> /app/start.sh && \
    echo 'echo "Waiting for Service Discovery (30s)..."' >> /app/start.sh && \
    echo 'sleep 30' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Start Auth Service in background' >> /app/start.sh && \
    echo 'echo "Starting Auth Service..."' >> /app/start.sh && \
    echo 'java -jar /app/services/auth-service.jar > /tmp/auth-service.log 2>&1 &' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Start Trip Service in background' >> /app/start.sh && \
    echo 'echo "Starting Trip Service..."' >> /app/start.sh && \
    echo 'java -jar /app/services/trip-service.jar > /tmp/trip-service.log 2>&1 &' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Start Email Service in background' >> /app/start.sh && \
    echo 'echo "Starting Email Service..."' >> /app/start.sh && \
    echo 'java -jar /app/services/email-service.jar > /tmp/email-service.log 2>&1 &' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Start AI Service in background' >> /app/start.sh && \
    echo 'echo "Starting AI Service..."' >> /app/start.sh && \
    echo 'java -jar /app/services/ai-service.jar > /tmp/ai-service.log 2>&1 &' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Wait for backend services' >> /app/start.sh && \
    echo 'echo "Waiting for backend services (20s)..."' >> /app/start.sh && \
    echo 'sleep 20' >> /app/start.sh && \
    echo '' >> /app/start.sh && \
    echo '# Start API Gateway in foreground (this keeps container running)' >> /app/start.sh && \
    echo 'echo "Starting API Gateway on port 9080..."' >> /app/start.sh && \
    echo 'java -jar /app/services/api-gateway.jar' >> /app/start.sh && \
    chmod +x /app/start.sh

# Expose API Gateway port
EXPOSE 9080

# Run the startup script
CMD ["/app/start.sh"]
