import java.util.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.User;

public class PokerGame{
    private Deck deck;
    private HashMap<Player, Hand> playerHands;
    private int numPlayers;
    private JDA jda;
    List<Player> players;

    public PokerGame(List<Player> players) {
        this.numPlayers = players.size();
        this.deck = new Deck();
        this.players = players;
        this.playerHands = new HashMap<Player, Hand>();
        for (Player player : players) {
            playerHands.put(player, new Hand());
        }
    }

    public void startGame() {
        if (numPlayers <= 0) {
            System.out.println("No players in the game!");
            return; // Exit early if there are no players
        }

        deck.shuffle();
        System.out.println("Deck shuffled!");
    
        // Deal hands to players
        for (int i = 0; i < numPlayers; i++) {
            Card[] cards = new Card[5];
            for (int j = 0; j < 5; j++) {
                cards[j] = deck.deal();
            }
            playerHands.get(players.get(i)).setCards(cards);
            sendMessageToPlayer(players.get(i), "Your hand: " + Arrays.toString(cards));
        }
        System.out.println("Hands dealt!");
    
    }
    private void sendMessageToPlayer(Player player, String message) {
        User user = player.getUser();
        if (user != null) {
            user.openPrivateChannel().queue((channel) -> {
                channel.sendMessage(message).queue();
            });
        }
    }
    
}
