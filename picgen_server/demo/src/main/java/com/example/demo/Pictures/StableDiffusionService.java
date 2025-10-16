// package com.example.demo.Pictures;

// import java.util.Map;

// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.client.RestTemplate;

// @Service
// public class StableDiffusionService {
//     // @Value("${sd.server.url}")
//     // private String serverURL;

//     private final RestTemplate restTemplate = new RestTemplate();

//     public ResponseEntity<?> generateImage(String prompt, int dimensions, int inference_steps, int guidance_scale) {
//         try{
//             String url = "https://sdserver123-sdserver123.hf.space/api/predict/";
            
//             Map<String, Object> requestBody = Map.of(
//                 "data", new Object[]{prompt, dimensions, inference_steps, guidance_scale}
//             );

//             HttpHeaders headers = new HttpHeaders();
//             headers.setContentType(MediaType.APPLICATION_JSON);

//             HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

//             ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

//             return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
//         }
//         catch(Exception e){
//             return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
//         }
//     }

//     public record modelResponse(String base64Image, boolean success, promptDTO dto ) {

//     }
//     public record promptDTO(String prompt, int dimensions, int inference_steps, int guidance_scale) {}
// }


package com.example.demo.Pictures;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class StableDiffusionService {

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<?> generateImage(String prompt, int dimensions, int inference_steps, int guidance_scale) {
        try {
            // Step 1: POST request to initiate the generation
            String postUrl = "https://sdserver123-sdserver123.hf.space/gradio_api/call/predict";

            Map<String, Object> body = Map.of(
                    "data", List.of(prompt, dimensions, inference_steps, guidance_scale)
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> postResponse = restTemplate.postForEntity(postUrl, entity, Map.class);

            if (postResponse.getStatusCode() != HttpStatus.OK || postResponse.getBody() == null) {
                return ResponseEntity.status(400).body("Failed to start generation");
            }

            // Step 2: Extract event_id
            Object eventId = postResponse.getBody().get("event_id");
            if (eventId == null) {
                return ResponseEntity.status(400).body("No event_id returned");
            }

            // Step 3: Poll the GET endpoint for the result
            String getUrl = "https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/" + eventId;
            ResponseEntity<Map> getResponse = restTemplate.getForEntity(getUrl, Map.class);

            return ResponseEntity.ok(getResponse.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body("Invalid request: " + e.getMessage());
        }
    }
}
