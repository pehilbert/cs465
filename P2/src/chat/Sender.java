package chat;

import java.util.Scanner;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Sender extends Thread {
    boolean hasJoined  = false;
    Scanner userInput;
    String inputLine = null;
    String command = null;
    String ip = null;
    String port = null;


    @Override
    public void run() {

        
        System.out.println("Please JOIN the chat before trying any other command");
        System.out.println("The command to JOIN the chat is JOIN IP PORT")
        while (true)
        {
            command = userInput.nextLine();
            
            // check if user is not joined
            if(!hasJoined)
            {
                String parts[] = inputLine.split(" ",3);
                command = parts[0];
                ip = parts[1];
                port = parts[2];
                Message message;

                // if command != JOIN
                if(command != "JOIN")
                {
                    System.out.println("Please JOIN the chat before trying any other command");
                    hasJoined = true;
                }
                else
                {
                    // send a JOIN to the node specified with ip and port
                    // create a node for recipient
                    message = new Message(JOIN,myNodeInfo);
                    NodeInfo recpient = new NodeInfo(ip,Integer.parseInt(port),"");
                    sendMessage(recpient, message);
                }
                    
            }
            else
            {
                // do a switch statment depending on input 
                switch (command)
                {   
                    // LEAVE
                    case LEAVE:
                        // send all node with LEAVE message
                        message = new Message(LEAVE,myNodeInfo);
                        sendToAll(message);
                        // make joined flag false 
                        hasJoined = false;
                        break;

                    // SHUTDOWN
                    case SHUTDOWN:
                        // send all node with SHUTDOWN message
                        message = new Message(SHUTDOWN,myNodeInfo);
                        sendToAll(message);
                        // shutdown client
                        break;

                    // SHUTDOWN_ALL
                    case SHUTDOWN_ALL:
                        // send all node with SHUTDOWN_ALL message
                        message = new Message(SHUTDOWN_ALL,myNodeInfo);
                        sendToAll(message);
                        // shutdown client
                        break;

                    // Default 
                    default:
                        // NOTE 
                        // send all node with NOTE message 
                        message = new Message(NOTE,myNodeInfo.name + command);
                        sendToALl(message);
                        break;
                }
            }
            
        }
    }
}
