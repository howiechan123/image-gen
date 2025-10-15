FROM pytorch/pytorch:2.3.0-cuda12.1-cudnn8-runtime

WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends \
    git \
    ffmpeg \
    libsm6 \
    libxext6 \
 && rm -rf /var/lib/apt/lists/*

COPY main.py .

RUN pip install --upgrade pip && \
    pip install --no-cache-dir \
        fastapi \
        uvicorn[standard] \
        torch==2.3.0 \
        torchvision==0.18.0 \
        transformers==4.44.0 \
        diffusers==0.31.0 \
        accelerate==0.33.0 \
        safetensors \
        pillow \
        xformers==0.0.26.post1

EXPOSE 8000

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
