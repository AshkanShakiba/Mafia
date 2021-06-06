package Server;

public class Player {
    private ClientHandler clientHandler;
    private String username;
    private Game game;
    private Role role;
    private int life;
    private int requests;

    public Player(ClientHandler clientHandler,Game game,String username){
        this.clientHandler=clientHandler;
        this.username=username;
        this.game=game;
        this.role=null;
        requests=0;
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
        if(role==Role.dieHard)
            life=2;
        else
            life=1;
        System.out.println(username+": "+role.name());
        clientHandler.send("Your role: "+role.name()+"\n");
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
    public void kill(){
        if(role==Role.godfather)
            game.setGodfather();
        role=Role.citizen;
        clientHandler.kill();
    }
    public void shoot(){
        life--;
        if(life==0) kill();
    }
    public void mute(){
        clientHandler.mute();
    }
    public boolean canRequest(){
        if(requests<2)
            return true;
        return false;
    }
    public void request(){
        requests++;
    }
}