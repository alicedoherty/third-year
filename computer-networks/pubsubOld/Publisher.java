// SenderProcess.java

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;
import java.util.Scanner;

public class Publisher {

	/**
	 * This method converts a string into a byte array, attaches the byte array as
	 * payload to a datagram packet and sends the packet to a given hostname and
	 * port number; currently, the name is fixed to the local host and to port
	 * number is fixed to 12345.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		final int DEST_PORT = 12345;

		DatagramPacket packet;
		DatagramSocket socket;
		InetAddress brokerAddress;
		int port;

		ObjectOutputStream ostream;
		ByteArrayOutputStream bstream;
		byte[] buffer;
		// String brokerAddress = "192.168.10.20";

		try {
			System.out.println("Publisher - Program start");

			// extract destination from arguments
			brokerAddress= InetAddress.getByName("192.168.10.20");   // InetAddress.getByName(args[0]);
			port= DEST_PORT;                       // Integer.parseInt(args[1]);

			Random rand = new Random();
			int upperBound = 10;

			// Scanner input = new Scanner(System.in);

			for(int i = 0; i < 10; i++) {

				// System.out.println("Enter data: ");
				// String data = input.nextLine();

				int randomInt = rand.nextInt(upperBound);

				// convert string "Hello World" to byte array
				bstream= new ByteArrayOutputStream();
				ostream= new ObjectOutputStream(bstream);
	
				if(i % 3 == 0) {
					ostream.writeUTF("humidity " + randomInt);
					System.out.println("Sending: humidity " + randomInt);
				}
				else {
					ostream.writeUTF("temperature " + randomInt);
					System.out.println("Sending: temperature " + randomInt);
				}

				// ostream.writeUTF(data);

				ostream.flush();
				buffer= bstream.toByteArray();
	
				// create packet addressed to destination
				packet= new DatagramPacket(buffer, buffer.length,
						brokerAddress, port);
	
				// create socket and send packet
				socket= new DatagramSocket();
				socket.send(packet);
				// String sampleData = "temp 20";
				// socket = new DatagramSocket();
				// byte[] buf = new byte[sampleData.length()];
				// buf = sampleData.getBytes();
				// packet = new DatagramPacket(buf, buf.length, brokerAddress, port);

				// socket.send(packet);
				Thread.sleep(1000);
			}


			// System.out.println("Publisher - Program end");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

}
