# ExploreMate - Railway Deployment Guide

## Why Railway?

Railway has a better free tier:
- **$5 credit/month** on free plan (equivalent to ~2-3 small services)
- **512MB RAM** per service on free tier
- Better Docker support with docker-compose

## Deploy Steps

### Option 1: One-Click Deploy (Recommended)

1. Go to [Railway.app](https://railway.app)
2. Sign up/Login with GitHub
3. Click "New Project" → "Deploy from GitHub repo"
4. Select your `exploreMate` repository
5. Railway will auto-detect the `docker-compose.yml`

### Option 2: Railway CLI

```bash
# Install Railway CLI
npm i -g @railway/cli

# Login
railway login

# Initialize project
railway init

# Deploy
railway up
```

## Environment Variables Needed

Set these in Railway dashboard:

| Variable | Value |
|----------|-------|
| `MONGODB_URI` | Your MongoDB connection string |
| `JWT_SECRET` | Your JWT secret key |
| `EMAIL_USERNAME` | Your Gmail address |
| `EMAIL_PASSWORD` | Your Gmail app password |
| `SPRING_PROFILES_ACTIVE` | `prod` |

## Services

The docker-compose.yml will deploy 6 services:

1. **service-discovery** - Eureka Server (port 8761)
2. **auth-service** - Authentication (port 8080)
3. **trip-service** - Trip management (port 8083)
4. **email-service** - Email notifications (port 9090)
5. **api-gateway** - Main entry point (port 9080)
6. **ai-service** - AI suggestions (port 9091)

## Memory Limits

Each service has a 512MB memory limit set in docker-compose.yml.

## Files Modified

- `docker-compose.yml` - Updated with Railway deployment config
- `railway.json` - Railway project config
- Service Dockerfiles - Added JVM memory limits

## Troubleshooting

If you get OOM errors, reduce JVM heap in each service Dockerfile:
```dockerfile
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-jar", "app.jar"]
```
