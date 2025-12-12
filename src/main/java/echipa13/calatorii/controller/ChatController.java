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
            "cazare", "zbor", "hotel", "obiective turistice", "restaurant", "transport", "destinație", "călătorie"
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
                "temperature", 0.5,          // răspuns concis
                "max_output_tokens", 100,    // ~50 cuvinte
                "system_prompt", "Ești un ghid de călătorii profesionist. Răspunzi doar la întrebări legate de călătorii, cum ar fi cazare, zboruri, obiective turistice, restaurante sau transport. Vorbești în română și oferi răspunsuri scurte și concise. Dacă întrebarea nu ține de călătorii, răspunde strict: \"Îmi pare rău, pot răspunde doar la întrebări legate de călătorii.\""
        );

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(request, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(apiUrl, httpEntity, Map.class);
            String reply = response.get("text").toString();

            // Filtrare suplimentară: dacă nu conține cuvinte cheie, răspuns standard
            String finalReply = reply;
            boolean containsKeyword = travelKeywords.stream()
                    .anyMatch(keyword -> finalReply.toLowerCase().contains(keyword.toLowerCase()));

            if (!containsKeyword) {
                reply = "Îmi pare rău, pot răspunde doar la întrebări legate de călătorii.";
            }

            return Map.of("reply", reply);
        } catch (Exception e) {
            return Map.of("reply", "A apărut o eroare: " + e.getMessage());
        }
    }
}
