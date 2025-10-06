package com.example.demo.Pictures;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class ImgBBService {

    @Value("${imgbb.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ResponseEntity<?> uploadImage(String base64Image) {
        if (base64Image == null || base64Image.isEmpty()) {
            throw new IllegalArgumentException("Base64 image is empty");
        }

        if (base64Image.startsWith("data:image")) {
            base64Image = base64Image.substring(base64Image.indexOf(",") + 1);
        }

        String url = "https://api.imgbb.com/1/upload?key=" + apiKey;

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("image", base64Image);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                String imageUrl = (String) data.get("url");
                String deleteUrl = (String) data.get("delete_url");

                // Generate a default file name from the URL or timestamp
                String fileName = "img_" + System.currentTimeMillis();
                imgBBDTO dto = new imgBBDTO(fileName, imageUrl, deleteUrl);

                return ResponseEntity.ok(new imgBBResponse(dto, "Image uploaded successfully", true));
            }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new imgBBResponse(null, "ImgBB upload failed: " + response.getStatusCode(), false));
    }

    public record imgBBResponse(imgBBDTO dto, String message, boolean success) { }

    public record imgBBDTO(String fileName, String filePath, String deleteUrl) { }
}