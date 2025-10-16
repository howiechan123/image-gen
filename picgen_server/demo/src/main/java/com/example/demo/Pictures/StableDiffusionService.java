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
        String postUrl = "https://sdserver123-sdserver123.hf.space/gradio_api/call/predict";
        Map<String, Object> body = Map.of(
                "data", List.of(prompt, dimensions, inference_steps, guidance_scale)
        );

        return webClient.post()
                .uri(postUrl)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(postResp -> {
                    if (postResp == null || !postResp.containsKey("event_id")) {
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                    String eventId = postResp.get("event_id").toString();
                    String getUrl = "https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/" + eventId;
                    return pollForResult(getUrl);
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    private Mono<ResponseEntity<HFImageData>> pollForResult(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(HFResponseDTO.class)
                .map(resp -> {
                    Map<String, Object> dataMap = resp.data().get(0);
                    String base64 = dataMap.get("image").toString();
                    boolean success = Boolean.parseBoolean(dataMap.get("success").toString());
                    Map<String, Object> promptMap = (Map<String, Object>) dataMap.get("prompt params");
                    PromptDTO promptDTO = new PromptDTO(
                            promptMap.get("prompt").toString(),
                            ((Number) promptMap.get("dimensions")).intValue(),
                            ((Number) promptMap.get("inf_steps")).intValue(),
                            ((Number) promptMap.get("scale")).intValue()
                    );
                    HFImageData hfImageData = new HFImageData(base64, success, promptDTO);
                    return ResponseEntity.ok(hfImageData);
                })
                .repeatWhenEmpty(r -> r.delayElements(Duration.ofSeconds(2)).take(30))
                .switchIfEmpty(Mono.just(
                        ResponseEntity.status(504)
                                .body(new HFImageData(null, false, new PromptDTO("", 0, 0, 0)))
                ));
    }

    public record HFResponseDTO(
            List<Map<String, Object>> data
    ) {}

    public record HFImageData(
            String image,
            boolean success,
            PromptDTO promptParams
    ) {}

    public record PromptDTO(
            String prompt,
            int dimensions,
            int inf_steps,
            int scale
    ) {}
}
