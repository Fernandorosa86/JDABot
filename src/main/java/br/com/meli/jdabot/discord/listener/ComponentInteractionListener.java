package br.com.meli.jdabot.discord.listener;


import br.com.meli.jdabot.service.JokeService;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class ComponentInteractionListener extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if ("questionnaire_filters".equals(event.getComponentId())) {
            var values = String.join(", ", event.getValues());
            event.reply("Você selecionou os filtros: " + values).setEphemeral(true).queue();
        }
    }

    @Autowired
    private JokeService jokeService;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if ("joke_another".equals(event.getComponentId())) {
            event.deferEdit().queue();

            CompletableFuture.runAsync(() -> {
                try {
                    String joke = jokeService.getRandomJoke();
                    System.out.println("Vai editar mensagem no Discord: " + joke);
                    event.getHook().editOriginal(joke)
                            .setActionRow(Button.primary("joke_another", "Outra piada 😂"))
                            .queue(
                                    success -> System.out.println("Mensagem editada no Discord com sucesso."),
                                    error -> {
                                        System.err.println("Falha ao tentar editar mensagem no Discord:");
                                        error.printStackTrace();
                                    }
                            );
                } catch (Exception ex) {
                    System.err.println("Erro ao gerar ou editar piada:");
                    ex.printStackTrace();
                    event.getHook().editOriginal("Desculpe, erro ao buscar nova piada!").queue();
                }
            });
        }
    }
}
