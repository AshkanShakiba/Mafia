package Server;

public class ServerMain {
    private static Game game=new Game();

    public static void main(String[] args) {
        int port = 4321;
        Server server = new Server(game, port);
        Thread thread = new Thread(server);
        thread.start();
    }
}