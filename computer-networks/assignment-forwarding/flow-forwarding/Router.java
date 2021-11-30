import java.net.DatagramSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;

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
        // TODO Put into function?
        byte[] data = packet.getData();
        byte[] buffer = new byte[packet.getLength()-1];
        System.arraycopy(data, 1, buffer, 0, buffer.length);
        String forwardingTableString = new String(buffer);

        // Convert forwardingTableString to a String array
        String[] forwardingTableArray = forwardingTableString.split(", ");

        // Set forwardingTable
        // TODO add constant for 3?
        forwardingTable = new String[forwardingTableArray.length/3][3];

        for(int i = 0, j = 0; i < forwardingTableArray.length; i += 3, j++) {
            forwardingTable[j][DEST] = forwardingTableArray[i];
            forwardingTable[j][IN] = forwardingTableArray[i+1];
            forwardingTable[j][OUT] = forwardingTableArray[i+2];
        }

        printForwardingTable();
    }

    private void printForwardingTable() {
        System.out.println("Current Forwarding Table: ");

        String format = "%-7s %3s %3s %3s %3s %n";
        System.out.printf(format, "DEST", "|", "IN", "|", "OUT");
        System.out.println("-----------------------");
        for(int i = 0; i < forwardingTable.length; i++) {
            System.out.printf(format, forwardingTable[i][DEST], "|", forwardingTable[i][IN], "|", forwardingTable[i][OUT]);
        }
    }

    // TODO Get rid of exceptions if getting rid of Thread.sleep()
    public synchronized void forwardMessage(DatagramPacket receivedPacket) throws IOException {
        String nextHop = getNextHop(receivedPacket);

        if(nextHop.equals("error")) {
            contactController(receivedPacket);
        }

        else {
            System.out.println("Next hop for packet is: " + nextHop);

            InetSocketAddress nextHopAddr = new InetSocketAddress(nextHop, PORT_NUMBER);
            DatagramPacket packet = new DatagramPacket(receivedPacket.getData(), receivedPacket.getLength(), nextHopAddr);
            socket.send(packet);
            System.out.println("Message forwarded.");
        }
    }

    private String getNextHop(DatagramPacket packet) {
        String destination = getDestination(packet);
        String source = packet.getAddress().getHostName().substring(0,2);

        System.out.println("The final destination of this packet is: " + destination);

        // e.g trim "E1.assignment-forwarding_flow-forwarding" to "E1"
        // String trimmedSource = source.substring(0,2);
        System.out.println("Packet came from container: " + source);

        for(int i = 0; i < forwardingTable.length; i++) {
            if(destination.equals(forwardingTable[i][DEST])) {
                if(forwardingTable[i][IN].equals(source)) {
                    return forwardingTable[i][OUT];
                }
            }
        }
        return "error";
    }

    // private String getDestination(DatagramPacket packet) {
    //     byte[] data = packet.getData();
    //     int dstLength = data[LENGTH];

    //     byte[] buffer = new byte[dstLength];
	// 	System.arraycopy(data, CONTROL_HEADER_LENGTH, buffer, 0, buffer.length);
	// 	String destination = new String(buffer);
    //     return destination;
    // }

    // If the Router does not know where to forward the packet to next,
    // contact the controller to ask if it knows the next hop.
    public synchronized void contactController(DatagramPacket receivedPacket) throws IOException {
        // OPFT_PACKET_IN - transfer control of packet to controller
        byte[] data = receivedPacket.getData();
        data[TYPE] = OFPT_PACKET_IN;

        InetSocketAddress controllerAddr = new InetSocketAddress("controller", PORT_NUMBER);
        DatagramPacket packet = new DatagramPacket(data, data.length, controllerAddr);
        socket.send(packet);

        System.out.println("Next hop cannot be established - forwarding packet to controller");
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
