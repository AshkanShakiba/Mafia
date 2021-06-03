package Server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class Server {
    public static void main(String[] args){
        int port=4321;
        try {
            ServerSocket serverSocket=new ServerSocket(port);
            while (true) {
                System.out.println("Waiting for client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connection accepted from "+clientSocket);
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}