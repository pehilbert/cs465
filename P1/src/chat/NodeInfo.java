package chat;

import java.io.Serializable;

public class NodeInfo implements Serializable {
    String address;
    int port;
    String name = null;

    // Constructor
    public NodeInfo(String address, int port, String name) 
    {
        this.address = address;
        this.port = port;
        this.name = name;
    }

    public NodeInfo(String address, int port)
    {
        this(address, port, null);
    }

    // Getter methods
    public String getAddress() 
    {
        return this.address;
    }

    public int getPort() 
    {
        return this.port;
    }

    public String getName() 
    {
        return this.name;
    }

    @Override
    public boolean equals(Object other) {
        String otherIP = ((NodeInfo) other).getAddress();
        int otherPort = ((NodeInfo) other).getPort();
        
        return otherIP.equals(this.address) && (this.port == otherPort);
    }
}
