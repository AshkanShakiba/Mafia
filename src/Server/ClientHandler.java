package Server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Player player;
    private Game game;
    private Socket clientSocket;
    //private BufferedReader reader;
    private InputStream inputStream;
    //private OutputStream outputStream;
    private boolean isReadyToPlay;
    private boolean isReadyToVote;
    private String vote;

    public BufferedReader reader;
    public OutputStream outputStream;

    public ClientHandler(Game game, Socket clientSocket) {
        this.game = game;
        this.clientSocket = clientSocket;
        isReadyToPlay =false;
        isReadyToVote=false;
    }

    @Override
    public void run() {
        try {
            String message, username;
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            outputStream.write("Username: ".getBytes());
            username = reader.readLine();
            while ((message = checkUsername(username)) != null) {
                outputStream.write(message.getBytes());
                outputStream.write("Username: ".getBytes());
                username = reader.readLine();
            }
            player = new Player(this,game,username);
            //outputStream.write("type 'READY' to start!\n".getBytes());
            //while(!(message=reader.readLine()).equalsIgnoreCase("READY"));
            send("U R ready\n");
            isReadyToPlay =true;
            while ((message = reader.readLine()) != null) {
                String[] words = message.split(" ");
                String command = words[0];
                if (command.equalsIgnoreCase("EXIT")) break;
                else game.sendMessage(getUsername(), message);
            }
            game.removeClientHandler(this);
            clientSocket.close();
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
        return isReadyToVote;
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
    public void vote(){
        String vote="";
        try {
            outputStream.write("(SKIP) Your vote: ".getBytes());
            vote=reader.readLine();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        this.vote=vote;
        send("Vote wrote = "+vote);
    }
    public String getVote() {
        return vote;
    }
    public boolean allow(Player victim){
        String answer=null;
        try {
            send(victim.getUsername()+" is gonna be out, will you allow? (Y/N)");
            answer=reader.readLine();
            while(!answer.equalsIgnoreCase("Y") && !answer.equalsIgnoreCase("N")){
                send("Invalid input, Try again\n");
                answer=reader.readLine();
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        if(answer.equalsIgnoreCase("Y"))
            return true;
        return false;
    }
}