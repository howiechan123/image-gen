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


    private Mono<ResponseEntity<HFImageResponse>> pollForResult(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(HFImageResponse.class)
                .repeatWhenEmpty(repeat -> repeat
                        .delayElements(Duration.ofSeconds(2))
                        .take(30)
                )
                .map(resp -> ResponseEntity.ok(resp))
                .switchIfEmpty(Mono.just(
                        ResponseEntity.status(504)
                                .body(new HFImageResponse(
                                        null,
                                        false,
                                        new HFImageResponse.PromptParams("", 0, 0, 0)
                                ))
                ));
    }


    public record HFImageResponse(
            String image,
            boolean success,
            PromptParams promptParams
    ) {
        public record PromptParams(
                String prompt,
                int dimensions,
                int inf_steps,
                int scale
        ) {}
    }



}
