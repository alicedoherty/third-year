// Server.java

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Subscriber extends Node {
	static final int DEFAULT_PORT = 50001;

    static final int HEADER_LENGTH = 2;
	static final int TYPE_POS = 0;
	
	static final byte TYPE_UNKNOWN = 0;
	
	static final byte TYPE_STRING = 1;
	static final int LENGTH_POS = 1;
	
	static final byte TYPE_ACK = 2;
	static final int ACKCODE_POS = 1;
	static final byte ACK_ALLOK = 10;
	/*
	 *
	 */
	Subscriber(int port) {
		try {
			socket= new DatagramSocket(port);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public void onReceipt(DatagramPacket packet) {
		try {
			System.out.println("Received packet");

			// PacketContent content= PacketContent.fromDatagramPacket(packet);

			// if (content.getType()==PacketContent.FILEINFO) {
			// 	System.out.println("File name: " + ((FileInfoContent)content).getFileName());
			// 	System.out.println("File size: " + ((FileInfoContent)content).getFileSize());

			// 	DatagramPacket response;
			// 	response= new AckPacketContent("OK - Received this").toDatagramPacket();
			// 	response.setSocketAddress(packet.getSocketAddress());
			// 	socket.send(response);
			// }
            
            String content;
			byte[] data;
			byte[] buffer;
			
			data = packet.getData();			
			switch(data[TYPE_POS]) {
			case TYPE_STRING:
				buffer= new byte[data[LENGTH_POS]];
				System.arraycopy(data, HEADER_LENGTH, buffer, 0, buffer.length);
				content= new String(buffer);
				System.out.println("|" + content + "|");
				System.out.println("Length: " + content.length());
				// You could test here if the String says "end" and terminate the
				// program with a "this.notify()" that wakes up the start() method.
				data = new byte[HEADER_LENGTH];
				data[TYPE_POS] = TYPE_ACK;
				data[ACKCODE_POS] = ACK_ALLOK;
				
				DatagramPacket response;
				response = new DatagramPacket(data, data.length);
				response.setSocketAddress(packet.getSocketAddress());
				socket.send(response);
				break;
			default:
				System.out.println("Unexpected packet" + packet.toString());
			}

		}
		catch(Exception e) {e.printStackTrace();}
	}


	public synchronized void start() throws Exception {
		System.out.println("Waiting for contact");
		this.wait();
	}

	/*
	 *
	 */
	public static void main(String[] args) {
		try {
			(new Subscriber(DEFAULT_PORT)).start();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}