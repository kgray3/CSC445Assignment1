import java.io.*;
import java.net.*;
import java.util.*;

public class UDPClient {
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Host? ");
        String host = scanner.next();
        System.out.println("Port number? ");
        int port = scanner.nextInt();
        System.out.println("Testing [RTT or throughput]? ");
        String choice = scanner.next();

        // will get rid of harcoded value later :)
        long key = 1927391273;
            // get a datagram socket
        DatagramSocket socket = new DatagramSocket();

        InetAddress address = InetAddress.getByName(host);

        if(choice.equalsIgnoreCase("RTT")) {
            measureRTT(8, socket, address, key, port);
            measureRTT(32, socket, address, key, port);
            measureRTT(512, socket, address, key, port);
            measureRTT(1024, socket, address, key, port);
        } else if(choice.equalsIgnoreCase("throughput")) {

        } else {
            System.out.println("Error, incorrect choice. Exiting.");
        }

        socket.close();
        scanner.close();
    }

    public static void measureRTT(int byteSize, DatagramSocket socket, InetAddress address, long key, int port) throws IOException {
        System.out.println("*********************Length " + byteSize + " Message*********************");
        for(int i = 0; i < 30; i++) {
            byte[] buf = performXOR(createMessage(byteSize), key).getBytes();
            DatagramPacket packet = new DatagramPacket(buf,buf.length,address,port);
            long startTime = System.nanoTime();
            socket.send(packet);
            // get response
            packet = new DatagramPacket(buf, buf.length);;
            socket.receive(packet);
            long duration = System.nanoTime() - startTime;

            // display response
            String received = performXOR(new String(packet.getData(), 0, packet.getLength()),key);
            System.out.println("Return Buffer: " + received);
            System.out.println("The RTT is: " + duration);
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
