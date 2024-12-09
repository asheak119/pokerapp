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
    private boolean gameInProgress = false;
    private PokerGame game = null;

    public static void main(String[] args) {
        // Initialize the bot
        JDABuilder.createDefault("") // Replace with your bot token
            .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS)
            .setEnabledIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
            .addEventListeners(new Main()) // Add event listener
            .build();
        // Hand value representations
    }

    @Override
    public void onEvent(GenericEvent event) {
        System.out.println("Event received: " + event.getClass().getSimpleName());
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageEvent = (MessageReceivedEvent) event;
            String messageContent = messageEvent.getMessage().getContentDisplay();
            System.out.println("Received a message: " + messageContent);
            MessageChannel channel = messageEvent.getChannel();
            User user = messageEvent.getAuthor();

            if (messageContent.equalsIgnoreCase("!poker")) {
                gameInitiated = true;
                players.clear(); // Clear any previous players
                channel.sendMessage("Type \"!join\" to join the game, or type \"!start\" to start the game.").queue();
            } else if (gameInitiated) {
                if (messageContent.equalsIgnoreCase("!join")) {
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
                    if (gameInProgress && game != null && game.getTurn() != -1) {
                        channel.sendMessage("A game is already in progress!").queue();
                    } else {
                        if (gameInProgress && game != null) {
                            game = null;
                            gameInProgress = false;
                        }
                        if (players.size() < 2) {
                            channel.sendMessage("Not enough players to start the game.").queue();
                        } else {
                            channel.sendMessage("Starting the game with " + players.size() + " players!").queue();
                            game = new PokerGame(players, channel);
                            game.startGame();
                            gameInProgress = true;
                            return;
                        }
                    }
                }
            } if (gameInProgress && game != null && user.equals(players.get(game.getTurn()).getUser())) {
                if (messageContent.equalsIgnoreCase("!fold")) {
                    game.fold();
                } else if (messageContent.equalsIgnoreCase("!call")) {
                    game.call();  
                } else {
                    try {
                    game.bettingLogic(Integer.parseInt(messageContent));
                    } catch (NumberFormatException e) {
                        channel.sendMessage("Invalid input. Please enter a valid input.").queue();
                    }
                }
            }
        }
    }
}
