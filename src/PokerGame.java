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
            channel.sendMessage("Player " + players.get(i).getUser().getAsMention() + " has: " + getHandName(handValue[0])).queue();

            // Update the best hand
            if (bestHandValue == null || compareHands(handValue, bestHandValue) > 0) {
                bestHandValue = handValue;
                winnerIndex = i;
            }
        }

        if (winnerIndex >= 0) {
            channel.sendMessage("Player " + (winnerIndex + 1) + " wins with a " + getHandName(bestHandValue[0])).queue();
        } else {
            channel.sendMessage("No winner could be determined.").queue();
        }
    }

    public void bettingRound() {
        int pot = 0;
        int currentBet = 0;
        Map<Player, Integer> currentBets = new HashMap<>();
        List<Player> activePlayers = new ArrayList<>(players); // To handle folded players
    
        // Initial betting round prompt
        for (Player player : players) {
            currentBets.put(player, 0); // Initialize all players' bets to 0
        }
    
        // Proceed with betting rounds until all active players have either folded or called the highest bet
        while (activePlayers.size() > 1) {  // At least two players should remain active
            for (Player player : activePlayers) {
                channel.sendMessage(player.getUser().getAsMention() + 
                    " Your current chips: " + player.getChips() +
                    ". Current bet: " + currentBet + ". Type your action (bet <amount>, fold, check):").queue();
                
                // Example input handling:
                String response = getPlayerResponse();
                if (response.equalsIgnoreCase("fold")) {
                     activePlayers.remove(player);  // Player folds, so they are no longer active
                     continue;
                } else if (response.equalsIgnoreCase("check") && currentBet > 0) {
                     player.placeBet(currentBet);  // Player matches current bet
                } else if (response.startsWith("bet ")) {
                     int betAmount = Integer.parseInt(response.split(" ")[1]);
                     if (betAmount > player.getChips()) {
                         channel.sendMessage("You don't have enough chips!").queue();
                     } else {
                         player.placeBet(betAmount);
                         currentBet = betAmount;
                     }
                 }
                // Update pot based on bet
                pot += player.getBet();
            }
        }
        // End of the betting round
        channel.sendMessage("Betting round complete. Total pot: " + pot).queue();
    }
    
    private void distributePot(Player winner, int pot) {
        winner.winChips(pot);
        channel.sendMessage(winner.getUser().getAsMention() + " wins the pot of " + pot + " chips!").queue();
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
