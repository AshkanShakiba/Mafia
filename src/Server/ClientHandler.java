package Server;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ClientHandler implements Runnable {
    Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            String message;
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
            while((message=reader.readLine())!=null){
                if(message.equalsIgnoreCase("EXIT")) break;
                outputStream.write((message+" from "+clientSocket+"\n").getBytes());
            }
            clientSocket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}