package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * The entry class of console client application.
 *
 * @author Ashkan Shakiba
 * @version 2021-11-6
 */
public class ConsoleClient {
    private static String ip;
    private static int port;
    private static Socket socket;
    private static DataInputStream inputStream;
    private static DataOutputStream outputStream;
    private static Scanner scanner = new Scanner(System.in);

    /**
     * Instantiates a new Console client.
     *
     * @param ip   the ip
     * @param port the port
     */
    public ConsoleClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * The entry point of console client application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        String ip;
        int port;
        System.out.print("ip: ");
        ip = scanner.nextLine();
        System.out.print("port: ");
        port = Integer.parseInt(scanner.nextLine());
        ConsoleClient client = new ConsoleClient(ip, port);
        //ConsoleClient client = new ConsoleClient("127.0.0.1", 4321);
        if (client.connectToServer()) {
            System.out.println("Connected to the server");
            client.start();
        } else {
            System.err.println("Connection failed");
        }
    }

    /**
     * Connect client to the server.
     *
     * @return the boolean
     */
    public boolean connectToServer() {
        try {
            socket = new Socket(ip, port);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    /**
     * Start Communicating with server.
     */
    public void start() {
        String message = "";
        new Thread(new MessageReader(inputStream)).start();
        while (true) {
            message = scanner.nextLine();
            try {
                outputStream.writeUTF((message + "\n"));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            if (message.equalsIgnoreCase("EXIT")) System.exit(0);
        }
    }

    /**
     * Send a message to the server
     *
     * @param message the message
     */
    public void send(String message) {
        try {
            outputStream.writeUTF(message + "\n");
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Gets input stream of client socket.
     *
     * @return the input stream
     */
    public static DataInputStream getInputStream() {
        return inputStream;
    }
}