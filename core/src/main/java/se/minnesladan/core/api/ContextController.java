package se.minnesladan.core.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ContextController {

    @GetMapping("/context")
    public ResponseEntity<Map<String, String>> getContext(@RequestParam("question") String question) {
        Map<String, String> responseBody = Map.of(
                "question", question,
                "context", "No context available yet"
        );

        return ResponseEntity.ok(responseBody);
    }
}
