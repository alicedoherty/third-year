import java.net.DatagramSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class Router extends Node {
    // Forwarding table layout
    // Dest | In | Out
    String[][] forwardingTable = {
        {"test", "E1", "E4"}
    };
    // String[][] preconfigInfo = {
    //     {"trinity", "E1", "R1", "E1", "R2"},
    //     {"trinity", "E1", "R2", "R1", "R4"},
    //     {"trinity", "E1", "R4", "R2", "E4"},
    //     {"home", "E1", "R1", "E1", "R3"},
    //     {"home", "E1", "R3", "R1", "E4"},
    //     {"test", "E1", "R1", "E1", "E4"}
    // };

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
            //byte[] data = packet.getData();
            forwardMessage(packet);
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
