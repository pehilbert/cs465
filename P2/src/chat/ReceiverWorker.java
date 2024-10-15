package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import chat.message.Message;
import chat.message.MessageTypes;
import java.util.ArrayList;
import java.util.Iterator;

public class ReceiverWorker extends Thread implements MessageTypes 
{
    Socket participantConnection;
    ObjectInputStream readFromNet = null;
    ObjectOutputStream writeToNet = null;
    Message message = null;

    public ReceiverWorker(Socket participantConnection)
    {
        this.participantConnection = participantConnection;

        try
        {
            readFromNet = new ObjectInputStream(participantConnection.getInputStream());
            writeToNet = new ObjectOutputStream(participantConnection.getOutputStream());
        }
        catch(IOException ex)
        {
            System.out.println(ex.toString());
        }
    }

    @Override
    @SuppressWarnings("unchecked") // supress warnings about unchecked casts, since we know message content should only be of certain types
    public void run()
    {
        try
        {
            message = (Message)readFromNet.readObject();
        }
        catch (IOException | ClassNotFoundException ex)
        {
            System.out.println(ex.toString());
            System.exit(1);
        }
        
        switch (message.getType()) 
        {
            case JOIN:

                // grab the sender's NodeInfo
                NodeInfo joinerNodeInfo = (NodeInfo)message.getContent();
                System.out.println("Received JOIN from " + joinerNodeInfo.getName());

                // Send INITIALIZE message back to sender with myself included in the list
                ArrayList<NodeInfo> listToSend = new ArrayList<NodeInfo>(ChatClient.participants);
                listToSend.add(ChatClient.myNodeInfo);

                ChatClient.sendMessage(joinerNodeInfo, new Message(INITIALIZE, (Object)listToSend));

                // Forward JOINED message to other participants
                ChatClient.sendToAll(new Message(JOINED, (Object)joinerNodeInfo));

                // Add new person to our own list
                System.out.println("Adding " + joinerNodeInfo.getName() + " to list");
                ChatClient.participants.add(joinerNodeInfo);

                break;


            case NOTE:

                // Just print the note
                System.out.println(message.getContent());
                break;


            case LEAVE:
            case SHUTDOWN:

                // debug message
                System.out.println("Received LEAVE/SHUTDOWN, removing participant.");

                // remove person from the list
                ChatClient.participants.remove( ( NodeInfo )message.getContent() );
    
                break;

            case SHUTDOWN_ALL:

                // debug message 
                System.out.println("Received SHUTDOWN ALL, terminating program.");

                // Terminate processes
                System.exit(0);

                break;

            case JOINED:

                // debug message
                System.out.println("Received JOINED, adding participant.");

                // add person
                ChatClient.participants.add( ( NodeInfo )message.getContent() );

                break;

            case INITIALIZE:

                // debug message
                System.out.println("Received INITIALIZE, setting participants list.");
                System.out.print("Received list: ");
                ArrayList<NodeInfo> receivedList = (ArrayList<NodeInfo>)message.getContent();
                Iterator<NodeInfo> iter = receivedList.iterator();

                while (iter.hasNext()) 
                {
                    NodeInfo participant = iter.next();
                    System.out.print(participant.getName() + " | ");
                }

                System.out.println();

                // set participants list
                ChatClient.participants = receivedList;

                break;

            default:

                // shouldn't be possible but whatever
                System.out.println("ERROR: Somehow got to the default case.");
                break;
        }
    }
}
