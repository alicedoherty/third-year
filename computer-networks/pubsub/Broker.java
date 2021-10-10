import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;

public class Broker extends Node {
    // static final int HEADER_LENGTH = 2; // Fixed length of the header
	// static final int TYPE_POS = 0; // Position of the type within the header

	// static final byte TYPE_UNKNOWN = 0;

	// static final byte TYPE_STRING = 1; // Indicating a string payload
	// static final int LENGTH_POS = 1;

	// static final byte TYPE_ACK = 2;   // Indicating an acknowledgement
	// static final int ACKCODE_POS = 1; // Position of the acknowledgement type in the header
	// static final byte ACK_ALLOK = 10; // Inidcating that everything is ok

    // static final int DEFAULT_PORT = 50001;

    // private Map<String, ArrayList<InetSocketAddress>> subscriberMap;
    InetSocketAddress dstAddress;
    
    Broker() {
		try {
			// dstAddress= new InetSocketAddress(dstHost, dstPort);
			socket= new DatagramSocket(BKR_PORT);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
        // subscriberMap = new HashMap<String, ArrayList<InetSocketAddress>>();
	}

    // Receiver code
    public synchronized void onReceipt(DatagramPacket packet) {
        try {
            System.out.println("Received packet");

            // String content;
            byte[] data;
           // byte[] buffer;
    
            data = packet.getData();
    
            switch(data[TYPE_POS]) {
                case PUBLISH:
                    System.out.println("Received request to publish.");
                    sendMessage();
                    // this.notify();
                    // buffer= new byte[data[LENGTH_POS]];
                    // System.arraycopy(data, HEADER_LENGTH, buffer, 0, buffer.length);
                    // content= new String(buffer);
                    // System.out.println("|" + content + "|");
                    // System.out.println("Length: " + content.length());
                    // // You could test here if the String says "end" and terminate the
                    // // program with a "this.notify()" that wakes up the start() method.
                    // data = new byte[HEADER_LENGTH];
                    // data[TYPE_POS] = TYPE_ACK;
                    // data[ACKCODE_POS] = ACK_ALLOK;
                    
                    // DatagramPacket response;
                    // response = new DatagramPacket(data, data.length);
                    // response.setSocketAddress(packet.getSocketAddress());
                    // socket.send(response);
                    break;
                default:
                    System.out.println("Unexpected packet" + packet.toString());
            }

        }
        catch(Exception e) {e.printStackTrace();}
		// PacketContent content= PacketContent.fromDatagramPacket(packet);

		// System.out.println(content.toString());
		// this.notify();

	}

    // Sender code - publish code
    public synchronized void sendMessage() throws Exception {
        byte[] data = null;
        byte[] buffer= null;
		DatagramPacket packet= null;
		String input;

        input= "humidity 20";
        buffer = input.getBytes();
        data = new byte[HEADER_LENGTH+buffer.length];
        data[TYPE_POS] = PUBLISH;
        data[LENGTH_POS] = (byte)buffer.length;
        System.arraycopy(buffer, 0, data, HEADER_LENGTH, buffer.length);

        System.out.println("Sending packet...");

        dstAddress = new InetSocketAddress(DEFAULT_DST, SUB_PORT);

        packet= new DatagramPacket(data, data.length, dstAddress);

        // packet.setSocketAddress(dstAddress);
        socket.send(packet);
        System.out.println("Packet sent");
        this.wait();
	}

    public synchronized void start() throws Exception {
		System.out.println("Waiting for contact");
		while (true) {
			this.wait();
		}
	}

	public static void main(String[] args) {
		try {
			(new Broker()).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
