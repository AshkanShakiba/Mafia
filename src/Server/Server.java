package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * Server of each game.
 */
public class Server implements Runnable {
    private int port;
    private Game game;
    private long joinTime;

    /**
     * Instantiates a new Server.
     *
     * @param game the game that server run
     * @param port the port which server runs on
     */
    public Server(Game game, int port) {
        this.game = game;
        this.port = port;
        joinTime = 60000;
    }

    /**
     * Entrance point of server thread.
     */
    @Override
    public void run() {
        try {
            int playersCount = 0;
            long startTime = new Date().getTime();
            ServerSocket serverSocket = new ServerSocket(port);
            while (new Date().getTime() - startTime < joinTime || playersCount < 6) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connection accepted from " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(game, clientSocket);
                game.addClientHandler(clientHandler);
                new Thread(clientHandler).start();
                playersCount++;
                if (playersCount == 15) break;
            }
            while (!game.playersAreReadyToPlay()) ;
            game.setPlayers();
            game.play();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}