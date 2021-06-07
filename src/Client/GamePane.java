package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

public class GamePane extends JPanel {
    private Client client;

    private static DefaultListModel<String> listModel = new DefaultListModel<>();
    private static JList<String> messageList = new JList<>(listModel);
    private static JTextField inputField = new JTextField();

    public GamePane(Client client){
        this.client=client;

        setLayout(new BorderLayout());
        add(new JScrollPane(messageList), BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);

        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String message = inputField.getText();
                client.send(message);
                inputField.setText("");
            }
        });
    }

    public static void main(String[] args){
        Client client=new Client("127.0.0.1",4321);
        client.connectToServer();
        GamePane messagePane = new GamePane(client);

        JFrame frame = new JFrame("MaFiA");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(512, 512);
        frame.getContentPane().add(messagePane, BorderLayout.CENTER);
        frame.setVisible(true);

        String message;
        Scanner scanner=new Scanner(client.getInputStream());
        while((message=scanner.nextLine())!=null){
            if(message.length()>2)
                listModel.addElement(message);
        }
    }
}