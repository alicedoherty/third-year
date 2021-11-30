import java.net.DatagramSocket;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;

public class EndNode extends Node {
    EndNode() {
        try {
            socket = new DatagramSocket(PORT_NUMBER);
            listener.go();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public synchronized void onReceipt(DatagramPacket packet) {
        byte[] data = packet.getData();
        String message = getMessage(data, packet);
        System.out.println("Received message: " + message);
    }

    public synchronized void sendMessage() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the destination you want to send a message to: ");
        String destination = scanner.nextLine();

        System.out.println("Enter the message you want to send to " + destination + ":");
        String message = scanner.nextLine();

        String stringData = destination + message;

        byte[] data = makeDataByteArray(stringData);
        data[TYPE] = NETWORK_ID;
        data[LENGTH] = (byte) destination.length();

        InetSocketAddress dstAddress = new InetSocketAddress("R1", PORT_NUMBER);
        DatagramPacket packet = new DatagramPacket(data, data.length, dstAddress);
        socket.send(packet);

        System.out.println("Message "+ message + " sent to " + destination);

        // TODO Check this doesn't break code
        // scanner.close();
    }

    // Set header using type-length-value format
    // private void setHeaderInfo() {

    // }

    // If an EndNode is set to "waiting", it can then indicate it wants to receive traffic for a given string.
    public synchronized void setDestination() throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the string (destination) you want to receive messages for: ");
        String destination = scanner.nextLine();

        byte[] data = makeDataByteArray(destination);
        data[TYPE] = SET_DESTINATION;
        data[LENGTH] = (byte) destination.length();

        InetSocketAddress controllerAddr = new InetSocketAddress("controller", PORT_NUMBER);
        DatagramPacket packet = new DatagramPacket(data, data.length, controllerAddr);
        socket.send(packet);

        System.out.println("Request made to controller to receive messages for \"" + destination + "\"");
    }

    private synchronized void start() throws IOException, InterruptedException {
        System.out.println("EndNode program starting...");

        Scanner scanner = new Scanner(System.in);
        boolean finished = false;

        while(!finished) {
            System.out.println("Do you want to send or receive a message?");
            System.out.println("Enter SEND or WAIT: ");

            String choice = scanner.nextLine();

            if (choice.equalsIgnoreCase("SEND")) {
                sendMessage();
                // finished = true;
            } else if (choice.equalsIgnoreCase("WAIT")) {
                System.out.println("Waiting for messages...");
                setDestination();
                // finished = true;
                this.wait();
            } else if (choice.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye ;(");
                finished = true;
            } else {
                System.out.println("Invalid input.");
            }
        }
        scanner.close();
    }

    public static void main(String[] args) {
		try {
			(new EndNode()).start();
			System.out.println("EndNode program completed.");
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}
}
