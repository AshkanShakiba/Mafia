package Server;

import java.util.ArrayList;

public class Data {
    private int port;
    private ArrayList<ClientHandler> clientHandlers;

    public Data(int port){
        this.port=port;
        clientHandlers=new ArrayList<>();
    }

    public int getPort() {
        return port;
    }
    public void addClientHandler(ClientHandler clientHandler) {
        clientHandlers.add(clientHandler);
    }
    public ArrayList<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }
}