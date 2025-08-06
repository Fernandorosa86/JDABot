package br.com.meli.jdabot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JokeService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getRandomJoke() {

        try {
            String url = "https://api.chucknorris.io/jokes/random";
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("API response: " + response);

            if (response.trim().startsWith("{")) {
                JsonNode jsonNode = objectMapper.readTree(response);
                return jsonNode.get("Value").asText();
            } else {
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Ops, estou sem inspiração agora 😅";
        }
    }
}
