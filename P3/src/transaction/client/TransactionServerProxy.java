package transaction.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import transaction.comm.Message;
import transaction.comm.MessageTypes;


/**
 * This class represents the proxy that acts on behalf of the transaction server on the client side.
 * It provides an implementation of the coordinator interface to the client, hiding the fact
 * that there is a network in between.
 * From the client's perspective, an object of this class IS the transaction.
 * @author wolfdieterotte
 */
public class TransactionServerProxy implements MessageTypes{

    String host = null;
    int port;

    private Socket serverConnection = null;
    private ObjectOutputStream writeToNet = null;
    private ObjectInputStream readFromNet = null;
    private Integer transactionID = 0;

    
    /**
     * Constructor
     * @param host IP address of the transaction server
     * @param port port number of the transaction server
     */
    TransactionServerProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    
    /**
     * Opens a transaction
     * 
     * @return the transaction ID 
     */
    public int openTransaction() {

        // open up connection to server
        // ...
        int transactionID = -1;

        System.out.println("Made it here 1");


        // throw in a try/except idk what might go wrong
        try 
        {   
            // create new connections
            serverConnection = new Socket(host, port);

            System.out.println("Made it here 1.1");
            writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());

            System.out.println("Made it here 1.2");
            readFromNet = new ObjectInputStream(serverConnection.getInputStream());
            System.out.println("Made it here 1.3");


            // send OPEN_TRANSACTION message & receive transactionID
            // leave connection open!
            // ...
            Message openMessage = new Message(MessageTypes.OPEN_TRANSACTION);
            writeToNet.writeObject(openMessage);
            writeToNet.flush();
            //writeToNet.flush();
            System.out.println("Made it here 1.4");

            // Receive transactionID from server
            transactionID = readFromNet.readInt();
            System.out.println("Made it here 1.5");
            // transactionID = (Integer) response.;
        } 
        
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        
        return transactionID;
    }

    
    /**
     * Requests this transaction to be closed.
     * 
     * @return the status, i.e. either TRANSACTION_COMMITTED or TRANSACTION_ABORTED
     */
    public int closeTransaction() {
        int returnStatus = TRANSACTION_ABORTED;

        // send CLOSE_TRANSACTION message & receive returnStatus
        // shut down connection
        // ...
        
        try 
        {
            // Send CLOSE_TRANSACTION message
            Message closeMessage = new Message(MessageTypes.CLOSE_TRANSACTION, transactionID);
            writeToNet.writeObject(closeMessage);
            writeToNet.flush();

            // Receive return status
            Message response = (Message) readFromNet.readObject();
            returnStatus = (Integer) response.getContent();

            // Close the connection
            serverConnection.close();
        } 
        
        catch (IOException | ClassNotFoundException e) 
        {
            e.printStackTrace();
        }
        System.out.println("Made it here 2");

        return returnStatus;
    }

   
    /**
     * Reading a value from an account
     * 
     * @param accountNumber
     * @return the balance of the account
     */
    public int read(int accountNumber) {
        int balance = 0;


        // write READ_REQUEST and receive balance
        // ...

        try 
        {
            // Send READ_REQUEST message with transaction ID and account number
            Message readMessage = new Message(READ_REQUEST, accountNumber);

            writeToNet.writeObject(readMessage);
            writeToNet.flush();
    
            // Receive the account balance
            balance =  readFromNet.readInt();
            //balance = (Integer) response.getContent();
        } 
        catch (IOException e) 
            {
            e.printStackTrace();
        }

        System.out.println("Made it here 3");
        return balance;
    }

    
/**
 * Writing value to account
 * @param accountNumber
 * @param amount
 * 
 * @return the prior account balance
 */
    public int write(int accountNumber, int amount) {  
        int balance = 0;
        
        // write WRITE_REQUEST and receive prior balance
        // ...

        try 
        {
            // Send WRITE_REQUEST message with transaction ID, account number, and amount
            Message writeMessage = new Message(WRITE_REQUEST, new int[]{accountNumber, amount});
            writeToNet.writeObject(writeMessage);
            writeToNet.flush();
    
            // Receive the prior account balance
            balance = readFromNet.readInt();
            //balance = (Integer) response.getContent();
        } 
        
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        System.out.println("Made it here 4");
        return balance;
    }
}
