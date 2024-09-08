package chat;

public class NodeInfo {
    private String ip;
    private int port;
    private String name;

    // Constructor
    public NodeInfo(String inIp, int inPort, String inName) 
    {
        ip = inIp;
        port = inPort;
        name = inName;
    }

    public NodeInfo(String inIp, int inPort)
    {
        ip = inIp;
        port = inPort;
    }

    // Getter methods
    public String getIp() 
    {
        return ip;
    }

    public int getPort() 
    {
        return port;
    }

    public String getName() 
    {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        String otherIP = ((NodeInfo) other).getIp();
        int otherPort = ((NodeInfo) other).getPort();
        
        return this.ip.equals(otherIP) && this.port == otherPort;
    }
}
