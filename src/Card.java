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
        return value + " of " + suitNames[suit];
    }
}
