public class PokerGame {
    private Deck deck;
    private Hand[] playerHands;
    private int numPlayers;

    public PokerGame(int numPlayers) {
        this.numPlayers = numPlayers;
        this.deck = new Deck();
        this.playerHands = new Hand[numPlayers];
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
            playerHands[i] = new Hand(cards);
            System.out.println("Player " + (i + 1) + "'s hand: " + playerHands[i]);
        }
    
    }
    
}
