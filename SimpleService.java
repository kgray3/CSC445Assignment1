import java.net.*;
import java.io.*;

public class SimpleService {
    static final int PORT = 3000;
    
    public static void main(String[] args) {
        try{
            
            // bool for if client wants to test RTT
            boolean testingRTT = true;
            // total bytes for throughput calculations
            int totalBytes = 1024000;
            // key for XOR encoding
            long key = 1927391273;
            // init Socket
            ServerSocket serverSocket = new ServerSocket(PORT);
            Socket client = serverSocket.accept();

            // init PrintWriter and BufferedReader
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            BufferedReader in =
                new BufferedReader(new InputStreamReader(client.getInputStream()));

            for (;;) {
                    
                    // Read in client message
                    String cmd = in.readLine();
                    // XOR decode client message
                    String decodedCMD = performXOR(cmd, key);


                    String reply = "";
                    /*
                     * Conditional to determine testing mode/response.
                     * --if testing RTT, set the boolean testingRTT to true
                     * --if testing Throughput, set the boolean testingRTT to false
                     * --if testingRTT = true, echo the client message
                     * --if testingRTT = false; send an 8 byte ACK after 1Mbyte of data has been transmitted
                     * --if any other message is received, close the server
                     */
                    if(decodedCMD.equalsIgnoreCase("RTT")) {
                        testingRTT = true;
                        out.println(performXOR("RECEIVED", key));
                    } else if (decodedCMD.equalsIgnoreCase("throughput")) {
                        testingRTT = false;
                        out.println(performXOR("RECEIVED", key));
                    }else if(testingRTT) {
                        // if we're measuring RTT, echo the message
                        reply = cmd;
                        out.println(reply);
                    } else if(!testingRTT){
                        // if we're measuring throughput, send an 8-byte acknowledgement
                        reply = performXOR("RECEIVED", key);
                        totalBytes = totalBytes - decodedCMD.getBytes().length;
                        // System.out.println(totalBytes);
                        // only reply once we hit 1Mbyte -- on UDP we can't do this, since packet receive is not
                        // guaranteed
                        if( totalBytes <= 0) {
                            // reset for next message test & send reply
                            totalBytes = 1024000;
                            out.println(reply);
                        }
                        
                    } else {
                        out.close();
                        in.close();
                        client.close();
                        serverSocket.close();
                    }
                    
                    
                    //System.out.println("The decoded message is: " + decodedCMD);
                    
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
