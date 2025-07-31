package br.com.meli.jdabot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.text.Normalizer;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();


    private static final Map<String, String> stateFullNames = Map.ofEntries(
            Map.entry("AC", "Acre"),
            Map.entry("AL", "Alagoas"),
            Map.entry("AP", "Amapá"),
            Map.entry("AM", "Amazonas"),
            Map.entry("BA", "Bahia"),
            Map.entry("CE", "Ceará"),
            Map.entry("DF", "Distrito Federal"),
            Map.entry("ES", "Espírito Santo"),
            Map.entry("GO", "Goiás"),
            Map.entry("MA", "Maranhão"),
            Map.entry("MT", "Mato Grosso"),
            Map.entry("MS", "Mato Grosso do Sul"),
            Map.entry("MG", "Minas Gerais"),
            Map.entry("PA", "Pará"),
            Map.entry("PB", "Paraíba"),
            Map.entry("PR", "Paraná"),
            Map.entry("PE", "Pernambuco"),
            Map.entry("PI", "Piauí"),
            Map.entry("RJ", "Rio de Janeiro"),
            Map.entry("RN", "Rio Grande do Norte"),
            Map.entry("RS", "Rio Grande do Sul"),
            Map.entry("RO", "Rondônia"),
            Map.entry("RR", "Roraima"),
            Map.entry("SC", "Santa Catarina"),
            Map.entry("SP", "São Paulo"),
            Map.entry("SE", "Sergipe"),
            Map.entry("TO", "Tocantins")
    );

    private String removeAccents(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    public String getWeatherByZipCode(String zipCode) {
        try {

            String urlViaCep = "https://viacep.com.br/ws/" + zipCode + "/json/";
            String viaCepResponse = restTemplate.getForObject(urlViaCep, String.class);
            JsonNode viaCepNode = objectMapper.readTree(viaCepResponse);
            if (viaCepNode.has("erro") && viaCepNode.get("erro").asBoolean()) {
                return "❌ CEP inválido";
            }

            String city = viaCepNode.get("localidade").asText();
            String stateSigla = viaCepNode.get("uf").asText();
            String state = stateFullNames.getOrDefault(stateSigla, stateSigla);


            String[] tryQueries = {
                    city + ", " + state + ", Brazil",
                    city + ", " + state,
                    city + ", Brazil",
                    city,
                    removeAccents(city) + ", " + state + ", Brazil", // fallback sem acento
                    removeAccents(city) + ", " + state,
                    removeAccents(city) + ", Brazil",
                    removeAccents(city)
            };
            String latitude = null, longitude = null;
            for (String q : tryQueries) {
                String query = URLEncoder.encode(q, StandardCharsets.UTF_8);
                String urlNominatim = "https://nominatim.openstreetmap.org/search?format=json&limit=1&q=" + query;
                System.out.println("Tentando Nominatim: " + urlNominatim);
                String nominatimResponse = restTemplate.getForObject(urlNominatim, String.class);
                JsonNode nominatimArray = objectMapper.readTree(nominatimResponse);
                if (nominatimArray.isArray() && !nominatimArray.isEmpty()) {
                    latitude = nominatimArray.get(0).get("lat").asText();
                    longitude = nominatimArray.get(0).get("lon").asText();
                    break;
                }
                Thread.sleep(1000);
            }
            if (latitude == null || longitude == null) {
                return "❌ Não achei essa cidade na base de dados.";
            }


            String urlOpenMeteo = String.format(
                    "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&current_weather=true&lang=pt",
                    latitude, longitude);
            String openMeteoResponse = restTemplate.getForObject(urlOpenMeteo, String.class);
            JsonNode openMeteoNode = objectMapper.readTree(openMeteoResponse);

            JsonNode current = openMeteoNode.path("current_weather");
            if (current.isMissingNode()) {
                return "❌ Não consegui obter o clima desta localidade.";
            }

            double temperature = current.get("temperature").asDouble();
            double windspeed = current.get("windspeed").asDouble();
            String weatherCode = current.get("weathercode").asText();

            String weatherDescription = getWeatherDescription(weatherCode);

            return String.format(
                    "🌤️ Tempo em %s/%s\nAgora: %.1f°C, vento: %.0f km/h\nCondição: %s",
                    city, stateSigla, temperature, windspeed, weatherDescription);

        } catch (Exception e) {
            e.printStackTrace();
            return "⚠ Erro ao consultar o clima. Tente novamente ou verifique o CEP.";
        }
    }

    private String getWeatherDescription(String code) {
        switch (code) {
            case "0": return "Céu limpo";
            case "1":
            case "2":
            case "3": return "Parcialmente nublado";
            case "45":
            case "48": return "Névoa";
            case "51":
            case "53":
            case "55": return "Garoa";
            case "61":
            case "63":
            case "65": return "Chuva";
            case "71":
            case "73":
            case "75": return "Neve";
            case "80":
            case "81":
            case "82": return "Pancadas de chuva";
            case "95": return "Trovoada";
            case "96":
            case "99": return "Trovoada com granizo";
            default: return "Desconhecido";
        }
    }
}