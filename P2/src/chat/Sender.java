package chat;

import java.util.Scanner;
// import java.io.ObjectInputStream;
// import java.io.ObjectOutputStream;
import chat.message.Message;
import chat.message.MessageTypes;

public class Sender extends Thread implements MessageTypes {
    boolean hasJoined  = false;
    Scanner userInput;
    String inputLine = null;
    String command = null;
    String ip = null;
    String port = null;


    @Override
    public void run() {
        System.out.println("Please JOIN the chat before trying any other command");
        System.out.println("The command to JOIN the chat is JOIN IP PORT");

        while (true)
        {
            command = userInput.nextLine();
            Message message;
            
            // check if user is not joined
            if(!hasJoined)
            {
                String parts[] = inputLine.split(" ",3);
                command = parts[0];
                ip = parts[1];
                port = parts[2];

                // if command != JOIN
                if(!command.equals("JOIN"))
                {
                    System.out.println("Please JOIN the chat before trying any other command");
                    hasJoined = true;
                }
                else
                {
                    // send a JOIN to the node specified with ip and port
                    // create a node for recipient
                    message = new Message(JOIN, ChatClient.myNodeInfo);
                    NodeInfo recpient = new NodeInfo(ip,Integer.parseInt(port),"");
                    ChatClient.sendMessage(recpient, message);
                }
                    
            }
            // LEAVE
            else if (command.equals("LEAVE"))
            {
                // send all node with LEAVE message
                message = new Message(LEAVE, ChatClient.myNodeInfo);
                ChatClient.sendToAll(message);
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
            }
            // SHUTDOWN_ALL
            else if (command.equals("SHUTDOWN ALL"))
            {
                // send all node with SHUTDOWN_ALL message
                message = new Message(SHUTDOWN_ALL, ChatClient.myNodeInfo);
                ChatClient.sendToAll(message);
                // shutdown client
            }
            // Default
            else
            {
                // NOTE 
                // send all node with NOTE message 
                message = new Message(NOTE, ChatClient.myNodeInfo.getName() + command);
                ChatClient.sendToAll(message);
                break;
            }
        }
    }
}
