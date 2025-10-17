package com.example.demo.Pictures;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StableDiffusionService {

    // Start HF generation and return event_id
    public ResponseEntity<?> generateImage(String prompt, int dimensions, int inference_steps, int guidance_scale) {
        try {
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

            String output;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                output = reader.lines().collect(Collectors.joining("\n"));
            }
            p.waitFor();
            p.destroy();

            String eventId = output.replaceAll(".*\"event_id\"\\s*:\\s*\"([^\"]+)\".*", "$1").trim();
            if (eventId.isEmpty() || eventId.equals(output)) {
                return ResponseEntity.status(500)
                        .body(Map.of("success", false, "message", "Failed to extract event_id"));
            }

            return ResponseEntity.ok(Map.of("success", true, "event_id", eventId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // Poll HF for results using event_id
    public ResponseEntity<?> pollHF(String eventId) {
        try {
            String getCmd = "curl -s https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/" + eventId;
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", getCmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            String output;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                output = reader.lines().collect(Collectors.joining("\n"));
            }
            p.waitFor();
            p.destroy();

            if (!output.contains("\"data\"")) {
                return ResponseEntity.ok(Map.of("success", false, "message", "Still processing"));
            }

            if (output.contains("base64")) {
                String base64 = output.replaceAll("(?s).*\"data\":\\[\"(.*?)\".*", "$1").trim();
                return ResponseEntity.ok(Map.of("success", true, "image", base64));
            }

            return ResponseEntity.ok(Map.of("success", false, "message", "Not ready yet"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
