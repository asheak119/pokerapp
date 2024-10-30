public class Hand {
    private Card[] cards;
    public Hand() {
        this.cards = new Card[5];
    }
    public Hand(Card[] cards) {
        this.cards = cards;
    }
    public Hand(Card c1, Card c2, Card c3, Card c4, Card c5) {
        this.cards = new Card[] {c1, c2, c3, c4, c5};
    }
}