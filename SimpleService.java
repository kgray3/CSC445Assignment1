import java.net.*;
import java.io.*;

public class SimpleService {
    static final int PORT = 3000;
    
    public static void main(String[] args) {
        try{
            //TO-DO: Scanner inputs for host, port, and key
            boolean testingRTT = true;
            int totalBytes = 1000010;
            long key = 1927391273;
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket client = serverSocket.accept();

                PrintWriter out = new PrintWriter(client.getOutputStream(), true);
                BufferedReader in =
                    new BufferedReader(new InputStreamReader(client.getInputStream()));

            for (;;) {
                        
                    String cmd = in.readLine();
                    String decodedCMD = performXOR(cmd, key);

                    /*
                     * Conditional to determine testing mode or exit.
                     * --if testing RTT, set the boolean testingRTT to true
                     * --if testing Throughput, set the boolean testingRTT to false
                     * --if an exit message is received, close the server
                     */
                    if(decodedCMD.equalsIgnoreCase("RTT")) {
                        testingRTT = true;
                    } else if (decodedCMD.equalsIgnoreCase("throughput")) {
                        testingRTT = false;
                        out.println(performXOR("RECEIVED", key));
                    } else if(decodedCMD.equalsIgnoreCase("exit")) {
                        out.close();
                        in.close();
                        client.close();
                    }

                    

                    String reply = "";
                    if(testingRTT) {
                        // if we're measuring RTT, echo the message
                        reply = cmd;
                        out.println(reply);
                    } else{
                        // if we're measuring throughput, send an 8-byte acknowledgement
                        reply = performXOR("RECEIVED", key);
                        totalBytes = totalBytes - decodedCMD.getBytes().length;
                        System.out.println(totalBytes);
                        if( totalBytes <= 0) {
                            totalBytes = 1000000;
                            out.println(reply);
                        }
                        
                    }
                    
                    //int len = reply.length();
                    // send the response
                    //out.println(reply);
                    System.out.println("The decoded message is: " + decodedCMD.getBytes().length);
                    
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
