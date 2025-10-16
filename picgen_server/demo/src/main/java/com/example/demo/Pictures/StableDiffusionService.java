package com.example.demo.Pictures;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class StableDiffusionService {

    private final WebClient webClient = WebClient.builder().build();

    public Mono<ResponseEntity<?>> generateImage(
            String prompt,
            int dimensions,
            int inference_steps,
            int guidance_scale
    ) {
        System.out.println("Start HF call");

        Map<String, Object> body = Map.of(
                "data", List.of(prompt, dimensions, inference_steps, guidance_scale)
        );

        return webClient.post()
                .uri("https://sdserver123-sdserver123.hf.space/gradio_api/call/predict")
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(postResp -> {
                    System.out.println("POST response: " + postResp);
                    if (postResp == null || !postResp.containsKey("event_id")) {
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                    String eventId = postResp.get("event_id").toString();
                    return pollForResult(eventId, 0);
                })
                .onErrorResume(e -> {
                    System.out.println("POST error: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(500).build());
                });
    }

    private Mono<ResponseEntity<responseHF>> pollForResult(String eventId, int attempt) {
        if (attempt >= 30) { // max 30 attempts
            System.out.println("Polling timeout");
            return Mono.just(ResponseEntity.status(504)
                    .body(new responseHF(null, false, new promptDTO("", 0, 0, 0))));
        }

        String url = "https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/" + eventId;
        System.out.println("Polling attempt " + (attempt + 1) + " URL: " + url);

        return Mono.delay(Duration.ofSeconds(2)) // delay between attempts
                .flatMap(t -> webClient.get()
                        .uri(url)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .flatMap(resp -> {
                            System.out.println("GET response: " + resp);
                            if (resp == null) return Mono.empty();
                            String event = (String) resp.getOrDefault("event", "");
                            if (!"complete".equals(event)) {
                                return pollForResult(eventId, attempt + 1);
                            }

                            List<Map<String, Object>> dataList = (List<Map<String, Object>>) resp.get("data");
                            if (dataList == null || dataList.isEmpty()) {
                                return Mono.just(ResponseEntity.status(504)
                                        .body(new responseHF(null, false, new promptDTO("", 0, 0, 0))));
                            }

                            Map<String, Object> dataItem = dataList.get(0);
                            String image = (String) dataItem.getOrDefault("image", null);
                            boolean success = Boolean.parseBoolean(dataItem.getOrDefault("success", false).toString());

                            Map<String, Object> promptMap = (Map<String, Object>) dataItem.getOrDefault("prompt params", Map.of());
                            promptDTO dto = new promptDTO(
                                    (String) promptMap.getOrDefault("prompt", ""),
                                    ((Number) promptMap.getOrDefault("dimensions", 0)).intValue(),
                                    ((Number) promptMap.getOrDefault("inf_steps", 0)).intValue(),
                                    ((Number) promptMap.getOrDefault("scale", 0)).intValue()
                            );

                            return Mono.just(ResponseEntity.ok(new responseHF(image, success, dto)));
                        })
                        .switchIfEmpty(pollForResult(eventId, attempt + 1))
                        .onErrorResume(e -> {
                            System.out.println("Polling error: " + e.getMessage());
                            return pollForResult(eventId, attempt + 1);
                        })
                );
    }

    public record responseHF(String image, boolean success, promptDTO dto) {}
    public record promptDTO(String prompt, int dimensions, int inference_steps, int guidance_scale) {}
}
