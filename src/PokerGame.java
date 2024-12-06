import java.util.*;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.User;

public class PokerGame implements EventListener {
    private Deck deck;
    private HashMap<Player, Hand> playerHands;
    private int numPlayers;
    private MessageChannel channel;
    private List<Player> players;

    public PokerGame(List<Player> players, MessageChannel channel) {
        this.numPlayers = players.size();
        this.deck = new Deck();
        this.players = players;
        this.channel = channel;
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
        channel.sendMessage("Hands dealt!").queue();

        determineWinner();
    }

    public void determineWinner() {
        
        // Evaluate hands and determine the winner
        int winnerIndex = -1;
        int[] bestHandValue = null;

        for (int i = 0; i < numPlayers; i++) {
            int[] handValue = playerHands.get(players.get(i)).evaluate();
            channel.sendMessage("Player " + players.get(i).getUser().getAsMention() + " has: " + getHandName(handValue[0]));

            // Update the best hand
            if (bestHandValue == null || compareHands(handValue, bestHandValue) > 0) {
                bestHandValue = handValue;
                winnerIndex = i;
            }
        }

        if (winnerIndex >= 0) {
            channel.sendMessage("Player " + (winnerIndex + 1) + " wins with a " + getHandName(bestHandValue[0]));
        } else {
            channel.sendMessage("No winner could be determined.");
        }
    }

    public void bettingRound() {
        // Implement betting round logic here
        for (Player player : players) {
            channel.sendMessage(player.getUser().getAsMention() + "What is your bet?").queue();
            // Wait for player input

        }
    }

    private void sendMessageToPlayer(Player player, String message) {
        User user = player.getUser();
        if (user != null) {
            user.openPrivateChannel().queue((channel) -> {
                channel.sendMessage(message).queue();
            });
        }
    }

    private int compareHands(int[] hand1, int[] hand2) {
        if (hand1[0] != hand2[0]) {
            return Integer.compare(hand1[0], hand2[0]); // Compare hand types
        }
        if (hand1[1] != hand2[1]) {
            return Integer.compare(hand1[1], hand2[1]); // Compare high cards
        }
        return Integer.compare(hand1[2], hand2[2]); // Compare secondary cards (for ties)
    }

    private String getHandName(int value) {
        String[] handNames = {
            "High Card", "Pair", "Two Pair", "Three of a Kind",
            "Straight", "Flush", "Full House", "Four of a Kind",
            "Straight Flush", "Royal Flush"
        };
        if (value >= 1 && value <= handNames.length) {
            return handNames[value - 1];
        }
        return "Unknown Hand";
    }

    @Override
    public void onEvent(GenericEvent event) {
        // Handle events if needed
    }
}
