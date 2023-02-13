import java.io.*;
import java.net.*;
import java.util.Date;


public class UDPServerThread extends Thread {

    // init the DatagramSocket
    protected DatagramSocket socket = null;
    // init BufferedReader for reading inputs
    protected BufferedReader in = null;
    // bool for if there are more packets??
    protected boolean moreQuotes = true;

    public UDPServerThread() throws IOException {
        this("UDPServer");
    }

    public UDPServerThread(String name) throws IOException {
        super(name);
        // initialize server on port 3000
        socket = new DatagramSocket(3000);
        
        String in = "joe mama";

    }

    public void run() {
        // while there are more packets to receive(?)
        while (moreQuotes) {
            try{
                // size of request?
                byte[] buf = new byte[256];

                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                // figure out response
                String dString = null;
                if (in == null)
                    dString = new Date().toString();
                else
                    dString = getNextQuote();

                buf = dString.getBytes();

                // send the response to the client at "address" and "port"
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                packet = new DatagramPacket(buf, buf.length, address, port);
                socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                moreQuotes = false;
            }
        }
        socket.close();
    }

    protected String getNextQuote() {
        String returnValue = null;
        try {
            if ((returnValue = in.readLine()) == null) {
                in.close();
                moreQuotes = false;
                returnValue = "No more quotes. Goodbye.";
            }
        } catch (IOException e) {
            returnValue = "IOException occurred in server.";
        }
        return returnValue;
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
