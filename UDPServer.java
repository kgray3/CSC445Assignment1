import java.io.*;

public class UDPServer {
    public static void main(String[] args) throws IOException{
        new UDPServerThread().start();
    }    
}
