package br.com.meli.jdabot.discord;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class BotStartUpService {

    @Value("${discord.token}")
    private String token;
    private JDA jda;

    private final EventListener eventListener;

    public BotStartUpService(EventListener eventListener) {
        this.eventListener = eventListener;
    }

    @PostConstruct
    public void start() throws Exception {
        jda = JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(eventListener)
                .build();
    }


}
