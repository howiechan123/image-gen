package com.example.demo.Pictures;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Service
public class StableDiffusionService {

    public Mono<ResponseEntity<?>> generateImage(String prompt, int dimensions, int inference_steps, int guidance_scale) {
        System.out.println("Start HF API call");

        return Mono.fromCallable(() -> {
            // 1️⃣ Step 1: POST request to start prediction
            String postCmd = String.format(
                "curl -s -X POST https://sdserver123-sdserver123.hf.space/gradio_api/call/predict " +
                "-H 'Content-Type: application/json' " +
                "-d '{\"data\": [\"%s\", %d, %d, %d]}'",
                prompt.replace("\"", "\\\""),
                dimensions,
                inference_steps,
                guidance_scale
            );

            ProcessBuilder pb1 = new ProcessBuilder("bash", "-c", postCmd);
            pb1.redirectErrorStream(true);
            Process p1 = pb1.start();

            String postOutput;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p1.getInputStream()))) {
                postOutput = reader.lines().collect(Collectors.joining("\n"));
            }
            p1.waitFor();
            p1.destroy();

            System.out.println("POST response: " + postOutput);

            // Parse event_id
            String eventId = postOutput.replaceAll(".*\"event_id\"\\s*:\\s*\"([^\"]+)\".*", "$1").trim();
            if (eventId.isEmpty() || eventId.equals(postOutput)) {
                return ResponseEntity.status(500).body("Failed to extract event_id from response: " + postOutput);
            }

            // 2️⃣ Step 2: GET result with event_id
            String getCmd = "curl -s -N https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/" + eventId;
            ProcessBuilder pb2 = new ProcessBuilder("bash", "-c", getCmd);
            pb2.redirectErrorStream(true);
            Process p2 = pb2.start();

            String getOutput;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p2.getInputStream()))) {
                getOutput = reader.lines().collect(Collectors.joining("\n"));
            }
            p2.waitFor();
            p2.destroy();

            System.out.println("GET response: " + getOutput);

            if (getOutput.isEmpty()) {
                return ResponseEntity.status(500).body("Error: Empty Hugging Face response");
            }

            return ResponseEntity.ok(getOutput);
        });
    }
}
