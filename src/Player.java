import net.dv8tion.jda.api.entities.User;

public class Player {
    private User user;
    private int chips;
    private int curBet;
    public Player(User user) {
        this.user = user;
        this.chips = 1000;
        this.curBet = 0;
    }
    public User getUser() {
        return user;
    }
    public int balance() {
        return chips;
    }
    public void bet(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Bet amount must be positive!");
        }
        if (amount > chips) {
            throw new IllegalArgumentException("Not enough chips!");
        }
        chips -= amount;
    }
    public void win(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Winning amount cannot be negative!");
        }
        chips += amount;
    }
    public void setCurBet(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Bet amount cannot be negative!");
        }
        curBet = amount;
    }
    public int getCurBet() {
        return curBet;
    }
    public void resetCurBet() {
        curBet = 0;
    }
}
