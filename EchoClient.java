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
            //String userInput;
            String byteMessage8 = performXOR("Hello hi", key);
            String byteMessage32 = performXOR("abcdefghijklmnopqrstuvwxyz123456", key);
            String byteMessage512 = performXOR(createMessage(512), key);
            String byteMessage1024 = performXOR(createMessage(1024), key);
            // reads in userinput on console and outputs to service?
            // echoes what the service returns
            System.out.println("*********************Length 8 Message*********************");
            for(int k = 0; k < 30; k++) {
                long startTime = System.nanoTime();
                out.println(byteMessage8);
                long duration = System.nanoTime() - startTime;
                System.out.println(duration);

            }

            System.out.println("*********************Length 32 Message*********************");
            for(int k = 0; k < 30; k++) {
                long startTime = System.nanoTime();
                out.println(byteMessage32);
                long duration = System.nanoTime() - startTime;
                System.out.println(duration);

            }

            System.out.println("*********************Length 512 Message*********************");
            for(int k = 0; k < 30; k++) {
                long startTime = System.nanoTime();
                out.println(byteMessage512);
                long duration = System.nanoTime() - startTime;
                System.out.println(duration);
            }

            System.out.println("*********************Length 1024 Message*********************");
            for(int k = 0; k < 30; k++) {
                long startTime = System.nanoTime();
                out.println(byteMessage1024);
                long duration = System.nanoTime() - startTime;
                System.out.println(duration);
            }
            
            

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

    // Method performing XOR encoding/decoding on   a message with an input key
    public static String performXOR(String message, long key) {
        String xorResponse = "";
        for(int i = 0; i < message.length(); i++) {
            xorResponse += (char) (message.charAt(i) ^ key);
        }
        //System.out.println("The encrypted message is: " + encryptedResponse);
        return xorResponse;
    }

    public static String createMessage(int bytes) {
        String message = "";
        for(int i = 0; i < bytes; i++) {
            message += 'a';
        }
        return message;
    }


}