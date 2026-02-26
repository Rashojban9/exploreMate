# ExploreMate - Hugging Face Spaces Deployment
# Builds all microservices and runs them with memory optimization

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

# Install utilities
RUN apk add --no-cache curl bash

# Create services directory
RUN mkdir -p /app/services

# Copy all JARs
COPY --from=builder /build/service-discovery/target/*.jar /app/services/
COPY --from=builder /build/auth-service/target/*.jar /app/services/
COPY --from=builder /build/api-gateway/target/*.jar /app/services/
COPY --from=builder /build/trip-service/target/*.jar /app/services/
COPY --from=builder /build/email-service/target/*.jar /app/services/
COPY --from=builder /build/ai-service/target/*.jar /app/services/

# Set environment
ENV SPRING_PROFILES_ACTIVE=prod

# Expose all ports
EXPOSE 8761 8080 9080 8083 9090 9091

# Start all services with memory limits (512MB heap each)
# Hugging Face gives 16GB RAM on free tier, so we can use more memory
CMD ["sh", "-c", "\
    echo 'Starting Service Discovery...' && \
    java -Xmx384m -Xms256m -jar /app/services/service-discovery.jar & \
    sleep 15 && \
    echo 'Starting Auth Service...' && \
    java -Xmx384m -Xms256m -jar /app/services/auth-service.jar & \
    sleep 10 && \
    echo 'Starting Trip Service...' && \
    java -Xmx384m -Xms256m -jar /app/services/trip-service.jar & \
    sleep 10 && \
    echo 'Starting Email Service...' && \
    java -Xmx384m -Xms256m -jar /app/services/email-service.jar & \
    sleep 10 && \
    echo 'Starting AI Service...' && \
    java -Xmx384m -Xms256m -jar /app/services/ai-service.jar & \
    sleep 10 && \
    echo 'Starting API Gateway...' && \
    java -Xmx512m -Xms384m -jar /app/services/api-gateway.jar"]
