package Server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Player player;
    private Game game;
    private Socket clientSocket;
    private BufferedReader reader;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean isReadyToPlay;
    private boolean isReadyToVote;
    private String message;
    private boolean isMuted;
    private boolean isAlive;

    public ClientHandler(Game game, Socket clientSocket) {
        this.game = game;
        this.clientSocket = clientSocket;
        isReadyToPlay =false;
        isReadyToVote=false;
        message="";
        isMuted=false;
        isAlive=true;
    }

    @Override
    public void run() {
        try {
            String check,username;
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            outputStream.write("Username: ".getBytes());
            username = reader.readLine();
            while ((check = checkUsername(username)) != null) {
                outputStream.write(check.getBytes());
                outputStream.write("Username: ".getBytes());
                username = reader.readLine();
            }
            player = new Player(this,game,username);
            outputStream.write("type 'READY' to start!\n".getBytes());
            while(!(message=reader.readLine()).equalsIgnoreCase("READY"));
            send("U R ready\n");
            isReadyToPlay =true;
            eraseMessage();
            while ((message = reader.readLine()) != null) {
                String[] words = message.split(" ");
                String command = words[0];
                if (command.equalsIgnoreCase("EXIT")) break;
                else if(command.equalsIgnoreCase("READY")) isReadyToVote=true;
                else if(!isMuted && isAlive) game.sendMessage(getUsername(), message);
                //else send("You've been muted by psychiatrist\n");
            }
            player.kill();
            game.removeClientHandler(this);
        } catch (IOException exception) {
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
            outputStream.write(message.getBytes());
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
}