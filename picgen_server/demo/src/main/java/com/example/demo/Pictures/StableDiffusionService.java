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
        System.out.println("Start hf call");
        String postUrl = "https://sdserver123-sdserver123.hf.space/gradio_api/call/predict";
        Map<String, Object> body = Map.of("data", List.of(prompt, dimensions, inference_steps, guidance_scale));

        return webClient.post()
                .uri(postUrl)
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
                    System.out.println("Start polling after 20s delay");
                    return pollForResult(eventId);
                })
                .onErrorResume(e -> {
                    System.out.println("POST error: " + e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    private Mono<ResponseEntity<responseHF>> pollForResult(String eventId) {
        String url = "https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/" + eventId;
        System.out.println("Polling URL: " + url);

        return Mono.delay(Duration.ofSeconds(20))
                .flatMap(t -> webClient.get()
                        .uri(url)
                        .header("Accept", "application/json")
                        .retrieve()
                        .bodyToMono(Map.class)
                        .flatMap(resp -> {
                            System.out.println("GET response: " + resp);
                            if (resp == null || !resp.containsKey("data")) {
                                System.out.println("data missing in GET response");
                                return Mono.just(ResponseEntity.status(504)
                                        .body(new responseHF(null, false, new promptDTO("", 0, 0, 0))));
                            }

                            List<Map<String, Object>> dataList = (List<Map<String, Object>>) resp.get("data");
                            if (dataList.isEmpty()) {
                                System.out.println("data empty in GET response");
                                return Mono.just(ResponseEntity.status(504)
                                        .body(new responseHF(null, false, new promptDTO("", 0, 0, 0))));
                            }

                            Map<String, Object> dataItem = dataList.get(0);
                            String image = (String) dataItem.getOrDefault("image", null);
                            boolean success = Boolean.parseBoolean(dataItem.getOrDefault("success", false).toString());
                            System.out.println("image retrieved: " + (image != null));

                            Map<String, Object> promptMap = (Map<String, Object>) dataItem.getOrDefault("prompt params", Map.of());
                            promptDTO dto = new promptDTO(
                                    (String) promptMap.getOrDefault("prompt", ""),
                                    ((Number) promptMap.getOrDefault("dimensions", 0)).intValue(),
                                    ((Number) promptMap.getOrDefault("inf_steps", 0)).intValue(),
                                    ((Number) promptMap.getOrDefault("scale", 0)).intValue()
                            );
                            System.out.println("prompt DTO created");
                            return Mono.just(ResponseEntity.ok(new responseHF(image, success, dto)));
                        })
                        .onErrorResume(e -> {
                            System.out.println("error during poll: " + e.getMessage());
                            return Mono.just(ResponseEntity.badRequest()
                                    .body(new responseHF(null, false, new promptDTO("", 0, 0, 0))));
                        })
                );
    }

    public record responseHF(String image, boolean success, promptDTO dto) {}
    public record promptDTO(String prompt, int dimensions, int inference_steps, int guidance_scale) {}
}
