package com.example.demo.Pictures;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StableDiffusionService {

    private final Map<String, String> cookies = new HashMap<>();
    private final WebClient webClient;

    public StableDiffusionService() {
        HttpClient httpClient = HttpClient.create();
        this.webClient = WebClient.builder()
                .clientConnector(new org.springframework.http.client.reactive.ReactorClientHttpConnector(httpClient))
                .filter((request, next) -> {
                    if (!cookies.isEmpty()) {
                        String cookieHeader = cookies.entrySet()
                                .stream()
                                .map(e -> e.getKey() + "=" + e.getValue())
                                .reduce((a, b) -> a + "; " + b)
                                .orElse("");
                        request = org.springframework.web.reactive.function.client.ClientRequest.from(request)
                                .header("Cookie", cookieHeader)
                                .build();
                    }
                    return next.exchange(request).doOnNext(resp -> {
                        List<String> setCookies = resp.headers().asHttpHeaders().get("Set-Cookie");
                        if (setCookies != null) {
                            for (String setCookie : setCookies) {
                                String[] parts = setCookie.split(";", 2);
                                String[] kv = parts[0].split("=", 2);
                                if (kv.length == 2) cookies.put(kv[0], kv[1]);
                            }
                        }
                    });
                })
                .build();
    }

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
                    String getUrl = "https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/" + eventId;
                    return pollForResult(getUrl);
                })
                .onErrorResume(e -> {
                    System.out.println("POST error: " + e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    private Mono<ResponseEntity<responseHF>> pollForResult(String url) {
        System.out.println("Start polling");
        return webClient.get()
                .uri(url)
                .header("Accept", "text/event-stream")
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

                    System.out.println("event complete, processing data");
                    List<Map<String, Object>> dataList = (List<Map<String, Object>>) resp.get("data");
                    if (dataList == null || dataList.isEmpty()) {
                        System.out.println("data empty");
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
                .next()
                .repeatWhenEmpty(r -> r.delayElements(Duration.ofSeconds(10)).take(60))
                .switchIfEmpty(Mono.just(ResponseEntity.status(504)
                        .body(new responseHF(null, false, new promptDTO("", 0, 0, 0)))))
                .onErrorResume(e -> {
                    System.out.println("error during poll: " + e.getMessage());
                    return Mono.just(ResponseEntity.badRequest()
                            .body(new responseHF(null, false, new promptDTO("", 0, 0, 0))));
                });
    }

    public record responseHF(String image, boolean success, promptDTO dto) {}
    public record promptDTO(String prompt, int dimensions, int inference_steps, int guidance_scale) {}
}
