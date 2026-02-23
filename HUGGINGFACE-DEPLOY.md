# ExploreMate - Hugging Face Spaces Deployment

## Your Space
https://huggingface.co/spaces/rashojban9/explore-mate-backend

## Deploy Steps

### Option 1: Direct Push (Recommended)

1. Push the `Dockerfile` to your GitHub repo connected to Hugging Face
2. Hugging Face will auto-detect and deploy

### Option 2: Manual Upload

1. Go to your Space: https://huggingface.co/spaces/rashojban9/explore-mate-backend
2. Click "Files" → "Upload"
3. Upload the Dockerfile

## Dockerfile Features

The Dockerfile now includes:
- ✅ Builds all 6 microservices
- ✅ Java 17 (lighter weight)
- ✅ JVM memory limits per service (384MB heap)
- ✅ Starts services in correct order with delays
- ✅ Exposes all ports: 8761, 8080, 9080, 8083, 9090, 9091

## Environment Variables

Set these in your Space settings:

| Variable | Value |
|----------|-------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `MONGODB_URI` | Your MongoDB URI |
| `JWT_SECRET` | Your JWT secret |
| `EMAIL_USERNAME` | Your Gmail |
| `EMAIL_PASSWORD` | Your Gmail app password |

## Why This Works on Hugging Face

- **16GB RAM** on free tier (vs 512MB on Render)
- **No OOM issues**
- **Docker native** support

## Port Mapping

| Service | Port |
|---------|------|
| Service Discovery | 8761 |
| Auth Service | 8080 |
| API Gateway | 9080 |
| Trip Service | 8083 |
| Email Service | 9090 |
| AI Service | 9091 |
