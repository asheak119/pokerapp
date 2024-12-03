import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        for (int suit = 0; suit < 4; suit++) {
            for (int value = 1; value <= 13; value++) {
                cards.add(new Card(value, suit));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card deal() {
        if (!cards.isEmpty()) {
            return cards.remove(0);
        } else {
            return null; // No cards left to deal
        }
    }

    public int size() {
        return cards.size();
    }
}
