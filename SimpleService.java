import java.net.*;
import java.io.*;

public class SimpleService {
    static final int PORT = 3000;
    
    public static void main(String[] args) {
        try{
            long key = 1927391273;
            ServerSocket serverSocket = new ServerSocket(PORT);

            for (;;) {
                Socket client = serverSocket.accept();

                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in =
                    new BufferedReader(new InputStreamReader(client.getInputStream()));
                
                    String cmd = in.readLine();

                    // System.out.println("The decrypted message is: " + performXOR(cmd, key));

                    String reply = cmd;
                    
                    int len = reply.length();

                    // out.println("HTTP/1.0 200 OK");
                    // out.println("Content-Length: " + len);
                    // out.println("Content-Type: text/html\n");
                    out.println(reply);

                    out.close();
                    in.close();
                    client.close();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    // Method performing XOR encoding/decoding on a message with an input key
    public static String performXOR(String message, long key) {
        String xorMessage = "";
        for(int i = 0; i < message.length(); i++) {
            xorMessage += (char) (message.charAt(i) ^ key);
        }

        return xorMessage;
    }
}
