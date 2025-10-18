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

    // Store session cookie from generateImage to reuse in pollHF
    private String sessionCookie = null;

    public ResponseEntity<?> generateImage(String prompt, int dimensions, int inference_steps, int guidance_scale) {
        try {
            System.out.println("Start HF call: prompt=" + prompt);
            String postCmd = String.format(
                "curl -s -i -X POST https://sdserver123-sdserver123.hf.space/gradio_api/call/predict " +
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

            // Log full response
            System.out.println("========== FULL HF RAW RESPONSE ==========");
            System.out.println(output);
            System.out.println("==========================================");

            // Extract event_id
            String eventId = output.replaceAll(".*\"event_id\"\\s*:\\s*\"([^\"]+)\".*", "$1").trim();
            if (eventId.isEmpty() || eventId.equals(output)) {
                System.out.println("Failed to extract event_id from output");
                return ResponseEntity.status(500).body(Map.of("success", false, "message", "Failed to extract event_id"));
            }
            System.out.println("Extracted event_id: " + eventId);

            // Capture session cookie if present
            String[] lines = output.split("\n");
            for (String line : lines) {
                if (line.toLowerCase().startsWith("set-cookie:")) {
                    System.out.println("Cookie from HF response: " + line);
                    // Save first session cookie for reuse
                    if (sessionCookie == null) {
                        sessionCookie = line.split(";", 2)[0]; // e.g., session_id=xxxx
                    }
                }
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

            String cookieHeader = sessionCookie != null ? "-H 'Cookie: " + sessionCookie + "'" : "";

            String getCmd = "curl -s --no-buffer -H 'Content-Type: application/json' " +
                            "-H 'User-Agent: PostmanRuntime/7.32.3' " +
                            cookieHeader + " " +
                            "https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/" + eventId;

            ProcessBuilder pb = new ProcessBuilder("bash", "-c", getCmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            long startTime = System.currentTimeMillis();
            String line;
            StringBuilder sseData = new StringBuilder();

            while ((System.currentTimeMillis() - startTime) < 10_000 && (line = reader.readLine()) != null) {
                System.out.println("SSE line: " + line); // your original sysout
                sseData.append(line).append("\n");

                if (line.startsWith("data:")) {
                    String dataLine = line.replaceFirst("data:", "").trim();
                    System.out.println("SSE data event: " + dataLine); // log actual SSE output
                }

                if (line.startsWith("event: complete")) {
                    String dataLine = reader.readLine();
                    if (dataLine != null && dataLine.startsWith("data:")) {
                        String jsonData = dataLine.replaceFirst("data:", "").trim();
                        String base64 = jsonData.replaceAll("(?s).*\"image\"\\s*:\\s*\"(.*?)\".*", "$1");
                        p.destroy();

                        System.out.println("Poll returning success. EventId=" + eventId);
                        System.out.println("Full SSE data collected:\n" + sseData);

                        return ResponseEntity.ok(Map.of(
                                "success", true,
                                "eventId", eventId,
                                "sseData", sseData.toString(),
                                "image", base64
                        ));
                    }
                }
            }

            p.destroy();
            System.out.println("Poll timeout for eventId=" + eventId);
            System.out.println("Returning still processing. Full SSE data collected:\n" + sseData);

            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "eventId", eventId,
                    "message", "Still processing",
                    "sseData", sseData.toString()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "eventId", eventId,
                    "message", e.getMessage()
            ));
        }
    }
}
