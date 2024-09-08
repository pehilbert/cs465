package chat;

import java.io.IOException;
import java.net.ServerSocket;
import utils.NetworkUtilities;

import java.util.logging.Level;
import java.util.logging.Logger;

import utils.PropertyHandler;
import java.util.Properties;

import java.util.List;
import java.util.ArrayList;

/*
    Chat server class
    Will read and confgiure information as well as start up the 
    ChatServer

    @author wolfdieterotte
*/

public class ChatServer implements Runnable{
    
    // array list that contains all participants info
    public static List<NodeInfo> participants = new ArrayList<>();

    // connection related variables
    int port =0;
    ServerSocket serverSocket = null;

    public ChatServer(String propertiesFile)
    {
        Properties properties = null;

        try
        {
            properties = new PropertyHandler(propertiesFile);
        }
        catch(IOException ex)
        {
            System.out.println(ex.toString());
            System.exit(1);
        }

        // get port number
        try
        {
            port = Integer.parseInt(properties.getProperty("PORT"));
        }
        catch (NumberFormatException ex)
        {
            System.out.println(ex.toString());
            System.exit(1);
        }
    }

    @Override
    public void run()
    {
        // setting up server socket
        try
        {
            serverSocket = new ServerSocket(port);
            System.out.println("ChatServer listening on" + NetworkUtilities.getMyIP() + ":" + port);
        }
        catch(IOException ex)
        {
            System.out.println(ex.toString());
            System.exit(1);
        }

        // server loop
        while(true)
        {
            try
            {
                (new ChatServerWorker(serverSocket.accept())).start();
            }
            catch(IOException ex)
            {
                System.out.println(ex.toString());
            }
        }
    }

    public static void main(String[] args)
    {
        String propertiesFile = null;

        try
        {
            propertiesFile = args[0];
        }
        catch( ArrayIndexOutOfBoundsException ex)
        {
            propertiesFile = "config/ChatServer.properties";
        }

        // start server
        (new ChatServer(propertiesFile)).run();
    }
}
