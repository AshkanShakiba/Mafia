package Server;

/**
 * The Main class to run the server.
 *
 * @author Ashkan Shakiba
 * @version 2021-11-6
 */
public class ServerMain {

    /**
     * The entry point of server application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        int port = 4321;
        Game game = new Game();
        Server server = new Server(game, port);
        Thread thread = new Thread(server);
        thread.start();
    }
}