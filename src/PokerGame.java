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
    private int waitingOnBet = -1;
    private int lastRaised = 0;
    private int pot = 0;
    private int currentBet = 0;
    private int maxBet;
    private boolean stillPlaying = true;

    public PokerGame(List<Player> players, MessageChannel channel) {
        this.numPlayers = players.size();
        this.deck = new Deck();
        this.players = players;
        maxBet = players.get(0).balance();
        this.channel = channel;
        this.playerHands = new HashMap<Player, Hand>();
        for (Player player : players) {
            playerHands.put(player, new Hand());
            if (player.balance() < maxBet) {
                maxBet = player.balance();
            }
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
        bettingRound();
    }

    public void bettingRound() {
        if (waitingOnBet == -1) {
            waitingOnBet = 0;
        }
        Player player = players.get(waitingOnBet);
        channel.sendMessage(player.getUser().getAsMention() +
        " Current bet: " + currentBet + " | Your chips: " + player.balance() +
        " | Type your bet (or type '!fold' to fold, '!check' to match the bet)").queue();
    }

    public void bettingLogic(int bet) {
        Player player = players.get(waitingOnBet);
        try {
            if (bet > 0 && bet <= maxBet) {
                player.bet(bet - currentBet);
                waitingOnBet = (waitingOnBet + 1) % numPlayers;
                pot += bet - currentBet;
                if (bet > currentBet) {
                    lastRaised = waitingOnBet;
                    currentBet = bet;
                    bettingRound();
                } else if (waitingOnBet == lastRaised) {
                    determineWinner();
                }
            } else {
                channel.sendMessage("Invalid bet amount. Please enter a valid amount.");
            }
        } catch (NumberFormatException e) {
            channel.sendMessage("Invalid bet amount. Please enter a valid amount.");
        }
    }

    public void determineWinner() {
        
        // Evaluate hands and determine the winner
        int winnerIndex = -1;
        int[] bestHandValue = null;

        for (int i = 0; i < numPlayers; i++) {
            int[] handValue = playerHands.get(players.get(i)).evaluate();
            channel.sendMessage(players.get(i).getUser().getAsMention() + " has a " + getHandName(handValue[0]) + " with cards: " + 
            playerHands.get(players.get(i)).toString()).queue();

            // Update the best hand
            if (bestHandValue == null || compareHands(handValue, bestHandValue) > 0) {
                bestHandValue = handValue;
                winnerIndex = i;
            }
        }

        if (winnerIndex >= 0) {
            channel.sendMessage(players.get(winnerIndex).getUser().getAsMention() + " wins the pot of $" + pot).queue();
            players.get(winnerIndex).win(pot);
        } else {
            channel.sendMessage("No winner could be determined.").queue();
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

    public void fold(Player player) {
    if (!foldedPlayers.contains(player)) {
        foldedPlayers.add(player);
        channel.sendMessage(player.getUser().getAsMention() + " has folded.").queue();
    } else {
        channel.sendMessage(player.getUser().getAsMention() + " has already folded.").queue();
    }
}


    @Override
    public void onEvent(GenericEvent event) {
        // Handle events if needed
        if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageEvent = (MessageReceivedEvent) event;
            String messageContent = messageEvent.getMessage().getContentDisplay();
            User user = messageEvent.getAuthor();

            if (waitingOnBet != -1 && user.equals(players.get(waitingOnBet).getUser())) {
                Player player = players.get(waitingOnBet);
                 if (messageContent.equalsIgnoreCase("!check")) {
                    if (currentBet == player.getCurBet()) {
                        waitingOnBet = (waitingOnBet + 1) % numPlayers;
                        channel.sendMessage(player.getUser().getAsMention() + " has checked.").queue();
                    } else {
                        sendMessageToPlayer(player, "You must match the current bet or fold.");
                    }
                } else {
                    try {
                        int bet = Integer.parseInt(messageContent);
                        if (bet > 0 && bet <= maxBet) {
                            player.bet(bet - currentBet);
                            waitingOnBet = (waitingOnBet + 1) % numPlayers;
                            pot += bet - currentBet;
                            if (bet > currentBet) {
                                lastRaised = waitingOnBet;
                                currentBet = bet;
                            } else if (waitingOnBet == lastRaised) {
                                stillPlaying = false;
                            }
                        } else {
                            channel.sendMessage("Invalid bet amount. Please enter a valid amount.");
                        }
                    } catch (NumberFormatException e) {
                        channel.sendMessage("Invalid bet amount. Please enter a valid amount.");
                    }
                }
            }
        }
    }
}
