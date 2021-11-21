import java.net.DatagramSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class Controller extends Node {
    String[][] preconfigInfo = {
        {"trinity", "E1", "R1", "E1", "R2"},
        {"trinity", "E1", "R2", "R1", "R4"},
        {"trinity", "E1", "R4", "R2", "E4"},
        {"home", "E1", "R1", "E1", "R3"},
        {"home", "E1", "R3", "R1", "E4"},
        {"test", "E1", "R1", "E1", "E4"}
    };

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

        }
        catch(Exception e) {e.printStackTrace();}
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
