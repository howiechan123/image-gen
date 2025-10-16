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
        System.out.println("Start HF cURL call");

        // cURL pipeline identical to Hugging Face example
        String cmd = String.format(
                "curl -s -X POST https://sdserver123-sdserver123.hf.space/gradio_api/call/predict " +
                "-H 'Content-Type: application/json' " +
                "-d '{\"data\": [\"%s\", %d, %d, %d]}' | " +
                "awk -F'\"' '{ print $4 }' | " +
                "read EVENT_ID; " +
                "curl -s -N https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/$EVENT_ID",
                prompt.replace("\"", "\\\""),
                dimensions,
                inference_steps,
                guidance_scale
        );

        return Mono.fromCallable(() -> {
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String output = reader.lines().collect(Collectors.joining("\n"));
                int exitCode = process.waitFor();
                System.out.println("HF process exited with code: " + exitCode);
                System.out.println("HF output: " + output);

                if (exitCode != 0 || output.isEmpty()) {
                    return ResponseEntity.status(500).body("Error: Empty or failed Hugging Face response");
                }
                return ResponseEntity.ok(output);
            } finally {
                process.destroy();
            }
        });
    }
}
