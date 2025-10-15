package com.example.demo.Pictures;

import org.springframework.stereotype.Service;

@Service
public class StableDiffusionService {
    private String serverURL;




    public record modelResponse(String base64Image, boolean success, promptDTO dto ) {

    }
    public record promptDTO(String prompt, int dimensions, int inference_steps, int guidance_scale) {}
}


