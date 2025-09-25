# from fastapi import FastAPI, HTTPException
# from pydantic import BaseModel
# from diffusers import DiffusionPipeline
# import torch
# from PIL import Image
# from io import BytesIO
# import base64
# from fastapi.responses import JSONResponse
# from pydantic import BaseModel
# from fastapi.middleware.cors import CORSMiddleware

# app = FastAPI()

# app.add_middleware(
#     CORSMiddleware,
#     allow_origins=["*"],
#     allow_credentials=True,
#     allow_methods=["POST"],
#     allow_headers=["*"],
# )

# class ImageRequest(BaseModel):
#     prompt: str
    

# def generate_pixel_art(p):
    
#     pipe = DiffusionPipeline.from_pretrained("stabilityai/stable-diffusion-2-1-base", use_safetensors=True)
#     pipe.to("cuda")
#     pipe.safety_checker = None
    
#     height = 512
#     width = 512

#     prompt = p
    
#     image = pipe(prompt=prompt, height=height, width=width).images[0]
#     image.show()
#     return image

# def image_to_base64(image):
#     buffered = BytesIO()
#     image.save(buffered, format="PNG")
#     return base64.b64encode(buffered.getvalue()).decode("utf-8")

# @app.post("/generate-image")
# async def generateImage(request: ImageRequest):
    
#     prompt = request.prompt
    
#     if not prompt:
#         raise HTTPException(status_code=400, detail="Prompt is required")
 
#     image = generate_pixel_art(prompt)
#     image_base64 = image_to_base64(image)

#     return JSONResponse(content={"image": image_base64, "success":True})

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from diffusers import StableDiffusionPipeline
import torch
from PIL import Image
from io import BytesIO
import base64
from fastapi.responses import JSONResponse
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["POST"],
    allow_headers=["*"],
)

class ImageRequest(BaseModel):
    prompt: str


if torch.cuda.is_available():
    device = "cuda"
    dtype = torch.float16
else:
    device = "cpu"
    dtype = torch.float32


model_id = "stabilityai/stable-diffusion-2-1-base"

pipe = StableDiffusionPipeline.from_pretrained(
    model_id,
    torch_dtype=dtype,
    revision="fp16" if device == "cuda" else None
)

pipe.to(device)
pipe.safety_checker = None

# Memory optimizations
pipe.enable_attention_slicing()
pipe.enable_sequential_cpu_offload()
try:
    pipe.enable_xformers_memory_efficient_attention()
except Exception:
    print("xformers not installed, continuing without it")


def generate_pixel_art(prompt: str):
    image = pipe(
        prompt=prompt,
        height=256,
        width=256,
        num_inference_steps=30,
    ).images[0]
    return image


def image_to_base64(image: Image.Image):
    buffered = BytesIO()
    image.save(buffered, format="PNG")
    return base64.b64encode(buffered.getvalue()).decode("utf-8")


@app.post("/generate-image")
async def generateImage(request: ImageRequest):
    if not request.prompt:
        raise HTTPException(status_code=400, detail="Prompt is required")

    try:
        image = generate_pixel_art(request.prompt)
    except torch.cuda.OutOfMemoryError:
        raise HTTPException(status_code=500, detail="GPU out of memory try smaller size or fewer steps")

    image_base64 = image_to_base64(image)
    return JSONResponse(content={"image": image_base64, "success": True})
