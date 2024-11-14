public class Card {
    private int value;
    private int suit;
    private String[] suitNames = {"Clubs", "Diamonds", "Hearts", "Spades"};
    public Card(int value, int suit) {
        this.value = value;
        this.suit = suit;
    }
    public int getValue() {
        return value;
    }
    public int getSuit() {
        return suit;
    }
    public String toString() {
        if (value == 1) {
            return "Ace of " + suitNames[suit];
        } else if (value == 11) {
            return "Jack of " + suitNames[suit];
        } else if (value == 12) {
            return "Queen of " + suitNames[suit];
        } else if (value == 13) {
            return "King of " + suitNames[suit];
        } else {
            return value + " of " + suitNames[suit];
        }
    }
    public int compare(Card card) {
        if (value == 1 && card.getValue() != 1) {
            return 1;
        } else if (value != 1 && card.getValue() == 1) {
            return -1;
        } else {
            if (value > card.getValue()) {
                return 1;
            } else if (value < card.getValue()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
