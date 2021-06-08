package Server;

public class ServerMain {

    public static void main(String[] args) {
        int port = 4321;
        Game game=new Game();
        Server server = new Server(game, port);
        Thread thread = new Thread(server);
        thread.start();
    }
}