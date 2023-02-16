import java.io.*;
import java.net.*;
import java.util.*;

public class UDPClient {
    public static File rttFile = new File("UDPRTT.csv");
    public static File throughputFile = new File("UDPThroughput.csv");
    public static void main(String[] args) throws IOException {

        // Scanner for collecting host, port #, and testing mode
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

        // establish host address
        InetAddress address = InetAddress.getByName(host);

        /*
         * Conditional for what test we are running
         * --if RTT, tell the server we are testing RTT, perform RTT tests, and tell server to close
         * --if Throughput, tell the server we are testing Throughput, perform Throughput tests, and tell server to close
         */
        if(choice.equalsIgnoreCase("RTT")) {
            FileWriter csvWriter = new FileWriter(rttFile.getName());
            csvWriter.write("Message Size, RTT");
            sendMessage("RTT", key, address, port, socket);
            measureRTT(8, socket, address, key, port, csvWriter);
            measureRTT(32, socket, address, key, port, csvWriter);
            measureRTT(512, socket, address, key, port, csvWriter);
            measureRTT(1024, socket, address, key, port, csvWriter);
            csvWriter.close();
            sendMessage("exit", key, address, port, socket);
        } else if(choice.equalsIgnoreCase("throughput")) {
            FileWriter csvWriter = new FileWriter(throughputFile.getName());
            csvWriter.write("Message Number x Size, Throughput(Megabits/second)");
            sendMessage("throughput", key, address, port, socket);
            measureThroughput(1024, 1024, key, socket, address, port, csvWriter);
            measureThroughput(512, 2048, key, socket, address, port, csvWriter);
            measureThroughput(128, 8192, key, socket, address, port, csvWriter);
            csvWriter.close();
            sendMessage("exit", key, address, port, socket);
        } else {
            System.out.println("Error, incorrect choice. Exiting.");
        }

        socket.close();
        scanner.close();
    }

    // Method to send testing mode and exit messages to the server
    public static void sendMessage(String message, long key, InetAddress address, int port, DatagramSocket socket) throws IOException {
        byte[] buf = performXOR(message, key).getBytes();
            DatagramPacket packet = new DatagramPacket(buf,buf.length,address,port);
            socket.send(packet);
            socket.receive(packet);
    }

    // Method for measuring RTT. Gets duration of send and receive. Sends message based on input byteSize
    public static void measureRTT(int byteSize, DatagramSocket socket, InetAddress address, long key, int port, FileWriter csv) throws IOException {
        System.out.println("*********************Length " + byteSize + " Message*********************");
        for(int i = 0; i < 30; i++) {
            csv.write(System.getProperty("line.separator"));
            byte[] buf = performXOR(createMessage(byteSize), key).getBytes();
            DatagramPacket packet = new DatagramPacket(buf,buf.length,address,port);
            long startTime = System.nanoTime();
            socket.send(packet);
            // get response
            packet = new DatagramPacket(buf, buf.length);;
            socket.receive(packet);
            long duration = System.nanoTime() - startTime;

            // display response
            //String received = performXOR(new String(packet.getData(), 0, packet.getLength()),key);
            //System.out.println("Return Buffer: " + received);
            System.out.println("The RTT is: " + duration);
            csv.write(String.valueOf(byteSize) + ", " + String.valueOf(duration));
        }
    }

    // Method for measuring Throughput. Sends x messages based on byteSize and sampleSize. Calculates Throughput.
    public static void measureThroughput(int byteSize, int sampleSize, long key, DatagramSocket socket, InetAddress address, int port, FileWriter csv) throws IOException {
        System.out.println("********************* " + sampleSize + " X " + byteSize + "Byte Messages*********************");
        for(int k = 0; k < 20; k++) {
            csv.write(System.getProperty("line.separator"));
            long startTime = System.nanoTime();
            for(int i = 0; i < sampleSize; i++) {
                
                byte[] buf = performXOR(createMessage(byteSize), key).getBytes();
                DatagramPacket packet = new DatagramPacket(buf,buf.length,address,port);
            
                socket.send(packet);
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
            
                //System.out.println("Response: " + performXOR(new String(packet.getData(),0,packet.getLength()), key));
            
            }
            long duration = System.nanoTime() - startTime;
            double throughput = ((byteSize * 8.0 * sampleSize)/Math.pow(10, 6))/(duration/1000000000.00);
            System.out.println("Throughput (Megasbits/second): " + throughput);
            csv.write(String.valueOf(sampleSize) + " x " + String.valueOf(byteSize) + ", " + String.valueOf(throughput));
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
