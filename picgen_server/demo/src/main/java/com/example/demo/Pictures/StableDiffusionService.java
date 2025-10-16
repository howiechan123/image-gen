package com.example.demo.Pictures;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

@Service
public class StableDiffusionService {
    @Value("${SDServer.url}")
    private String serverURL;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<?> generateImage(String prompt, int dimensions, int inference_steps, int guidance_scale) {
        try{
            String url = "https://sdserver123-sdserver123.hf.space/api/predict/";
            promptDTO body = new promptDTO(prompt, dimensions, inference_steps, guidance_scale);

            modelResponse response = restTemplate.postForObject(url, body, modelResponse.class);

            return ResponseEntity.ok(response);
        }
        catch(Exception e){
            return ResponseEntity.status(400).body("invalid request");
        }
    }

    public record modelResponse(String base64Image, boolean success, promptDTO dto ) {

    }
    public record promptDTO(String prompt, int dimensions, int inference_steps, int guidance_scale) {}
}


