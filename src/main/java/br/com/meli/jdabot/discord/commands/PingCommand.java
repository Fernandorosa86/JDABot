package br.com.meli.jdabot.discord.commands;


import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

@Component
public class PingCommand implements SlashCommandHandler {

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("🏓 pong!").setEphemeral(true).queue();
    }
}
