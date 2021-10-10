// Server.java

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class Subscriber extends Node {
	InetSocketAddress dstAddress;

	Subscriber() {
		try {
			dstAddress = new InetSocketAddress(DEFAULT_DST, BKR_PORT);
			socket= new DatagramSocket(SUB_PORT);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	public void onReceipt(DatagramPacket packet) {
		try {
			System.out.println("Received packet");
			byte[] data = packet.getData();

			switch(data[TYPE_POS]) {
				case PUBLISH:
					byte[] buffer = new byte[data[LENGTH_POS]];
					System.arraycopy(data, HEADER_LENGTH, buffer, 0, buffer.length);
					String content = new String(buffer);
					System.out.println("Received data:" + content);
					System.out.println("Length: " + content.length());
					// You could test here if the String says "end" and terminate the
					// program with a "this.notify()" that wakes up the start() method.
					// data = new byte[HEADER_LENGTH];
					// data[TYPE_POS] = TYPE_ACK;
					// data[ACKCODE_POS] = ACK_ALLOK;
					
					// DatagramPacket response;
					// response = new DatagramPacket(data, data.length);
					// response.setSocketAddress(packet.getSocketAddress());
					// socket.send(response);
					// System.out.println("New publication: " + getMessage(data));
					break;
				default:
					System.out.println("Unexpected packet" + packet.toString());
			}

		}
		catch(Exception e) {e.printStackTrace();}
	}

	public synchronized void sendMessage(String message) throws Exception {
		byte[] buffer = message.getBytes();
		byte[] data = new byte[HEADER_LENGTH + buffer.length];

		data[TYPE_POS] = SUBSCRIBE;
		data[LENGTH_POS] = (byte) buffer.length;
		System.arraycopy(buffer, 0, data, HEADER_LENGTH, buffer.length);

		System.out.println("Sending subscribe request packet...");
		DatagramPacket packet = new DatagramPacket(data, data.length, dstAddress);
		socket.send(packet);
		System.out.println("Packet sent");
		// this.wait();
	}

	public synchronized void start() throws Exception {
		System.out.println("Waiting for contact");
		System.out.println("Sending request to subscribe to \"temperature\"");
		sendMessage("temperature");
		this.wait();
	}
	public static void main(String[] args) {
		try {
			(new Subscriber()).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}