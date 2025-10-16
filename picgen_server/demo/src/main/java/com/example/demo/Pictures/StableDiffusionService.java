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

    public Mono<ResponseEntity<?>> generateImage(String prompt, int dimensions, int inference_steps, int guidance_scale) {
        System.out.println("Start hf call");
        String postUrl = "https://sdserver123-sdserver123.hf.space/gradio_api/call/predict";

        Map<String, Object> body = Map.of(
                "data", List.of(prompt, dimensions, inference_steps, guidance_scale)
        );

        return webClient.post()
                .uri(postUrl)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("User-Agent", "Mozilla/5.0")         // mimic browser
                .header("Origin", "https://your-frontend-domain.com") // match your frontend
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(postResp -> {
                    System.out.println("POST response: " + postResp);
                    if (postResp == null || !postResp.containsKey("event_id")) {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                    String eventId = postResp.get("event_id").toString();
                    String getUrl = postUrl + "/" + eventId;
                    return pollForResult(getUrl);
                })
                .onErrorResume(e -> {
                    System.out.println("POST error: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(500).build());
                });
    }

    private Mono<ResponseEntity<responseHF>> pollForResult(String url) {
        System.out.println("Start polling");
        return webClient.get()
                .uri(url)
                .header("Accept", "text/event-stream")
                .header("User-Agent", "Mozilla/5.0")
                .header("Origin", "https://your-frontend-domain.com")
                .retrieve()
                .bodyToFlux(String.class)
                .flatMap(line -> {
                    if (!line.startsWith("data:")) return Mono.empty();
                    String json = line.substring(5).trim();
                    if (json.isEmpty() || json.equals("[DONE]")) return Mono.empty();

                    Map<String, Object> resp;
                    try {
                        resp = new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Map.class);
                    } catch (Exception e) {
                        return Mono.empty();
                    }

                    String event = (String) resp.getOrDefault("event", "");
                    System.out.println("event: " + event);
                    if (!"complete".equals(event)) return Mono.empty();

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
                .next()
                .repeatWhenEmpty(r -> r.delayElements(Duration.ofSeconds(2)).take(15))
                .switchIfEmpty(Mono.just(ResponseEntity.status(504)
                        .body(new responseHF(null, false, new promptDTO("", 0, 0, 0)))))
                .onErrorResume(e -> {
                    System.out.println("error during poll: " + e.getMessage());
                    return Mono.just(ResponseEntity.status(500)
                            .body(new responseHF(null, false, new promptDTO("", 0, 0, 0))));
                });
    }

    public record responseHF(String image, boolean success, promptDTO dto) {}
    public record promptDTO(String prompt, int dimensions, int inference_steps, int guidance_scale) {}
}
