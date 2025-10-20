package com.example.demo.Pictures;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.HFQueue.QueueService;

@Service
public class StableDiffusionService {

    private final QueueService queueService;

    public StableDiffusionService(QueueService queueService) {
        this.queueService = queueService;
    }

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

            p.waitFor();
            p.destroy();

            System.out.println("========== FULL HF RAW RESPONSE ==========");
            System.out.println(output);
            System.out.println("==========================================");

            String eventId = null;
            for (String line : output.split("\n")) {
                if (line.contains("\"event_id\"")) {
                    eventId = line.replaceAll(".*\"event_id\"\\s*:\\s*\"([^\"]+)\".*", "$1").trim();
                    break;
                }
            }

            if (eventId == null || eventId.isEmpty()) {
                System.out.println("Failed to extract event_id from JSON body");
                return ResponseEntity.status(500).body(Map.of(
                        "success", false,
                        "message", "Failed to extract event_id"
                ));
            }

            int queueLength = queueService.incrementQueue();
            System.out.println("Queue incremented. Current queue count: " + queueLength);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "event_id", eventId,
                    "number_of_processes", queueLength
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }


    public ResponseEntity<?> pollHF(String eventId) {
        Process p = null;
        try {
            System.out.println("Poll call: eventId=" + eventId);

            String getCmd = "curl -s --no-buffer -H 'Content-Type: application/json' " +
                    "-H 'User-Agent: PostmanRuntime/7.32.3' " +
                    "https://sdserver123-sdserver123.hf.space/gradio_api/call/predict/" + eventId;

            ProcessBuilder pb = new ProcessBuilder("bash", "-c", getCmd);
            pb.redirectErrorStream(true);
            p = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            StringBuilder sseData = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                sseData.append(line).append("\n");

                if (line.startsWith("data:")) {
                    String dataLine = line.replaceFirst("data:", "").trim();
                    System.out.println("SSE data event: " + dataLine);
                }

                if (line.startsWith("event: complete")) {
                    String dataLine = reader.readLine();
                    if (dataLine != null && dataLine.startsWith("data:")) {
                        String jsonData = dataLine.replaceFirst("data:", "").trim();
                        String base64 = jsonData.replaceAll("(?s).*\"image\"\\s*:\\s*\"(.*?)\".*", "$1");

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

            System.out.println("Poll ended before receiving complete event for eventId=" + eventId);
            System.out.println("Returning still processing. Full SSE data collected:\n" + sseData);

            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "eventId", eventId,
                    "message", "Stream closed before completion",
                    "sseData", sseData.toString()
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "eventId", eventId,
                    "message", e.getMessage()
            ));
        } finally {
            if (p != null) p.destroy();
            int q = queueService.decrementQueue();
            System.out.println("Decrement queue: count: " + q);
        }
    }
}
