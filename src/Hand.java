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
        // Insertion sort in descending order
        for (Card card : cards) {
            int i = 0;
            while (i < 5 && sorted.cards[i] != null && card.compare(sorted.cards[i]) < 0) {
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
        // Sort the cards in descending order
        sort();

        // Check for Royal Flush
        if (isRoyalFlush()) {
            return new int[] {10, cards[0].getValue()};
        }

        // Check for Straight Flush
        if (isStraightFlush()) {
            // Check for 5-high straight flush (A-2-3-4-5)
            if (cards[4].getValue() == 5) {
                return new int[] {9, 5};
            }
            return new int[] {9, cards[0].getValue()};
        }

        // Check for Four of a Kind
        if (isFourOfAKind()) {
            if (cards[0].getValue() == cards[3].getValue()) {
                return new int[] {8, cards[0].getValue(), cards[4].getValue()};
            } else {
                return new int[] {8, cards[1].getValue(), cards[0].getValue()};
            }
        }

        // Check for Full House
        if (isFullHouse()) {
            if (cards[0].getValue() == cards[2].getValue()) {
                return new int[] {7, cards[0].getValue(), cards[3].getValue()};
            } else {
                return new int[] {7, cards[2].getValue(), cards[0].getValue()};
            }
        }

        // Check for Flush
        if (isFlush()) {
            return new int[] {6, cards[0].getValue()};
        }

        // Check for Straight
        if (isStraight()) {
            // Check for 5-high straight (A-2-3-4-5)
            if (cards[4].getValue() == 5) {
                return new int[] {5, 5};
            }
            return new int[] {5, cards[0].getValue()};
        }

        // Check for Three of a Kind
        if (isThreeOfAKind()) {
            for (int i = 0; i < 3; i++) {
                if (cards[i].getValue() == cards[i + 1].getValue() && cards[i].getValue() == cards[i + 2].getValue()) {
                    return new int[] {4, cards[i].getValue(), cards[4].getValue()};
                }
            }
        }

        // Check for Two Pair
        if (isTwoPair()) {
            if (cards[0].getValue() == cards[1].getValue() && cards[2].getValue() == cards[3].getValue()) {
                return new int[] {3, cards[0].getValue(), cards[2].getValue()};
            } else if (cards[0].getValue() == cards[1].getValue() && cards[3].getValue() == cards[4].getValue()) {
                return new int[] {3, cards[0].getValue(), cards[3].getValue()};
            } else if (cards[1].getValue() == cards[2].getValue() && cards[3].getValue() == cards[4].getValue()) {
                return new int[] {3, cards[1].getValue(), cards[3].getValue()};
            }
        }

        // Check for Pair
        if (isPair()) {
            for (int i = 0; i < 4; i++) {
                if (cards[i].getValue() == cards[i + 1].getValue()) {
                    return new int[] {2, cards[i].getValue(), cards[4].getValue()};
                }
            }
        }

        // High Card
        return new int[] {1, cards[0].getValue()};
    }

    private boolean isStraight() {
        int[] values = new int[5];
        for (int i = 0; i < 5; i++) {
            values[i] = cards[i].getValue();
        }
        Arrays.sort(values);

        // Check for 5-high straight (A-2-3-4-5)
        if (values[0] == 1 && values[1] == 2 && values[2] == 3 && values[3] == 4 && values[4] == 5) {
            return true;
        }

        // Check for Ace-high straight (10-J-Q-K-A)
        if (values[0] == 1 && values[1] == 10 && values[2] == 11 && values[3] == 12 && values[4] == 13) {
            return true;
        }

        for (int i = 0; i < 4; i++) {
            if (values[i] + 1 != values[i + 1]) {
                return false;
            }
        }
        return true;
    }

    private boolean isStraightFlush() {
        return isFlush() && isStraight();
    }

    private boolean isRoyalFlush() {
        return isStraightFlush() && cards[0].getValue() == 1 && cards[1].getValue() == 13;
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

    public boolean isFourOfAKind() {
        for (int i = 0; i < 2; i++) {
            if (cards[i].getValue() == cards[i + 1].getValue() && cards[i].getValue() == cards[i + 2].getValue() && cards[i].getValue() == cards[i + 3].getValue()) {
                return true;
            }
        }
        return false;
    }

    public boolean isFullHouse() {
        if (cards[0].getValue() == cards[1].getValue() && cards[0].getValue() == cards[2].getValue() && cards[3].getValue() == cards[4].getValue()) {
            return true;
        }
        if (cards[0].getValue() == cards[1].getValue() && cards[2].getValue() == cards[3].getValue() && cards[2].getValue() == cards[4].getValue()) {
            return true;
        }
        return false;
    }

    public boolean isThreeOfAKind() {
        for (int i = 0; i < 3; i++) {
            if (cards[i].getValue() == cards[i + 1].getValue() && cards[i].getValue() == cards[i + 2].getValue()) {
                return true;
            }
        }
        return false;
    }

    public boolean isTwoPair() {
        if (cards[0].getValue() == cards[1].getValue() && cards[2].getValue() == cards[3].getValue()) {
            return true;
        }
        if (cards[0].getValue() == cards[1].getValue() && cards[3].getValue() == cards[4].getValue()) {
            return true;
        }
        if (cards[1].getValue() == cards[2].getValue() && cards[3].getValue() == cards[4].getValue()) {
            return true;
        }
        return false;
    }

    public boolean isPair() {
        for (int i = 0; i < 4; i++) {
            if (cards[i].getValue() == cards[i + 1].getValue()) {
                return true;
            }
        }
        return false;
    }
}