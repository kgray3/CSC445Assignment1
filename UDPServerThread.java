import java.io.*;
import java.net.*;
import java.util.Date;


public class UDPServerThread extends Thread {

    // key for XOR encoding
    long key = 1927391273;
    // init the DatagramSocket
    protected DatagramSocket socket = null;
    // init BufferedReader for reading inputs
    protected BufferedReader in = null;
    // bool to keep running server
    protected boolean moreQuotes = true;

    public UDPServerThread() throws IOException {
        this("UDPServer");
    }

    public UDPServerThread(String name) throws IOException {
        super(name);
        // initialize server on port
        socket = new DatagramSocket(3000);

    }

    public void run() {
        // bool for if we are testing RTT or not
        boolean testingRTT = true;
        // totalBytes var used for throughput testing
       // int totalBytes = 1024010;
        while (moreQuotes) {
            try{
                // size of request
                byte[] buf = new byte[5000];

                // receive request from client
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                // decode the message using XOR
                String decodedPacket = performXOR(new String(packet.getData(), 0, packet.getLength()), key);
                
                /*
                 * Conditional for determining test "mode"/response if test mode has already been sent
                 * --if message "RTT" is received, testingRTT is set to true
                 * --if message "throughput" is received, testingRTT is set to false
                 * --if testingRTT = true, echo message back to client
                 * --if testingRTT = false, send 8 byte ACK back to client for throughput test
                 */
                String responseString = "";
                
                 if(decodedPacket.equalsIgnoreCase("RTT")) {
                    // set test mode
                    testingRTT = true;
                    // return ACK
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    buf = performXOR("RECEIVED", key).getBytes();
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    socket.send(packet);
                } else if(decodedPacket.equalsIgnoreCase("throughput")) {
                    // set test mode
                    testingRTT = false;
                    // return ACK
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    buf = performXOR("RECEIVED", key).getBytes();
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    socket.send(packet);
                } else if(testingRTT) {
                    // echo client response
                    responseString = performXOR(decodedPacket, key);
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    buf = responseString.getBytes();
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    socket.send(packet);
                } else if(!testingRTT) {
                    //System.out.println("The received packet is: " + decodedPacket);
                    
                    // subtract from totalBytes so we can track when 1Mbyte of datahas been sent
                    //totalBytes = totalBytes - decodedPacket.getBytes().length;
                    
                    //System.out.println(totalBytes);
                    
                    // return ACK after each packet to ensure everything makes it to server
                    responseString = performXOR("RECEIVED", key);
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    buf = responseString.getBytes();
                    packet = new DatagramPacket(buf, buf.length, address, port);
                    socket.send(packet);
                    
                    
                    // if(totalBytes <= 0) {
                    //     totalBytes = 1024000;
                    //     // responseString = performXOR("RECEIVED", key);
                    //     // InetAddress address = packet.getAddress();
                    //     // int port = packet.getPort();
                    //     // buf = responseString.getBytes();
                    //     // packet = new DatagramPacket(buf, buf.length, address, port);
                    //     // socket.send(packet);
                    // }
                } else{
                    socket.close();
                }
                

                

                // convert resposne string to bytes for packet creation
                buf = responseString.getBytes();

                // send the response to the client at "address" and "port"
                // InetAddress address = packet.getAddress();
                // int port = packet.getPort();
                // packet = new DatagramPacket(buf, buf.length, address, port);
                // socket.send(packet);
                
            } catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            }
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
}
