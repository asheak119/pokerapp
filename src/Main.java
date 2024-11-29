import java.util.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Main implements EventListener {
    public static void main(String[] args) {
        // Initialize the bot
        JDABuilder.createDefault("BOT_TOKEN_HERE") // Replace with your bot token
        .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
            .setEnabledIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
            .addEventListeners(new Main()) // Add event listener
            .build();
        // Hand value representations
        String[] handValues = {
            "High Card",
            "Pair",
            "Two Pair",
            "Three of a Kind",
            "Straight",
            "Flush",
            "Full House",
            "Four of a Kind",
            "Straight Flush",
            "Royal Flush"
        };
    }

    @Override
    public void onEvent(GenericEvent event) {
        System.out.println("Event received: " + event.getClass().getSimpleName());
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageEvent = (MessageReceivedEvent) event;
            String messageContent = messageEvent.getMessage().getContentDisplay();
            System.out.println("Received a message: " + messageContent);
        }
    }
}