// Alice Doherty
// Student Number: 19333356

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Subscriber extends Node {
	InetSocketAddress dstAddress;
	String storedRequest;
	Timer timer;

	Subscriber() {
		try {
			dstAddress = new InetSocketAddress("broker", PORT);
			socket= new DatagramSocket(PORT);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	public void onReceipt(DatagramPacket packet) {
		try {
			byte[] data = packet.getData();

			switch(data[TYPE_POS]) {
				case PUBLISH:
					String content = getStringData(data, packet);
					String[] contentSplit = content.split(":");
					System.out.println("Received payload \"" + contentSplit[1] + "\" with the topic \"" + contentSplit[0] + "\"");
					break;
				case SUBACK:
					System.out.println("Received subscribe ack from broker");
					// timer.cancel();
					break;
				case UNSUBACK:
					System.out.println("Received unsubscribe ack from broker");
					// timer.cancel();
					break;
				default:
					System.out.println("Unexpected packet" + packet.toString());
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	// Send a subscribe request to the broker
	public synchronized void sendSubscriptionRequest(String message) throws Exception {
		byte[] data = makeDataByteArray(message);
		data[TYPE_POS] = SUBSCRIBE;

		DatagramPacket packet = new DatagramPacket(data, data.length, dstAddress);
		socket.send(packet);
		System.out.println("Subscribe request packet sent...");

		storedRequest = message;
		// startTimer(SUBSCRIBE);
	}

	// Send an unsubscribe request to the broker
	public synchronized void sendUnsubscriptionRequest(String message) throws Exception {
		byte[] data = makeDataByteArray(message);
		data[TYPE_POS] = UNSUBSCRIBE;

		DatagramPacket packet = new DatagramPacket(data, data.length, dstAddress);
		socket.send(packet);
		System.out.println("Unsubscribe request packet sent...");

		storedRequest = message;
		// startTimer(UNSUBSCRIBE);
	}

	// Due to a bug, the calls to this function are commented out to prevent unintended side effects
	// However, the logic still stands
	private void startTimer(byte requestType) {
		TimerTask resendRequest = new TimerTask() {
			public void run() {
				switch(requestType) {
					case SUBSCRIBE:
						try {
							sendSubscriptionRequest(storedRequest);
						} catch (Exception e) {
							e.printStackTrace();
						}
						System.out.println("Packet resent as no SUBACK was received");
						break;
					case UNSUBSCRIBE:
						try {
							sendUnsubscriptionRequest(storedRequest);
						} catch (Exception e) {
							e.printStackTrace();
						}
						System.out.println("Packet resent as no UNSUBACK was received");
						break;
					default:
						System.out.println("Unexpected request");
				}
			}
		};

		timer = new Timer();
		timer.schedule(resendRequest, 5000);
	}

	public synchronized void start() throws Exception {
		System.out.println("Subscriber program starting...");

		Scanner scanner = new Scanner(System.in);
		boolean finished = false;

		while(!finished) {
			System.out.println("To subscribe to a topic enter \"sub:<topic>\"");
			System.out.println("To unsubscribe from a topic enter \"unsub:<topic>\"");

			String input = scanner.nextLine();

			String[] splitInput = input.split(":");

			if(input.equalsIgnoreCase("exit")) {
				finished = true;
			} else if (splitInput[0].equals("sub")){
				sendSubscriptionRequest(splitInput[1]);
			} else if (splitInput[0].equals("unsub")) {
				sendUnsubscriptionRequest(splitInput[1]);
			} else {
				System.out.println("Invalid input.");
			}
		}
		scanner.close();
	}
	public static void main(String[] args) {
		try {
			(new Subscriber()).start();
			System.out.println("Subscriber program completed.");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}