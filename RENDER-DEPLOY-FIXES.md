# Render Deployment Fixes

## Problem
The deployment was failing with: **"Ran out of memory (used over 512MB)"**

## Root Causes Identified

### 1. All-in-One Dockerfile (Main Issue)
The root `Dockerfile` was trying to run ALL 6 microservices in a single container, which far exceeded the 512MB free tier limit.

### 2. No JVM Memory Limits
Individual service Dockerfiles had no JVM memory constraints, causing each Spring Boot service to use default memory settings (often 25%+ of available RAM).

### 3. Using Java 21
Java 21 has a larger footprint compared to Java 17.

## Fixes Applied

### 1. Disabled All-in-One Dockerfile
Renamed `Dockerfile` → `Dockerfile.all-in-one.bak` to prevent accidental deployment of the monolithic container.

### 2. Optimized All Service Dockerfiles
Updated all 6 service Dockerfiles with:
- **Java 17** (lighter than Java 21)
- **JVM Memory Limits** optimized for 512MB container:
  - `-Xmx192m` - Max heap 192MB
  - `-Xms128m` - Initial heap 128MB
  - `-XX:MaxMetaspaceSize=64m` - Limit metaspace
  - `-XX:+UseG1GC` - G1 garbage collector for smaller footprint

### 3. Updated render.yaml
- Added proper Eureka service discovery URLs to all services
- Ensured service-to-service communication is properly configured

## Memory Per Service
| Service | Memory Limit |
|---------|-------------|
| Service Discovery | 192MB max heap |
| Auth Service | 192MB max heap |
| Trip Service | 192MB max heap |
| Email Service | 192MB max heap |
| AI Service | 192MB max heap |
| API Gateway | 192MB max heap |

**Total if all running**: ~384MB (plus native memory) - should fit in 512MB with some buffer.

## Next Steps

### Deploy to Render
1. Push these changes to your GitHub repository
2. Render will auto-deploy based on `render.yaml`
3. Monitor the deployment logs for any remaining issues

### Potential Issues to Watch

#### Kafka Dependency
Both `auth-service` and `email-service` depend on Kafka, which is not available on Render's free tier. You may need to:
- Create a production profile that disables Kafka
- Or use a managed Kafka service (e.g., Confluent Cloud free tier)

#### Service Discovery
Make sure all services can register with Eureka. The services will fall back to direct URL configuration if Eureka registration fails.

## Files Modified
- `api-gateway/api-gateway/Dockerfile` - Added JVM memory limits
- `auth-service/auth-service/Dockerfile` - Added JVM memory limits
- `trip-service/trip-service/Dockerfile` - Added JVM memory limits
- `service-discovery/service-discovery/Dockerfile` - Added JVM memory limits
- `email-service/email-service/Dockerfile` - Added JVM memory limits
- `ai-service/ai-service/Dockerfile` - Added JVM memory limits
- `Dockerfile` → `Dockerfile.all-in-one.bak` - Disabled monolithic deployment
- `render.yaml` - Updated service configuration
