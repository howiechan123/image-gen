# Use latest CUDA 12.1 PyTorch runtime
FROM pytorch/pytorch:2.3.0-cuda12.1-cudnn8-runtime

WORKDIR /app

# Install required system packages
RUN apt-get update && apt-get install -y \
    git \
    ffmpeg \
    libsm6 \
    libxext6 \
 && rm -rf /var/lib/apt/lists/*

# Copy your app
COPY main.py .

# Upgrade pip and install Python dependencies
RUN pip install --upgrade pip \
 && pip install \
    fastapi \
    uvicorn[standard] \
    torch==2.3.0 \
    torchvision==0.18.0 \
    transformers==4.47.0 \
    diffusers==0.35.1 \
    accelerate \
    safetensors \
    pillow \
    xformers==0.0.26.post1

# Expose FastAPI port
EXPOSE 8000

# Run FastAPI server
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
