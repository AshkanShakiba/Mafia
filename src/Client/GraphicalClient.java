package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

/**
 * The entry class of graphical client application.
 */
public class GraphicalClient extends JPanel {
    private ConsoleClient client;

    private static DefaultListModel<String> listModel = new DefaultListModel<>();
    private static JList<String> messages = new JList<>(listModel);
    private static JTextField input = new JTextField();

    /**
     * Instantiates a new Graphical client.
     *
     * @param client the client which connect to the server
     */
    public GraphicalClient(ConsoleClient client) {
        this.client = client;

        setLayout(new BorderLayout());
        add(new JScrollPane(messages), BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);

        input.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                String message = input.getText();
                client.send(message);
                input.setText("");
                if (message.equalsIgnoreCase("EXIT")) System.exit(0);
            }
        });
    }

    /**
     * The entry point of graphical client application.
     *
     * @param args the input arguments
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String ip;
        int port;
        System.out.print("ip: ");
        ip = input.nextLine();
        System.out.print("port: ");
        port = Integer.parseInt(input.nextLine());
        ConsoleClient client = new ConsoleClient(ip, port);
        //ConsoleClient client = new ConsoleClient("127.0.0.1", 4321);
        if (!client.connectToServer()) {
            System.err.println("Connection failed");
            System.exit(-1);
        }
        client.connectToServer();
        GraphicalClient graphicalClient = new GraphicalClient(client);

        JFrame frame = new JFrame("Mafia");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(275, 500);
        frame.getContentPane().add(graphicalClient, BorderLayout.CENTER);
        frame.setVisible(true);

        String message;
        Scanner scanner = new Scanner(client.getInputStream());
        while ((message = scanner.nextLine()) != null) {
            listModel.addElement(message);
        }
    }
}