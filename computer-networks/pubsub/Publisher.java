// Client.java

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Random;
import java.util.Scanner;

public class Publisher extends Node {
	InetSocketAddress dstAddress;

	Publisher() {
		try {
			// String IP = "192.168.10.30";
			String IP = "localhost";
			dstAddress = new InetSocketAddress(IP, BKR_PORT);
			//dstAddress = new InetSocketAddress("broker", BKR_PORT);
			socket = new DatagramSocket(PUB_PORT);
			listener.go();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void onReceipt(DatagramPacket packet) {
		try {
			byte[] data = packet.getData();
			switch(data[TYPE_POS]) {
				case PUBACK:
					System.out.println("Received publish ack from broker");
					break;
				default:
					System.out.println("Received unexpected packet" + packet.toString());
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	// Publish message to broker
	public synchronized void sendMessage(String message) throws Exception {
		byte[] buffer = message.getBytes();
		byte[] data = new byte[HEADER_LENGTH + buffer.length];

		data[TYPE_POS] = PUBLISH;
		data[LENGTH_POS] = (byte) buffer.length;
		System.arraycopy(buffer, 0, data, HEADER_LENGTH, buffer.length);

		DatagramPacket packet = new DatagramPacket(data, data.length, dstAddress);
		socket.send(packet);
		System.out.println("\"" + message + "\" sent to broker");
		// this.wait();
	}

	private void start() throws Exception {
		System.out.println("Publisher program starting...");

		Scanner scanner = new Scanner(System.in);
		boolean finished = false;
		while(!finished) {
			System.out.println("Enter data to be published (topic:payload): ");

			String input = scanner.nextLine();

			if(input.equalsIgnoreCase("exit")) {
				finished = true;
			} else {
				sendMessage(input);
			}	
		}

		// Random rand = new Random();
		// int upperBound = 50;

		// for(int i = 0; i < 20; i++) {
		// 	int randomInt = rand.nextInt(upperBound);

		// 	if(i % 3 == 0) {
		// 		sendMessage("humidity " + randomInt);
		// 	} 
		// 	else {
		// 		sendMessage("temperature " + randomInt);
		// 	}
		// 	Thread.sleep(2000);
		// }
	}

	public static void main(String[] args) {
		try {
			(new Publisher()).start();
			System.out.println("Publisher program completed.");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
}