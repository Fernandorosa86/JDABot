package br.com.meli.jdabot.discord.commands;


import br.com.meli.jdabot.service.WeatherService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class WeatherCommand implements SlashCommandHandler {

    private final WeatherService weatherService;

    public WeatherCommand(WeatherService weatherService) {
        this.weatherService = weatherService;
    }


    @Override
    public String getName() {
        return "clima";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String cep = event.getOption("cep").getAsString();
        event.deferReply(true).queue(hook ->
                CompletableFuture.runAsync(() -> {
                    String response = weatherService.getWeatherByZipCode(cep);
                    hook.editOriginal(response).queue();
                })
        );
    }
}
