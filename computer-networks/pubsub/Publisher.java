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
			//dstAddress = new InetSocketAddress(DEFAULT_DST, BKR_PORT);
			dstAddress = new InetSocketAddress("broker", BKR_PORT);
			socket = new DatagramSocket(PUB_PORT);
			listener.go();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void onReceipt(DatagramPacket packet) {
		// PacketContent content= PacketContent.fromDatagramPacket(packet);
		// System.out.println(content.toString());
		// this.notify();

		// byte[] data = packet.getData();

		// switch (data[TYPE_POS]) {
		// 	case PUBACK:
		// 		System.out.println("Received publish ack");
		// 		this.notify();
		// 		break;
		// 	default:
		// 		System.out.println("Unexpected packet" + packet.toString());
		// }
	}

	// Publish message to broker
	public synchronized void sendMessage(String message) throws Exception {
		byte[] buffer = message.getBytes();
		byte[] data = new byte[HEADER_LENGTH + buffer.length];

		data[TYPE_POS] = PUBLISH;
		data[LENGTH_POS] = (byte) buffer.length;
		System.arraycopy(buffer, 0, data, HEADER_LENGTH, buffer.length);

		System.out.println("Sending packet to broker...");
		DatagramPacket packet = new DatagramPacket(data, data.length, dstAddress);
		socket.send(packet);
		System.out.println("Packet sent");
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