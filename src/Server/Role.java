package Server;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * The enum Role.
 *
 * @author Ashkan Shakiba
 * @version 2021-11-6
 */
public enum Role {
    /**
     * Godfather role.
     */
    godfather,
    /**
     * Dr lecter role.
     */
    drLecter,
    /**
     * Mafia role.
     */
    mafia,
    /**
     * Doctor role.
     */
    doctor,
    /**
     * Detective role.
     */
    detective,
    /**
     * Professional role.
     */
    professional,
    /**
     * Citizen role.
     */
    citizen,
    /**
     * Mayor role.
     */
    mayor,
    /**
     * Psychiatrist role.
     */
    psychiatrist,
    /**
     * Die hard role.
     */
    dieHard;

    /**
     * Sets roles for given players.
     *
     * @param players the players
     */
    public static void setRoles(ArrayList<Player> players) {
        SecureRandom random = new SecureRandom();
        int index, turn = 1, playersCount = players.size();
        while (neutralPlayerExist(players)) {
            index = random.nextInt(playersCount);
            while (players.get(index).hasRole())
                index = random.nextInt(playersCount);
            players.get(index).setRole(getRole(turn));
            turn++;
        }
    }

    /**
     * Gets role by turn.
     *
     * @param turn
     * @return the role
     */
    private static Role getRole(int turn) {
        if (turn == 1)
            return godfather;
        else if (turn == 2)
            return detective;
        else if (turn == 3)
            return doctor;
        else if (turn == 5)
            return mayor;
        else if (turn == 7)
            return drLecter;
        else if (turn == 8)
            return dieHard;
        else if (turn == 9)
            return professional;
        else if (turn == 10)
            return psychiatrist;
        else if (turn == 4 || turn == 11 || turn == 14)
            return mafia;
        else
            return citizen;
    }

    /**
     * Check if there is any neutral player.
     *
     * @param players
     * @return true if there is, false if not.
     */
    private static boolean neutralPlayerExist(ArrayList<Player> players) {
        for (Player player : players)
            if (!player.hasRole())
                return true;
        return false;
    }
}