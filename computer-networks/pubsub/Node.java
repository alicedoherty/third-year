// Alice Doherty
// Student Number: 19333356

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

public abstract class Node {
	static final int PACKETSIZE = 65000;

	static final int PORT = 50000; 				// Port number

	static final int CONTROL_HEADER_LENGTH = 2;	// Fixed length of the control header
	static final int TYPE_POS = 0; 				// Position of the type within the header
	static final int RETAIN_FLAG = 1;			// Position of the retain flag within the header

	static final byte FALSE = 0;
	static final byte TRUE = 1;

	// Packet Types
	static final byte PUBLISH = 1;
	static final byte PUBACK = 2;
	static final byte SUBSCRIBE = 3;
	static final byte SUBACK = 4;
	static final byte UNSUBSCRIBE = 5;
	static final byte UNSUBACK = 6;

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

	protected byte[] makeDataByteArray(String message) {
		byte[] buffer = message.getBytes();
		byte[] data = new byte[CONTROL_HEADER_LENGTH + buffer.length];
		System.arraycopy(buffer, 0, data, CONTROL_HEADER_LENGTH, buffer.length);
		return data;
	}

	protected String getStringData(byte[] data, DatagramPacket packet) {
		byte[] buffer = new byte[packet.getLength()-CONTROL_HEADER_LENGTH];
		System.arraycopy(data, CONTROL_HEADER_LENGTH, buffer, 0, buffer.length);
		String string = new String(buffer);
		return string;
	}

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