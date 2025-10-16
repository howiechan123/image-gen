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

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class StableDiffusionService {

    private final WebClient webClient = WebClient.builder().build();

    public Mono<ResponseEntity<?>> generateImage(String prompt, int dimensions, int inference_steps, int guidance_scale) {

        String postUrl = "https://sdserver123-sdserver123.hf.space/gradio_api/call/predict";

        Map<String, Object> body = Map.of(
                "data", List.of(prompt, dimensions, inference_steps, guidance_scale)
        );

        // Step 1: POST request to get event_id
        return webClient.post()
                .uri(postUrl)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(postResp -> {
                    if (postResp == null || !postResp.containsKey("event_id")) {
                        return Mono.just(ResponseEntity.badRequest().body("No event_id returned"));
                    }

                    String eventId = postResp.get("event_id").toString();
                    String getUrl = "https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/" + eventId;

                    // Step 2: Poll GET endpoint
                    return pollForResult(getUrl);
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body("Invalid request: " + e.getMessage())));
    }

    private Mono<ResponseEntity<Map>> pollForResult(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .repeatWhenEmpty(repeat -> repeat
                        .delayElements(Duration.ofSeconds(10))
                        .take(60)
                )
                .map(resp -> ResponseEntity.ok(resp))
                .switchIfEmpty(Mono.just(
                        ResponseEntity.status(504)
                                .body(Map.of("error", "Timed out waiting for result"))
                ));
    }


}
