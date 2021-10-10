// CLient.java

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
// import java.io.File;
// import java.io.FileInputStream;

/**
 *
 * Client class
 *
 * An instance accepts user input
 *
 */
public class Publisher extends Node {
	static final int DEFAULT_SRC_PORT = 50000;
	static final int DEFAULT_DST_PORT = 50001;
	static final String DEFAULT_DST_NODE = "localhost";

    // Below from LUA example code
	static final int HEADER_LENGTH = 2; // Fixed length of the header
	static final int TYPE_POS = 0; // Position of the type within the header

	static final byte TYPE_UNKNOWN = 0;

	static final byte TYPE_STRING = 1; // Indicating a string payload
	static final int LENGTH_POS = 1;

	static final byte TYPE_ACK = 2;   // Indicating an acknowledgement
	static final int ACKCODE_POS = 1; // Position of the acknowledgement type in the header
	static final byte ACK_ALLOK = 10; // Inidcating that everything is ok

	InetSocketAddress dstAddress;

	/**
	 * Constructor
	 *
	 * Attempts to create socket at given port and create an InetSocketAddress for the destinations
	 */
	Publisher(String dstHost, int dstPort, int srcPort) {
		try {
			dstAddress= new InetSocketAddress(dstHost, dstPort);
			socket= new DatagramSocket(srcPort);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}


	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public synchronized void onReceipt(DatagramPacket packet) {
		// PacketContent content= PacketContent.fromDatagramPacket(packet);

		// System.out.println(content.toString());
		// this.notify();
        byte[] data;

        data = packet.getData();

        switch(data[TYPE_POS]) {
            case TYPE_ACK:
                System.out.println("Received ack");
                this.notify();
                break;
            default:
                System.out.println("Unexpected packet" + packet.toString());
        }
	}


	/**
	 * Sender Method
	 *
	 */
	public synchronized void sendMessage() throws Exception {
		// String fname;
		// File file= null;
		// FileInputStream fin= null;

		// FileInfoContent fcontent;

		// int size;
		// byte[] buffer= null;
		// DatagramPacket packet= null;

		// fname= "message.txt";//terminal.readString("Name of file: ");

		// file= new File(fname);				// Reserve buffer for length of file and read file
		// buffer= new byte[(int) file.length()];
		// fin= new FileInputStream(file);
		// size= fin.read(buffer);
		// if (size==-1) {
		// 	fin.close();
		// 	throw new Exception("Problem with File Access:"+fname);
		// }
		// System.out.println("File size: " + buffer.length);

		// fcontent= new FileInfoContent(fname, size);

		// System.out.println("Sending packet w/ name & length"); // Send packet with file name and length
		// packet= fcontent.toDatagramPacket();
		// packet.setSocketAddress(dstAddress);
		// socket.send(packet);
		// System.out.println("Packet sent");
		// this.wait();
		// fin.close();

        byte[] data = null;
        byte[] buffer= null;
		DatagramPacket packet= null;
		String input;

        input= "Payload: temperature 20";
        buffer = input.getBytes();
        data = new byte[HEADER_LENGTH+buffer.length];
        data[TYPE_POS] = TYPE_STRING;
        data[LENGTH_POS] = (byte)buffer.length;
        System.arraycopy(buffer, 0, data, HEADER_LENGTH, buffer.length);

        System.out.println("Sending packet...");
        packet= new DatagramPacket(data, data.length);
        packet.setSocketAddress(dstAddress);
        socket.send(packet);
        System.out.println("Packet sent");
        this.wait();
	}


	/**
	 * Test method
	 *
	 * Sends a packet to a given address
	 */
	public static void main(String[] args) {
		try {
			(new Publisher(DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).sendMessage();
			System.out.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}