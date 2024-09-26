package chat;

import java.util.Scanner;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Sender extends Thread {
    boolean hasJoined;
    Scanner userInput;
    String inputLine = null;

    @Override
    public void run() {
        ObjectOutputStream writeToNet;
        ObjectInputStream readFromNet;

        while (true)
        {
            inputLine = userInput.nextLine();
            
            // TODO: actually do shit based on input
        }
    }
}
