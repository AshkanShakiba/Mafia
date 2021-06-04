package Server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class Game {
    private boolean chatroomIsOpen;
    private ArrayList<Player> players;
    private ArrayList<ClientHandler> clientHandlers;

    public Game(){
        chatroomIsOpen=true;
        players=new ArrayList<>();
        clientHandlers=new ArrayList<>();
    }

    public void play(){
        chatroomIsOpen=false;
        Role.setRoles(players);
        // Introduction night
        for(Player player:players){
            player.introduce();
        }
        // Day, Voting, Night
        while(true){
            chatroomIsOpen=true;
            broadcast("(Day) Chatroom is open\n");
            long start=new Date().getTime();
            while(new Date().getTime()-start<300000 && !playersAreReadyToVote());
            chatroomIsOpen=false;
            voting();
        }
    }

    public void addClientHandler(ClientHandler clientHandler){
        clientHandlers.add(clientHandler);
    }
    public void removeClientHandler(ClientHandler clientHandler){
        players.remove(clientHandler.getPlayer());
        clientHandlers.remove(clientHandler);
    }
    public boolean isNonRepetitive(String username) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (username.equals(clientHandler.getUsername()))
                return false;
        }
        return true;
    }
    public boolean playersAreReadyToPlay(){
        for(ClientHandler clientHandler:clientHandlers)
            if(!clientHandler.isReadyToPlay())
                return false;
        return true;
    }
    public boolean playersAreReadyToVote(){
        for(ClientHandler clientHandler:clientHandlers)
            if(!clientHandler.isReadyToVote())
                return false;
        return true;
    }
    public void setPlayers(){
        for(ClientHandler clientHandler:clientHandlers)
            players.add(clientHandler.getPlayer());
    }
    public void broadcast(String message){
        for (ClientHandler clientHandler : clientHandlers)
            clientHandler.send(message+"\n");
    }
    public void sendMessage(String username,String message){
        if(!chatroomIsOpen) return;
        for (ClientHandler clientHandler : clientHandlers)
            if(clientHandler.isReadyToPlay())
                clientHandler.send(username+": "+message+"\n");
    }
    public String getMafias(){
        String mafias="Mafias:\n";
        for(Player player:players){
            if(player.getRole()==Role.godfather)
                mafias+="\tGodfather: "+player.getUsername()+"\n";
        }
        for(Player player:players){
            if(player.getRole()==Role.drLecter)
                mafias+="\tDr. Lecter: "+player.getUsername()+"\n";
        }
        for(Player player:players){
            if(player.getRole()==Role.mafia)
                mafias+="\tMafia: "+player.getUsername()+"\n";
        }
        return mafias;
    }
    public String getMayor(){
        String mayor="Mayor: ";
        for(Player player:players){
            if(player.getRole()==Role.mayor)
                mayor+=player.getUsername()+"\n";
        }
        return mayor;
    }
    public String getDoctor(){
        String doctor="Doctor: ";
        for(Player player:players){
            if(player.getRole()==Role.doctor)
                doctor+=player.getUsername()+"\n";
        }
        return doctor;
    }
    private void voting(){
        String vote;
        Player victim;
        HashMap<Player,Integer> votes=new HashMap<>();
        for(ClientHandler clientHandler:clientHandlers){
            victim=null;
            vote=clientHandler.getVote();
            while((victim=getPlayer(vote))==null){
                clientHandler.send("Invalid input, Try again\n");
                vote=clientHandler.getVote();
            }
            broadcast(clientHandler.getUsername()+" voted "+victim.getUsername());
            if(votes.containsKey(victim))
                votes.put(victim,votes.get(victim)+1);
            else
                votes.put(victim,1);
        }
        victim=getVictim(votes);
        if(victim==null){
            broadcast("Tie, No one will reject\n");
            return;
        }
        else{
            for (ClientHandler clientHandler:clientHandlers)
                if(clientHandler.getPlayer().getRole()==Role.mayor){
                    if(clientHandler.allow(victim)){
                        broadcast(victim.getUsername()+" is out!\n");
                    }
                }
        }
    }
    private Player getPlayer(String username){
        for(Player player:players)
            if(username.equalsIgnoreCase(player.getUsername()))
                return player;
        return null;
    }
    private Player getVictim(HashMap<Player,Integer> votes){
        int max=0;
        Player player,victim=null;
        Iterator<Player> iterator=votes.keySet().iterator();
        while (iterator.hasNext()){
            player=iterator.next();
            if(votes.get(player)>max){
                max=votes.get(player);
                victim=player;
            }
        }
        if(victim==null) return null;
        iterator=votes.keySet().iterator();
        while (iterator.hasNext()){
            player=iterator.next();
            if(votes.get(player)==max && !player.equals(victim)){
                return null;
            }
        }
        return victim;
    }
}