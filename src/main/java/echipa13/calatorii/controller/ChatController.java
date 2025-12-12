package echipa13.calatorii.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Value("${cohere.api.key}")
    private String apiKey;

    @Value("${cohere.api.url}")
    private String apiUrl;

    // Lista de cuvinte cheie pentru călătorii
    private final List<String> travelKeywords = Arrays.asList(
            "cazare", "zbor", "hotel", "obiective turistice",
            "restaurant", "transport", "destinație", "călătorie"
    );

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        headers.set("Cohere-Version", "2022-12-06");

        Map<String, Object> request = Map.of(
                "model", "command-a-03-2025",
                "message", userMessage,
                "temperature", 0.1,
                "max_output_tokens", 20,
                "system_prompt",
                "Răspunzi scurt vreau sa FII FOARTE CONCIS, direct, ca pe WhatsApp. DOAR la întrebări despre călătorii. "
                        + "Dacă nu e despre călătorii, răspunzi: «Îmi pare rău, pot răspunde doar la întrebări legate de călătorii.»"
        );

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(request, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(apiUrl, httpEntity, Map.class);
            String reply = response.getOrDefault("text", "").toString();

            // Verificăm cuvintele-cheie
            String finalReply = reply;
            boolean containsKeyword = travelKeywords.stream()
                    .anyMatch(k -> finalReply.toLowerCase().contains(k.toLowerCase()));
            if (!containsKeyword) {
                reply = "Îmi pare rău, pot răspunde doar la întrebări legate de călătorii.";
            }

            return Map.of("reply", reply);
        } catch (Exception e) {
            return Map.of("reply", "A apărut o eroare: " + e.getMessage());
        }
    }
}
