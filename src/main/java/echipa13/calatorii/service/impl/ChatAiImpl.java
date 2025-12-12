package echipa13.calatorii.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ChatAiImpl {

    private final WebClient webClient;

    @Value("${cohere.api.key}")
    private String apiKey;

    @Value("${cohere.api.url}")
    private String apiUrl;

    // Lista de cuvinte cheie pentru călătorii
    private final List<String> travelKeywords = Arrays.asList(
            "cazare", "zbor", "hotel", "obiective turistice", "restaurant", "transport", "destinație", "călătorie"
    );

    public ChatAiImpl(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public Mono<String> sendMessage(String userMessage) {

        Map<String, Object> requestBody = Map.of(
                "model", "command-a-03-2025",
                "message", userMessage,
                "temperature", 0.5,
                "max_output_tokens", 100,
                "system_prompt", "Ești un ghid de călătorii profesionist. Vorbești în română și oferi răspunsuri scurte și concise despre cazare, zboruri, obiective turistice, restaurante și transport. Dacă întrebarea nu ține de călătorii, răspunde strict: \"Îmi pare rău, pot răspunde doar la întrebări legate de călătorii.\""
        );

        return webClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Cohere-Version", "2022-12-06")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    String reply = response.get("text") != null ? response.get("text").toString() : "";
                    String finalReply = reply;
                    boolean containsKeyword = travelKeywords.stream()
                            .anyMatch(keyword -> finalReply.toLowerCase().contains(keyword.toLowerCase()));
                    if (!containsKeyword) {
                        reply = "Îmi pare rău, pot răspunde doar la întrebări legate de călătorii.";
                    }
                    return reply;
                });
    }
}
