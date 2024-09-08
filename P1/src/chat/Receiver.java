// claire

package chat;

import java.io.IOException;

import java.net.ServerSocket;

import java.util.logging.Level;
import java.util.logging.Logger;

//import java.io.IOException;

public class Receiver extends Thread 
{

    static ServerSocket receiverSocket = null;
    static String userName = null;

    // Constructor 
    public Receiver()
    {
        try 
        {
            receiverSocket = new ServerSocket(ChatClient.myNodeInfo.getPort());
            System.out.println("[Receiver.Receiver] receiver socket created, listening on <IMPLEMENT PORT!>");
        }

        catch( IOException ex ) // change to print
        {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, "ERROR CREATING RECEIVER");
        }
    
        System.out.println(ChatClient.myNodeInfo.getName() + " listening on <IMPLEMENT PORT!>" );

    }

    // thread entry point
    @Override
    public void run()
    {
        // begin server loop
        while( true )
        {
            // listen for connections and accept
            try
            {
                ( new ReceiverWorker( receiverSocket.accept() ) ).start();
            }
            catch ( IOException e )
            {
                System.err.println("[Receiver.run] Warning: Error accepting client");
            }
        }
    }
}
