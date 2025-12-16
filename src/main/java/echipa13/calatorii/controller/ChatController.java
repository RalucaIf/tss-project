package echipa13.calatorii.controller;

import echipa13.calatorii.Dto.PointsDto;
import echipa13.calatorii.models.UserEntity;
import echipa13.calatorii.models.UserPoints;
import echipa13.calatorii.repository.UserPointsRepository;
import echipa13.calatorii.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.Transient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/api/chat")
@Transient
@CrossOrigin(origins = "*")
public class ChatController {

    private final UserRepository userRepository;
    private final UserPointsRepository userPointsRepository;

    @Value("${cohere.api.key}")
    private String apiKey;

    @Value("${cohere.api.url}")
    private String apiUrl;

    private final List<String> travelKeywords = List.of(
            "cazare", "zbor", "hotel", "obiective turistice",
            "restaurant", "transport", "destinație", "călătorie"
    );

    @Autowired
    public ChatController(UserRepository userRepository,
                          UserPointsRepository userPointsRepository) {
        this.userRepository = userRepository;
        this.userPointsRepository = userPointsRepository;
    }

    @PostMapping
    @Transactional
    public Map<String, Object> chat(@RequestBody Map<String, String> payload,
                                    Authentication auth) {

        String userMessage = payload.get("message");
        String reply;

        // =======================
        // 1️⃣ COHERE
        // =======================
        try {
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
                    "Răspunzi scurt, ca pe WhatsApp. DOAR la întrebări despre călătorii. " +
                            "Dacă nu e despre călătorii, răspunzi: «Îmi pare rău, pot răspunde doar la întrebări legate de călătorii.»"
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            Map<String, Object> response =
                    restTemplate.postForObject(apiUrl, entity, Map.class);

            reply = response.getOrDefault("text", "").toString();

            final String finalReply = reply;
            boolean ok = travelKeywords.stream()
                    .anyMatch(k -> finalReply.toLowerCase().contains(k));

            if (!ok) {
                reply = "Îmi pare rău, pot răspunde doar la întrebări legate de călătorii.";
            }

        } catch (Exception e) {
            reply = "A apărut o eroare 😕";
        }

        // =======================
        // 2️⃣ BONUS +5 (O SINGURĂ DATĂ)
        // =======================
        Integer newPoints = null;
        Integer level = null;
        boolean bonusReceivedNow = false;

        if (auth != null && auth.isAuthenticated()
                && !"anonymousUser".equals(auth.getPrincipal())) {

            String username = auth.getName();
            UserEntity user = userRepository.findByEmail(username);
            if (user == null) user = userRepository.findByUsername(username);

            if (user != null) {

                UserPoints userPoints = userPointsRepository
                        .findByUser_Id(user.getId())
                        .orElseThrow(); // există deja din register

                // 🔥 AICI e magia
                if (!userPoints.isChatBonusReceived()) {
                    userPoints.setPoints(userPoints.getPoints() + 5);
                    userPoints.setChatBonusReceived(true);

                    userPointsRepository.saveAndFlush(userPoints); // 🔥 IMPORTANT

                    bonusReceivedNow = true;
                }

                newPoints = userPoints.getPoints();
                level = userPoints.getLevel();

            }
        }

        // =======================
        // 3️⃣ RESPONSE
        // =======================
        Map<String, Object> result = new HashMap<>();
        result.put("reply", reply);
        result.put("newPoints", newPoints);
        result.put("level", level);
        result.put("bonusReceivedNow", bonusReceivedNow);

        return result;

    }
}
