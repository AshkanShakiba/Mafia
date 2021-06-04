package Server;

public class Player {
    private String username;
    private Role role;

    public Player(String username){
        this.username=username;
    }

    public String getUsername() {
        return username;
    }
}