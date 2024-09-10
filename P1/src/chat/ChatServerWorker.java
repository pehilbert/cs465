package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import java.util.Iterator;

import chat.message.Message;
import chat.message.MessageTypes;

public class ChatServerWorker extends Thread implements MessageTypes{

    Socket chatConnection = null;

    ObjectOutputStream writeToNet = null;
    ObjectInputStream readFromNet = null;

    Message message = null;

    public ChatServerWorker(Socket chatConnection)
    {
        this.chatConnection = chatConnection;
    }

    @Override
    public void run()
    {
        NodeInfo participantInfo = null;
        Iterator <NodeInfo> participantIterator;

        try
        {
            // get object streams
            writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
            readFromNet = new ObjectInputStream(chatConnection.getInputStream());

            // read message
            message = (Message) readFromNet.readObject();

            chatConnection.close();
        }
        catch(IOException | ClassNotFoundException ex)
        {
            System.out.println(ex.toString());
            System.exit(1);
        }

        //System.out.println("Message Type" + message.getType() ) ;

        switch(message.getType())
        {
            case JOIN:
                // read participants nodeinfo
                NodeInfo joiningParticipantNodeInfo = (NodeInfo) message.getContent();

                // add client to list of participants
                ChatServer.participants.add(joiningParticipantNodeInfo);

                System.out.print(joiningParticipantNodeInfo.getName()+ "Joined. All current participants: ");

                participantIterator = ChatServer.participants.iterator();

                while(participantIterator.hasNext())
                {
                    participantInfo = participantIterator.next();
                    System.out.print(participantInfo.getName());
                }
                System.out.println();
                break;

            case LEAVE:
            case SHUTDOWN:
                
                // remove the participants info
                NodeInfo leavingParticipantInfo = (NodeInfo) message.getContent();
                if(ChatServer.participants.remove(leavingParticipantInfo))
                {
                    System.err.println(leavingParticipantInfo.getName() + "removed");
                }
                else
                {
                    System.err.println(leavingParticipantInfo.getName() + "not found");
                }

                System.out.print(leavingParticipantInfo.getName()+ "left. Remaining participants: ");

                participantIterator = ChatServer.participants.iterator();

                while(participantIterator.hasNext())
                {
                    participantInfo = participantIterator.next();
                    System.out.print(participantInfo.getName());
                }
                System.out.println();
                break;

            case SHUTDOWN_ALL:
                
                // run through all the particiapants and shut each down
                participantIterator = ChatServer.participants.iterator();
                while(participantIterator.hasNext())
                {
                    participantInfo = participantIterator.next();
                    
                    try
                    {
                        // connect to client
                        chatConnection = new Socket(participantInfo.getAddress(), participantInfo.getPort());

                        // open object stream
                        writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
                        readFromNet = new ObjectInputStream(chatConnection.getInputStream());

                        writeToNet.writeObject(new Message(SHUTDOWN, null));

                        chatConnection.close();
                    }
                    catch(IOException ex)
                    {
                        System.out.println(ex.toString());
                    }
                }

                System.out.println("Shutting down and exiting");

                System.exit(0);
               
            case NOTE:

                // display note
                System.out.println((String) message.getContent());

                // SYstem.out.println((String)message.)

                // run through all the particiapants and send each a note
                participantIterator = ChatServer.participants.iterator();

                while(participantIterator.hasNext())
                {

                    participantInfo = participantIterator.next();
                    
                    try
                    {
                        // connect to client
                        chatConnection = new Socket(participantInfo.getAddress(), participantInfo.getPort());

                        // open object stream
                        writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
                        readFromNet = new ObjectInputStream(chatConnection.getInputStream());

                        // write message
                        writeToNet.writeObject(message);

                        System.out.println("Sent msg to " + (String)participantInfo.getName());

                        chatConnection.close();
                    }
                    catch(IOException ex)
                    {
                        System.out.println(ex.toString());
                    }

                }
                break;

            default:
                // cannot occur

        }
    }
    
}
