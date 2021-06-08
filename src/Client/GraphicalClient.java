package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

public class GraphicalClient extends JPanel {
    private ConsoleClient client;

    private static DefaultListModel<String> listModel = new DefaultListModel<>();
    private static JList<String> messageList = new JList<>(listModel);
    private static JTextField inputField = new JTextField();

    public GraphicalClient(ConsoleClient client){
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
//        System.out.println(args[0]);
//        System.out.println(args[1]);
//        Client client=new Client(args[0],Integer.parseInt(args[1]));
        ConsoleClient client=new ConsoleClient("127.0.0.1",4321);
        client.connectToServer();
        GraphicalClient gamePane = new GraphicalClient(client);

        JFrame frame = new JFrame("♠ Mafia ♠");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(274, 500);
        frame.getContentPane().add(gamePane, BorderLayout.CENTER);
        frame.setVisible(true);

        String message;
        Scanner scanner=new Scanner(client.getInputStream());
        while((message=scanner.nextLine())!=null){
            listModel.addElement(message);
//            if(message.length()>2)
//                listModel.addElement(message.substring(2));
        }
    }
}