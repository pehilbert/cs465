package chat;

import java.util.Scanner;
// import java.io.ObjectInputStream;
// import java.io.ObjectOutputStream;
import chat.message.Message;
import chat.message.MessageTypes;

public class Sender extends Thread implements MessageTypes {
    boolean hasJoined  = false;
    Scanner userInput;
    String command = null;
    String ip = null;
    String port = null;


    @Override
    public void run() {
        System.out.println("Please JOIN the chat before trying any other command");
        System.out.println("The command to JOIN the chat is JOIN IP PORT, or if you are the first person, just type JOIN");

        userInput = new Scanner(System.in);

        while (true)
        {
            command = userInput.nextLine();
            Message message;
            
            // check if user is not joined
            if (command.startsWith("JOIN")) {
                String parts[] = command.split(" ",3);
                
                // if they provide an IP and port, actually send a JOIN message
                // otherwise, don't do anything except set hasJoined (for the first person)
                if (parts.length == 3)
                {
                    // send a JOIN to the node specified with ip and port
                    // create a node for recipient
                    ip = parts[1];
                    port = parts[2];

                    message = new Message(JOIN, ChatClient.myNodeInfo);
                    NodeInfo recpient = new NodeInfo(ip,Integer.parseInt(port),"");
                    ChatClient.sendMessage(recpient, message);
                }

                hasJoined = true;
            }
            // LEAVE
            else if (command.equals("LEAVE"))
            {
                // send all node with LEAVE message
                message = new Message(LEAVE, ChatClient.myNodeInfo);
                ChatClient.sendToAll(message);

                // clear participants list
                ChatClient.participants.clear();

                // make joined flag false 
                hasJoined = false;
            }
            // SHUTDOWN
            else if (command.equals("SHUTDOWN"))
            {
                // send all node with SHUTDOWN message
                message = new Message(SHUTDOWN, ChatClient.myNodeInfo);
                ChatClient.sendToAll(message);

                // shutdown client
                System.exit(0);
            }
            // SHUTDOWN_ALL
            else if (command.equals("SHUTDOWN ALL"))
            {
                // send all node with SHUTDOWN_ALL message
                message = new Message(SHUTDOWN_ALL, ChatClient.myNodeInfo);
                ChatClient.sendToAll(message);

                // shutdown client
                System.exit(0);
            }
            // Default
            else
            {
                // NOTE 
                // send all node with NOTE message 
                message = new Message(NOTE, ChatClient.myNodeInfo.getName() + command);
                ChatClient.sendToAll(message);
            }
        }
    }
}
