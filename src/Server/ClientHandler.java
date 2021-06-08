package Server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Player player;
    private Game game;
    private Socket clientSocket;
    private Scanner scanner;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private boolean isReadyToPlay;
    private String message;
    private boolean isMuted;
    private boolean isAlive;

    public ClientHandler(Game game, Socket clientSocket) {
        this.game = game;
        this.clientSocket = clientSocket;
        isReadyToPlay =false;
        message="";
        isMuted=false;
        isAlive=true;
    }

    @Override
    public void run() {
        try {
            String check,username;
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
            player = new Player(this,game,username);
            send("Type 'START' to start!\n");
            while(!(message=next()).equalsIgnoreCase("START")){
            }
            isReadyToPlay =true;
            game.broadcast(username+" logged in");
            eraseMessage();
            while ((message = next()) != null) {
                String[] words = message.split(" ");
                String command = words[0];
                if (command.equalsIgnoreCase("EXIT")) break;
                else if(command.equalsIgnoreCase("HISTORY")) send(game.getHistory());
                else if(!isMuted && isAlive) game.sendMessage(getUsername(), message);
                else if(isMuted && isAlive) send("You've been muted by psychiatrist\n");
                else send("You're dead :( R.I.P.\n");
            }
            player.kill();
            game.removeClientHandler(this);
            game.broadcast(player.getUsername()+" left the game");
        } catch (IOException exception) {
            player.kill();
            game.removeClientHandler(this);
            game.broadcast(player.getUsername()+" left the game");
            exception.printStackTrace();
        }
    }

    public String getUsername() {
        if (player == null) return "";
        return player.getUsername();
    }
    public Player getPlayer() {
        return player;
    }
    public boolean isReadyToPlay(){
        return isReadyToPlay;
    }
    public boolean isReadyToVote() {
        if(message.equalsIgnoreCase("READY"))
            return true;
        return false;
    }
    private String checkUsername(String username) {
        if (username.contains(" ")) return "Username can't have space\n";
        else if (username.length() < 3) return "Username must contains at least 3 characters\n";
        else if (20 < username.length()) return "Username can't contain more than 20 characters\n";
        else if (!game.isNonRepetitive(username)) return "This username is already occupied\n";
        else return null;
    }
    public void send(String message) {
        try {
            outputStream.writeUTF((message+"\n"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    public void eraseMessage(){
        message="";
    }
    public String getMessage() {
        if(isAlive)
            return message;
        return "[Dead:x_x]";
    }
    public void closeSocket(){
        try {
            clientSocket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    public void mute(){
        isMuted=true;
    }
    public void unmute(){
        isMuted=false;
    }
    public void kill(){
        isAlive=false;
    }
    public String next(){
        message=scanner.nextLine();
        if(message.length()>2)
            return message.substring(2);
        return "";
    }
}