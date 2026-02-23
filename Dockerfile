# ExploreMate - Microservices Application
# 
# For local development, use: docker-compose up
# For deployment to Render, this Dockerfile enables the initial build
#
# This is a multi-service application. Each service has its own Dockerfile:
# - service-discovery/service-discovery/Dockerfile
# - auth-service/auth-service/Dockerfile
# - api-gateway/api-gateway/Dockerfile
# - trip-service/trip-service/Dockerfile
# - email-service/email-service/Dockerfile
# - ai-service/ai-service/Dockerfile

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy application files
COPY . .

# This Dockerfile is a placeholder for CI/CD systems that require a root Dockerfile.
# The actual microservices are built and deployed using docker-compose.yml or render.yaml

EXPOSE 8080

CMD ["echo", "This is a multi-service application. Use 'docker-compose up' to run locally or deploy using render.yaml to Render.com"]
