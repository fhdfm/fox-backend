package br.com.foxconcursos.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.foxconcursos.dto.VimeoRequest;
import br.com.foxconcursos.services.AulaService;

@RestController
@RequestMapping(value = "/api/media", consumes = "application/json", produces = "application/json")
public class MediaController {

    @Value("${integracao.vimeo.access-token}")
    private String accessToken;

    @Value("${integracao.vimeo.endpoint}")
    private String vimeoUrl;

    private final AulaService aulaService;

    public MediaController(AulaService aulaService) {
        this.aulaService = aulaService;
    }

    @PostMapping
    public ResponseEntity<?> retrieveAuth(@RequestBody long fileSize) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Criando o corpo da requisição
        Map<String, Object> uploadMap = new HashMap<>();
        uploadMap.put("approach", "tus");
        uploadMap.put("size", fileSize);

        Map<String, Object> body = new HashMap<>();
        body.put("upload", uploadMap);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(vimeoUrl, request, String.class);

        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode root = mapper.readTree(response.getBody());
            String uploadLink = root.get("upload").get("upload_link").asText();
            String uri = root.get("uri").asText(); // Pegando o URI do vídeo

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("upload_link", uploadLink);
            responseBody.put("uri", uri);

            return ResponseEntity.status(response.getStatusCode()).body(responseBody);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(value = "/salvar/{aulaId}")
    public ResponseEntity<?> salvarIframe(
            @PathVariable UUID aulaId,
            @RequestBody VimeoRequest vimeo
    ) throws Exception {
            return ResponseEntity.ok(aulaService.criarConteudoVimeo(aulaId, vimeo));
    }

    @GetMapping("/video/{videoUri}")
    public ResponseEntity<?> getVideoUrl(@PathVariable String videoUri) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(this.accessToken);
        headers.set("Accept", "application/vnd.vimeo.*+json;version=3.4"); // Apenas Accept, sem Content-Type

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        String videoEndpoint = vimeoUrl + videoUri; // Certifique-se de que vimeoUrl termina com "/videos/"

        try {
            ResponseEntity<String> response = restTemplate.exchange(videoEndpoint, HttpMethod.GET, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());

                String playerUrl = root.path("player_embed_url").asText(); // Obtendo a URL do player

                Map<String, String> responseBody = new HashMap<>();
                responseBody.put("player_url", playerUrl);

                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Erro ao buscar o vídeo no Vimeo.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar o vídeo: " + e.getMessage());
        }
    }




}
