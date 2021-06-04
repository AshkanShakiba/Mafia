package Server;

public class Game {
    private static Data data;

    public Game(int port){
        data=new Data(port);
    }

    public int getPort(){
        return data.getPort();
    }
    public void addClientHandler(ClientHandler clientHandler){
        data.addClientHandler(clientHandler);
    }

    public boolean isNonRepetitive(String username) {
        for (ClientHandler clientHandler : data.getClientHandlers()) {
            if (username.equals(clientHandler.getUsername()))
                return false;
        }
        return true;
    }
    public void sendMessage(String username,String message){
        for (ClientHandler clientHandler : data.getClientHandlers()) {
            clientHandler.send(username+": "+message+"\n");
        }
    }
}