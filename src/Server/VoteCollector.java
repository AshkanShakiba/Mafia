package Server;

public class VoteCollector implements Runnable{
    private ClientHandler clientHandler;

    public VoteCollector(ClientHandler clientHandler){
        this.clientHandler=clientHandler;
    }

    @Override
    public void run(){
        clientHandler.vote();
    }
}