package com.example.demo.Pictures;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class StableDiffusionService {

    public Mono<ResponseEntity<?>> generateImage(String prompt, int dimensions, int inference_steps, int guidance_scale) {
        System.out.println("Start HF call");
        return Mono.fromCallable(() -> {
            String postCmd = String.format(
                    "curl -s -X POST https://sdserver123-sdserver123.hf.space/gradio_api/call/predict " +
                    "-H 'Content-Type: application/json' " +
                    "-d '{\"data\": [\"%s\", %d, %d, %d]}'",
                    prompt.replace("\"", "\\\""),
                    dimensions,
                    inference_steps,
                    guidance_scale
            );

            ProcessBuilder pb = new ProcessBuilder("bash", "-c", postCmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            String postOutput;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                postOutput = reader.lines().collect(Collectors.joining("\n"));
            }
            p.waitFor();
            p.destroy();

            String eventId = postOutput.replaceAll(".*\"event_id\"\\s*:\\s*\"([^\"]+)\".*", "$1").trim();
            if (eventId.isEmpty() || eventId.equals(postOutput)) {
                return ResponseEntity.status(500).body("{\"success\":false,\"message\":\"Failed to extract event_id\"}");
            }
            System.out.println(eventId);
            return ResponseEntity.ok("{\"success\":true,\"event_id\":\"" + eventId + "\"}");
        });
    }

    @Async
    public CompletableFuture<ResponseEntity<?>> pollHF(String eventId) {
        System.out.println("Poll call");

        return CompletableFuture.supplyAsync(() -> {
            try {
                String getCmd = "curl -s https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/" + eventId;
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", getCmd);
                pb.redirectErrorStream(true);
                Process p = pb.start();

                String getOutput;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    getOutput = reader.lines().collect(Collectors.joining("\n"));
                }
                p.waitFor();
                p.destroy();

                System.out.println("HF poll raw output: " + getOutput);

                if (!getOutput.contains("\"data\"")) {
                    return ResponseEntity.ok(Map.of("success", false, "message", "Still processing"));
                }

                if (getOutput.contains("base64")) {
                    String base64 = getOutput.replaceAll("(?s).*\"data\":\\[\"(.*?)\".*", "$1").trim();
                    return ResponseEntity.ok(Map.of("success", true, "image", base64));
                }

                return ResponseEntity.ok(Map.of("success", false, "message", "Not ready yet"));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
            }
        });
    }
}
