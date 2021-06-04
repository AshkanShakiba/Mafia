package Server;

import java.security.SecureRandom;
import java.util.ArrayList;

public enum Role {
    /*
    godfather(1),
    drLecter(1),
    mafia(3),
    doctor(1),
    detective(1),
    professional(1),
    citizen(4),
    mayor(1),
    psychiatrist(1),
    dieHard(1);
     */
    godfather,
    drLecter,
    mafia,
    doctor,
    detective,
    professional,
    citizen,
    mayor,
    psychiatrist,
    dieHard;
    /*
    private int number;
    Role(int number){
        this.number=number;
    }
    private int getNumber(){
        return number;
    }
    private void decrease(){
        number--;
    }
    */
    public static void setRoles(ArrayList<Player> players){
        SecureRandom random=new SecureRandom();
        int index,turn=1,playersCount=players.size();
        while(neutralPlayerExist(players)){
            index=random.nextInt(playersCount);
            while(players.get(index).hasRole())
                index=random.nextInt(playersCount);
            players.get(index).setRole(getRole(turn));
            turn++;
        }
    }
    private static Role getRole(int turn){
        if(turn==1)
            return godfather;
        else if(turn==2)
            return detective;
        else if(turn==3)
            return doctor;
        else if(turn==5)
            return mayor;
        else if(turn==7)
            return drLecter;
        else if(turn==8)
            return dieHard;
        else if(turn==9)
            return professional;
        else if(turn==10)
            return psychiatrist;
        else if(turn==4 || turn==11 || turn==14)
            return mafia;
        else
            return citizen;
        /*
        // #1
        if(godfather.getNumber()>0){
            godfather.decrease();
            return godfather;
        }
        // #2
        else if(detective.getNumber()>0){
            detective.decrease();
            return detective;
        }
        // #3
        else if(doctor.getNumber()>0){
            doctor.decrease();
            return doctor;
        }
        // #4
        else if(mafia.getNumber()>2){
            mafia.decrease();
            return mafia;
        }
        // #5
        else if(mayor.getNumber()>0){
            mayor.decrease();
            return mayor;
        }
        // #6
        else if(citizen.getNumber()>3){
            citizen.decrease();
            return citizen;
        }
        // #7
        else if(drLecter.getNumber()>0){
            drLecter.decrease();
            return drLecter;
        }
        // #8
        else if(dieHard.getNumber()>0){
            dieHard.decrease();
            return dieHard;
        }
        // #9
        else if(professional.getNumber()>0){
            professional.decrease();
            return professional;
        }
        // #10
        else if(psychiatrist.getNumber()>0){
            psychiatrist.decrease();
            return psychiatrist;
        }
        // #11
        else if(mafia.getNumber()>1){
            mafia.decrease();
            return mafia;
        }
        // #12
        else if(citizen.getNumber()>2){
            citizen.decrease();
            return citizen;
        }
        // #13
        else if(citizen.getNumber()>1){
            citizen.decrease();
            return citizen;
        }
        // #14
        else if(mafia.getNumber()>0){
            mafia.decrease();
            return mafia;
        }
        // #15
        else if(citizen.getNumber()>0){
            citizen.decrease();
            return citizen;
        }
        return null;
        */
    }
    private static boolean neutralPlayerExist(ArrayList<Player> players){
        for(Player player:players)
            if(!player.hasRole())
                return true;
        return false;
    }
}