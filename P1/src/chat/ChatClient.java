package chat;

import java.lang.ArrayIndexOutOfBoundsException;
import java.io.IOException;

public class ChatClient implements Runnable {
    static Receiver receiver = null;
    static Sender sender = null;

    public static NodeInfo myNodeInfo = null;
    public static NodeInfo serverNodeInfo = null;

    public ChatClient(String propertiesFile) 
    {
        Properties properties = null;

        try 
        {
            Properties properties = new PropertyHandler(propertiesFile);
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

        int serverPort = 0;
        try 
        {
            serverPort = Integer.parseInt(properties.getProperty("SERVER_PORT"));
        } 
        catch (NumberFormatException e) 
        {
            System.out.println("Could not receiver port");
            System.exit(1);
        }

        String serverIp = properties.getProperty("SERVER_IP");
        if (serverIp == null)
        {
            System.out.println("Could not read server IP");
            System.exit(1);
        }

        if (serverPort != 0 && serverIp != null)
        {
            serverNodeInfo = new NodeInfo(serverIp, serverPort);
        }
    }

    @Override
    public void run() {
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
