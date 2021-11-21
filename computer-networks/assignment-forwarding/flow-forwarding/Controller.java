import java.net.DatagramSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class Controller extends Node {
    String[][] preconfigInfo = {
        {"trinity", "E1", "R1", "E1", "R2"},
        {"trinity", "E1", "R2", "R1", "R4"},
        {"trinity", "E1", "R4", "R2", "E4"},
        {"home", "E1", "R1", "E1", "R3"},
        {"home", "E1", "R3", "R1", "E4"},
        {"test", "E1", "R1", "E1", "E4"}
    };

    ArrayList<String> routers = new ArrayList<String>();
    ArrayList<String> endNodes = new ArrayList<String>();

    Controller() {
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
                case OFPT_HELLO:
                    String source = packet.getAddress().getHostName().substring(0,2);
                    System.out.println("Received hello from router " + source);
                    registerElement(source);
                    break; 
                default:
                System.out.println("Received unexpected packet" + packet.toString());
            }
        }
        catch(Exception e) {e.printStackTrace();}
    }

    public synchronized void sendForwardingTable(String router) throws IOException {
        ArrayList<String> table = new ArrayList<String>();
        for(int i = 0; i < preconfigInfo.length; i++) {
            if(router.equals(preconfigInfo[i][ROUTER])) {
                table.add(preconfigInfo[i][DEST_ADDR]);
                table.add(preconfigInfo[i][ROUTER_IN]);
                table.add(preconfigInfo[i][ROUTER_OUT]);
            }
        }

        String tableString = String.join(", ", table);
        
        // Make into separate function
        byte[] buffer = tableString.getBytes();
        byte[] data = new byte[buffer.length+1];
        System.arraycopy(buffer, 0, data, 1, buffer.length);
        data[TYPE] = OFPT_FLOW_MOD;

        InetSocketAddress routerAddr = new InetSocketAddress(router, PORT_NUMBER);
        DatagramPacket packet = new DatagramPacket(data, data.length, routerAddr);
        socket.send(packet);
    }

    // Register network element
    // e.g for routers send them forwarding table info
    public synchronized void registerElement(String container) throws IOException {
        // Add for endNodes too
        routers.add(container);
        sendForwardingTable(container);
    }

    public synchronized void start() throws Exception {
        System.out.println("Controller program starting...");
		while (true) {
			this.wait();
		}
    }    
    public static void main(String[] args) {
		try {
			(new Controller()).start();
			System.out.println("Controller program completed.");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
}
