package br.com.meli.jdabot.discord.listener;


import br.com.meli.jdabot.service.QuestionnaireService;
import br.com.meli.jdabot.service.WeatherService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class CommandListener extends ListenerAdapter {

    private final WeatherService weatherService;
    private final QuestionnaireService questionnaireService;

    public CommandListener(WeatherService weatherService, QuestionnaireService questionnaireService) {
        this.weatherService = weatherService;
        this.questionnaireService = questionnaireService;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        switch(event.getName()) {
            case "ping" -> event.reply("🏓 pong!").setEphemeral(true).queue();
            case "clima" -> {
                String cep = event.getOption("cep").getAsString();
                String response = weatherService.getWeatherByZipCode(cep);
                event.reply(response).setEphemeral(true).queue();
            }
            case "questionario" -> {
                event.reply("Selecione um ou mais filtros:")
                        .addActionRow(
                                StringSelectMenu.create("questionario_filters")
                                        .setPlaceholder("Selecione filtros...")
                                        .setMinValues(1).setMaxValues(3)
                                        .addOption("Pending", "pending", "Questionários pendentes 🕑")
                                        .addOption("Done", "done", "Questionários finalizados ✅")
                                        .addOption("Expired", "expired", "Questionários expirados ⏰")
                                        .build())
                        .setEphemeral(true)
                        .queue();

            }
        }
    }
}
