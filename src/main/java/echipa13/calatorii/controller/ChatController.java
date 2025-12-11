
package echipa13.calatorii.controller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*") // doar pentru test, în producție setează domeniul tău
public class ChatController {

    @Value("${openai.api.key}") // cheia ta din environment
    private String openaiApiKey;

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("https://api.openai.com/v1/chat/completions"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "Ești un expert prietenos de călătorii care răspunde clar și concis."));
        messages.add(Map.of("role", "user", "content", userMessage));

        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        Map<String, Object> response = restTemplate.postForObject("", request, Map.class);

        // Extragem textul răspunsului
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String reply = (String) message.get("content");

        return Map.of("reply", reply);
    }
}
