package chat;

import java.util.Scanner;
// import java.io.ObjectInputStream;
// import java.io.ObjectOutputStream;
import chat.message.Message;
import chat.message.MessageTypes;
// import utils.PropertyHandler;

public class Sender extends Thread implements MessageTypes 
{
    boolean hasJoined  = false;
    Scanner userInput;
    String command = null;
    String ip = null;
    String port = null;


    @Override
    public void run() 
    {
        System.out.println("Please JOIN the chat before trying any other command");
        System.out.println("The command to JOIN the chat is JOIN IP PORT, or if you are the first person, just type JOIN");

        userInput = new Scanner(System.in);

        while (true)
        {
            command = userInput.nextLine();
            Message message;
            
            // check if user is not joined
            if (command.startsWith("JOIN")) {
                if (hasJoined) 
                {
                    System.out.println("You have already joined!");
                    continue;
                }

                String parts[] = command.split(" ",3);
                
                // if they provide an IP and port, actually send a JOIN message
                // otherwise, don't do anything except set hasJoined (for the first person)
                if (parts.length == 3)
                {
                    // send a JOIN to the node specified with ip and port
                    // create a node for recipient
                    ip = parts[1];
                    port = parts[2];

                    message = new Message(JOIN, (Object)ChatClient.myNodeInfo);
                    NodeInfo recpient = new NodeInfo(ip,Integer.parseInt(port),"");
                    ChatClient.sendMessage(recpient, message);
                }

                hasJoined = true;
            }
            // LEAVE
            else if (command.equals("LEAVE"))
            {
                if (!hasJoined) 
                {
                    System.out.println("You have to JOIN before you can LEAVE");
                    continue;
                }

                // send all node with LEAVE message
                message = new Message(LEAVE, (Object)ChatClient.myNodeInfo);
                ChatClient.sendToAll(message);

                // clear participants list
                ChatClient.participants.clear();

                // make joined flag false 
                hasJoined = false;
            }
            // SHUTDOWN
            else if (command.equals("SHUTDOWN"))
            {
                // if the user is currently in the chat, let everyone else know
                // they are shutting down
                if (hasJoined) 
                {
                    message = new Message(SHUTDOWN, (Object)ChatClient.myNodeInfo);
                    ChatClient.sendToAll(message);
                }

                // shutdown client
                System.exit(0);
            }
            // SHUTDOWN_ALL
            else if (command.equals("SHUTDOWN ALL"))
            {
                if (!hasJoined) 
                {
                    System.out.println("You have to JOIN before you can SHUTDOWN ALL");
                    continue;
                }

                // send all node with SHUTDOWN_ALL message
                message = new Message(SHUTDOWN_ALL, (Object)ChatClient.myNodeInfo);
                ChatClient.sendToAll(message);

                // shutdown client
                System.exit(0);
            }
            // Default
            else
            {
                if (!hasJoined) 
                {
                    System.out.println("You have to JOIN before you can send a NOTE");
                    continue;
                }

                // NOTE 
                // send all node with NOTE message 
                message = new Message(NOTE, (Object)(ChatClient.myNodeInfo.getName() + command));
                ChatClient.sendToAll(message);
            }
        }
    }
}
