package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import java.net.Socket;

import message.MessageTypes;
import message.Message;

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
        ObjectOutputStream writeToNet;
        ObjectInputStream readFromNet;

        while (true)
        {
            inputLine = userInput.nextLine();

            if (inputLine.startsWith("JOIN"))
            {
                // The user cannot join if they are already in the chat
                if (hasJoined)
                {
                    System.out.println("You already joined!");
                    continue;
                }

                // get connectivity info if provided in the command
                String[] connectivityInfo = inputLine.split("[ ]+");

                try
                {
                    ChatClient.serverNodeInfo = new NodeInfo(connectivityInfo[1], Integer.parseInt(connectivityInfo[2]));
                }
                catch (ArrayIndexOutOfBoundsException e)
                {
                    // do nothing if no info was provided
                }

                // we cannot do anything without the server's information
                if (ChatClient.serverNodeInfo == null)
                {
                    System.out.println("No server info node");
                    continue;
                }

                // connect to server and send join message
                try
                {
                    serverConnection = new Socket(ChatClient.serverNodeInfo.getIp(), ChatClient.serverNodeInfo.getPort());

                    readFromNet = new ObjectInputStream(serverConnection.getInputStream());
                    writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());

                    writeToNet.writeObject(new Message(MessageTypes.JOIN, ChatClient.myNodeInfo));

                    serverConnection.close();
                } 
                catch (IOException e)
                {
                    System.out.println("Error connecting to server");
                    continue;
                }

                // everything went well if we got here, so set has joined flag to true
                hasJoined = true;
            }
            else if (inputLine.startsWith("LEAVE"))
            {
                // the user cannot leave if they are not in the chat
                if (!hasJoined)
                {
                    System.out.println("You are not in the chat yet!");
                    continue;
                }

                // we cannot do anything without the server's information
                if (ChatClient.serverNodeInfo == null)
                {
                    System.out.println("No server info node");
                    continue;
                }

                // connect to server and send leave message
                try
                {
                    serverConnection = new Socket(ChatClient.serverNodeInfo.getIp(), ChatClient.serverNodeInfo.getPort());

                    readFromNet = new ObjectInputStream(serverConnection.getInputStream());
                    writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());

                    writeToNet.writeObject(new Message(MessageTypes.LEAVE, ChatClient.myNodeInfo));

                    serverConnection.close();
                } 
                catch (IOException e)
                {
                    System.out.println("Error connecting to server");
                    continue;
                }

                // everything went well if we got here, so set has joined flag to false
                hasJoined = false;
            }
            else if (inputLine.startsWith("SHUTDOWN"))
            {
                // if the user is in the chat, we must let the server know, otherwise this is not needed
                // (this is essentially the same thing as leaving the chat)
                if (hasJoined && ChatClient.serverNodeInfo != null)
                {
                    // connect to server and send shutdown message
                    try
                    {
                        serverConnection = new Socket(ChatClient.serverNodeInfo.getIp(), ChatClient.serverNodeInfo.getPort());

                        readFromNet = new ObjectInputStream(serverConnection.getInputStream());
                        writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());

                        writeToNet.writeObject(new Message(MessageTypes.SHUTDOWN, ChatClient.myNodeInfo));

                        serverConnection.close();
                    } 
                    catch (IOException e)
                    {
                        System.out.println("Error connecting to server");
                        System.exit(1);
                    }
                }

                // if all went well, exit the program successfully
                System.out.println("Shutting down...");
                System.exit(0);
            }
            else if (inputLine.startsWith("SHUTDOWN ALL"))
            {
                // the user cannot shutdown all unless they're in the chat
                if (!hasJoined)
                {
                    System.out.println("You are not in the chat yet!");
                    continue;
                }

                // connect to server and send shutdown all message if possible
                if (ChatClient.serverNodeInfo != null)
                {
                    try
                    {
                        serverConnection = new Socket(ChatClient.serverNodeInfo.getIp(), ChatClient.serverNodeInfo.getPort());

                        readFromNet = new ObjectInputStream(serverConnection.getInputStream());
                        writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());

                        writeToNet.writeObject(new Message(MessageTypes.SHUTDOWN_ALL, ChatClient.myNodeInfo));

                        serverConnection.close();
                    } 
                    catch (IOException e)
                    {
                        System.out.println("Error connecting to server");
                        System.exit(1);
                    }
                }

                // if all went well, exit successfully
                System.out.println("Shutting down...");
                System.exit(0);
            }
            else
            {
                if (!hasJoined)
                {
                    System.out.println("You cannot send a message when you are not in the chat");
                    continue;
                }

                // we cannot do anything without the server's information
                if (ChatClient.serverNodeInfo == null)
                {
                    System.out.println("No server info node");
                    continue;
                }

                // connect to server and send note
                try
                {
                    serverConnection = new Socket(ChatClient.serverNodeInfo.getIp(), ChatClient.serverNodeInfo.getPort());

                    readFromNet = new ObjectInputStream(serverConnection.getInputStream());
                    writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());

                    writeToNet.writeObject(new Message(MessageTypes.NOTE, String.format("%s: %s", ChatClient.myNodeInfo.getName(), inputLine)));

                    serverConnection.close();
                } 
                catch (IOException e)
                {
                    System.out.println("Error connecting to server");
                    continue;
                }
            }
        }
    }
}
