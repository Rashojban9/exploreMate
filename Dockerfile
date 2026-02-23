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
RUN apk add --no-cache curl bash netcat-openbsd procps

# Create directories for each service
RUN mkdir -p /app/services

# Copy built JARs from build stage
COPY --from=builder /build/service-discovery/target/*.jar /app/services/service-discovery.jar
COPY --from=builder /build/auth-service/target/*.jar /app/services/auth-service.jar
COPY --from=builder /build/api-gateway/target/*.jar /app/services/api-gateway.jar
COPY --from=builder /build/trip-service/target/*.jar /app/services/trip-service.jar
COPY --from=builder /build/email-service/target/*.jar /app/services/email-service.jar
COPY --from=builder /build/ai-service/target/*.jar /app/services/ai-service.jar

# Set environment variables for service communication
ENV SPRING_PROFILES_ACTIVE=prod
ENV AUTH_SERVICE_URL=http://localhost:8080
ENV TRIP_SERVICE_URL=http://localhost:8083
ENV EMAIL_SERVICE_URL=http://localhost:9090
ENV AI_SERVICE_URL=http://localhost:9091
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/

# Create startup script with better error handling
RUN printf '%s\n' '#!/bin/bash' \
    'set -e' \
    '' \
    'log() { echo "[$(date +%%Y-%%m-%%dT%%H:%%M:%%S)] $*"; }' \
    'error() { echo "[ERROR] $*" >&2; }' \
    '' \
    'log "========================================="' \
    'log "Starting ExploreMate All-in-One Service"' \
    'log "========================================="' \
    '' \
    '# Start Service Discovery (Eureka)' \
    'log "Starting Service Discovery on port 8761..."' \
    'java -jar /app/services/service-discovery.jar > /tmp/service-discovery.log 2>&1 &' \
    'SPID=$!' \
    'log "Service Discovery started (PID: $SPID)"' \
    '' \
    '# Wait for Service Discovery' \
    'log "Waiting 20s for Service Discovery..."' \
    'sleep 20' \
    '' \
    '# Check if Service Discovery is running' \
    'if ! ps -p $SPID > /dev/null; then' \
    '    error "Service Discovery failed to start!"' \
    '    cat /tmp/service-discovery.log' \
    '    exit 1' \
    'fi' \
    '' \
    '# Start Auth Service' \
    'log "Starting Auth Service on port 8080..."' \
    'java -jar /app/services/auth-service.jar > /tmp/auth-service.log 2>&1 &' \
    'APID=$!' \
    'log "Auth Service started (PID: $APID)"' \
    '' \
    '# Start Trip Service' \
    'log "Starting Trip Service on port 8083..."' \
    'java -jar /app/services/trip-service.jar > /tmp/trip-service.log 2>&1 &' \
    'TPID=$!' \
    'log "Trip Service started (PID: $TPID)"' \
    '' \
    '# Start Email Service' \
    'log "Starting Email Service on port 9090..."' \
    'java -jar /app/services/email-service.jar > /tmp/email-service.log 2>&1 &' \
    'EPID=$!' \
    'log "Email Service started (PID: $EPID)"' \
    '' \
    '# Start AI Service' \
    'log "Starting AI Service on port 9091..."' \
    'java -jar /app/services/ai-service.jar > /tmp/ai-service.log 2>&1 &' \
    'AIPID=$!' \
    'log "AI Service started (PID: $AIPID)"' \
    '' \
    '# Wait for backend services' \
    'log "Waiting 15s for backend services..."' \
    'sleep 15' \
    '' \
    '# Show service status' \
    'log "All backend services started, starting API Gateway..."' \
    'ps aux | grep java' \
    '' \
    '# Start API Gateway (foreground)' \
    'log "========================================="' \
    'log "Starting API Gateway on port 9080..."' \
    'log "========================================="' \
    'exec java -jar /app/services/api-gateway.jar' \
    > /app/start.sh && chmod +x /app/start.sh

# Expose all service ports
EXPOSE 8761 8080 8083 9090 9091 9080

# Run the startup script
CMD ["/app/start.sh"]
