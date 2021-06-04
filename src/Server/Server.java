package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server implements Runnable {
    private int port;
    private Game game;

    public Server(Game game, int port) {
        this.game = game;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            int playersCount = 0;
            long startTime = new Date().getTime();
            ServerSocket serverSocket = new ServerSocket(port);
            while (new Date().getTime() - startTime < 90000 || playersCount < 6) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connection accepted from " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(game, clientSocket);
                game.addClientHandler(clientHandler);
                new Thread(clientHandler).start();
                playersCount++;
                if (playersCount == 15) break;
            }
            while(!game.playersAreReadyToPlay());
            game.setPlayers();
            game.play();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}