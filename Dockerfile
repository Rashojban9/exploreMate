# ExploreMate - Hugging Face Spaces Deployment
# Builds all microservices from source

# ============ BUILD STAGE ============
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy all service projects
COPY service-discovery/service-discovery/pom.xml /build/service-discovery/pom.xml
COPY service-discovery/service-discovery/src /build/service-discovery/src

COPY auth-service/auth-service/pom.xml /build/auth-service/pom.xml
COPY auth-service/auth-service/src /build/auth-service/src

COPY api-gateway/api-gateway/pom.xml /build/api-gateway/pom.xml
COPY api-gateway/api-gateway/src /build/api-gateway/src

COPY trip-service/trip-service/pom.xml /build/trip-service/pom.xml
COPY trip-service/trip-service/src /build/trip-service/src

COPY email-service/email-service/pom.xml /build/email-service/pom.xml
COPY email-service/email-service/src /build/email-service/src

COPY ai-service/ai-service/pom.xml /build/ai-service/pom.xml
COPY ai-service/ai-service/src /build/ai-service/src

# Build all services
RUN cd /build/service-discovery && mvn clean package -DskipTests -B
RUN cd /build/auth-service && mvn clean package -DskipTests -B
RUN cd /build/api-gateway && mvn clean package -DskipTests -B
RUN cd /build/trip-service && mvn clean package -DskipTests -B
RUN cd /build/email-service && mvn clean package -DskipTests -B
RUN cd /build/ai-service && mvn clean package -DskipTests -B

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

# MongoDB URIs for each service
ENV MONGODB_URI_AUTH=${MONGODB_URI_AUTH}
ENV MONGODB_URI_TRIP=${MONGODB_URI_TRIP}
ENV MONGODB_URI_AI=${MONGODB_URI_AI}

# JWT Secret
ENV JWT_SECRET=${JWT_SECRET}

# Email
ENV SPRING_MAIL_USERNAME=${EMAIL_USERNAME}
ENV SPRING_MAIL_PASSWORD=${EMAIL_PASSWORD}

# Groq AI
ENV GROQ_API_KEY=${GROQ_API_KEY}
ENV GROQ_MODEL=${GROQ_MODEL}

# Expose all ports
EXPOSE 8761 8080 9080 8083 9090 9091

# Start all services with memory limits
CMD ["sh", "-c", "\
    echo 'Starting Service Discovery...' && \
    java -Xmx384m -Xms256m -jar /app/services/service-discovery*.jar & \
    sleep 20 && \
    echo 'Starting Auth Service...' && \
    java -Xmx384m -Xms256m -jar /app/services/auth-service*.jar & \
    sleep 15 && \
    echo 'Starting Trip Service...' && \
    java -Xmx384m -Xms256m -jar /app/services/trip-service*.jar & \
    sleep 15 && \
    echo 'Starting Email Service...' && \
    java -Xmx384m -Xms256m -jar /app/services/email-service*.jar & \
    sleep 15 && \
    echo 'Starting AI Service...' && \
    java -Xmx384m -Xms256m -jar /app/services/ai-service*.jar & \
    sleep 15 && \
    echo 'Starting API Gateway...' && \
    java -Xmx512m -Xms384m -jar /app/services/api-gateway*.jar"]
