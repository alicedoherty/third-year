// Alice Doherty
// Student Number: 19333356

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Publisher extends Node {
	InetSocketAddress dstAddress;
	String storedMessage;
	Timer timer;

	Publisher() {
		try {
			dstAddress = new InetSocketAddress("broker", PORT);
			socket = new DatagramSocket(PORT);
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
					timer.cancel();
					break;
				default:
					System.out.println("Received unexpected packet" + packet.toString());
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	// Publish message to the broker
	public synchronized void sendMessage(String message, String retainChoice) throws Exception {
		byte[] data = makeDataByteArray(message);
		data[TYPE_POS] = PUBLISH;

		// Set the retain flag to TRUE or FALSE depending on the user's choice
		if(retainChoice.equalsIgnoreCase("y")) {
			data[RETAIN_FLAG] = TRUE;
		} else if(retainChoice.equalsIgnoreCase("n")) {
			data[RETAIN_FLAG] = FALSE;
		}
		
		DatagramPacket packet = new DatagramPacket(data, data.length, dstAddress);
		socket.send(packet);

		String[] splitMessage = message.split(":");
		System.out.println("\"" + splitMessage[0] + "\" with topic \"" + splitMessage[1] + "\" sent to broker");

		// Stores last PUBLISH packet until PUBACK received
		storedMessage = message;
		startTimer();
	}

	// Starts timer once PUBLISH packet sent, if PUBACK isn't received within 5 secs,
	// the packet is resent.
	private void startTimer() {
		TimerTask resendPacket = new TimerTask() {
			public void run() {
				try {
					sendMessage(storedMessage, "y");
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("Packet resent as no PUBACK was received");
			}
		};

		timer = new Timer();
		timer.schedule(resendPacket, 5000);
	}

	private void start() throws Exception {
		System.out.println("Publisher program starting...");

		Scanner scanner = new Scanner(System.in);
		boolean finished = false;

		while(!finished) {
			System.out.println("Enter data to be published (topic:payload), or enter \"exit\": ");

			String input = scanner.nextLine();

			if(input.equalsIgnoreCase("exit")) {
				finished = true;
			} else {
				System.out.println("Would you like to set the RETAIN flag? (y/n)");
				String retainChoice = scanner.nextLine();
				sendMessage(input, retainChoice);
			}	
		}
		scanner.close();
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