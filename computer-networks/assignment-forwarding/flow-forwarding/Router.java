import java.net.DatagramSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import javax.xml.crypto.Data;

public class Router extends Node {
    // Forwarding table layout
    // Dest | In | Out
    String[][] forwardingTable;

    Router() {
        try {
            socket = new DatagramSocket(PORT_NUMBER);
            listener.go();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public synchronized void onReceipt(DatagramPacket packet) {
        try {
            byte[] data = packet.getData();
            switch(data[TYPE]) {
                // TODO Change NETWORK_ID bit?
                case NETWORK_ID:
                    forwardMessage(packet);
                    break;
                case OFPT_FLOW_MOD:
                    System.out.println("Received request to update forwarding table");
                    updateForwardingTable(packet);
                    break;
            }
            
        }
        catch(Exception e) {e.printStackTrace();}
    }

    // When Router starts up, register with the Controller by sending hello.
    public synchronized void sendHello() throws IOException {
        byte[] data = new byte[1];
        data[TYPE] = OFPT_HELLO;

        InetSocketAddress dstAddress = new InetSocketAddress("controller", PORT_NUMBER);
        DatagramPacket packet = new DatagramPacket(data, data.length, dstAddress);
        socket.send(packet);

        System.out.println("Hello sent to controller");
    }

    public synchronized void updateForwardingTable(DatagramPacket packet) {
        System.out.println("Info received from controller:");

        // TODO Put into function?
        byte[] data = packet.getData();
        byte[] buffer = new byte[packet.getLength()-1];
        System.arraycopy(data, 1, buffer, 0, buffer.length);
        String forwardingTableString = new String(buffer);
        System.out.println(forwardingTableString);

        // Convert forwardingTableString to a String array
        String[] forwardingTableArray = forwardingTableString.split(", ");
        System.out.println(forwardingTableArray.length);

        // Set forwardingTable
        // TODO add constant for 3?
        forwardingTable = new String[forwardingTableArray.length/3][3];

        for(int i = 0, j = 0; i < forwardingTableArray.length; i += 3, j++) {
            forwardingTable[j][DEST] = forwardingTableArray[i];
            forwardingTable[j][IN] = forwardingTableArray[i+1];
            forwardingTable[j][OUT] = forwardingTableArray[i+2];
        }
      
    }

    // TODO Get rid of exceptions if getting rid of Thread.sleep()
    public synchronized void forwardMessage(DatagramPacket receivedPacket) throws IOException, InterruptedException {
        // Thread.sleep(1500);

        String nextHop = getNextHop(receivedPacket);
        System.out.println("Next hop for packet is: " + nextHop);

        // Pattern p = Pattern.compile("^\\s*(.*?):(\\d+)\\s*$");
        // Matcher m = p.matcher(nextHop);

        // String nextHopIP = "";

        // if(m.matches()) {
        //     nextHopIP = m.group(1);
        // }

        // InetSocketAddress nextHopAddr = new InetSocketAddress(nextHopIP.substring(1), PORT_NUMBER);
        // InetSocketAddress nextHopAddr = new InetSocketAddress(E4, PORT_NUMBER);
        InetSocketAddress nextHopAddr = new InetSocketAddress(nextHop, PORT_NUMBER);
        DatagramPacket packet = new DatagramPacket(receivedPacket.getData(), receivedPacket.getLength(), nextHopAddr);
        socket.send(packet);
        System.out.println("Message forwarded.");
    }

    private String getNextHop(DatagramPacket packet) {
        String destination = getDestination(packet);
        // InetSocketAddress src = (InetSocketAddress) packet.getSocketAddress();
        // String srcAddress = src.toString();
        String source = packet.getAddress().getHostName();

        System.out.println("The final destination of this packet is: " + destination);
        // System.out.println("Packet came in from: " + srcAddress);
        System.out.println("Packet came from container: " + source);

        // e.g trim "E1.assignment-forwarding_flow-forwarding" to "E1"
        String trimmedSource = source.substring(0,2);
        
        for(int i = 0; i < forwardingTable.length; i++) {
            if(destination.equals(forwardingTable[i][DEST])) {
                if(forwardingTable[i][IN].equals(trimmedSource)) {
                    return forwardingTable[i][OUT];
                }
            }
        }
        return "error";
    }

    private String getDestination(DatagramPacket packet) {
        byte[] data = packet.getData();
        int dstLength = data[LENGTH];

        byte[] buffer = new byte[dstLength];
		System.arraycopy(data, CONTROL_HEADER_LENGTH, buffer, 0, buffer.length);
		String destination = new String(buffer);
        return destination;
    }

    public synchronized void start() throws Exception {
        System.out.println("Router program starting...");
        sendHello();
		while (true) {
			this.wait();
		}
    }    
    public static void main(String[] args) {
		try {
			(new Router()).start();
			System.out.println("Router program completed.");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
}
