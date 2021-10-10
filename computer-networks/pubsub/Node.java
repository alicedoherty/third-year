import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

public abstract class Node {
	static final int PACKETSIZE = 65536;
	static final String DEFAULT_DST = "localhost";

	// Port numbers
	static final int PUB_PORT = 50000;
	static final int BKR_PORT = 50001;
	static final int SUB_PORT = 50002;

	static final int HEADER_LENGTH = 2; // Fixed length of the header
	static final int TYPE_POS = 0; // Position of the type within the header
	static final int LENGTH_POS = 1;

	// Packet types
	static final byte PUBLISH = 3;
	static final byte PUBACK = 4;
	static final byte SUBSCRIBE = 8;
	static final byte SUBACK = 9;
	static final byte UNSUBSCRIBE = 10;
	static final byte UNSUBACK = 11;

	// static final byte TYPE_ACK = 2; // Indicating an acknowledgement
	// static final int ACKCODE_POS = 1; // Position of the acknowledgement type in
	// the header
	// static final byte ACK_ALLOK = 10; // Inidcating that everything is ok

	DatagramSocket socket;
	Listener listener;
	CountDownLatch latch;

	Node() {
		latch= new CountDownLatch(1);
		listener= new Listener();
		listener.setDaemon(true);
		listener.start();
	}


	public abstract void onReceipt(DatagramPacket packet);

	/**
	 *
	 * Listener thread
	 *
	 * Listens for incoming packets on a datagram socket and informs registered receivers about incoming packets.
	 */
	class Listener extends Thread {

		/*
		 *  Telling the listener that the socket has been initialized
		 */
		public void go() {
			latch.countDown();
		}

		/*
		 * Listen for incoming packets and inform receivers
		 */
		public void run() {
			try {
				latch.await();
				// Endless loop: attempt to receive packet, notify receivers, etc
				while(true) {
					DatagramPacket packet = new DatagramPacket(new byte[PACKETSIZE], PACKETSIZE);
					socket.receive(packet);

					onReceipt(packet);
				}
			} catch (Exception e) {if (!(e instanceof SocketException)) e.printStackTrace();}
		}
	}
}