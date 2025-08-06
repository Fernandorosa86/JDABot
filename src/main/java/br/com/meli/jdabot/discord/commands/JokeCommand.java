package br.com.meli.jdabot.discord.commands;


import br.com.meli.jdabot.service.JokeService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Component;

@Component
public class JokeCommand implements  SlashCommandHandler{

    private final JokeService jokeService;

    public JokeCommand(JokeService jokeService) {
        this.jokeService = jokeService;
    }

    @Override
    public String getName() {
        return "piada";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        event.deferReply(true).queue(hook ->{
            String joke = jokeService.getRandomJoke();
            hook.editOriginal(joke)
                    .setActionRow(Button.primary("joke_another", "Outra piada 😂"))
                    .queue();
        });

    }
}
