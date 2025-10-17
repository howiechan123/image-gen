package com.example.demo.Pictures;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;
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

            if (!p.waitFor(10, TimeUnit.SECONDS)) {
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
            String getCmd = "curl -s --no-buffer -H 'Content-Type: application/json' " +
                    "-H 'User-Agent: PostmanRuntime/7.32.3' " +
                    "https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/" + eventId;

            ProcessBuilder pb = new ProcessBuilder("bash", "-c", getCmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            long startTime = System.currentTimeMillis();
            String line;
            while ((System.currentTimeMillis() - startTime) < 10_000 && (line = reader.readLine()) != null) {
                System.out.println("SSE line: " + line);

                if (line.startsWith("event: complete")) {
                    String dataLine = reader.readLine();
                    if (dataLine != null && dataLine.startsWith("data:")) {
                        String jsonData = dataLine.replaceFirst("data: ", "").trim();
                        String base64 = jsonData.replaceAll("(?s).*\"image\"\\s*:\\s*\"(.*?)\".*", "$1");
                        p.destroy();
                        return ResponseEntity.ok(Map.of("success", true, "image", base64));
                    }
                }
            }

            // Timeout reached, close connection
            p.destroy();
            System.out.println("Poll timeout for eventId=" + eventId);
            return ResponseEntity.ok(Map.of("success", false, "message", "Still processing"));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

}
