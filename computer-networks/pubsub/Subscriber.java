// Server.java

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class Subscriber extends Node {
	InetSocketAddress dstAddress;

	Subscriber() {
		try {
			// dstAddress = new InetSocketAddress(DEFAULT_DST, BKR_PORT);
			dstAddress = new InetSocketAddress("broker", BKR_PORT);
			socket= new DatagramSocket(SUB_PORT);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	public void onReceipt(DatagramPacket packet) {
		try {
			//this.notify();
			System.out.println("Received packet");
			byte[] data = packet.getData();

			switch(data[TYPE_POS]) {
				case PUBLISH:
					// byte[] data = packet.getData()
					// System.arraycopy(data, 1)


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
					// System.out.println("Enter topic to subscribe to: ");
					break;
				default:
					System.out.println("Unexpected packet" + packet.toString());
			}
			// getUserInput();
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

	// private void getUserInput() throws Exception {
	// 	Scanner scanner = new Scanner(System.in);
	// 	boolean finished = false;

	// 	while(!finished) {
	// 		System.out.println("To subscribe to a topic enter \"sub <topic>\"");
	// 		System.out.println("To unsubscribe from a topic enter \"unsub <topic>\"");
	// 		String input = scanner.nextLine();

	// 		String[] splitInput = input.split("\\s+");

	// 		if(input.equalsIgnoreCase("exit")) {
	// 			finished = true;
	// 		} else if (splitInput[0].equals("sub")){
	// 			sendSubscriptionRequest(splitInput[1]);
	// 			this.wait();
	// 		} else if (splitInput[0].equals("unsub")) {
	// 			sendUnsubscriptionRequest(splitInput[1]);
	// 			this.wait();
	// 		} else {
	// 			System.out.println("Invalid input.");
	// 		}
	// 	}
 	// }
	public static void main(String[] args) {
		try {
			(new Subscriber()).start();
			System.out.println("Subscriber program completed.");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}