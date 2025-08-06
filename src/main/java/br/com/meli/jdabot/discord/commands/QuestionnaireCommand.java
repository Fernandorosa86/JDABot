package br.com.meli.jdabot.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.springframework.stereotype.Component;

@Component
public class QuestionnaireCommand implements SlashCommandHandler{
    @Override
    public String getName() {
        return "questionario";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        event.reply("Escolha um ou mais filtros")
                .addActionRow(
                        StringSelectMenu.create("questionnaire_filters")
                                .setPlaceholder("Selecione filtros")
                                .setMinValues(1).setMaxValues(3)
                                .addOption("Pending", "pending", "Questionários pendentes 🕑")
                                .addOption("Done", "done", "Questionários finalizados ✅")
                                .addOption("Expired", "expired", "Questionários expirados ⏰")
                                .build()
                )
                .setEphemeral(true)
                .queue();
    }

}

