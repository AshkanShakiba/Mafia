package Client;

import java.io.InputStream;
import java.util.Scanner;

/**
 * The message reader thread of console client.
 */
public class MessageReader implements Runnable {
    private Scanner scanner;

    /**
     * Instantiates a new Message reader.
     *
     * @param inputStream the input stream of client socket
     */
    public MessageReader(InputStream inputStream) {
        scanner = new Scanner(inputStream);
    }

    /**
     * Entrance point of message reader thread.
     */
    public void run() {
        try {
            String message;
            while ((message = scanner.nextLine()) != null) {
                System.out.println(message);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}