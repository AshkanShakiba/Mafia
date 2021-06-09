package Server;

import java.io.*;
import java.util.*;

/**
 * The Game data and methods.
 */
public class Game {
    private boolean gameIsFinished;
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

    /**
     * Instantiates a new Game.
     */
    public Game() {
        gameIsFinished = false;
        chatroomIsOpen = true;
        chatroomIsOpenForMafias = false;
        players = new ArrayList<>();
        clientHandlers = new ArrayList<>();
        whatHappened = "";
        dieHardHaveRequested = false;
        history = new File("history.txt");
        clearHistory();
        dayTime = 300000;
        votingTime = 30000;
        mayorTime = 15000;
        nightTime = 30000;
        roleTime = 45000;
    }

    /**
     * Start playing the game.
     */
    public void play() {
        chatroomIsOpen = false;
        Role.setRoles(players);
        // Introduction night
        for (Player player : players) {
            player.introduce();
        }
        // Day, Voting, Night
        while (!isGameFinished()) {
            // Day
            chatroomIsOpen = true;
            broadcast("(Day) Chatroom is open");
            long start = new Date().getTime();
            while (new Date().getTime() - start < dayTime && !playersAreReadyToVote()) ;
            unmutePlayers();
            // Voting
            broadcast("(Voting) Your vote: ");
            chatroomIsOpen = false;
            voting();
            unpreparedPlayers();
            if (isGameFinished()) break;
            // Night
            broadcast("(Night) Chatroom is open only for mafias");
            night();
        }
        unmutePlayers();
        alivePlayers();
        gameIsFinished = true;
    }

    /**
     * Add client handler.
     *
     * @param clientHandler the client handler
     */
    public void addClientHandler(ClientHandler clientHandler) {
        clientHandlers.add(clientHandler);
    }

    /**
     * Remove client handler.
     *
     * @param clientHandler the client handler
     */
    public void removeClientHandler(ClientHandler clientHandler) {
        players.remove(clientHandler.getPlayer());
        clientHandler.closeSocket();
        clientHandlers.remove(clientHandler);
    }

    /**
     * Check if the username is repetitive or not.
     *
     * @param username the username
     * @return false if it's repetitive, false if not
     */
    public boolean isNonRepetitive(String username) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (username.equals(clientHandler.getUsername()))
                return false;
        }
        return true;
    }

    /**
     * Check if all players are ready to play.
     *
     * @return true if ready, false if not
     */
    public boolean playersAreReadyToPlay() {
        for (ClientHandler clientHandler : clientHandlers)
            if (!clientHandler.isReadyToPlay())
                return false;
        return true;
    }

    /**
     * Check if all players are ready to vote.
     *
     * @return true if ready, false if not
     */
    public boolean playersAreReadyToVote() {
        for (ClientHandler clientHandler : clientHandlers)
            if (clientHandler.isAlive() && !clientHandler.isReadyToVote())
                return false;
        return true;
    }

    /**
     * Fill players list.
     */
    public void setPlayers() {
        for (ClientHandler clientHandler : clientHandlers)
            players.add(clientHandler.getPlayer());
    }

    /**
     * Broadcast a message to all users.
     *
     * @param message the message
     */
    public void broadcast(String message) {
        for (ClientHandler clientHandler : clientHandlers)
            if (clientHandler.isReadyToPlay())
                clientHandler.send(message);
    }

    /**
     * Send a message on chatroom.
     *
     * @param username the username
     * @param message  the message
     */
    public void sendMessage(String username, String message) {
        if (gameIsFinished) {
            addHistory(username + ": " + message);
            for (ClientHandler clientHandler : clientHandlers)
                if (clientHandler.isReadyToPlay())
                    clientHandler.send(username + ": " + message);
            return;
        }
        if (chatroomIsOpen) {
            addHistory(username + ": " + message);
            for (ClientHandler clientHandler : clientHandlers)
                if (clientHandler.isReadyToPlay())
                    clientHandler.send(username + ": " + message);
        }
        if (chatroomIsOpenForMafias) {
            for (ClientHandler clientHandler : clientHandlers) {
                Role role = clientHandler.getPlayer().getRole();
                if (role == Role.godfather || role == Role.drLecter || role == Role.mafia)
                    clientHandler.send(username + ": " + message);
            }
        }
    }

    /**
     * Gets mafias as a string.
     *
     * @return the mafias
     */
    public String getMafias() {
        String mafias = "Mafias:\n";
        for (Player player : players) {
            if (player.getRole() == Role.godfather)
                mafias += "\tGodfather: " + player.getUsername() + "\n";
        }
        for (Player player : players) {
            if (player.getRole() == Role.drLecter)
                mafias += "\tDr. Lecter: " + player.getUsername() + "\n";
        }
        for (Player player : players) {
            if (player.getRole() == Role.mafia)
                mafias += "\tMafia: " + player.getUsername() + "\n";
        }
        return mafias;
    }

    /**
     * Gets doctor as a string.
     *
     * @return the doctor
     */
    public String getDoctor() {
        String doctor = "Doctor: ";
        for (Player player : players) {
            if (player.getRole() == Role.doctor)
                doctor += player.getUsername();
        }
        return doctor;
    }

    /**
     * Does voting tasks.
     */
    private void voting() {
        String vote;
        Player victim;
        HashMap<Player, Integer> votes = new HashMap<>();
        eraseMessages();
        try {
            Thread.sleep(votingTime);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        for (ClientHandler clientHandler : clientHandlers) {
            victim = null;
            vote = clientHandler.getMessage();
            if (vote.equals("[Dead:x_x]")) continue;
            victim = getPlayer(vote);
            if (victim == null) {
                broadcast(clientHandler.getUsername() + " skipped");
                continue;
            }
            broadcast(clientHandler.getUsername() + " voted " + victim.getUsername());
            if (votes.containsKey(victim))
                votes.put(victim, votes.get(victim) + 1);
            else
                votes.put(victim, 1);
        }
        victim = getVictim(votes);
        if (victim == null) {
            broadcast("No one will reject");
            return;
        }
        eraseMessages();
        for (ClientHandler clientHandler : clientHandlers) {
            if (clientHandler.getPlayer().getRole() == Role.mayor) {
                clientHandler.send(victim.getUsername() + " is gonna be out, will you allow? (Y/N)");
                try {
                    Thread.sleep(mayorTime);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
                if (clientHandler.getMessage().equalsIgnoreCase("Y")) {
                    broadcast("Mayor allowed, " + victim.getUsername() + " is out");
                    victim.kill();
                } else {
                    broadcast("Mayor didn't allow, " + victim.getUsername() + " will stay");
                }
                return;
            }
        }
        broadcast(victim.getUsername() + " is out!");
        victim.kill();
    }

    /**
     * Gets player by username.
     *
     * @param username
     * @return the player
     */
    private Player getPlayer(String username) {
        for (Player player : players)
            if (username.equalsIgnoreCase(player.getUsername()))
                return player;
        return null;
    }

    /**
     * Gets victim by votes.
     *
     * @param votes
     * @return the victim
     */
    private Player getVictim(HashMap<Player, Integer> votes) {
        int max = 0;
        Player player, victim = null;
        Iterator<Player> iterator = votes.keySet().iterator();
        while (iterator.hasNext()) {
            player = iterator.next();
            if (votes.get(player) > max) {
                max = votes.get(player);
                victim = player;
            }
        }
        if (victim == null) return null;
        iterator = votes.keySet().iterator();
        while (iterator.hasNext()) {
            player = iterator.next();
            if (votes.get(player) == max && !player.equals(victim)) {
                return null;
            }
        }
        return victim;
    }

    /**
     * Does night tasks.
     */
    private void night() {
        chatroomIsOpenForMafias = true;
        try {
            Thread.sleep(nightTime);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        chatroomIsOpenForMafias = false;
        eraseMessages();
        for (ClientHandler clientHandler : clientHandlers) {
            Role role = clientHandler.getPlayer().getRole();
            if (role == Role.godfather)
                clientHandler.send("Select someone to kill");
            else if (role == Role.drLecter || role == Role.doctor)
                clientHandler.send("Select someone to save");
            else if (role == Role.detective)
                clientHandler.send("Select someone to inquiry");
            else if (role == Role.professional)
                clientHandler.send("Select someone to shoot");
            else if (role == Role.psychiatrist)
                clientHandler.send("Select someone to mute");
            else if (role == Role.dieHard) {
                if (clientHandler.getPlayer().canRequest())
                    clientHandler.send("Are you agree to public announcement? (Y/N)");
            }
        }
        try {
            Thread.sleep(roleTime);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        Player targetedByGodfather = null, savedByDoctor = null;
        Player targetedByProfessional = null, savedByDrLecter = null;
        for (ClientHandler clientHandler : clientHandlers) {
            Role role = clientHandler.getPlayer().getRole();
            if (role == Role.godfather) {
                targetedByGodfather = getPlayer(clientHandler.getMessage());
            } else if (role == Role.doctor) {
                Player player = getPlayer(clientHandler.getMessage());
                if (player != null && player.getRole() == Role.doctor) {
                    if (player.canSaveItself()) {
                        savedByDoctor = player;
                    }
                } else {
                    savedByDoctor = getPlayer(clientHandler.getMessage());
                }
            } else if (role == Role.professional) {
                targetedByProfessional = getPlayer(clientHandler.getMessage());
            } else if (role == Role.drLecter) {
                Player player = getPlayer(clientHandler.getMessage());
                if (player != null && player.getRole() == Role.drLecter) {
                    if (player.canSaveItself()) {
                        savedByDrLecter = player;
                    }
                } else {
                    savedByDrLecter = getPlayer(clientHandler.getMessage());
                }
                if (savedByDrLecter != null) {
                    for (ClientHandler clientHandlerM : clientHandlers) {
                        Role roleM = clientHandlerM.getPlayer().getRole();
                        if (roleM == Role.godfather || roleM == Role.mafia) {
                            clientHandlerM.send("Dr. Lecter saved " + savedByDrLecter.getUsername());
                        }
                    }
                }
            } else if (role == Role.detective) {
                Player player = getPlayer(clientHandler.getMessage());
                if (player != null)
                    clientHandler.send(inquiry(player));
            } else if (role == Role.psychiatrist) {
                Player player = getPlayer(clientHandler.getMessage());
                if (player != null)
                    player.mute();
            } else if (role == Role.dieHard) {
                if (clientHandler.getPlayer().canRequest())
                    if (clientHandler.getMessage().equalsIgnoreCase("Y")) {
                        dieHardHaveRequested = true;
                        clientHandler.getPlayer().request();
                    }
            }
        }
        if (targetedByGodfather != null) {
            if (savedByDoctor != null && targetedByGodfather.equals(savedByDoctor)) {
                broadcast("Doctor Saved the target");
            } else {
                String roleName = targetedByGodfather.getRole().name();
                if (targetedByGodfather.shoot()) {
                    broadcast(targetedByGodfather.getUsername() + " have been killed");
                    if (dieHardHaveRequested) {
                        whatHappened += "The " + roleName + " is out";
                    }
                }
            }
        }
        if (targetedByProfessional != null) {
            Role role = targetedByProfessional.getRole();
            if (role == Role.godfather || role == Role.drLecter || role == Role.mafia) {
                if (savedByDrLecter != null && targetedByProfessional.equals(savedByDrLecter)) {
                    broadcast("Dr. Lecter Saved the target");
                } else {
                    String roleName = targetedByProfessional.getRole().name();
                    targetedByProfessional.kill();
                    broadcast(targetedByProfessional.getUsername() + " have been killed");
                    if (dieHardHaveRequested) {
                        whatHappened += "The " + roleName + " is out";
                    }
                }
            } else {
                for (ClientHandler clientHandler : clientHandlers) {
                    if (clientHandler.getPlayer().getRole() == Role.professional) {
                        clientHandler.send("Wrong decision, You will be out!");
                        String roleName = clientHandler.getPlayer().getRole().name();
                        clientHandler.getPlayer().kill();
                        broadcast(clientHandler.getUsername() + " have been killed");
                        if (dieHardHaveRequested) {
                            whatHappened += "The " + roleName + " is out";
                        }
                    }
                }
            }
        }
        broadcast(whatHappened);
        dieHardHaveRequested = false;
        whatHappened = "";
    }

    /**
     * Inquiries for detective.
     *
     * @param player
     * @return the result
     */
    private String inquiry(Player player) {
        if (player.getRole() == Role.mafia || player.getRole() == Role.drLecter)
            return "Yes, a mafia!";
        return "No! not a mafia";
    }

    /**
     * Unmute all players.
     */
    private void unmutePlayers() {
        for (ClientHandler clientHandler : clientHandlers)
            clientHandler.unmute();
    }

    /**
     * Check if the game is finished.
     *
     * @return true if finished, false if not
     */
    private boolean isGameFinished() {
        int mafias = 0, citizens = 0;
        for (Player player : players) {
            Role role = player.getRole();
            if (role == Role.godfather || role == Role.drLecter || role == Role.mafia)
                mafias++;
            else
                citizens++;
        }
        if (mafias >= citizens) {
            broadcast("Mafia group wins!");
            return true;
        }
        if (mafias == 0) {
            broadcast("Citizen group wins");
            return true;
        }
        return false;
    }

    /**
     * Sets new godfather after previous godfather being killed.
     */
    public void setGodfather() {
        for (Player player : players) {
            if (player.getRole() == Role.mafia) {
                player.setRole(Role.godfather);
                return;
            }
        }
        for (Player player : players) {
            if (player.getRole() == Role.drLecter) {
                player.setRole(Role.godfather);
            }
        }
    }

    /**
     * Erases message field of all players.
     */
    private void eraseMessages() {
        for (ClientHandler clientHandler : clientHandlers)
            clientHandler.eraseMessage();
    }

    /**
     * Gets chat history from file.
     *
     * @return the history as string
     */
    public String getHistory() {
        String historyString = "History:\n";
        try {
            Scanner scanner = new Scanner(history);
            while (scanner.hasNextLine()) {
                historyString += scanner.nextLine() + "\n";
            }
            scanner.close();
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
        return historyString;
    }

    /**
     * Add a message to history file.
     *
     * @param message
     */
    private void addHistory(String message) {
        try {
            FileWriter writer = new FileWriter(history, true);
            writer.append(message + "\n");
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Clear history file.
     */
    private void clearHistory() {
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

    /**
     * Kill a player.
     *
     * @param player the player
     */
    public void killPlayer(Player player) {
        players.remove(player);
    }

    /**
     * Make players alive to be able to chat again.
     */
    private void alivePlayers() {
        for (ClientHandler clientHandler : clientHandlers)
            clientHandler.alive();
    }

    /**
     * Make players unprepared for next voting.
     */
    private void unpreparedPlayers() {
        for (ClientHandler clientHandler : clientHandlers)
            clientHandler.unprepared();
    }
}