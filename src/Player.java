import net.dv8tion.jda.api.entities.User;

public class Player {
    User user;
    Hand hand;
    int chips;
    int bet;
    public Player(User user) {
        this.user = user;
        this.chips = 1000;
        this.bet = 0;
    }
    public User getUser() {
        return user;
    }
    public int getChips() {
        return chips;
    }

    public void placeBet(int amount) {
        if (amount > chips) {
            throw new IllegalArgumentException("Not enough chips!");
        }
        chips -= amount;
        bet += amount;
    }

    public void resetBet() {
        bet = 0;
    }

    public int getBet() {
        return bet;
    }

    public void winChips(int amount) {
        chips += amount;
    }
}
