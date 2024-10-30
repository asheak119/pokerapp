import java.util.*;
public class Hand {
    private Card[] cards;

    /**
     * Constructor for Hand class with no parameters
     */
    public Hand() {
        this.cards = new Card[5];
    }
    /**
     * Constructor for Hand class with grouped parameter
     * @param cards - array of Card objects
     */
    public Hand(Card[] cards) {
        this.cards = cards;
    }
    /**
     * Constructor for Hand class with individual parameters
     * @param c1 - first Card object
     * @param c2 - second Card object
     * @param c3 - third Card object
     * @param c4 - fourth Card object
     * @param c5 - fifth Card object
     */
    public Hand(Card c1, Card c2, Card c3, Card c4, Card c5) {
        this.cards = new Card[] {c1, c2, c3, c4, c5};
    }

    /**
     * Getter for cards
     * @return - array of Card objects
     */
    public Card[] getCards() {
        return cards;
    }
    /**
     * Setter for individual card
     * @param index - index of card to set
     * @param card - Card object to set
     */
    public void setCard(int index, Card card) {
        cards[index] = card;
    }
    /**
     * Setter for all cards
     * @param cards - array of Card objects
     */
    public void setCards(Card[] cards) {
        this.cards = cards;
    }
    /**
     * Setter for all cards
     * @param c1 - first Card object
     * @param c2 - second Card object
     * @param c3 - third Card object
     * @param c4 - fourth Card object
     * @param c5 - fifth Card object
     */
    public void setCards(Card c1, Card c2, Card c3, Card c4, Card c5) {
        this.cards = new Card[] {c1, c2, c3, c4, c5};
    }
    /**
     * Method to return a string representation of the Hand object
     */
    public String toString() {
        String result = "Hand: ";
        for (Card card : cards) {
            result += card.toString() + ", ";
        }
        return result;
    }
    public void sort() {
        Hand sorted = new Hand();
        // Insertion sort
        for (Card card : cards) {
            int i = 0;
            while (i < 5 && sorted.cards[i] != null && card.getValue() > sorted.cards[i].getValue()) {
                i++;
            }
            for (int j = 4; j > i; j--) {
                sorted.cards[j] = sorted.cards[j - 1];
            }
            sorted.cards[i] = card;
        }
        cards = sorted.cards;
    }
    /**
     * Method to evaluate the hand
     * @return - array of two integers representing the hand's value, followed by the highest card value
     *         (e.g. {10, 10, 0} for a high card of 10, {9, 11, 0} for a pair of Jacks, {2, 6, 0} for a 6 high straight flush, {4, 14, 8} for a full house with Aces over 8s)
     * Key: 10 - High Card, 9 - Pair, 8 - Two Pair, 7 - Three of a Kind, 6 - Straight, 5 - Flush, 4 - Full House, 3 - Four of a Kind, 2 - Straight Flush, 1 - Royal Flush
     * Format: {handValue, highCardValue, secondaryCardValue(in case of two pair or full house)}
     */
    public int[] evaluate() {
        sort();
        if (isFlush()) {
            if (isStraight()) {
                if (cards[4].getValue() == 14) {
                    if (cards[0].getValue() == 2) {
                        return new int[] {2, 5, 0};
                    } else {
                        return new int[] {1, 14, 0};
                    }
                }
                return new int[] {2, cards[4].getValue(), 0};
            }
            return new int[] {5, cards[4].getValue(), 0};
        }
        if (isStraight()) {
            if (cards[4].getValue() == 14 && cards[0].getValue() == 2) {
                return new int[] {6, 5, 0};
            }
            return new int[] {6, cards[4].getValue(), 0};
        }
        
        return new int[] {10, cards[0].getValue(), 0};
    }
    public boolean isFlush() {
        int suit = cards[0].getSuit();
        for (Card card : cards) {
            if (card.getSuit() != suit) {
                return false;
            }
        }
        return true;
    }
    public boolean isStraight() {
        int[] values = new int[5];
        for (int i = 0; i < 5; i++) {
            values[i] = cards[i].getValue();
        }
        if (Arrays.equals(values, new int[] {2,3,4,5,14})) {
            return true;
        }
        for (int i = 0; i < 4; i++) {
            if (values[i] + 1 != values[i + 1]) {
                return false;
            }
        }
        return true;
    }
}