import java.net.*;
import java.io.*;

public class EchoClient {

    public static void main(String[] args) {
        // host and port of service we are connecting to
        String host = "localhost";
        int echoServicePortNumber = 3000;
        long key = 1927391273;

        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            echoSocket = new Socket(host, echoServicePortNumber);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                                            echoSocket.getInputStream()));

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + host);
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection.");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));

            
            // reads in userinput on console and outputs to service?
            // echoes what the service returns
            
            measureRTT(8, key, out);
            measureRTT(32, key, out);
            measureRTT(512, key, out);
            measureRTT(1024, key, out);
            
            // close connection
            out.close();
            in.close();
            stdIn.close();
            echoSocket.close();
        } catch (IOException ex) {
            System.err.println("IO failure.");
            ex.printStackTrace();
        }
    }

    public static void measureRTT(int byteSize, long key, PrintWriter out) {
        System.out.println("*********************Length " + byteSize + " Message*********************");
            for(int k = 0; k < 30; k++) {
                long startTime = System.nanoTime();
                out.println(performXOR(createMessage(byteSize),key));
                long duration = System.nanoTime() - startTime;
                System.out.println(duration);
            }
    }

    // Method performing XOR encoding/decoding on   a message with an input key
    public static String performXOR(String message, long key) {
        String xorResponse = "";
        for(int i = 0; i < message.length(); i++) {
            xorResponse += (char) (message.charAt(i) ^ key);
        }
        //System.out.println("The encrypted message is: " + encryptedResponse);
        return xorResponse;
    }

    // Method to create a message of an input byte size
    public static String createMessage(int bytes) {
        String message = "";
        for(int i = 0; i < bytes; i++) {
            message += 'a';
        }
        return message;
    }


}