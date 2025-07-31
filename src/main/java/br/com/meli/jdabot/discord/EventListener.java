package br.com.meli.jdabot.discord;

import br.com.meli.jdabot.service.WeatherService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
public class EventListener extends ListenerAdapter {

    private final WeatherService weatherService;

    public EventListener(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        String message = event.getMessage().getContentRaw().toLowerCase();

        if (message.startsWith("!ping")) {
            event.getChannel().sendMessage("🏓 pong!").queue();
        } else if (message.matches(".*\\b(oi|olá|ola|opa|eai|e aí|eae|salve)\\b.*")) {
            String greeting = generateGreeting();
            event.getChannel().sendMessage("Olá " + event.getAuthor().getAsMention() + "! " + greeting).queue();
        } else if (message.startsWith("!clima")) {
            String[] parts = message.split(" ");
            if (parts.length < 2) {
                event.getChannel().sendMessage("Por favor, envie: `!clima <cep>`").queue();
            } else {
                String zipCode = parts[1].replaceAll("\\D", "");
                String response = weatherService.getWeatherByZipCode(zipCode);
                event.getChannel().sendMessage(response).queue();
            }
        }
    }

    private String generateGreeting() {
        int hour = java.time.LocalTime.now().getHour();

        if (hour >= 6 && hour < 12) {
            return "Bom dia! ☀️";
        } else if (hour >= 12 && hour < 18) {
            return "Boa tarde! 🌞";
        } else {
            return "Boa noite! 🌙";
        }
    }
}