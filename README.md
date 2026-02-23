# ExploreMate - Hugging Face Spaces Deployment

## Current Status

Your backend is deployed at: https://huggingface.co/spaces/rashojban9/explore-mate-backend

## To Deploy All Services on Hugging Face

Hugging Face Spaces has a **free tier with 16GB RAM** - much better than Render!

### Option 1: Docker Space (Recommended)

Create a `Dockerfile` in your Space root:

```dockerfile
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy all service JARs
COPY services/* /app/

# Start all services with memory optimization
CMD ["sh", "-c", "\
    java -Xmx512m -Xms256m -jar /app/service-discovery.jar & \
    java -Xmx512m -Xms256m -jar /app/auth-service.jar & \
    java -Xmx512m -Xms256m -jar /app/trip-service.jar & \
    java -Xmx512m -Xms256m -jar /app/email-service.jar & \
    java -Xmx512m -Xms256m -jar /app/ai-service.jar & \
    java -Xmx512m -Xms256m -jar /app/api-gateway.jar"]
```

### Option 2: Pre-build JARs

You need to first build all JARs locally:

```bash
cd backend

# Build each service
cd service-discovery/service-discovery && mvn clean package -DskipTests
cd ../..
cd auth-service/auth-service && mvn clean package -DskipTests
# ... repeat for all services
```

Then copy the JARs to a `services/` folder and push to your Space.

## Hugging Face Spaces Tips

- **16GB RAM** on free tier - plenty for all services!
- **No OOM issues** like Render
- Auto-deploys from GitHub

## Files to Push

Make sure your Space has:
- `Dockerfile` 
- All service JARs in `services/` folder
- Environment variables set in Space settings
