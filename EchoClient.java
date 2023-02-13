import java.net.*;
import java.util.Scanner;
import java.io.*;

public class EchoClient {
    // TO-DO: write to file instead of printing to console
    public static void main(String[] args) {
        // use scanner to collect host name, port number, and what test we're running
        Scanner scanner = new Scanner(System.in);
        System.out.println("Host name? ");
        String host = scanner.nextLine();
        System.out.println("Port number? ");
        int echoServicePortNumber = scanner.nextInt();
        System.out.println("Testing [RTT or throughput]? ");
        String choice = scanner.next();

        // will get rid of hardcoded key later :)
        long key = 1927391273;

        // vars for socket + output/input streams
        Socket echoSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            // init socket + output/input streams
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

            /*
             * Conditional for what is being tested
             * --if RTT is being tested, tell the server we're testing RTT, followed by
             *   running the measureRTT method for the different byte sizes
             * --if Throughput is being test, tell the server we're testing Throughput,
             *   followed by running the measureThroughput method for the different
             *   byte and sample sizes
             */
            if(choice.equalsIgnoreCase("RTT")) {
                out.println(performXOR("RTT",key));
                in.readLine();
                measureRTT(8, key, out, in);
                measureRTT(32, key, out, in);
                measureRTT(512, key, out, in);
                measureRTT(1024, key, out, in);
                out.println(performXOR("exit",key));
                in.readLine();
            } else if(choice.equalsIgnoreCase("throughput")) {
                out.println(performXOR("throughput", key));
                in.readLine();
                measureThroughput(1024, 1024, key, out, in);
                measureThroughput(512, 2048, key, out, in);
                measureThroughput(128, 8192, key, out, in);
                out.println(performXOR("exit", key));
                in.readLine();

            } else {
                System.out.println("Error: Incorrect choice. Exiting.");
            }
            
            
            // close connections + scanner
            out.close();
            in.close();
            stdIn.close();
            echoSocket.close();
            scanner.close();
        } catch (IOException ex) {
            System.err.println("IO failure.");
            ex.printStackTrace();
        }
    }

    // Method to measure RTT based on input byteSize. 30 samples total. XOR encoding before sending messages. Measures duration.
    public static void measureRTT(int byteSize, long key, PrintWriter out, BufferedReader in) throws IOException{
        System.out.println("*********************Length " + byteSize + " Message*********************");
            for(int k = 0; k < 30; k++) {
                long startTime = System.nanoTime();
                out.println(performXOR(createMessage(byteSize),key));
                //System.out.println("Echo: " + performXOR(in.readLine().toString(),key));
                long duration = System.nanoTime() - startTime;
                System.out.println(duration);
            }
    }
    // Method to measure throughput based on input byteSize and sampleSize. XOR encoding before sending messages.
    public static void measureThroughput(int byteSize, int sampleSize, long key, PrintWriter out, BufferedReader in) throws IOException {
        System.out.println("********************* " + sampleSize + " X " + byteSize + "Byte Messages*********************");
        for(int k = 0; k < sampleSize; k++) {
            long startTime = System.nanoTime();
            out.println(performXOR(createMessage(byteSize), key));
            long duration = System.nanoTime() - startTime;
            System.out.println("Response: " + performXOR(in.readLine().toString(), key));
            double throughput = (byteSize * 8.0)/(duration/1000000000.00);
            System.out.println("Throughput (bits/second): " + throughput);
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