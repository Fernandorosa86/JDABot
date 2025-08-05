package br.com.meli.jdabot.discord.listener;


import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ComponentInteractionListener extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if(event.getComponentId().equals("questionario_filters")) {
            List<String> filters = event.getValues();

            //nesta parte, acionar um service que buscará os resultados [QuestionnaireService].

            event.reply("Você escolheu os filtros: " + String.join(", ", filters))
                    .setEphemeral(true)
                    .queue();

            event.getHook().sendMessage("Clique em um filtro para continuar...")
                    .addActionRow(
                            Button.primary("pending_btn", "Pending"),
                            Button.success("done_btn", "Done"),
                            Button.danger("expired_btn", "Expired"))
                    .setEphemeral(true)
                    .queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        switch (event.getComponentId()) {
            case "pending_btn" -> event.reply("Você clicou: PENDING!").setEphemeral(true).queue();
            case "done_btn" -> event.reply("Você clicou: DONE!").setEphemeral(true).queue();
            case "expired_btn" -> event.reply("Você clicou: EXPIRED!").setEphemeral(true).queue();
        }
    }
}
