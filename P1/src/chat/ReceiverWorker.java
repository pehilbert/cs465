package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import message.Message;
import static message.MessageTypes.NOTE;
import static message.MessageTypes.SHUTDOWN;

public class ReceiverWorker extends Thread
{
    Socket serverConnection = null;

    ObjectInputStream readFromNet = null;
    ObjectOutputStream writeToNet = null;

    Message message = null;

    public ReceiverWorker(Socket serverConnection)
    {
        this.serverConnection = serverConnection;

        try
        {
            readFromNet = new ObjectInputStream(serverConnection.getInputStream());
            writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
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
    

        switch (message.getType())
        {
            case SHUTDOWN:
                System.out.println("Received shutdown message from server, exiting");

                try
                {
                    serverConnection.close();
                }
                catch (IOException ex)
                {
                    // dont care gonna exit anyways
                }

                System.exit(0);

                break;

            case NOTE:
                System.out.println((String) message.getContent());

                try
                {
                    serverConnection.close();
                }
                catch (IOException ex)
                {
                    System.out.println(ex.toString());
                }

                break;
            
            default:
                // cannot occur
        }
    }
}
