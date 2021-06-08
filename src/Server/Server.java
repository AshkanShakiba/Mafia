package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server implements Runnable {
    private int port;
    private Game game;
    private long joinTime;

    public Server(Game game, int port) {
        this.game = game;
        this.port = port;
        joinTime=10000; //90000
    }

    @Override
    public void run() {
        try {
            int playersCount = 0;
            long startTime = new Date().getTime();
            ServerSocket serverSocket = new ServerSocket(port);
            while (new Date().getTime() - startTime < joinTime || playersCount < 3) {
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