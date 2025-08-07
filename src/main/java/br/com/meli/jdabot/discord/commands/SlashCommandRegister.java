package br.com.meli.jdabot.discord.commands;


import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SlashCommandRegister {

    private final JDA jda;
    private final String guildId;

    public SlashCommandRegister(JDA jda, @Value("${discord.guild.id}") String guildId) {
        this.jda = jda;
        this.guildId = guildId;
    }

    @PostConstruct
    public void registerCommands() {
        jda.getGuildById(guildId).updateCommands().addCommands(
                Commands.slash("ping", "Testa o bot"),
                Commands.slash("clima", "Consulta o clima pelo CEP")
                        .addOption(OptionType.STRING, "cep", "Digite o CEP", true),
                Commands.slash("questionario", "Seleciona questionários usando filtros"),
                Commands.slash("piada", "Te conto uma piada com botão 😂")
        ).queue();
    }
}
