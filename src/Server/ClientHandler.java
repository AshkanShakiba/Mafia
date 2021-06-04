package Server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Player player;
    private Game game;
    private Socket clientSocket;
    InputStream inputStream;
    OutputStream outputStream;


    public ClientHandler(Game game,Socket clientSocket) {
        this.game=game;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            String message,username;
            inputStream = clientSocket.getInputStream();
            outputStream = clientSocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            outputStream.write("Username: ".getBytes());
            username=reader.readLine();
            while((message=checkUsername(username))!=null){
                outputStream.write(message.getBytes());
                outputStream.write("Username: ".getBytes());
                username=reader.readLine();
            }
            player=new Player(username);
            while ((message = reader.readLine()) != null) {
                String[] words = message.split(" ");
                String command = words[0];
                if (command.equalsIgnoreCase("EXIT")) break;
                else game.sendMessage(getUsername(),message);
            }
            clientSocket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public String getUsername() {
        if(player==null) return "";
        return player.getUsername();
    }
    private String checkUsername(String username){
        if(username.contains(" ")) return "Username can't have space\n";
        else if(username.length()<3) return "Username must contains at least 3 characters\n";
        else if(20<username.length()) return "Username can't contain more than 20 characters\n";
        else if(!game.isNonRepetitive(username)) return "This username is already occupied\n";
        else return null;
    }
    public void send(String message){
        try {
            outputStream.write(message.getBytes());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}