package Client;

import java.io.InputStream;
import java.util.Scanner;

public class MessageReader implements Runnable{
    private Scanner scanner;

    public MessageReader(InputStream inputStream){
        scanner =new Scanner(inputStream);
    }

    public void run(){
        try {
            String message;
            while ((message = scanner.nextLine()) != null) {
                System.out.println(message);
//                if(message.length()>2)
//                    System.out.print(message.substring(2)+"\n");
            }
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }
}