package com.example.demo.Pictures;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

@Service
public class StableDiffusionService {
    // @Value("${sd.server.url}")
    // private String serverURL;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<?> generateImage(String prompt, int dimensions, int inference_steps, int guidance_scale) {
        try{
            String url = "https://sdserver123-sdserver123.hf.space/api/predict/";
            
            Map<String, Object> requestBody = Map.of(
                "data", new Object[]{prompt, dimensions, inference_steps, guidance_scale}
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        }
        catch(Exception e){
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }

    public record modelResponse(String base64Image, boolean success, promptDTO dto ) {

    }
    public record promptDTO(String prompt, int dimensions, int inference_steps, int guidance_scale) {}
}


