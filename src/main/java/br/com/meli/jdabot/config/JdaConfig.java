package br.com.meli.jdabot.config;


import br.com.meli.jdabot.discord.listener.ComponentInteractionListener;
import br.com.meli.jdabot.discord.listener.GreetingsListener;
import br.com.meli.jdabot.discord.listener.SlashCommandDispatcher;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdaConfig {
    @Bean
    public JDA jda(@Value("${discord.token}") String token,
                   SlashCommandDispatcher slashDispatcher,
                   ComponentInteractionListener componentInteractionListener,
                   GreetingsListener greetingsListener) throws Exception {

        return JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(slashDispatcher, componentInteractionListener, greetingsListener)
                .build()
                .awaitReady();
    }
}
