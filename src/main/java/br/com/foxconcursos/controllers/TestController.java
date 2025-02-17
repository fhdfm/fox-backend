package br.com.foxconcursos.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
public class TestController {

    @PostMapping(value = "/mp")
    public ResponseEntity<String> teste(@RequestBody Map<String, Object> json) throws Exception {

        printJson(json, "");

        return ResponseEntity.ok("JSON recebido.");

    }

    private void printJson(Map<String, Object> json, String indent) {
        json.forEach((key, value) -> {
            if (value instanceof Map) {
                printJson((Map<String, Object>) value, indent + "  ");
            } else {
                System.out.println(indent + key + ": " + value);
            }
        });
    }
}
