package br.com.meli.jdabot.discord;

import br.com.meli.jdabot.service.WeatherService;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.Normalizer;

@Component
public class EventListener extends ListenerAdapter {

    private final WeatherService weatherService;

    public EventListener(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    public static String removeAccents(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        String rawMessage = event.getMessage().getContentRaw();
        String message = removeAccents(rawMessage);



        if (message.startsWith("!ping")) {
            event.getChannel().sendMessage("🏓 pong!").queue();
        } else {
            Pattern p = Pattern.compile(".*\\b(oi|ola|opa|eai|e ai|eae|salve)\\b",
                    Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(message);
            if (m.find()) {
                String userWord = m.group();
                event.getChannel().sendMessage(userWord + " " + event.getAuthor().getAsMention() + " " + generateGreeting()).queue();
                return;
            }

            if (message.startsWith("!clima")) {
                String[] parts = message.split(" ");
                if (parts.length < 2) {
                    event.getChannel().sendMessage("Por favor, envie: !clima <cep> \nEx: !clima 00000000").queue();
                } else {
                    event.getChannel().sendMessage(":hourglass_flowing_sand: Consultando Previsão...").queue(msg -> {
                        String zipCode = parts[1].replaceAll("\\D", "");
                        String response = weatherService.getWeatherByZipCode(zipCode);

                        msg.editMessage(response).queue();
                    });
                }
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