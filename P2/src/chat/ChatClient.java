package chat;

import chat.message.Message;
import utils.NetworkUtilities;
import utils.PropertyHandler;
import java.util.Properties;
import java.io.IOException;
import java.io.ObjectOutputStream;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.net.Socket;

public class ChatClient implements Runnable {
    public static List<NodeInfo> participants = new ArrayList<NodeInfo>();
    public static NodeInfo myNodeInfo;
    static Receiver receiver = null;
    static Sender sender = null;

    public ChatClient(String propertiesFile) 
    {
        Properties properties = null;

        try 
        {
            properties = new PropertyHandler(propertiesFile);
        } 
        catch (IOException e) 
        {
            System.out.println("Could not open properties file");
            System.exit(1);
        }

        int myPort = 0;
        try 
        {
            myPort = Integer.parseInt(properties.getProperty("MY_PORT"));
        } 
        catch (NumberFormatException e) 
        {
            System.out.println("Could not receiver port");
            System.exit(1);
        }
        
        String myName = properties.getProperty("MY_NAME");
        if (myName == null)
        {
            System.out.println("Could not read my name");
            System.exit(1);
        }

        myNodeInfo = new NodeInfo(NetworkUtilities.getMyIP(), myPort, myName);
    }

    private static String nodeToString(NodeInfo node) 
    {
        return node.getName() + " | " + node.getAddress() + ":" + Integer.toString(node.getPort());
    }

    public static void sendMessage(NodeInfo recipient, Message msg) 
    {
        try 
        {
            System.out.println("[DEBUG] Trying to send a message of type " + Integer.toString(msg.getType()) + " to " + nodeToString(recipient));

            Socket sendSocket = new Socket(recipient.getAddress(), recipient.getPort());

            sendSocket.getInputStream();
            ObjectOutputStream writeToNet = (ObjectOutputStream)sendSocket.getOutputStream();

            writeToNet.writeObject(msg);

            sendSocket.close();

            System.out.println("[DEBUG] Message sent to " + nodeToString(recipient));
        } 
        catch (IOException e) 
        {
            System.out.println("[DEBUG] Error sending message to " + nodeToString(recipient));
        }
    }

    public static void sendToAll(Message msg) 
    {
        System.out.println("[DEBUG] Sending message of type " + Integer.toString(msg.getType()) + " to all participants");

        Iterator<NodeInfo> iter = participants.iterator();

        while (iter.hasNext()) 
        {
            NodeInfo participant = iter.next();
            sendMessage(participant, msg);
        }

        System.out.println("[DEBUG] Finished sending message of type " + Integer.toString(msg.getType()) + " to all participants");
    }

    @Override
    public void run() 
    {
        (receiver = new Receiver()).start();
        (sender = new Sender()).start();
    }

    public static void main(String[] args) 
    {
        String propertiesFile = null;

        try
        {
            propertiesFile = args[0];
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            propertiesFile = "config/ChatNodeDefaults.properties";
        }

        (new ChatClient(propertiesFile)).run();
    }
}
