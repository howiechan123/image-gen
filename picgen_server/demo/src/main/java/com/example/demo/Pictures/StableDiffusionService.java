package com.example.demo.Pictures;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

@Service
public class StableDiffusionService {

    public Mono<ResponseEntity<?>> generateImage(String prompt, int dimensions, int inference_steps, int guidance_scale) {
        System.out.println("Start HF POST call");

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

            System.out.println("POST response: " + postOutput);

            String eventId = postOutput.replaceAll(".*\"event_id\"\\s*:\\s*\"([^\"]+)\".*", "$1").trim();
            if (eventId.isEmpty() || eventId.equals(postOutput)) {
                return ResponseEntity.status(500).body("Failed to extract event_id from response: " + postOutput);
            }

            return ResponseEntity.ok("{\"event_id\": \"" + eventId + "\"}");
        });
    }

    public Mono<ResponseEntity<?>> pollHF(String eventId) {
        System.out.println("Polling HF for event_id: " + eventId);

        return Mono.fromCallable(() -> {
            String getOutput = "";
            int maxAttempts = 600;
            int delayMs = 10000;

            for (int i = 0; i < maxAttempts; i++) {
                String getCmd = "curl -s https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/" + eventId;
                ProcessBuilder pb = new ProcessBuilder("bash", "-c", getCmd);
                pb.redirectErrorStream(true);
                Process p = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                    getOutput = reader.lines().collect(Collectors.joining("\n"));
                }
                p.waitFor();
                p.destroy();

                if (getOutput.contains("\"data\"") && getOutput.contains("base64")) {
                    break;
                }

                Thread.sleep(delayMs);
            }

            System.out.println("Final GET response: " + getOutput);

            String jsonArray = getOutput.replaceAll("(?s).*\\\"data\\\"\\s*:\\s*(\\[.*?\\])\\s*}.*", "$1").trim();
            if (jsonArray.isEmpty() || jsonArray.equals(getOutput)) {
                return ResponseEntity.status(500)
                        .body("Error: Could not extract image data from Hugging Face response. Response: " + getOutput);
            }

            String simplifiedJson = jsonArray
                    .replaceAll("^\\[\\s*\\{", "{")
                    .replaceAll("}\\s*]$", "}")
                    .replace("\"prompt params\":", "\"prompt_params\":");

            simplifiedJson = simplifiedJson
                    .replaceAll("(?s)\\\"prompt_params\\\"\\s*:\\s*\\{[^}]*\\\"prompt\\\"\\s*:\\s*\\\"([^\\\"]+)\\\"[^}]*}", "\"prompt\":\"$1\"");

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(simplifiedJson);
        });
    }
}
