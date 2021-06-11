package Server;

/**
 * The type Player.
 *
 * @author Ashkan Shakiba
 * @version 2021-11-6
 */
public class Player {
    private ClientHandler clientHandler;
    private String username;
    private Game game;
    private Role role;
    private int life;
    private int requests;
    private boolean canSaveItself;

    /**
     * Instantiates a new Player.
     *
     * @param clientHandler the client handler that is connected to client
     * @param game          the game that player plays on
     * @param username      the username of player
     */
    public Player(ClientHandler clientHandler, Game game, String username) {
        this.clientHandler = clientHandler;
        this.username = username;
        this.game = game;
        this.role = null;
        requests = 0;
        canSaveItself = true;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Check if player has role or not.
     *
     * @return true if has, false if not
     */
    public boolean hasRole() {
        if (role == null)
            return false;
        return true;
    }

    /**
     * Sets role for player.
     *
     * @param role the role
     */
    public void setRole(Role role) {
        this.role = role;
        if (role == Role.dieHard)
            life = 2;
        else
            life = 1;
        clientHandler.send("Your role: " + role.name());
    }

    /**
     * Gets role of player.
     *
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Introduce player at first night.
     */
    public void introduce() {
        if (role == Role.godfather || role == Role.drLecter || role == Role.mafia)
            clientHandler.send(game.getMafias());
        else if (role == Role.mayor)
            clientHandler.send(game.getDoctor());
    }

    /**
     * Kill player.
     */
    public void kill() {
        if (role == Role.godfather)
            game.setGodfather();
        role = Role.citizen;
        clientHandler.kill();
        game.killPlayer(this);
    }

    /**
     * Shoot player by godfather's order.
     *
     * @return true if player have been killed, false if not.
     */
    public boolean shoot() {
        life--;
        if (life == 0) {
            kill();
            return true;
        }
        return false;
    }

    /**
     * Mute player by psychiatrist's order.
     */
    public void mute() {
        clientHandler.mute();
    }

    /**
     * Check if die-hard can request for public announcement.
     *
     * @return true if can, false if not
     */
    public boolean canRequest() {
        if (requests < 2)
            return true;
        return false;
    }

    /**
     * Decrease die-hard requests.
     */
    public void request() {
        requests++;
    }

    /**
     * Check if doctor or dr. lecter can save themselves.
     *
     * @return true if can, false if not
     */
    public boolean canSaveItself() {
        if (!canSaveItself)
            return false;
        canSaveItself = false;
        return true;
    }
}