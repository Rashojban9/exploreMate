# Deploy Ollama on HuggingFace Spaces

This guide explains how to deploy your own Ollama instance on HuggingFace Spaces.

## Option 1: Use a Pre-built Community Space

Search HuggingFace Spaces for "ollama" and use one of these:
- `bbotio/ollama` 
- `cyborgbott/ollama`

Then update your docker-compose.yml:
```yaml
ai-service:
  environment:
    - OLLAMA_BASE_URL=https://your-space.hf.space
    - OLLAMA_MODEL=llama3.2:3b
```

## Option 2: Build Your Own Custom Space (Recommended)

### Step 1: Create a new Space on HuggingFace

1. Go to https://huggingface.co/spaces
2. Click "Create new Space"
3. Fill in the details:
   - **Space name**: `ollama` (or your preferred name)
   - **SDK**: Select **Docker** (NOT Streamlit)
   - **Hardware**: Select **NVIDIA T4** or **GPU** (important for GPU!)
   - **Template**: Select **Blank**
4. Click "Create Space"

### Step 2: Create these 2 files in your Space

After creating the Space, click "Add file" → "Create new file" for each:

---

**File 1 name: `Dockerfile`**

Copy and paste this content:

```dockerfile
FROM nvidia/cuda:12.4.0-base-ubuntu22.04

# Install dependencies
RUN apt-get update && apt-get install -y curl

# Download and install Ollama binary directly
RUN curl -fsSL https://ollama.ai/install.sh | sh

# Set environment
ENV OLLAMA_HOST=0.0.0.0:8080

EXPOSE 8080

# Run Ollama in background to keep container alive
CMD ["sh", "-c", "ollama serve & sleep infinity"]
```

---

**File 2 name: `README.md`**

Copy and paste this content:

```markdown
---
title: Ollama API
emoji: 🤖
colorFrom: blue
colorTo: purple
sdk: docker
app_port: 8080
pinned: false
---

# Ollama API

This Space runs Ollama for AI inference.

## Usage

Send POST requests to `/api/generate`:
```bash
curl -X POST https://your-space.hf.space/api/generate \
  -d '{"model": "llama3.2:3b", "prompt": "Hello", "stream": false}'
```
```

---

### Step 3: Important - Select GPU Hardware

**Before deploying, make sure you select GPU hardware:**
1. Go to your Space settings
2. Find "Hardware" option
3. Select **NVIDIA T4** or any GPU option (not CPU)

### Step 4: Wait for deployment

1. Click "Commit changes" 
2. Wait for HuggingFace to build the Docker image
3. The Space will start automatically

### Step 5: First API call (downloads model)

The first time you call the API, Ollama will download the model (~4GB). This takes a few minutes.

Test with:
```bash
curl -X POST https://your-username-ollama.hf.space/api/generate \
  -H "Content-Type: application/json" \
  -d '{"model": "llama3.2:3b", "prompt": "Hello", "stream": false}'
```

## Configuration

Update your docker-compose.yml:
```yaml
ai-service:
  environment:
    - OLLAMA_BASE_URL=https://your-username-ollama.hf.space
    - OLLAMA_MODEL=llama3.2:3b
```

Replace `your-username` with your HuggingFace username.

## Troubleshooting

**Error: "exec: ollama: not found"**
- Make sure you selected GPU hardware (NVIDIA T4) when creating the Space
- The install script may have failed - try the updated Dockerfile above

**Error: "NVIDIA Driver not detected"**
- You must select GPU hardware in Space settings
- CPU-only spaces cannot run Ollama with GPU acceleration

**Slow responses**: 
- GPU instance may be shared on free tier
- Make sure you're using a GPU-enabled Space

**Space goes to sleep**: 
- Free spaces sleep after inactivity - make a test call to wake it up
