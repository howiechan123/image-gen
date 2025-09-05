from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from diffusers import DiffusionPipeline
import torch
from PIL import Image
from io import BytesIO
import base64
from fastapi.responses import JSONResponse
from pydantic import BaseModel
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
    

def generate_pixel_art(p):
    
    pipe = DiffusionPipeline.from_pretrained("stabilityai/stable-diffusion-2-1-base", use_safetensors=True)
    pipe.to("cpu")
    pipe.safety_checker = None
    
    height = 512
    width = 512

    prompt = p
    
    image = pipe(prompt=prompt, height=height, width=width).images[0]
    image.show()
    return image

def image_to_base64(image):
    buffered = BytesIO()
    image.save(buffered, format="PNG")
    return base64.b64encode(buffered.getvalue()).decode("utf-8")

@app.post("/generate-image")
async def generateImage(request: ImageRequest):
    
    prompt = request.prompt
    
    if not prompt:
        raise HTTPException(status_code=400, detail="Prompt is required")
 
    image = generate_pixel_art(prompt)
    image_base64 = image_to_base64(image)

    return JSONResponse(content={"image": image_base64, "success":True})