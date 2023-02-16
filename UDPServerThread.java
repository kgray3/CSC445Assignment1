import java.io.*;
import java.net.*;
import java.util.Date;


public class UDPServerThread extends Thread {

    // add scanner for key later :)
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
        // initialize server on port 3000
        socket = new DatagramSocket(3000);

    }

    public void run() {
        // bool for if we are testing RTT or not
        boolean testingRTT = true;
        while (moreQuotes) {
            try{
                // size of request?
                byte[] buf = new byte[1024];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                // decode the message using XOR
                String decodedPacket = performXOR(new String(packet.getData(), 0, packet.getLength()), key);
                
                /*
                 * Conditional for determining test "mode"
                 * --if message "RTT" is received, testingRTT is set to true
                 * --if message "throughput" is received, testingRTT is set to false
                 * --if message "exit" is received, close out of server
                 */
                if(decodedPacket.equalsIgnoreCase("RTT")) {
                    testingRTT = true;
                } else if(decodedPacket.equalsIgnoreCase("throughput")) {
                    testingRTT = false;
                } else if(decodedPacket.equalsIgnoreCase("exit")) {
                    socket.close();
                }

                

                // figure out response
                String responseString = "";

                // if testing RTT, echo response; otherwise send 8 byte response of "RECEIVED"
                if(testingRTT) {
                    responseString = performXOR(decodedPacket, key);
                } else {
                    responseString = performXOR("RECEIVED", key);
                }

                // convert resposne string to bytes for packet creation
                buf = responseString.getBytes();

                // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
                System.out.println("The received packet is: " + decodedPacket);
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
