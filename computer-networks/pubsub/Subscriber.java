// Server.java

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class Subscriber extends Node {
	InetSocketAddress dstAddress;

	Subscriber() {
		try {
			String IP = "192.168.10.30";
			// String IP = "localhost"
			dstAddress = new InetSocketAddress(IP, BKR_PORT);
			//dstAddress = new InetSocketAddress("broker", BKR_PORT);
			socket= new DatagramSocket(SUB_PORT);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	public void onReceipt(DatagramPacket packet) {
		try {
			//this.notify();
			byte[] data = packet.getData();

			switch(data[TYPE_POS]) {
				case PUBLISH:
					// byte[] buffer = new byte[data[LENGTH_POS]];
					// System.arraycopy(data, HEADER_LENGTH, buffer, 0, buffer.length);
					// String content = new String(buffer);
					String content = getStringData(data);
					System.out.println("Received data:" + content);
					System.out.println("Length: " + content.length());
					break;
				case SUBACK:
					System.out.println("Received subscribe ack");
					break;
				case UNSUBACK:
					System.out.println("Received unsubscribe ack");
					break;
				default:
					System.out.println("Unexpected packet" + packet.toString());
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	public synchronized void sendSubscriptionRequest(String message) throws Exception {
		byte[] data = getDataByteArray(message);
		data[TYPE_POS] = SUBSCRIBE;

		System.out.println("Sending subscribe request packet...");
		DatagramPacket packet = new DatagramPacket(data, data.length, dstAddress);
		socket.send(packet);
		System.out.println("Packet sent");
	}

	public synchronized void sendUnsubscriptionRequest(String message) throws Exception {
		byte[] data = getDataByteArray(message);
		data[TYPE_POS] = UNSUBSCRIBE;

		System.out.println("Sending unsubscribe request packet...");
		DatagramPacket packet = new DatagramPacket(data, data.length, dstAddress);
		socket.send(packet);
		System.out.println("Packet sent");
	}

	public synchronized void start() throws Exception {
		System.out.println("Subscriber program starting...");
		//getUserInput();
		Scanner scanner = new Scanner(System.in);
		boolean finished = false;

		while(!finished) {
			System.out.println("To subscribe to a topic enter \"sub <topic>\"");
			System.out.println("To unsubscribe from a topic enter \"unsub <topic>\"");
			String input = scanner.nextLine();

			String[] splitInput = input.split("\\s+");

			if(input.equalsIgnoreCase("exit")) {
				finished = true;
			} else if (splitInput[0].equals("sub")){
				sendSubscriptionRequest(splitInput[1]);
				// this.wait();
			} else if (splitInput[0].equals("unsub")) {
				sendUnsubscriptionRequest(splitInput[1]);
				// this.wait();
			} else {
				System.out.println("Invalid input.");
			}
			//this.wait();
		}
		while(true) {
			this.wait();
		}
	}
	public static void main(String[] args) {
		try {
			(new Subscriber()).start();
			System.out.println("Subscriber program completed.");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}