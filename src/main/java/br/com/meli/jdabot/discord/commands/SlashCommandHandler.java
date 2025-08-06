package br.com.meli.jdabot.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface SlashCommandHandler {
    String getName();
    void execute(SlashCommandInteractionEvent event);
}
