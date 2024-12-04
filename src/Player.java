import net.dv8tion.jda.api.entities.User;

public class Player {
    User user;
    Hand hand;
    int chips;
    int bet;
    public Player(User user) {
        this.user = user;
        this.chips = 1000;
    }
    public User getUser() {
        return user;
    }
}
