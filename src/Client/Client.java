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

    public Client(String ip,int port){
        this.ip=ip;
        this.port=port;
    }

    public static void main(String[] args){
        Client client=new Client("127.0.0.1",4321);
        if(!client.connectToServer()) {
            System.err.println("Connection failed");
        }
        else{
            System.out.println("Connected to the server");
            client.start();
        }
    }
    public boolean connectToServer(){
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
    public void start(){
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
}