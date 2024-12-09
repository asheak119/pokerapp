import java.util.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.User;

public class Main implements EventListener {
    private List<Player> players = new ArrayList<>();
    private HashMap<User, Player> knownPlayers = new HashMap<>();
    private boolean gameInitiated = false;
    private PokerGame game;

    public static void main(String[] args) {
        // Initialize the bot
        JDABuilder.createDefault("") // Replace with your bot token
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
            MessageChannel channel = messageEvent.getChannel();

            if (messageContent.equalsIgnoreCase("!poker")) {
                gameInitiated = true;
                players.clear(); // Clear any previous players
                channel.sendMessage("Type \"!join\" to join the game, or type \"!start\" to start the game.").queue();
            } else if (gameInitiated) {
                if (messageContent.equalsIgnoreCase("!join")) {
                    User user = messageEvent.getAuthor();
                    if (!knownPlayers.containsKey(user)) {
                        knownPlayers.put(user, new Player(user));
                    }
                    if (!players.contains(knownPlayers.get(user))) {
                        players.add(knownPlayers.get(user));
                        channel.sendMessage(user.getAsMention() + " has joined the game!").queue();
                    } else {
                        channel.sendMessage(user.getAsMention() + " you have already joined the game!").queue();
                    }
                }  else if (messageContent.equalsIgnoreCase("!start")) {
                if (players.size() < 2) {
                    channel.sendMessage("Not enough players to start the game.").queue();
                } else {
                    channel.sendMessage("Starting the game with " + players.size() + " players!").queue();
                    game = new PokerGame(players, channel);
                    game.startGame();
                    gameInProgress = true; 
                    
            } else if (messageContent.equalsIgnoreCase("!fold")) {
                if (gameInProgress) {
                    Player player = knownPlayers.get(messageEvent.getAuthor());
                    if (player != null) {
                        game.fold(player);
                    } else {
                        channel.sendMessage("You are not in the game!").queue();
                    }
                } else {
                    channel.sendMessage("No game in progress.").queue();
                }
            }
        }
    }
}
