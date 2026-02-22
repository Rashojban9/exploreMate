# Render Deployment Guide for ExploreMate Backend

This guide explains how to deploy the ExploreMate backend microservices to Render.

## Prerequisites

1. **Docker Hub Account** or **GitHub Container Registry (ghcr.io)**
2. **Render Account** with billing set up (for private services)
3. **MongoDB Atlas Account** (for database - Render has MongoDB addons but using Atlas is recommended)

## Architecture Overview

The ExploreMate backend consists of:
- **API Gateway** (port 9080) - Main entry point
- **Auth Service** (port 8080) - Authentication
- **Trip Service** (port 8083) - Trip management
- **Email Service** (port 9090) - Email notifications
- **AI Service** (port 9091) - AI recommendations
- **Service Discovery** (port 8761) - Eureka server

## Option 1: Deploy to Render with Docker

### Step 1: Push Images to Registry

First, tag and push your Docker images to Docker Hub:

```bash
# Login to Docker Hub
docker login -u YOUR_USERNAME

# Tag images
docker tag exploremate-api-gateway:latest YOUR_USERNAME/exploremate-api-gateway:latest
docker tag exploremate-auth-service:latest YOUR_USERNAME/exploremate-auth-service:latest
docker tag exploremate-trip-service:latest YOUR_USERNAME/exploremate-trip-service:latest
docker tag exploremate-email-service:latest YOUR_USERNAME/exploremate-email-service:latest
docker tag exploremate-ai-service:latest YOUR_USERNAME/exploremate-ai-service:latest
docker tag exploremate-service-discovery:latest YOUR_USERNAME/exploremate-service-discovery:latest

# Push images
docker push YOUR_USERNAME/exploremate-api-gateway:latest
docker push YOUR_USERNAME/exploremate-auth-service:latest
docker push YOUR_USERNAME/exploremate-trip-service:latest
docker push YOUR_USERNAME/exploremate-email-service:latest
docker push YOUR_USERNAME/exploremate-ai-service:latest
docker push YOUR_USERNAME/exploremate-service-discovery:latest
```

### Step 2: Create Render Services

For each service, create a new **Private Service** on Render:

1. Go to Render Dashboard → New → Private Service
2. Configure each service:

#### Service Discovery (Eureka)
- **Name**: exploremate-service-discovery
- **Image URL**: YOUR_USERNAME/exploremate-service-discovery:latest
- **Port**: 8761
- **Environment**: Docker
- **Plan**: Starter (or your preferred plan)

#### Auth Service
- **Name**: exploremate-auth
- **Image URL**: YOUR_USERNAME/exploremate-auth-service:latest
- **Port**: 8080
- **Environment Variables**:
  - `SPRING_PROFILES_ACTIVE`: prod
  - `SPRING_DATA_MONGODB_URI`: Your MongoDB Atlas connection string
  - `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`: http://exploremate-service-discovery:8761/eureka/

#### Trip Service
- **Name**: exploremate-trip
- **Image URL**: YOUR_USERNAME/exploremate-trip-service:latest
- **Port**: 8083
- **Environment Variables**:
  - `SPRING_PROFILES_ACTIVE`: prod
  - `SPRING_DATA_MONGODB_URI`: Your MongoDB Atlas connection string
  - `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`: http://exploremate-service-discovery:8761/eureka/

#### Email Service
- **Name**: exploremate-email
- **Image URL**: YOUR_USERNAME/exploremate-email-service:latest
- **Port**: 9090
- **Environment Variables**:
  - `SPRING_PROFILES_ACTIVE`: prod
  - `SPRING_MAIL_USERNAME`: your-email@gmail.com
  - `SPRING_MAIL_PASSWORD`: your-app-password

#### AI Service
- **Name**: exploremate-ai
- **Image URL**: YOUR_USERNAME/exploremate-ai-service:latest
- **Port**: 9091
- **Environment Variables**:
  - `SPRING_PROFILES_ACTIVE`: prod
  - `EUREKA_SERVER_URL`: http://exploremate-service-discovery:8761/eureka

#### API Gateway (Main Entry Point)
- **Name**: exploremate-api-gateway
- **Image URL**: YOUR_USERNAME/exploremate-api-gateway:latest
- **Port**: 9080
- **Environment Variables**:
  - `SPRING_PROFILES_ACTIVE`: prod
  - `AUTH_SERVICE_URL`: http://exploremate-auth:8080
  - `TRIP_SERVICE_URL`: http://exploremate-trip:8083
  - `EMAIL_SERVICE_URL`: http://exploremate-email:9090
  - `AI_SERVICE_URL`: http://exploremate-ai:9091
  - `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE`: http://exploremate-service-discovery:8761/eureka/
- **HTTP Request Settings**:
  - **Health Check Path**: /actuator/health

### Step 3: Connect Services

On Render, you can use **Instance Connectivity** to allow services to communicate:
- Go to each service → Settings → Instance Connectivity
- Enable "Allow incoming connections from other Render services"
- Add the services that need to communicate

## Option 2: Simplified Single Service Deployment

If the microservices architecture is too complex, you can consolidate into fewer services:

1. **API Gateway + All Services** - Deploy everything in one container
2. **Use Render's managed services** instead of self-hosted

## Frontend Configuration

Update your frontend `.env` file to point to the Render API Gateway:

```
VITE_API_BASE_URL=https://exploremate-api-gateway.onrender.com
```

## Health Check Endpoints

Each service provides health checks at:
- `/actuator/health` - Spring Boot Actuator health

## Troubleshooting

### Service Discovery Issues
- Make sure Eureka client config points to the correct service discovery URL
- Check that all services have `SPRING_PROFILES_ACTIVE=prod`

### MongoDB Connection
- Ensure your MongoDB Atlas IP whitelist includes Render's IPs
- Use connection string with proper encoding for special characters

### CORS Issues
- Configure CORS in the API Gateway for your frontend domain

## Current Docker Images

The following images are available locally and ready for deployment:
- `exploremate-api-gateway:latest`
- `exploremate-auth-service:latest`
- `exploremate-trip-service:latest`
- `exploremate-email-service:latest`
- `exploremate-ai-service:latest`
- `exploremate-service-discovery:latest`
