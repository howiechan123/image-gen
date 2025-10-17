package com.example.demo.Pictures;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StableDiffusionService {

    public ResponseEntity<?> generateImage(String prompt, int dimensions, int inference_steps, int guidance_scale) {
        try {
            System.out.println("Start HF call: prompt=" + prompt);
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

            if (!p.waitFor(10, java.util.concurrent.TimeUnit.SECONDS)) {
                p.destroy();
                System.out.println("HF call timeout: " + postCmd);
                return ResponseEntity.ok(Map.of("success", false, "message", "HF server not responding, try polling later"));
            }
            p.destroy();

            System.out.println("HF call output: " + output);

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

    public ResponseEntity<?> pollHF(String eventId) {
        try {
            System.out.println("Poll call: eventId=" + eventId);
            String getCmd = "curl -s --max-time 10 https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/" + eventId;
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", getCmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            String output;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                output = reader.lines().collect(Collectors.joining("\n"));
            }

            if (!p.waitFor(10, java.util.concurrent.TimeUnit.SECONDS)) {
                p.destroy();
                System.out.println("Poll timeout for eventId=" + eventId);
                return ResponseEntity.ok(Map.of("success", false, "message", "Still processing"));
            }
            p.destroy();

            System.out.println("Poll input: " + getCmd);
            System.out.println("Poll output: " + output);

            if (!output.contains("\"data\"")) {
                return ResponseEntity.ok(Map.of("success", false, "message", "Still processing"));
            }

            if (output.contains("base64")) {
                String base64 = output.replaceAll("(?s).*\"data\":\\[\"(.*?)\".*", "$1").trim();
                return ResponseEntity.ok(Map.of("success", true, "image", base64));
            }

            return ResponseEntity.ok(Map.of("success", false, "message", "Still processing"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
