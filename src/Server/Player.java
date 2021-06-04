package Server;

public class Player {
    private ClientHandler clientHandler;
    private String username;
    private Game game;
    private Role role;

    public Player(ClientHandler clientHandler,Game game,String username){
        this.clientHandler=clientHandler;
        this.username=username;
        this.game=game;
        this.role=null;
    }

    public String getUsername() {
        return username;
    }
    public boolean hasRole(){
        if(role==null)
            return false;
        return true;
    }
    public void setRole(Role role){
        this.role=role;
        System.out.println(username+": "+role.name());
    }
    public Role getRole() {
        return role;
    }
    public void introduce(){
        if(role==Role.godfather || role==Role.drLecter || role==Role.mafia)
            clientHandler.send(game.getMafias());
        else if(role==Role.doctor)
            clientHandler.send(game.getMayor());
        else if(role==Role.mayor)
            clientHandler.send(game.getDoctor());
    }
}