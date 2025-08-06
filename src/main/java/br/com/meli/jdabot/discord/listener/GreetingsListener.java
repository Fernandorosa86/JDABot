package br.com.meli.jdabot.discord.listener;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.regex.Pattern;

import static br.com.meli.jdabot.util.AccentUtils.removeAccents;

@Component
public class GreetingsListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String originalMessage = event.getMessage().getContentRaw();
        String noAccentsMessage = removeAccents(originalMessage.toLowerCase());

        Pattern pattern = Pattern.compile("\\b(oi|ola|opa|eai|e\\sai|eae|salve)\\b", Pattern.CASE_INSENSITIVE);

        if (pattern.matcher(noAccentsMessage).find()) {
            String greetings = generateGreetings();
            event.getChannel().sendMessage(originalMessage + " " + event.getAuthor().getAsMention() + " " + greetings).queue();
        }
    }

    private String generateGreetings() {
        int hora = LocalTime.now().getHour();
        if (hora >= 6 && hora < 12) return "Bom dia! ☀️";
        if (hora >= 12 && hora < 18) return "Boa tarde! 🌞";
        return "Boa noite! 🌙";
    }

}
