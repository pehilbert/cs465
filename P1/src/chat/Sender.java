package chat;

import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.net.Socket;

import message.MessageTypes;

public class Sender extends Thread implements MessageTypes 
{
    Socket serverConnection = null;
    Scanner userInput;
    String inputLine = null;
    boolean hasJoined;

    public Sender()
    {
        userInput = new Scanner(System.in);
        hasJoined = false;
    }

    @Override
    public void run()
    {
        ObjectInputStream writeToNet;
        ObjectOutputStream readFromNet;

        while (true)
        {
            inputLine = userInput.nextLine();

            if (inputLine.startsWith("JOIN"))
            {
                if (hasJoined)
                {
                    System.out.println("You already joined!");
                    continue;
                }

                String[] connectivityInfo = inputLine.split("[]+");

                try
                {
                    ChatClient.serverNodeInfo = new NodeInfo(connectivityInfo[1], Integer.parseInt(connectivityInfo[2]));
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    // do nothing :)
                }

                if (ChatClient.serverNodeInfo == null)
                {
                    System.out.println("No server info node");
                    continue;
                }

                try
                {
                    serverConnection = new Socket(ChatClient.serverNodeInfo.getIp(), ChatClient.serverNodeInfo.getPort());

                    readFromNet = new ObjectInputStream(serverConnection.getInputStream());
                }
            }
            else if (inputLine.startsWith("LEAVE"))
            {
                if (!hasJoined)
                {
                    System.out.println("You are not in the chat yet!");
                    continue;
                }
            }
            else if (inputLine.startsWith("SHUTDOWN"))
            {
                if (!hasJoined)
                {
                    System.out.println("You are not in the chat yet!");
                    continue;
                }
            }
            else if (inputLine.startsWith("SHUTDOWN ALL"))
            {
                if (!hasJoined)
                {
                    System.out.println("You are not in the chat yet!");
                    continue;
                }
            }
            else
            {
                if (!hasJoined)
                {
                    System.out.println("You cannot send a message when you are not in the chat");
                    continue;
                }
            }
        }
    }
}
