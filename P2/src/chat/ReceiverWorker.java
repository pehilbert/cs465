package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import chat.message.Message;
import chat.message.MessageTypes;

public class ReceiverWorker extends Thread implements MessageTypes {
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
        
        // TODO: big ass switch statement
        // Note: You can probably pull a lot from the old server and old client receiver worker
        switch (message.getType()) 
        {
            case JOIN:
            break;

            case LEAVE:
            break;

            case NOTE:
            break;

            case SHUTDOWN:
            break;

            case SHUTDOWN_ALL:
            break;

            case JOINED:
            break;

            case INITIALIZE:
            break;

            default:
            // shouldn't be possible but whatever
            System.out.println("ERROR: You somehow got to the default case?? You fucked up");
            break;
        }
    }
}
