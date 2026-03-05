# ExploreMate - Hugging Face Spaces Deployment

## Your Backend Space
https://huggingface.co/spaces/rashojban9/explore-mate-backend

## Deploy Steps

### Option 1: Push to GitHub (Recommended)

1. Push the entire `backend/` folder to your GitHub repository
2. Connect your GitHub repo to Hugging Face Space
3. Hugging Face will auto-detect the Dockerfile and deploy

### Option 2: Direct Upload

1. Go to: https://huggingface.co/spaces/rashojban9/explore-mate-backend
2. Click "Files" → "Upload"
3. Upload the updated `Dockerfile`
4. Also upload `.env` file content in Space settings

## Environment Variables

Set these in your Hugging Face Space settings (Repository Settings → Variables):

| Variable | Value |
|----------|-------|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `MONGODB_URI_AUTH` | MongoDB URI for auth_service database |
| `MONGODB_URI_TRIP` | MongoDB URI for trip_db database |
| `MONGODB_URI_AI` | MongoDB URI for ai_service database |
| `JWT_SECRET` | Your JWT secret key |
| `EMAIL_USERNAME` | Your Gmail address |
| `EMAIL_PASSWORD` | Your Gmail app password |
| `GROQ_API_KEY` | Your Groq API key |
| `GROQ_MODEL` | `llama-3.3-70b-versatile` |

## Database Setup

Each service uses a separate MongoDB database:
- **auth-service** → `auth_db` (user accounts, authentication)
- **trip-service** → `trip_db` (trips, saved items)
- **ai-service** → `ai_service` (conversation history)

You can use the same MongoDB Atlas cluster with different database names:
```
# Example for MongoDB Atlas:
# auth_db: auth_service database
# trip_db: trip_db database  
# ai_service: ai_service database
```

## Frontend Configuration

The frontend has been updated to connect to your Hugging Face backend.

Update your frontend's `.env` file:
```
VITE_API_BASE_URL=https://rashojban9-explore-mate-backend.hf.space
```

## Port Mapping

| Service | Port |
|---------|------|
| Service Discovery (Eureka) | 8761 |
| Auth Service | 8080 |
| API Gateway | 9080 |
| Trip Service | 8083 |
| Email Service | 9090 |
| AI Service | 9091 |

## Why Hugging Face?

- **16GB RAM** on free tier (vs 512MB on Render)
- **No OOM issues** with microservices
- **Docker native** support
- **Free GPU** available for AI service

## Troubleshooting

**Services not starting:**
- Check the logs in Hugging Face Space
- Verify all environment variables are set
- Ensure MongoDB Atlas IP whitelist includes Hugging Face IPs

**Frontend can't connect:**
- Make sure `VITE_API_BASE_URL` points to your HF Space URL
- Check CORS settings in API Gateway

**AI service errors:**
- Verify `GROQ_API_KEY` is correct
- Check the model name is valid
