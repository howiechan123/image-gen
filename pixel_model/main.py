from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from diffusers import StableDiffusionPipeline
import torch
from PIL import Image
from io import BytesIO
import base64
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(title="Stable Diffusion 1.5 API")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["POST"],
    allow_headers=["*"],
)


class ImageRequest(BaseModel):
    prompt: str
    dimensions: int=512
    inference_steps:int=25
    guidance_scale:int=10

if torch.cuda.is_available():
    device = "cuda"
    dtype = torch.float16
else:
    device = "cpu"
    dtype = torch.float32

model_id = "runwayml/stable-diffusion-v1-5"
# model_id = "stabilityai/stable-diffusion-2-1"

pipe = StableDiffusionPipeline.from_pretrained(model_id, dtype=dtype, safety_checker=None)
pipe.to(device)

pipe.enable_attention_slicing()
pipe.enable_sequential_cpu_offload()

try:
    pipe.enable_xformers_memory_efficient_attention()
    print("xformers optimization enabled")
except Exception:
    print("xformers not installed, continuing without it")


def generate_pixel_art(prompt: str, dimensions, inference_steps, guidance_scale):

    image = pipe(
        prompt=prompt,
        height=dimensions,
        width=dimensions,
        num_inference_steps=inference_steps,  # balanced quality/speed
        guidance_scale=guidance_scale,      # improves prompt fidelity
    ).images[0]
    return image


def image_to_base64(image: Image.Image):
    buffered = BytesIO()
    image.save(buffered, format="PNG")
    return base64.b64encode(buffered.getvalue()).decode("utf-8")

@app.post("/generate-image")
async def generate_image(request: ImageRequest):
    print(request)
    if not request.prompt:
        raise HTTPException(status_code=400, detail="Prompt is required")

    try:
        image = generate_pixel_art(request.prompt, request.dimensions, request.inference_steps, request.guidance_scale)
    except torch.cuda.OutOfMemoryError:
        raise HTTPException(status_code=500, detail="GPU out of memory, try smaller size or fewer steps")
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

    image_base64 = image_to_base64(image)
    return JSONResponse(content={"image": image_base64, "success": True, "prompt params": {"prompt": request.prompt, "dimensions": request.dimensions, "inf_steps": request.inference_steps, "scale":request.guidance_scale}})

# ---- Run server (for local testing) ----
# Command: python -m uvicorn main:app --reload
