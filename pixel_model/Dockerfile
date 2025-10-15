# ---- Base image (PyTorch + CUDA 12.1 for GPU) ----
FROM pytorch/pytorch:2.3.1-cuda12.1-cudnn8-runtime

# For CPU-only systems, use instead:
# FROM python:3.11-slim

WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y git ffmpeg libsm6 libxext6 && rm -rf /var/lib/apt/lists/*

# Copy code (only main.py)
COPY main.py .

# Install dependencies
RUN pip install --upgrade pip
RUN pip install fastapi uvicorn diffusers transformers accelerate safetensors pillow torch torchvision xformers

# Expose port
EXPOSE 8000

# Run the app
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]