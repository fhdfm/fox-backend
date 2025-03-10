package br.com.foxconcursos.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping(value = "/media", consumes = "application/json", produces = "application/json")
public class MediaController {
    
    private final String accesToken = "";

    @PostMapping
    public ResponseEntity<?> retrieveAuth(@RequestBody long fileSize) {
        RestTemplate restTemplate = new RestTemplate();
        String vimeoUrl = "https://api.vimeo.com/me/videos";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.accesToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> uploadMap = new HashMap<>();
        uploadMap.put("approach", "tus");
        uploadMap.put("size", fileSize); // mudar pra o tamanho real

        Map<String, Object> body = new HashMap<>();
        body.put("upload", uploadMap);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(vimeoUrl, request, String.class);

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(response.getBody());
            String uploadLink = root.get("upload").get("upload_link").asText();
            return ResponseEntity.status(response.getStatusCode()).body(uploadLink);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

}
