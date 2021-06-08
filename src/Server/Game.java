package Server;

import java.io.*;
import java.util.*;

public class Game {
    private boolean chatroomIsOpen;
    private boolean chatroomIsOpenForMafias;
    private ArrayList<Player> players;
    private ArrayList<ClientHandler> clientHandlers;
    private String whatHappened;
    private boolean dieHardHaveRequested;
    private File history;
    private long dayTime;
    private long votingTime;
    private long mayorTime;
    private long nightTime;
    private long roleTime;

    public Game(){
        chatroomIsOpen=true;
        chatroomIsOpenForMafias=false;
        players=new ArrayList<>();
        clientHandlers=new ArrayList<>();
        whatHappened="";
        dieHardHaveRequested=false;
        history=new File("history.txt");
        clearHistory();
        dayTime=5000; //300000
        votingTime=15000; // 30000
        mayorTime=10000; //15000
        nightTime=5000; // 30000
        roleTime=30000;
    }

    public void play(){
        chatroomIsOpen=false;
        Role.setRoles(players);
        // Introduction night
        for(Player player:players){
            player.introduce();
        }
        // Day, Voting, Night
        while(!isGameFinished()){
            // Day
            chatroomIsOpen=true;
            broadcast("(Day) Chatroom is open\n");
            long start=new Date().getTime();
            while(new Date().getTime()-start<dayTime && !playersAreReadyToVote());
            broadcast("Time Up!\n");
            unmutePlayers();
            // Voting
            broadcast("(Voting) Your vote: ");
            chatroomIsOpen=false;
            voting();
            // Night
            broadcast("(Night) Chatroom is open only for mafias\n");
            night();
        }
        chatroomIsOpen=true;
    }

    public void addClientHandler(ClientHandler clientHandler){
        clientHandlers.add(clientHandler);
    }
    public void removeClientHandler(ClientHandler clientHandler){
        players.remove(clientHandler.getPlayer());
        clientHandler.closeSocket();
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
            if(clientHandler.isAlive() && !clientHandler.isReadyToVote())
                return false;
        return true;
    }
    public void setPlayers(){
        for(ClientHandler clientHandler:clientHandlers)
            players.add(clientHandler.getPlayer());
    }
    public void broadcast(String message){
        for (ClientHandler clientHandler : clientHandlers)
            if(clientHandler.isReadyToPlay())
                clientHandler.send(message+"\n");
    }
    public void sendMessage(String username,String message){
        if(chatroomIsOpen){
            addHistory(username+": "+message+"\n");
            for (ClientHandler clientHandler : clientHandlers)
                if(clientHandler.isReadyToPlay())
                    clientHandler.send(username+": "+message+"\n");
        }
        if(chatroomIsOpenForMafias){
            for (ClientHandler clientHandler : clientHandlers) {
                Role role=clientHandler.getPlayer().getRole();
                if (role== Role.godfather || role== Role.drLecter || role== Role.mafia)
                    clientHandler.send(username + ": " + message + "\n");
            }
        }
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
        eraseMessages();
        try {
            Thread.sleep(votingTime);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        for(ClientHandler clientHandler:clientHandlers){
            victim=null;
            vote=clientHandler.getMessage();
            if(vote.equals("[Dead:x_x]")) continue;
            System.out.println(clientHandler.getUsername()+" : "+vote);
            victim=getPlayer(vote);
            if(victim==null){
                broadcast(clientHandler.getUsername()+" skipped\n");
                continue;
            }
            broadcast(clientHandler.getUsername()+" voted "+victim.getUsername());
            if(votes.containsKey(victim))
                votes.put(victim,votes.get(victim)+1);
            else
                votes.put(victim,1);
        }
        victim=getVictim(votes);
        if(victim==null){
            broadcast("No one will reject\n");
            return;
        }
        eraseMessages();
        for (ClientHandler clientHandler:clientHandlers) {
            if (clientHandler.getPlayer().getRole() == Role.mayor) {
                if(clientHandler.getMessage().equals("[Dead:x_x]")) break;
                clientHandler.send(victim.getUsername() + " is gonna be out, will you allow? (Y/N)\n");
                try {
                    Thread.sleep(mayorTime);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                if (clientHandler.getMessage().equalsIgnoreCase("Y")) {
                    broadcast("Mayor allowed, " + victim.getUsername() + " is out\n");
                    victim.kill();
                } else {
                    broadcast("Mayor didn't allow, " + victim.getUsername() + " will stay\n");
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
    private void night(){
        chatroomIsOpenForMafias=true;
        try {
            Thread.sleep(nightTime);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        chatroomIsOpenForMafias=false;
        eraseMessages();
        for(ClientHandler clientHandler:clientHandlers){
            Role role=clientHandler.getPlayer().getRole();
            if(role==Role.godfather)
                clientHandler.send("Select someone to kill\n");
            else if(role==Role.drLecter || role==Role.doctor)
                clientHandler.send("Select someone to save\n");
            else if(role==Role.detective)
                clientHandler.send("Select someone to inquiry\n");
            else if(role==Role.professional)
                clientHandler.send("Select someone to shoot\n");
            else if(role==Role.psychiatrist)
                clientHandler.send("Select someone to mute");
            else if(role==Role.dieHard){
                if(clientHandler.getPlayer().canRequest())
                    clientHandler.send("Are you agree to public announcement? (Y/N)\n");
            }
        }
        try {
            Thread.sleep(roleTime);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        Player targetedByGodfather=null,savedByDoctor=null;
        Player targetedByProfessional=null,savedByDrLecter=null;
        for(ClientHandler clientHandler:clientHandlers){
            Role role=clientHandler.getPlayer().getRole();
            if(role==Role.godfather) {
                targetedByGodfather=getPlayer(clientHandler.getMessage());
            }
            else if(role==Role.doctor) {
                Player player=getPlayer(clientHandler.getMessage());
                if(player.getRole()==Role.doctor){
                    if(player.canSaveItself()) {
                        savedByDoctor = player;
                    }
                }
                else{
                    savedByDoctor=getPlayer(clientHandler.getMessage());
                }
            }
            else if(role==Role.professional) {
                targetedByProfessional=getPlayer(clientHandler.getMessage());
            }
            else if(role==Role.drLecter) {
                Player player=getPlayer(clientHandler.getMessage());
                if(player.getRole()==Role.drLecter){
                    if(player.canSaveItself()) {
                        savedByDrLecter = player;
                    }
                }
                else{
                    savedByDrLecter=getPlayer(clientHandler.getMessage());
                }
                if(savedByDrLecter!=null){
                    for (ClientHandler clientHandlerM:clientHandlers){
                        Role roleM=clientHandlerM.getPlayer().getRole();
                        if(roleM==Role.godfather || roleM==Role.mafia){
                            clientHandlerM.send("Dr. Lecter saved "+savedByDrLecter.getUsername());
                        }
                    }
                }
            }
            else if(role==Role.detective) {
                Player player=getPlayer(clientHandler.getMessage());
                if(player!=null)
                    clientHandler.send(inquiry(player));
            }
            else if(role==Role.psychiatrist) {
                Player player=getPlayer(clientHandler.getMessage());
                if(player!=null)
                    player.mute();
            }
            else if(role==Role.dieHard) {
                if(clientHandler.getPlayer().canRequest())
                    if(clientHandler.getMessage().equalsIgnoreCase("Y")){
                        dieHardHaveRequested=true;
                        clientHandler.getPlayer().request();
                    }
            }
        }
        if(targetedByGodfather!=null){
            if(savedByDoctor!=null && targetedByGodfather.equals(savedByDoctor)) {
                broadcast("Doctor Saved the target\n");
            }
            else{
                targetedByGodfather.shoot();
                broadcast(targetedByGodfather.getUsername()+" have been killed\n");
                if(dieHardHaveRequested){
                    whatHappened+="The "+targetedByGodfather.getRole().name()+" is out\n";
                }
            }
        }
        if(targetedByProfessional!=null){
            Role role=targetedByProfessional.getRole();
            if(role==Role.godfather || role==Role.drLecter || role==Role.mafia){
                if(savedByDrLecter!=null && targetedByProfessional.equals(savedByDrLecter)) {
                    broadcast("Dr. Lecter Saved the target\n");
                }
                else{
                    targetedByProfessional.kill();
                    broadcast(targetedByProfessional.getUsername()+" have been killed\n");
                    if(dieHardHaveRequested){
                        whatHappened+="The "+targetedByProfessional.getRole().name()+" is out\n";
                    }
                }
            }
            else{
                for(ClientHandler clientHandler:clientHandlers){
                    if(clientHandler.getPlayer().getRole()==Role.professional){
                        clientHandler.send("Wrong decision, You will be out!\n");
                        clientHandler.getPlayer().kill();
                        broadcast(clientHandler.getUsername()+" have been killed\n");
                        if(dieHardHaveRequested){
                            whatHappened+="The "+clientHandler.getPlayer().getRole().name()+" is out\n";
                        }
                    }
                }
            }
        }
        broadcast(whatHappened);
        dieHardHaveRequested=false;
        whatHappened="";
    }
    private String inquiry(Player player){
        if(player.getRole()==Role.mafia || player.getRole()==Role.drLecter)
            return "Yes, a mafia!\n";
        return "No! not a mafia\n";
    }
    private void unmutePlayers(){
        for(ClientHandler clientHandler:clientHandlers)
            clientHandler.unmute();
    }
    private boolean isGameFinished(){
        int mafias=0,citizens=0;
        for(Player player:players){
            Role role=player.getRole();
            if(role==Role.godfather || role==Role.drLecter || role==Role.mafia)
                mafias++;
            else
                citizens++;
        }
        if(mafias>=citizens){
            broadcast("Mafia group wins!\n");
            return true;
        }
        if(mafias==0){
            broadcast("Citizen group wins\n");
            return true;
        }
        return false;
    }
    public void setGodfather(){
        for (Player player:players){
            if(player.getRole()==Role.mafia){
                player.setRole(Role.godfather);
                return;
            }
        }
        for(Player player:players){
            if(player.getRole()==Role.drLecter){
                player.setRole(Role.godfather);
            }
        }
    }
    private void eraseMessages(){
        for(ClientHandler clientHandler:clientHandlers)
            clientHandler.eraseMessage();
    }
    public String getHistory(){
        String historyString="";
        try{
            Scanner scanner=new Scanner(history);
            while (scanner.hasNextLine()){
                historyString+=scanner.nextLine()+"\n";
            }
            scanner.close();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
        return historyString;
    }
    private void addHistory(String message){
        try {
            FileWriter writer = new FileWriter(history);
            writer.write(message+"\n");
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    private void clearHistory(){
        try {
            FileWriter fileWriter = new FileWriter(history, false);
            PrintWriter printWriter = new PrintWriter(fileWriter, false);
            printWriter.flush();
            printWriter.close();
            fileWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    public void killPlayer(Player player){
        players.remove(player);
    }
}