package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static String ip;
    private static int port;
    private static Socket socket;
    private static DataInputStream inputStream;
    private static DataOutputStream outputStream;
    private static Scanner scanner=new Scanner(System.in);

    public static void main(String[] args){
        //setIpAndPort();

        ip="127.0.0.1";
        port=4321;
        connectToServer();

        System.out.println("Connected to the server");
        String message="";
        new Thread(new MessageReader(inputStream)).start();
        while(true){
            message=scanner.nextLine();
            try {
                outputStream.writeUTF((message+"\n"));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            if(message.equalsIgnoreCase("EXIT")) break;
        }
    }
    private static void setIpAndPort(){
        System.out.print("ip: ");
        ip=scanner.next();
        System.out.print("port: ");
        port=scanner.nextInt();
        while(!connectToServer()) {
            System.err.println("Connection failed!\n Try again...");
            System.out.print("ip: ");
            ip=scanner.next();
            System.out.print("port: ");
            port=scanner.nextInt();
        }
    }
    private static boolean connectToServer(){
        try {
            socket=new Socket(ip,port);
            inputStream=new DataInputStream(socket.getInputStream());
            outputStream=new DataOutputStream(socket.getOutputStream());
            return true;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return false;
    }
}