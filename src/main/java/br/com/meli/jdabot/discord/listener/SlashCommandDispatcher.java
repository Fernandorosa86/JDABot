package br.com.meli.jdabot.discord.listener;


import br.com.meli.jdabot.discord.commands.SlashCommandHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SlashCommandDispatcher extends ListenerAdapter {

    private final Map<String, SlashCommandHandler> handlers;

    public SlashCommandDispatcher(List<SlashCommandHandler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(SlashCommandHandler::getName, h -> h));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandHandler handler = handlers.get(event.getName());
        if (handler != null) {
            handler.execute(event);
        } else {
            event.reply("Comando não implementado! Volte mais tarde!").setEphemeral(true).queue();
        }
    }
}
