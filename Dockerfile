# ExploreMate - Single Container Deployment with Kafka
# Builds all microservices from source and includes Kafka

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

COPY group-trip-service/pom.xml /build/group-trip-service/pom.xml
COPY group-trip-service/src /build/group-trip-service/src

COPY qr-guide-service/pom.xml /build/qr-guide-service/pom.xml
COPY qr-guide-service/src /build/qr-guide-service/src

COPY notification-service/pom.xml /build/notification-service/pom.xml
COPY notification-service/src /build/notification-service/src

# Build all services
RUN cd /build/service-discovery && mvn clean package -DskipTests -B
RUN cd /build/auth-service && mvn clean package -DskipTests -B
RUN cd /build/api-gateway && mvn clean package -DskipTests -B
RUN cd /build/trip-service && mvn clean package -DskipTests -B
RUN cd /build/email-service && mvn clean package -DskipTests -B
RUN cd /build/ai-service && mvn clean package -DskipTests -B
RUN cd /build/group-trip-service && mvn clean package -DskipTests -B
RUN cd /build/qr-guide-service && mvn clean package -DskipTests -B
RUN cd /build/notification-service && mvn clean package -DskipTests -B

# ============ RUNTIME STAGE ============
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install utilities including Kafka
RUN apk add --no-cache curl bash docker

# Download and extract Kafka (use archive mirror for reliability)
RUN curl -fsSL https://archive.apache.org/dist/kafka/3.7.0/kafka_2.13-3.7.0.tgz | tar -xz -C /opt/ \
    && ln -s /opt/kafka_2.13-3.7.0 /opt/kafka

# Set Kafka environment
ENV KAFKA_HOME=/opt/kafka
ENV PATH=$PATH:$KAFKA_HOME/bin

# Create services directory
RUN mkdir -p /app/services

# Copy all JARs
COPY --from=builder /build/service-discovery/target/*.jar /app/services/
COPY --from=builder /build/auth-service/target/*.jar /app/services/
COPY --from=builder /build/api-gateway/target/*.jar /app/services/
COPY --from=builder /build/trip-service/target/*.jar /app/services/
COPY --from=builder /build/email-service/target/*.jar /app/services/
COPY --from=builder /build/ai-service/target/*.jar /app/services/
COPY --from=builder /build/group-trip-service/target/*.jar /app/services/
COPY --from=builder /build/qr-guide-service/target/*.jar /app/services/
COPY --from=builder /build/notification-service/target/*.jar /app/services/

# Set environment
ENV SPRING_PROFILES_ACTIVE=prod

# MongoDB URIs for each service
ENV MONGODB_URI_AUTH=${MONGODB_URI_AUTH}
ENV MONGODB_URI_TRIP=${MONGODB_URI_TRIP}
ENV MONGODB_URI_AI=${MONGODB_URI_AI}
ENV MONGODB_URI_GROUP_TRIP=${MONGODB_URI_GROUP_TRIP}
ENV MONGODB_URI_QR_GUIDE=${MONGODB_URI_QR_GUIDE}
ENV MONGODB_URI_NOTIFICATION=${MONGODB_URI_NOTIFICATION}

# JWT Secret
ENV JWT_SECRET=${JWT_SECRET}

# Email - Resend API
ENV SPRING_MAIL_USERNAME=${EMAIL_USERNAME}
ENV SPRING_MAIL_PASSWORD=${EMAIL_PASSWORD}
ENV RESEND_API_KEY=${RESEND_API_KEY}

# Kafka
ENV KAFKA_ZOOKEEPER_CONNECT=localhost:2181
ENV KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092
ENV KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=PLAINTEXT:PLAINTEXT
ENV KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT
ENV KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1
ENV KAFKA_AUTO_CREATE_TOPICS_ENABLE=true
ENV SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Groq AI
ENV GROQ_API_KEY=${GROQ_API_KEY}
ENV GROQ_MODEL=${GROQ_MODEL}

# Expose all ports (Zookeeper, Kafka, and all services)
EXPOSE 2181 9092 8761 8080 9080 8083 8084 8085 8086 9090 9091

# Start Zookeeper, Kafka, and all services
CMD ["sh", "-c", "\
    echo 'Starting Zookeeper...' && \
    $KAFKA_HOME/bin/zookeeper-server-start.sh -daemon $KAFKA_HOME/config/zookeeper.properties & \
    sleep 10 && \
    echo 'Starting Kafka...' && \
    $KAFKA_HOME/bin/kafka-server-start.sh -daemon $KAFKA_HOME/config/server.properties & \
    sleep 15 && \
    echo 'Creating Kafka topics...' && \
    $KAFKA_HOME/bin/kafka-topics.sh --create --topic signup --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 2>/dev/null || true && \
    $KAFKA_HOME/bin/kafka-topics.sh --create --topic password-reset --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 2>/dev/null || true && \
    $KAFKA_HOME/bin/kafka-topics.sh --create --topic notification-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 2>/dev/null || true && \
    sleep 5 && \
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
    echo 'Starting Group Trip Service...' && \
    java -Xmx384m -Xms256m -jar /app/services/group-trip-service*.jar & \
    sleep 15 && \
    echo 'Starting QR Guide Service...' && \
    java -Xmx384m -Xms256m -jar /app/services/qr-guide-service*.jar & \
    sleep 15 && \
    echo 'Starting Notification Service...' && \
    java -Xmx384m -Xms256m -jar /app/services/notification-service*.jar & \
    sleep 15 && \
    echo 'Starting API Gateway...' && \
    java -Xmx512m -Xms384m -jar /app/services/api-gateway*.jar & \
    sleep 5 && \
    echo 'All services started!' && \
    tail -f /dev/null"]
