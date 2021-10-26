import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

// TODO comment functions

public class Publisher extends Node {
	InetSocketAddress dstAddress;
	String storedMessage;
	Timer timer;

	Publisher() {
		try {
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
					timer.cancel();
					break;
				default:
					System.out.println("Received unexpected packet" + packet.toString());
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	// Publish message to broker
	public synchronized void sendMessage(String message) throws Exception {
		byte[] data = makeDataByteArray(message);
		data[TYPE_POS] = PUBLISH;

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
					sendMessage(storedMessage);
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
			System.out.println("Enter data to be published (topic:payload): ");

			String input = scanner.nextLine();

			if(input.equalsIgnoreCase("exit")) {
				finished = true;
			} else {
				sendMessage(input);
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