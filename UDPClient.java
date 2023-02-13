import java.io.*;
import java.net.*;
import java.util.*;

public class UDPClient {
    public static void main(String[] args) throws IOException {

        long key = 1927391273;
            // get a datagram socket
        DatagramSocket socket = new DatagramSocket();


        System.out.println("*********************Length 8 Message*********************");
        InetAddress address = InetAddress.getByName("localhost");
        
        for(int i = 0; i < 30; i++) {
            byte[] buf = performXOR(createMessage(8), key).getBytes();
            DatagramPacket packet = new DatagramPacket(buf,buf.length,address,3000);
            long startTime = System.nanoTime();
            socket.send(packet);
            // get response
            packet = new DatagramPacket(buf, buf.length);;
            socket.receive(packet);
            long duration = System.nanoTime() - startTime;

            // display response
            String received = performXOR(new String(packet.getData(), 0, packet.getLength()),key);
            System.out.println("Return Buffer: " + received + " RTT: " + duration);
        }


        socket.close();
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
