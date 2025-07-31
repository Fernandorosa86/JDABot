package br.com.meli.jdabot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getWeatherByZipCode(String zipCode) {
        try {
            String urlViacep = "https://viacep.com.br/ws/" + zipCode + "/json/";
            String viacepResp = restTemplate.getForObject(urlViacep, String.class);
            JsonNode viacepNode = objectMapper.readTree(viacepResp);
            if (viacepNode.has("erro") && viacepNode.get("erro").asBoolean()) {
                return "❌ CEP inválido";
            }

            String cidade = viacepNode.get("localidade").asText().toLowerCase();
            String uf = viacepNode.get("uf").asText().toUpperCase();

            String urlCidades = "https://servicos.cptec.inpe.br/api/v1/cidade?city=" + cidade;
            String respCidades = restTemplate.getForObject(urlCidades, String.class);
            JsonNode arrayCidades = objectMapper.readTree(respCidades);

            JsonNode cidadeEscolhida = null;
            for (JsonNode item : arrayCidades) {
                if (item.get("nome").asText().toLowerCase().equals(cidade)
                        && item.get("estado").asText().toUpperCase().startsWith(uf)) {
                    cidadeEscolhida = item;
                    break;
                }
            }
            if (cidadeEscolhida == null) {
                return "❌ Não encontrei essa Cidade";
            }
            int codigoCidade = cidadeEscolhida.get("id").asInt();

            String urlPrevisao = "https://servicos.cptec.inpe.br/api/v1/clima/previsao/" + codigoCidade + ".json";
            String respPrevisao = restTemplate.getForObject(urlPrevisao, String.class);
            JsonNode previsaoNode = objectMapper.readTree(respPrevisao);

            JsonNode climaHoje = previsaoNode.path("clima").get(0);
            String resumo = climaHoje.get("resumo").asText();
            double temp_min = climaHoje.get("min").asDouble();
            double temp_max = climaHoje.get("max").asDouble();

            return String.format("🌤️ %s/%s\nHoje: %s\nMín: %.1f°C | Máx: %.1f°C",
                    cidadeEscolhida.get("nome").asText(),
                    cidadeEscolhida.get("estado").asText(),
                    resumo, temp_min, temp_max
            );
        } catch (Exception e) {
            e.printStackTrace();
            return "⚠ Erro ao consultar o Clima. Tente novamente ou verifique o CEP.";
        }
    }
}

