package Server;

public class ServerMain {
    private static Game game;
    public static void main(String[] args) {
        int port = 4321;
        game=new Game(port);
        Server server = new Server(game);
        Thread thread = new Thread(server);
        thread.start();
    }
}