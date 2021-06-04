package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private Game game;

    public Server(Game game) {
        this.game=game;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(game.getPort());
            while (true) {
                System.out.println("Waiting for client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connection accepted from " + clientSocket);
                ClientHandler clientHandler = new ClientHandler(game,clientSocket);
                game.addClientHandler(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}