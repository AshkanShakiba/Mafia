package Server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * The Client handler thread to handle clients together.
 */
public class ClientHandler implements Runnable {
    private Player player;
    private Game game;
    private Socket clientSocket;
    private Scanner scanner;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private boolean isReadyToPlay;
    private boolean isReadyToVote;
    private String message;
    private boolean isMuted;
    private boolean isAlive;

    /**
     * Instantiates a new Client handler.
     *
     * @param game         the game which client is playing on
     * @param clientSocket the client socket that client uses
     */
    public ClientHandler(Game game, Socket clientSocket) {
        this.game = game;
        this.clientSocket = clientSocket;
        isReadyToPlay = false;
        isReadyToVote = false;
        message = "";
        isMuted = false;
        isAlive = true;
    }

    /**
     * Entrance point of client handler thread.
     */
    @Override
    public void run() {
        try {
            String check, username;
            inputStream = new DataInputStream(clientSocket.getInputStream());
            outputStream = new DataOutputStream(clientSocket.getOutputStream());
            scanner = new Scanner(inputStream);
            send("Username: ");
            username = next();
            while ((check = checkUsername(username)) != null) {
                send(check);
                send("Username: ");
                username = next();
            }
            player = new Player(this, game, username);
            send("Type 'START' to start!");
            while (!(message = next()).equalsIgnoreCase("START")) {
            }
            isReadyToPlay = true;
            game.broadcast(username + " logged in");
            eraseMessage();
            while ((message = next()) != null) {
                String[] words = message.split(" ");
                String command = words[0];
                if (command.equalsIgnoreCase("EXIT")) break;
                else if (command.equalsIgnoreCase("READY")) isReadyToVote = true;
                else if (command.equalsIgnoreCase("HISTORY")) send(game.getHistory());
                else if (!isMuted && isAlive) game.sendMessage(getUsername(), message);
                else if (isMuted && isAlive) send("You've been muted by psychiatrist");
                else send("You're dead :( R.I.P.");
            }
            player.kill();
            game.removeClientHandler(this);
            game.broadcast(player.getUsername() + " left the game");
        } catch (IOException exception) {
            player.kill();
            game.removeClientHandler(this);
            game.broadcast(player.getUsername() + " left the game");
            exception.printStackTrace();
        }
    }

    /**
     * Gets username of client handler.
     *
     * @return the username
     */
    public String getUsername() {
        if (player == null) return "";
        return player.getUsername();
    }

    /**
     * Gets player of client handler.
     *
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets if the player is ready to play or not.
     *
     * @return true if it's ready, false if not
     */
    public boolean isReadyToPlay() {
        return isReadyToPlay;
    }

    /**
     * Gets if the player is ready to vote or not.
     *
     * @return true if it's ready, false if not
     */
    public boolean isReadyToVote() {
        return isReadyToVote;
    }

    /**
     * Check if the username is valid or not.
     *
     * @param username
     * @return response as a string
     */
    private String checkUsername(String username) {
        if (username.contains(" ")) return "Username can't have space";
        else if (username.length() < 3) return "Username must contains at least 3 characters";
        else if (20 < username.length()) return "Username can't contain more than 20 characters";
        else if (!game.isNonRepetitive(username)) return "This username is already occupied";
        else return null;
    }

    /**
     * Send a message to client.
     *
     * @param message the message
     */
    public void send(String message) {
        try {
            outputStream.writeUTF((message + "\n"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Erase message filed of client.
     */
    public void eraseMessage() {
        message = "";
    }

    /**
     * Gets message field of client.
     *
     * @return the message
     */
    public String getMessage() {
        if (isAlive)
            return message;
        return "[Dead:x_x]";
    }

    /**
     * Close client socket.
     */
    public void closeSocket() {
        try {
            clientSocket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Mute player by psychiatrist's order.
     */
    public void mute() {
        isMuted = true;
    }

    /**
     * Unmute player.
     */
    public void unmute() {
        isMuted = false;
    }

    /**
     * Kill player.
     */
    public void kill() {
        isAlive = false;
    }

    /**
     * read a message from input stream of socket.
     *
     * @return the message as a string
     */
    public String next() {
        message = scanner.nextLine();
        if (message.length() > 2)
            return message.substring(2);
        return "";
    }

    /**
     * Gets if the player is alive or not.
     *
     * @return true if yes, false if not
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Make the player alive again to be able to chat after the game.
     */
    public void alive() {
        isAlive = true;
    }

    /**
     * Make the player unprepared to vote.
     */
    public void unprepared() {
        isReadyToVote = false;
    }
}